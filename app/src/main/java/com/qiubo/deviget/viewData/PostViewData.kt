package com.qiubo.deviget.viewData

import android.graphics.drawable.Drawable
import android.os.Parcelable
import android.util.Log
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.qiubo.deviget.api.RedditChild
import kotlinx.android.parcel.Parcelize
import org.ocpsoft.prettytime.PrettyTime
import java.util.*

@Parcelize
data class PostViewData(
    val id: String,
    val title: String,
    val author: String,
    val date: String,
    val thumbnailUrl: String,
    val commentQty: String,
    var seen: Boolean = false
) : Parcelable

fun RedditChild.toViewData(prettyTime: PrettyTime) = PostViewData(
    data.id,
    data.title,
    data.author,
    prettyTime.format(Date(data.date * 1000)),
    data.thumbnailUrl,
    "${data.commentQty} comments"
)

@BindingAdapter("imageUrl")
fun loadImage(view: ImageView, url: String) {
    Glide
        .with(view.context)
        .load(url)
        .listener(object : RequestListener<Drawable> {
            override fun onLoadFailed(
                e: GlideException?,
                model: Any?,
                target: Target<Drawable>?,
                isFirstResource: Boolean
            ): Boolean {
                Log.e("PostViewData", "Image Load failed: ${e?.message}")
                return false
            }

            override fun onResourceReady(
                resource: Drawable?,
                model: Any?,
                target: Target<Drawable>?,
                dataSource: DataSource?,
                isFirstResource: Boolean
            ): Boolean {
                Log.d("PostViewData", "Image Loaded")
                return false
            }

        })
        .into(view)
}