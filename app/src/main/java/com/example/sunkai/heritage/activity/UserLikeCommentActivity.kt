package com.example.sunkai.heritage.activity

import android.os.Bundle
import android.util.Log
import com.example.sunkai.heritage.activity.baseActivity.BaseGlideActivity
import com.example.sunkai.heritage.activity.loginActivity.LoginActivity
import com.example.sunkai.heritage.adapter.MyLikeCommentRecyclerAdapter
import com.example.sunkai.heritage.connectWebService.HandleFind
import com.example.sunkai.heritage.interfaces.OnPageLoaded
import com.example.sunkai.heritage.R
import com.example.sunkai.heritage.value.GRID_LAYOUT_DESTINY
import kotlinx.android.synthetic.main.activity_user_like_comment.*

/**
 * 我的赞的Activity
 */

class UserLikeCommentActivity : BaseGlideActivity(), OnPageLoaded {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_like_comment)
        initview()
        getMyLikeData()
    }

    private fun initview() {
        userLikeActivityRefresh.setOnRefreshListener {
            getMyLikeData()
        }
    }

    private fun getMyLikeData() {
        onPreLoad()
        requestHttp {
            val data = HandleFind.GetUserLikeComment(LoginActivity.userID)
            runOnUiThread {
                onPostLoad()
                val adapter = MyLikeCommentRecyclerAdapter(this, data,glide)
                Log.d("destiny", GRID_LAYOUT_DESTINY.toString())
                userLikeActivityRecyclerView.layoutManager = androidx.recyclerview.widget.GridLayoutManager(this, GRID_LAYOUT_DESTINY)
                userLikeActivityRecyclerView.adapter = adapter
            }
        }
    }


    override fun onPreLoad() {
        userLikeActivityRefresh.isRefreshing = true
        userLikeActivityRecyclerView.adapter = null
    }

    override fun onPostLoad() {
        userLikeActivityRefresh.isRefreshing = false
    }
}