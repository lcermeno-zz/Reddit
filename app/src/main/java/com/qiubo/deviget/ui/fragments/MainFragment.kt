package com.qiubo.deviget.ui.fragments


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.qiubo.deviget.R
import com.qiubo.deviget.databinding.FragmentMainBinding
import com.qiubo.deviget.ui.adapters.PostAdapter
import com.qiubo.deviget.viewData.PostViewData
import com.qiubo.deviget.viewmodels.MainViewModel

/**
 * A simple [Fragment] subclass.
 */
class MainFragment : Fragment(), PostAdapter.OnClickListener {

    private lateinit var _viewModel: MainViewModel
    private lateinit var _binding: FragmentMainBinding
    private lateinit var _navController: NavController
    private val _adapter by lazy { PostAdapter(this) }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        _binding = DataBindingUtil.inflate(layoutInflater, R.layout.fragment_main, container, false)
        _binding.lifecycleOwner = this
        _binding.viewModel = _viewModel
        // Inflate the layout for this fragment
        return _binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _navController = Navigation.findNavController(view)
        _binding.mainRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = _adapter
            val dividerItemDecoration = DividerItemDecoration(
                context,
                LinearLayoutManager.VERTICAL
            )
            addItemDecoration(dividerItemDecoration)
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    if (!recyclerView.canScrollVertically(1)) _viewModel.loadMoreItems()
                }
            })
        }

        _binding.mainRefreshLayout.setOnRefreshListener { _viewModel.fetchPosts() }
        _viewModel.progress.observe(viewLifecycleOwner, Observer { _binding.mainRefreshLayout.isRefreshing = it })
        _viewModel.error.observe(viewLifecycleOwner, Observer { Snackbar.make(_binding.root, R.string.generic_error, Snackbar.LENGTH_LONG).show() })
        _viewModel.posts.observe(viewLifecycleOwner, Observer { _adapter.setItems(it) })
        _viewModel.morePosts.observe(viewLifecycleOwner, Observer { _adapter.loadMore(it) })

    }

    override fun onDismissPost(postViewData: PostViewData) {
        _adapter.remove(postViewData)
    }

    override fun showPost(postViewData: PostViewData) {
        val action = MainFragmentDirections.actionMainFragmentToDetailFragment(postViewData)
        _navController.navigate(action)
        _adapter.update(postViewData.apply { seen = true })
        _viewModel.markSeenPost(postViewData)
    }
}
