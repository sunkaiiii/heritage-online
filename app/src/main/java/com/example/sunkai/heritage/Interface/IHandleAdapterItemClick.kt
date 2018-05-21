package com.example.sunkai.heritage.Interface

import android.content.Context
import com.example.sunkai.heritage.Adapter.*
import com.example.sunkai.heritage.Adapter.BaseAdapter.BaseRecyclerAdapter

interface IHandleAdapterItemClick {
    fun handleBottomNewsAdapterItemClick(context: Context, adapter: BottomFolkNewsRecyclerviewAdapter)
    fun handleAllFolkNewsAdapterItemClick(context: Context, adapter: SeeMoreNewsRecyclerViewAdapter)
    fun handleFolkHeritageAdapterItemCLick(context: Context, adapter: FolkRecyclerViewAdapter)
    fun handleFindUserCommentAdapterItemClick(context: Context, adapter: FindFragmentRecyclerViewAdapter)
    fun handlePersonAdapterItemClick(context: Context, adapter: SearchUserRecclerAdapter)
    fun handleActivityRecyclerViewItemClick(context: Context, adapter: ActivityRecyclerViewAdapter)
    fun handleFocusListviewItemClick(context: Context, adapter: FocusListviewAdapter)
    fun handleMyLikeCommentRecyclerviewItemClick(context: Context, adapter: MyLikeCommentRecyclerAdapter)
    fun handleMyOwnCommentRecyclerViewItemClick(context: Context, adapter: MyOwnCommentRecyclerViewAdapter)
}