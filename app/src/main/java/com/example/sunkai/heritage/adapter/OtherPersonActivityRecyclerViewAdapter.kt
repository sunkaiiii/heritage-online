package com.example.sunkai.heritage.adapter

import android.content.Context
import android.os.AsyncTask
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.example.sunkai.heritage.R
import com.example.sunkai.heritage.adapter.baseAdapter.BaseRecyclerAdapter
import com.example.sunkai.heritage.connectWebService.HandleFind
import com.example.sunkai.heritage.entity.UserCommentImages
import com.example.sunkai.heritage.tools.GlobalContext
import com.example.sunkai.heritage.value.ERROR
import com.example.sunkai.heritage.value.RESULT_NULL
import com.example.sunkai.heritage.value.RESULT_OK
import java.lang.ref.WeakReference

/*
 * Created by sunkai on 2018/1/2.
 */
class OtherPersonActivityRecyclerViewAdapter(context: Context, val userID: Int, datas: List<UserCommentImages>, glide: RequestManager) : BaseRecyclerAdapter<OtherPersonActivityRecyclerViewAdapter.ViewHolder, UserCommentImages>(context, datas, glide) {


    init {
        getUserIdInfo(userID)
    }

    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.iv_other_person_view)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(GlobalContext.instance).inflate(R.layout.other_person_view, parent, false)
        view.setOnClickListener(this)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        getImage(datas[position], holder.imageView)
    }


    private fun getUserIdInfo(userID: Int) {
        GetUserInfoTask(userID, this).execute()
    }

    internal class GetUserInfoTask(val userID: Int, adapter: OtherPersonActivityRecyclerViewAdapter) : AsyncTask<Void, Void, Int>() {
        private val weakRefrece: WeakReference<OtherPersonActivityRecyclerViewAdapter> = WeakReference(adapter)

        override fun doInBackground(vararg params: Void?): Int {
            val adapter = weakRefrece.get()
            adapter?.let {
                adapter.datas = HandleFind.GetUserCommentIdByUser(userID).toMutableList()
                return RESULT_OK
            }
            return RESULT_NULL
        }

        override fun onPostExecute(result: Int) {
            val adapter = weakRefrece.get()
            when (result) {
                RESULT_OK -> adapter?.notifyDataSetChanged()
            }
        }
    }


    private fun getImage(imageInfo: UserCommentImages, imageview: ImageView) {
        if (imageInfo.imageUrl == null || imageInfo.imageUrl == ERROR) {
            return
        }
        glide.load(imageInfo.imageUrl).into(imageview)
    }


}