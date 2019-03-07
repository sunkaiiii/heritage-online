package com.example.sunkai.heritage.tools

import android.app.Activity
import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.ImageView
import com.example.sunkai.heritage.R
import com.example.sunkai.heritage.activity.ViewImageActivity
import com.example.sunkai.heritage.value.IMAGE_POSITION
import com.example.sunkai.heritage.value.IMAGE_URL

object ViewImageUtils {
    fun setViewImageClick(context: Context, imageView: View, url: String) {
        ViewImageUtils.setViewImageClick(context, imageView, arrayOf(url), 0)
    }

    fun setViewImageClick(context: Context, imageView: View, urls: Array<String>, position: Int) {
        val intent = Intent(context, ViewImageActivity::class.java)
        intent.putExtra(IMAGE_URL, urls)
        intent.putExtra(IMAGE_POSITION, position)
        if (context is Activity) {
            context.startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(context, imageView, context.getString(R.string.share_view_image)).toBundle())
        } else {
            context.startActivity(intent)
        }
    }
}