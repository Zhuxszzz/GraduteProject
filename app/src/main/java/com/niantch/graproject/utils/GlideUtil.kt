package com.niantch.graproject.utils

import android.content.Context
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.niantch.graproject.R

object GlideUtil {
    val REQUEST_OPTIONS: RequestOptions = RequestOptions()
            .placeholder(R.drawable.no_banner)
            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC) //硬盘缓存,内存缓存是自动开启的
            .dontAnimate()

    fun load(
            context: Context,
            res: Any?,
            imageView: ImageView
    ) {
        Glide.with(context)
                .load(res)
                .into(imageView)
    }

    fun load(
            context: Context,
            res: Any?,
            imageView: ImageView,
            requestOptions: RequestOptions
    ) {
        Glide.with(context)
                .load(res)
                .apply(requestOptions)
                .into(imageView)
    }

}