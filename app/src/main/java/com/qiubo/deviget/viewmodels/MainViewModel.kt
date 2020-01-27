package com.qiubo.deviget.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.qiubo.deviget.api.GetTopRequest
import com.qiubo.deviget.repositories.RedditRepository
import com.qiubo.deviget.usecases.GetTopUseCase
import com.qiubo.deviget.viewData.PostViewData
import com.qiubo.deviget.viewData.toViewData
import kotlinx.coroutines.launch
import org.ocpsoft.prettytime.PrettyTime
import java.util.*

class MainViewModel : ViewModel() {

    private var getTopUseCase: GetTopUseCase = GetTopUseCase(RedditRepository())

    private val _progress = MutableLiveData<Boolean>()
    private val _posts = MutableLiveData<List<PostViewData>>()
    private val _morePosts = MutableLiveData<List<PostViewData>>()
    private val _error = MutableLiveData<Unit>()
    private var _after: String? = null

    val progress: LiveData<Boolean>
        get() = _progress
    val posts: LiveData<List<PostViewData>>
        get() = _posts
    val morePosts: LiveData<List<PostViewData>>
        get() = _morePosts
    val error: LiveData<Unit>
        get() = _error

    init {
        fetchPosts()
    }

    fun fetchPosts(request: GetTopRequest? = null) = viewModelScope.launch {
        _progress.postValue(true)
        val event = getTopUseCase(request)
        when (event.success) {
            true -> {
                _after = event.getTopResponse?.data?.after
                event.getTopResponse?.data?.children?.let {
                    val items = it.map { it.toViewData(PrettyTime(Locale.getDefault())) }
                    if(request != null)
                        _morePosts.postValue(items)
                    else
                        _posts.postValue(items)
                }

            }
            false -> {
                event.message?.let { Log.e("MainViewModel", it) }
                _error.postValue(Unit)

            }
        }
        _progress.postValue(false)
    }

    fun loadMoreItems() {
        if (_progress.value == false) {
            _after?.let { fetchPosts(GetTopRequest(it)) }
        }
    }

    fun dismissAll() {
        _after = null
        _posts.postValue(mutableListOf())
    }

}