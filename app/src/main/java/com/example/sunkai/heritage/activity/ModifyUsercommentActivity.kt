package com.example.sunkai.heritage.activity

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Base64
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ProgressBar
import androidx.core.graphics.drawable.toBitmap
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.example.sunkai.heritage.activity.baseActivity.BaseTakeCameraActivity
import com.example.sunkai.heritage.connectWebService.BaseSetting
import com.example.sunkai.heritage.connectWebService.HandleFind
import com.example.sunkai.heritage.entity.UserCommentData
import com.example.sunkai.heritage.R
import com.example.sunkai.heritage.tools.MakeToast.toast
import com.example.sunkai.heritage.tools.toByteArray
import com.example.sunkai.heritage.value.DATA
import com.example.sunkai.heritage.value.ERROR
import com.example.sunkai.heritage.value.IMAGE
import com.example.sunkai.heritage.value.UPDATE_SUCCESS
import kotlinx.android.synthetic.main.activity_add_find_comment.*

/**
 * 修改帖子的页面
 */
class ModifyUsercommentActivity : BaseTakeCameraActivity(), View.OnClickListener {

    var data: UserCommentData? = null
    private var modifyImageBitmap: Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_modify_usercomment)
        initview()
        if (intent.getSerializableExtra(DATA) is UserCommentData) {
            data = intent?.getSerializableExtra(DATA) as UserCommentData
            add_comment_title.setText(data!!.commentTitle)
            add_comment_content.setText(data!!.commentContent)
            glide.load(BaseSetting.URL + data!!.imageUrl).into(simpleTarget)
        }
    }

    private val simpleTarget = object : SimpleTarget<Drawable>() {
        override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
            modifyImageBitmap = resource.toBitmap()
            add_comment_image.setImageDrawable(resource)
        }
    }

    private fun initview() {
        add_comment_title.setOnClickListener(this)
    }

    private fun setViewsIsEnable(isEnable: Boolean) {
        add_comment_title.isEnabled = isEnable
        add_comment_content.isEnabled = isEnable
        add_comment_image.isEnabled = isEnable
    }

    private fun setViewsUnable() {
        setViewsIsEnable(false)
    }

    private fun setViewsEnable() {
        setViewsIsEnable(true)
    }

    private fun setDatas() {
        val data = data ?: return
        data.commentTitle = add_comment_title.text.toString()
        data.commentContent = add_comment_content.text.toString()

        this.data = data
    }

    private fun updateUserCommentData(item: MenuItem) {
        val data = data
        val modifyBitmap = modifyImageBitmap ?: return
        val progressBar = ProgressBar(this@ModifyUsercommentActivity)
        item.actionView = progressBar
        item.isEnabled = false
        data?.let {
            setViewsUnable()
            requestHttp {
                setDatas()
                val result = HandleFind.UpdateUserCommentInformaiton(data.id, data.commentTitle, data.commentContent, Base64.encodeToString(modifyBitmap.toByteArray(), Base64.DEFAULT))
                runOnUiThread {
                    if (result != ERROR) {
                        toast(getString(R.string.update_success))
                        data.imageUrl = result
                        val intent = Intent()
                        intent.putExtra(DATA, data)
                        intent.putExtra(IMAGE, modifyBitmap.toByteArray())
                        setResult(UPDATE_SUCCESS, intent)
                        finish()
                    } else {
                        toast(getString(R.string.update_fail))
                        setViewsEnable()
                        item.isEnabled = true
                        item.actionView = null
                    }
                }

            }
        }
    }

    override fun setImageToImageView(bitmap: Bitmap) {
        add_comment_image.setImageBitmap(bitmap)
        modifyImageBitmap = bitmap
    }

    override fun onClick(v: View?) {}

    override fun getNeedOpenChooseImageView(): Array<View> {
        return arrayOf(add_comment_image)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.modify_comment_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.modify_comment_menu_modify -> {
                updateUserCommentData(item)
            }
        }
        return true
    }

    override fun onDestroy() {
        super.onDestroy()
        modifyImageBitmap = null
    }
}