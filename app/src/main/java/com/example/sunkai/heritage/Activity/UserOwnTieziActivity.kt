package com.example.sunkai.heritage.Activity

import android.app.ActivityOptions
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.widget.GridLayoutManager
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.example.sunkai.heritage.Activity.BaseActivity.BaseAutoLoginActivity
import com.example.sunkai.heritage.Activity.LoginActivity.LoginActivity
import com.example.sunkai.heritage.Adapter.MyOwnCommentRecyclerViewAdapter
import com.example.sunkai.heritage.ConnectWebService.HandleFind
import com.example.sunkai.heritage.Interface.OnItemClickListener
import com.example.sunkai.heritage.Interface.OnItemLongClickListener
import com.example.sunkai.heritage.Interface.OnPageLoaded
import com.example.sunkai.heritage.R
import com.example.sunkai.heritage.tools.CreateTransitionPair
import com.example.sunkai.heritage.tools.MakeToast
import com.example.sunkai.heritage.value.DATA
import com.example.sunkai.heritage.value.GRID_LAYOUT_DESTINY
import com.example.sunkai.heritage.value.MODIFY_USER_COMMENT
import com.example.sunkai.heritage.value.UPDATE_SUCCESS
import kotlinx.android.synthetic.main.activity_user_own_tiezi.*

/**
 * 我的帖子的Activity
 */
class UserOwnTieziActivity : BaseAutoLoginActivity(),OnPageLoaded {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_own_tiezi)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        initView()
        getInformation()
    }

    private fun initView(){
        userOwnTieziRefresh.setOnRefreshListener {
            getInformation()
        }
    }


    override fun getInformation() {
        onPreLoad()
        requestHttp {
            val datas = HandleFind.GetUserCommentInformaitonByOwn(LoginActivity.userID)
            runOnUiThread {
                onPostLoad()
                val adapter = MyOwnCommentRecyclerViewAdapter(this, datas, glide)
                setAdpterClick(adapter)
                setAdpterLongClick(adapter)
                setAdapterListener(adapter)
                userOwnList.layoutManager = GridLayoutManager(this, GRID_LAYOUT_DESTINY)
                userOwnList.adapter = adapter
            }
        }
    }

    private fun setAdpterClick(adapter: MyOwnCommentRecyclerViewAdapter) {

        adapter.setOnItemClickListen(object : OnItemClickListener {
            override fun onItemClick(view: View, position: Int) {
                val intent = Intent(this@UserOwnTieziActivity, UserCommentDetailActivity::class.java)
                intent.putExtra(DATA, adapter.getItem(position))
                if (Build.VERSION.SDK_INT >= 21) {
                    val imageView = view.findViewById<ImageView>(R.id.mycomment_item_image)
                    val title = view.findViewById<TextView>(R.id.mycomment_item_title)
                    val content = view.findViewById<TextView>(R.id.mycomment_item_content)
                    val pairs = arrayOf(CreateTransitionPair(imageView, R.string.find_share_view),
                            CreateTransitionPair(title, R.string.find_share_title),
                            CreateTransitionPair(content, R.string.find_share_content))
                    startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this@UserOwnTieziActivity, *pairs).toBundle())
                } else {
                    startActivity(intent)
                }
            }
        })
    }

    private fun setAdpterLongClick(adapter: MyOwnCommentRecyclerViewAdapter) {
        adapter.setOnItemLongClickListener(object : OnItemLongClickListener {
            override fun onItemlongClick(view: View, position: Int) {
                AlertDialog.Builder(this@UserOwnTieziActivity).setTitle("是否删除帖子")
                        .setPositiveButton("删除", { _, _ ->
                            val ad = AlertDialog.Builder(this@UserOwnTieziActivity)
                                    .setView(LayoutInflater.from(this@UserOwnTieziActivity).inflate(R.layout.progress_view, userOwnList, false))
                                    .create()
                            ad.show()
                            requestHttp {
                                val userCommentData = adapter.getItem(position)
                                val result = HandleFind.DeleteUserCommentByID(userCommentData.id)
                                runOnUiThread {
                                    if (ad.isShowing) {
                                        ad.dismiss()
                                        if (result) {
                                            MakeToast.MakeText("删除成功")
                                        } else {
                                            MakeToast.MakeText("出现问题，请稍后再试")
                                        }
                                    }
                                    refreshList()
                                }
                            }
                        }).setNegativeButton("取消", { _, _ -> })
                        .create().show()
            }
        })
    }

    private fun setAdapterListener(adapter: MyOwnCommentRecyclerViewAdapter) {
        adapter.setOnDeleteSuccessListener(object : MyOwnCommentRecyclerViewAdapter.onDeleteSuccessListener {
            override fun onDeleteSuccess() {
                getInformation()
            }

        })
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home -> finish()
        }
        return super.onOptionsItemSelected(item)
    }


    fun refreshList() {
        getInformation()
    }

    override fun onPreLoad() {
        userOwnTieziRefresh.isRefreshing=true
        userOwnList.adapter=null
    }

    override fun onPostLoad() {
        userOwnTieziRefresh.isRefreshing=false
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            MODIFY_USER_COMMENT -> {
                if (resultCode == UPDATE_SUCCESS) {
                    getInformation()
                }
            }
        }
    }
}