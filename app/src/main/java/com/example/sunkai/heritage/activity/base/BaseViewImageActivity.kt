package com.example.sunkai.heritage.activity.base

import android.util.Log
import android.view.View
import com.example.sunkai.heritage.views.SwipePhotoView
import com.example.sunkai.heritage.interfaces.onPhotoViewImageClick
import kotlin.math.abs
import kotlin.math.pow

abstract class BaseViewImageActivity : BaseGlideActivity(), SwipePhotoView.OnDragListner, onPhotoViewImageClick {

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        getRootView().background.alpha = 255
    }

    override fun onDrag(imageView: SwipePhotoView, dx: Int, dy: Int) {
        Log.d("ViewImageActivity", String.format("dy:%d,alpha:%d", dy, (255 - (255 * abs(dy) / getRootView().height / 2.0).pow(1.2)).toInt()))
        getRootView().background.alpha = (255 - (255 * abs(dy) / getRootView().height / 2.0).pow(1.2)).toInt()
        val scale = (1.0 - (1.0 * abs(dy) / getRootView().height / 2.0).pow(0.8)).toFloat()
        val height = imageView.getRawHeight()
        val width = imageView.getRawWidth()
        if (height != -1 && width != -1) {
            val layoutParams = imageView.layoutParams
            layoutParams.height = (height * scale).toInt()
            layoutParams.width = (width * scale).toInt()
            Log.d("ViewImageActivity", String.format("rawHeight:%d,rawWidth:%d,height:%d,width:%d", height, width, layoutParams.height, layoutParams.width))
            imageView.layoutParams = layoutParams
        }
    }

    override fun onDragClose() {
        onBackPressed()
    }

    override fun onImageBack(originalAlpha: Int, currentTime: Long, duration: Long) {
        val alpha = if (currentTime != duration) {
            originalAlpha + (255 - originalAlpha) * currentTime / duration
        } else {
            255
        }
        getRootView().background.alpha = alpha.toInt()
    }

    override fun onImageClick(position: Int, swipePhotoView: SwipePhotoView) {
        if (swipePhotoView.scale == 1.0f) {
            onBackPressed()
        } else {
            swipePhotoView.setScale(1.0f, true)
        }
    }

    override fun onImageLongCick(position: Int, swipePhotoView: SwipePhotoView) {
        if(swipePhotoView.isMoved()){
            return
        }
        swipePhotoView.showContextMenu()
    }

    override fun getRootViewAlphaWhenImageBack(): Int = getRootView().background.alpha
    abstract fun getRootView(): View
}