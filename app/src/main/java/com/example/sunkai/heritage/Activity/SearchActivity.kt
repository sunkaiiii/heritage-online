package com.example.sunkai.heritage.Activity

import android.content.Context
import android.content.res.ColorStateList
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.text.Editable
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.core.content.edit
import androidx.recyclerview.widget.RecyclerView
import com.example.sunkai.heritage.Activity.BaseActivity.BaseGlideActivity
import com.example.sunkai.heritage.Adapter.BaseAdapter.BaseRecyclerAdapter
import com.example.sunkai.heritage.Adapter.SearchActivitySearchHistoryAdapter
import com.example.sunkai.heritage.Adapter.SearchActivityViewpagerAdapter
import com.example.sunkai.heritage.Adapter.SearchUserRecclerAdapter
import com.example.sunkai.heritage.Data.SearchHistoryData
import com.example.sunkai.heritage.Interface.OnFocusChangeListener
import com.example.sunkai.heritage.Interface.OnItemClickListener
import com.example.sunkai.heritage.R
import com.example.sunkai.heritage.tools.*
import com.example.sunkai.heritage.tools.CreateSeachActivityAdapterUtils.CreateCorrespondingAdapterFactory
import com.example.sunkai.heritage.value.*
import kotlinx.android.synthetic.main.activity_search_news.*
import kotlin.collections.set

class SearchActivity : BaseGlideActivity(), TextView.OnEditorActionListener {


    private var searchType = TYPE_USER
    private var searchString = ""
    private val searchTypes = arrayOf(TYPE_BOTTOM_NEWS, TYPE_NEWS, TYPE_FOLK_HERITAGE, TYPE_COMMENT, TYPE_USER)
    val divide: Array<String>

