package com.example.sunkai.heritage.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.example.sunkai.heritage.adapter.baseAdapter.BaseLoadMoreRecyclerAdapter
import com.example.sunkai.heritage.entity.NewsListResponse
import com.example.sunkai.heritage.R
import com.example.sunkai.heritage.activity.BottomNewsDetailActivity
import com.example.sunkai.heritage.connectWebService.EHeritageApi
import com.example.sunkai.heritage.tools.ViewImageUtils
import com.example.sunkai.heritage.tools.loadImageFromServer
import com.example.sunkai.heritage.value.API
import com.example.sunkai.heritage.value.DATA

/**
 * 首页底部聚焦非遗的adapter
 * Created by sunkai on 2018/2/12.
 */
class BottomFolkNewsRecyclerviewAdapter(context: Context, data: List<NewsListResponse>, glide: RequestManager, private val requestDetailApi: EHeritageApi) : BaseLoadMoreRecyclerAdapter<BottomFolkNewsRecyclerviewAdapter.Holder, NewsListResponse>(context, data, glide) {

    class Holder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView
        val time: TextView
        val briefly: TextView
        val image: ImageView

        init {
            title = view.findViewById(R.id.bottom_view_title)
            time = view.findViewById(R.id.bottom_view_time)
            briefly = view.findViewById(R.id.bottom_view_briefly)
            image = view.findViewById(R.id.bottom_view_image)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(context).inflate(R.layout.bottom_folk_news_layout, parent, false)
        return Holder(view)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        super.onBindViewHolder(holder, position)
        val data = getItem(position)
        setData(holder, data)
    }

    private fun setData(holder: Holder, data: NewsListResponse) {
        holder.image.visibility = View.GONE
        holder.image.setImageDrawable(null)
        holder.title.text = data.title
        holder.time.text = data.date
        holder.briefly.text = data.content
        if (!data.img.isNullOrEmpty()) {
            holder.image.visibility = View.VISIBLE
            glide.loadImageFromServer(data.compressImg ?: data.img).into(holder.image)
            holder.image.setOnClickListener {
                ViewImageUtils.setViewImageClick(context, holder.image, data.img)
            }
        }
    }


    override fun addNewData(data: List<NewsListResponse>) {
        val extendData = this.datas.toMutableList()
        extendData.addAll(data)
        this.datas = extendData
        notifyDataSetChanged()
    }

    override fun setItemClick(itemView: View, item: NewsListResponse) {
        val intent = Intent(context, BottomNewsDetailActivity::class.java)
        intent.putExtra(DATA, item)
        intent.putExtra(API, requestDetailApi)
        context.startActivity(intent)
    }

}