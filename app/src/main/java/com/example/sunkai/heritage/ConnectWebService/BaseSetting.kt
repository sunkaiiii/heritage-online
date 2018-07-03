package com.example.sunkai.heritage.ConnectWebService

import android.util.Log
import com.example.sunkai.heritage.value.HOST
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException

/*
 * Created by sunkai on 2018/1/30.
 */

abstract class BaseSetting {
    companion object {
        const val SUCCESS = "SUCCESS"
        const val ERROR = "ERROR"
//        const val URL = "http://btbudinner.win:8080"
        const val URL=HOST
//        const val URL="http://10.0.2.2:8080"
        val gsonInstance= Gson()
    }
    //定义泛型方法，简单化Gson的使用
    fun <T> fromJsonToList(s: String, clazz: Class<Array<T>>): List<T> {
        val arr = gsonInstance.fromJson(s, clazz)
        return arr.toList()
    }

    fun PutGet(url:String):String{
        val request=Request.Builder().url(url).build()
        val client=OkHttpClient()
        try {
            val response = client.newCall(request).execute()
            val result=response.body()?.string() ?: ERROR
            Log.e("PutGet",url+"\n"+result)
            return result
        }catch (e:IOException){
            e.printStackTrace()
        }
        return ERROR
    }

    fun PutPost(url:String,form:FormBody):String{
        val request=Request.Builder().url(url).post(form).build()
        val client=OkHttpClient()
        try {
            val response = client.newCall(request).execute()
            val result=response.body()?.string() ?: ERROR
            Log.e("PutGet",url+"\n"+result)
            return result
        }catch (e:IOException){
            e.printStackTrace()
        }
        return ERROR
    }
}