    init {
        divide = arrayOf(GlobalContext.instance.getString(R.string.focus_heritage),
                GlobalContext.instance.getString(R.string.all_news),
                GlobalContext.instance.getString(R.string.folk_page),
                GlobalContext.instance.getString(R.string.user_comment),
                GlobalContext.instance.getString(R.string.user))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_news)
        if (!intent.getStringExtra(SEARCH_TYPE).isNullOrEmpty()) {
            searchType = intent.getStringExtra(SEARCH_TYPE)
        }
        initView()

    }

    private fun initView() {
        setEditTextHint()
        searchActivityTablayout.setSelectedTabIndicatorColor(getThemeColor())
        searchActivityTablayout.tabRippleColor = ColorStateList.valueOf(getLightThemeColor())
        val searchHistoryList = getSearchHistoryList()
        val adapter = SearchActivitySearchHistoryAdapter(this, searchHistoryList, glide)
        setHistoryAdapterClick(adapter)
        searchActivitySearchHistoryRecyckerView.adapter = adapter
        searchActivitySearchEditText.addTextChangedListener(object : BaseOnTextChangeListner() {
            override fun afterTextChanged(p0: Editable?) {
                p0 ?: return
                if (p0.isNullOrEmpty() && searchActivityRecyclerView.visibility == View.VISIBLE) {
                    searchActivitySearchHistoryRecyckerView.visibility = View.VISIBLE
                    searchActivityRecyclerView.visibility = View.GONE
                } else {
                    searchActivitySearchHistoryRecyckerView.visibility = View.GONE
                    searchActivityRecyclerView.visibility = View.VISIBLE
                }
                searchString = p0.toString()
                handleSearchText(p0)
            }
        })
        searchActivitySearchEditText.setOnEditorActionListener(this)
        searchActivityViewPager.adapter = SearchActivityViewpagerAdapter(this, divide, searchTypes)
        searchActivityViewPager.addOnPageChangeListener(viewpagerChangeListner)
        searchActivityTablayout.setupWithViewPager(searchActivityViewPager)
        divide.withIndex().forEach { searchActivityTablayout.getTabAt(it.index)?.text = it.value }
        activitySearchBackButton.setOnClickListener {
            onBackPressed()
        }
        activitySearchClearButton.setOnClickListener {
            searchString = ""
            searchActivitySearchEditText.setText("")
            changeSearchViewState(false)
        }
    }

    private fun setEditTextHint() {
        searchTypes.withIndex().forEach {
            if (searchType == it.value) {
                searchActivitySearchEditText.hint = divide[it.index] + ":" + getString(R.string.please_input_search)
            }
        }
    }

    private fun setHistoryAdapterClick(adapter: SearchActivitySearchHistoryAdapter) {
        adapter.setOnItemClickListen(object : OnItemClickListener {
            override fun onItemClick(view: View, position: Int) {
                val data = adapter.getItem(position)
                searchType = data.searchType
                searchString = data.searchString
                searchActivitySearchEditText.setText(searchString)
                handleKeyEvent()
                setEditTextHint()
            }
        })
    }

    private fun handleSearchText(editable: Editable) {
        if (searchHandler.hasMessages(SEARCH_FLAG)) {
            searchHandler.removeMessages(SEARCH_FLAG)
        }
        val msg = Message()
        val bundle = Bundle()
        bundle.putString(SEARCH_TEXT, editable.toString())
        msg.what = SEARCH_FLAG
        msg.data = bundle
        searchHandler.sendMessageDelayed(msg, DELAY)
    }

    private fun startSearchInfo(searchInfo: String) {
        requestHttp {
            val adapter = getCorrespodingAdapter(searchInfo)
            if (adapter is SearchUserRecclerAdapter) {
                setListener(adapter)
            }
            runOnUiThread {
                searchActivityRecyclerView.adapter = adapter
            }
        }
    }


    private fun setListener(adapter: SearchUserRecclerAdapter) {
        adapter.setOnFocusChangeListener(object : OnFocusChangeListener {
            override fun onFocusChange() {
                setResult(STATE_CHANGE)
            }
        })
    }

    private val searchHandler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message?) {
            if (msg?.what == SEARCH_FLAG) {
                val searchInfo = msg.data.getString(SEARCH_TEXT)
                if (searchInfo != null && !searchInfo.isNullOrEmpty()) {
                    startSearchInfo(searchInfo)
                }
            }
        }
    }

    private val viewpagerChangeListner = object : BaseOnPageChangeListener() {
        override fun onPageSelected(position: Int) {
            val adapter = searchActivityViewPager.adapter ?: return
            if (adapter is SearchActivityViewpagerAdapter) {
                val view = adapter.views[position]
                if (!adapter.isDivideSetData[position] && view is RecyclerView) {
                    adapter.isDivideSetData[position] = true
                    requestHttp {
                        val recyclerViewAdapter = getCorrespodingAdapter(searchString, adapter.searchType[position])
                        runOnUiThread {
                            view.adapter = recyclerViewAdapter
                        }
                    }
                }

            }
        }
    }

    private fun changeSearchViewState(showSecondlyView: Boolean) {
        TransitionManager.beginDelayedTransition(activitySearchNewsLinearLayout, AutoTransition())
        searchActivityRecyclerView.visibility = if (showSecondlyView) View.GONE else View.VISIBLE
        searchActivityViewPager.visibility = if (showSecondlyView) View.VISIBLE else {
            searchActivityViewPager.adapter = null
            View.GONE
        }
        searchActivityTablayout.visibility = searchActivityViewPager.visibility
        searchActivitySearchHistoryRecyckerView.visibility = if (showSecondlyView) {
            View.GONE
        } else {
            if (searchActivitySearchEditText.text.toString().isEmpty()) {
                searchActivityRecyclerView.visibility = View.GONE
                View.VISIBLE
            } else {
                View.GONE
            }
        }
    }

    private fun getCorrespodingAdapter(searchInfo: String, searchType: String = this.searchType): BaseRecyclerAdapter<*, *>? {
        return CreateCorrespondingAdapterFactory.create(this, glide, searchType, searchInfo)
    }

    private fun writeSearchSharePrefrence(searchString: String, searchType: String) {
        val sharePref = getSharedPreferences(SEARCH_SHAREPREF_NAME, Context.MODE_PRIVATE)
        val searchList = sharePref.getStringSet(searchType, mutableSetOf()) ?: mutableSetOf()
        searchList.add(searchString)
        sharePref.edit {
            putStringSet(searchType, searchList)
        }
    }

    private fun getSearchSharePrefrenceSearchMap(): Map<String, Set<String>> {
        val searchMap = mutableMapOf<String, Set<String>>()
        val sharePref = getSharedPreferences(SEARCH_SHAREPREF_NAME, Context.MODE_PRIVATE)
        val searchList = sharePref.all
        searchList.keys.forEach {
            val set = sharePref.getStringSet(it, setOf()) ?: setOf()
            searchMap[it] = set
        }
        return searchMap
    }

    private fun getSearchHistoryList(): List<SearchHistoryData> {
        val searchMap = getSearchSharePrefrenceSearchMap()
        val searchHistoryList = mutableListOf<SearchHistoryData>()
        searchMap.keys.forEach {
            val searchSet = searchMap[it]
            if (searchSet != null) {
                for (seachInfo in searchSet) {
                    searchHistoryList.add(SearchHistoryData(seachInfo, it))
                }
            }
        }
        return searchHistoryList
    }


    private fun handleKeyEvent() {
        SoftInputTools.hideKeyboard(this)
        changeSearchViewState(true)
        writeSearchSharePrefrence(searchString, searchType)
        searchTypes.withIndex().forEach {
            if (searchTypes[it.index] == searchType) {
                val index = it.index
                searchActivityViewPager.setCurrentItem(index, true)
                val adapter = searchActivityViewPager.adapter ?: return
                if (adapter is SearchActivityViewpagerAdapter) {
                    requestHttp {
                        val recyclerView = adapter.views[index]
                        if (recyclerView is RecyclerView) {
                            val getAdapter = getCorrespodingAdapter(searchString, searchType)
                            runOnUiThread {
                                recyclerView.adapter = getAdapter
                            }
                        }
                    }
                }
            }
        }
    }

    //监控在输入框按下搜索键
    override fun onEditorAction(p0: TextView?, p1: Int, p2: KeyEvent?): Boolean {
        if (p1 == KeyEvent.ACTION_DOWN || p1 == EditorInfo.IME_ACTION_DONE) {
            handleKeyEvent()
        }
        return true
    }

    override fun onBackPressed() {
        if (searchActivityViewPager.visibility == View.VISIBLE) {
            changeSearchViewState(false)
            return
        }
        super.onBackPressed()
    }

    companion object {
        const val SEARCH_FLAG = 1
        const val SEARCH_TEXT = "searchText"
        const val DELAY = 300L
    }
}
