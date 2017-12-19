package com.example.sunkai.heritage.Data

import android.app.ActivityManager
import android.app.Application
import android.content.Context
import android.util.Log
import com.xiaomi.channel.commonutils.logger.LoggerInterface
import com.xiaomi.mipush.sdk.Logger
import com.xiaomi.mipush.sdk.MiPushClient



/**
 * Created by sunkai on 2017/12/13.
 */
class GlobalContext : Application() {
    private val APP_ID:String="2882303761517683469"
    private val APP_KEY:String="5391768355469"
    private val TAG:String="GlobalContext"


    override fun onCreate() {
        super.onCreate()
        instance = this

        registMipush() //注册mipush
    }

    private fun registMipush() {
        if (shouldInit()) {
            MiPushClient.registerPush(instance, APP_ID, APP_KEY)
        }
        //打开Log
        val newLogger = object : LoggerInterface {

            override fun setTag(tag: String) {
                // ignore
            }

            override fun log(content: String, t: Throwable) {
                Log.d(TAG, content, t)
            }

            override fun log(content: String) {
                Log.d(TAG, content)
            }
        }
        Logger.setLogger(this, newLogger)
    }

    private fun shouldInit(): Boolean {
        val am = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val processInfos = am.runningAppProcesses
        val mainProcessName = packageName
        val myPid = android.os.Process.myPid()
        for (info in processInfos) {
            if (info.pid == myPid && mainProcessName == info.processName) {
                return true
            }
        }
        return false
    }

    companion object {
        var instance: GlobalContext? = null
            private set
    }
}