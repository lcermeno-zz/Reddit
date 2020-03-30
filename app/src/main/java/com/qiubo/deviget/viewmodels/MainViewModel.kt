package com.qiubo.deviget.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.qiubo.deviget.api.ApiClient
import com.qiubo.deviget.api.ErrorResponse
import com.qiubo.deviget.api.GetTopRequest
import com.qiubo.deviget.api.ResultWrapper
import com.qiubo.deviget.events.GetTopEvent
import com.qiubo.deviget.viewData.PostViewData
import com.qiubo.deviget.viewData.toViewData
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.ocpsoft.prettytime.PrettyTime
import retrofit2.HttpException
import java.io.IOException
import java.util.*
import kotlin.collections.HashMap

class MainViewModel : ViewModel() {

    private val _progress = MutableLiveData<Boolean>()
    private val _posts = MutableLiveData<List<PostViewData>>()
    private val _morePosts = MutableLiveData<List<PostViewData>>()
    private val _error = MutableLiveData<Unit>()
    private var _after: String? = null
    private val _seenPosts = HashMap<String, Boolean>()

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
        val event = fetchTopEntries(request)
        when (event.success) {
            true -> {
                _after = event.getTopResponse?.data?.after
                event.getTopResponse?.data?.children?.let {
                    val items = it.map {
                        it.toViewData(PrettyTime(Locale.getDefault())).apply {
                            seen = _seenPosts.containsKey(id)
                        }
                    }
                    if (request != null)
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

    fun markSeenPost(postViewData: PostViewData) {
        _seenPosts[postViewData.id] = true
    }

    // networking

    suspend fun <T> safeApiCall(
        dispatcher: CoroutineDispatcher = Dispatchers.IO,
        apiCall: suspend () -> T
    ): ResultWrapper<T> {
        return withContext(dispatcher) {
            try {
                ResultWrapper.Success(apiCall.invoke())
            } catch (throwable: Throwable) {

                throwable.message?.let { Log.e("Repository", it) }

                when (throwable) {
                    is IOException -> ResultWrapper.NetworkError
                    is HttpException -> {
                        val code = throwable.code()
                        val errorResponse = convertErrorBody(throwable)
                        ResultWrapper.GenericError(code, errorResponse)
                    }
                    else -> {
                        ResultWrapper.GenericError(null, null)
                    }
                }
            }
        }
    }

    private fun convertErrorBody(throwable: HttpException): ErrorResponse? {
        return try {
            throwable.response()?.errorBody()?.toString()?.let {
                Gson().fromJson(it, ErrorResponse::class.java)
            }
        } catch (exception: Exception) {
            null
        }
    }

    // web service

    suspend fun fetchTopEntries(request: GetTopRequest?): GetTopEvent {
        return when (val response =
            safeApiCall { ApiClient.getRedditService().fetchTop(request?.after) }) {
            is ResultWrapper.Success -> GetTopEvent(response.value).apply { success = true }
            is ResultWrapper.GenericError -> GetTopEvent().apply {
                message = response.error?.message
            }
            is ResultWrapper.NetworkError -> GetTopEvent().apply { message = "Network Error" }
        }

    }

}