package com.example.sunkai.heritage.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.sunkai.heritage.adapter.NewsListAdapter
import com.example.sunkai.heritage.interfaces.OnPageLoaded
import com.example.sunkai.heritage.R
import com.example.sunkai.heritage.connectWebService.RequestHelper
import com.example.sunkai.heritage.entity.response.NewsListResponse
import com.example.sunkai.heritage.entity.request.BasePathRequest
import com.example.sunkai.heritage.fragment.baseFragment.BaseLazyLoadFragment
import com.example.sunkai.heritage.interfaces.NetworkRequest
import com.example.sunkai.heritage.interfaces.RequestAction
import com.example.sunkai.heritage.tools.OnSrollHelper
import kotlinx.android.synthetic.main.news_list_framgent.*

class NewsListFragment : BaseLazyLoadFragment(), OnPageLoaded {
    var reqeustArgument: MainFragment.NewsPages? = null
    var pageNumber = 1

    private fun createRequestBean():NetworkRequest{
        return object : BasePathRequest() {
            override fun getPathParamerater(): List<String> {
                return listOf(pageNumber++.toString())
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.news_list_framgent, container, false)
    }


    override fun startLoadInformation() {
        loadInformation()
    }

    override fun onRestoreFragmentLoadInformation() {
        loadInformation()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initview()
    }


    private fun initview() {
        fragmentMainRecyclerview.addOnScrollListener(onScroller)
        bottomNewsRefreshLayout.setOnRefreshListener {
            pageNumber = 1
            loadInformation()
        }
    }


    private fun loadInformation() {
        onPreLoad()
        reqeustArgument = arguments?.getSerializable(MainFragment.PAGE) as MainFragment.NewsPages
        requestHttp(reqeustArgument?.reqeustApi ?: return, createRequestBean())
    }

    override fun onTaskReturned(api: RequestHelper, action: RequestAction, response: String) {
        super.onTaskReturned(api, action, response)
        when (api.getRequestApi()) {
            reqeustArgument?.reqeustApi -> {
                val data = fromJsonToList(response, NewsListResponse::class.java)
                if (fragmentMainRecyclerview.adapter == null) {
                    val adapter = NewsListAdapter(activity
                            ?: return, data, glide, reqeustArgument?.detailApi ?: return)
                    fragmentMainRecyclerview.adapter = adapter
                    onPostLoad()
                } else {
                    val adapter = fragmentMainRecyclerview.adapter as NewsListAdapter
                    adapter.addNewData(data)
                }
            }
        }
    }


    override fun onPreLoad() {
        bottomNewsRefreshLayout.isRefreshing = true
        fragmentMainRecyclerview.adapter = null
    }

    override fun onPostLoad() {
        bottomNewsRefreshLayout.isRefreshing = false
        //顶部卡片加载完成后，显示顶部卡片的背景图片

    }

    private val onScroller = object : OnSrollHelper() {
        override fun loadMoreData(recyclerView: RecyclerView) {
            requestHttp(reqeustArgument?.reqeustApi ?: return, createRequestBean())
        }

    }

}