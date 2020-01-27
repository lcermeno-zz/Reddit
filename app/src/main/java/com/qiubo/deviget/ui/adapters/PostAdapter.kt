package com.qiubo.deviget.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.qiubo.deviget.R
import com.qiubo.deviget.databinding.ItemPostMediaBinding
import com.qiubo.deviget.ui.viewholders.PostViewHolder
import com.qiubo.deviget.viewData.PostViewData


class PostAdapter(private val listener: OnClickListener) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    interface OnClickListener {
        fun onDismissPost(postViewData: PostViewData)

        fun showPost(postViewData: PostViewData)
    }

    private val _items: MutableList<PostViewData> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding: ItemPostMediaBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_post_media, parent,
            false
        )
        binding.listener = listener
        return PostViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is PostViewHolder)
            holder.binding.post = _items[position]
    }

    override fun getItemCount(): Int = _items.size

    fun setItems(items: List<PostViewData>) {
        val diffCallback = PostListDiffCallback(_items, items)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        _items.clear()
        _items.addAll(items)
        diffResult.dispatchUpdatesTo(this)
    }

    fun loadMore(items: List<PostViewData>) {
        val newList = mutableListOf<PostViewData>().apply {
            addAll(_items)
            addAll(items)
        }
        val diffCallback = PostListDiffCallback(_items, newList)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        _items.clear()
        _items.addAll(newList)
        diffResult.dispatchUpdatesTo(this)
    }

    fun remove(postViewData: PostViewData) {
        val index = _items.indexOf(postViewData)
        if (index != -1) {
            _items.remove(postViewData)
            notifyItemRemoved(index)
        }
    }

    fun update(postViewData: PostViewData) {
        val index = _items.indexOf(postViewData)
        if (index != -1) {
            notifyItemChanged(index)
        }
    }

    class PostListDiffCallback(private val oldList: List<PostViewData>, private val newList: List<PostViewData>): DiffUtil.Callback() {

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean = oldList[oldItemPosition].id == newList[newItemPosition].id

        override fun getOldListSize(): Int = oldList.size

        override fun getNewListSize(): Int = newList.size

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean = oldList[oldItemPosition] == newList[newItemPosition]
    }
}