package com.example.sunkai.heritage.Service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.support.annotation.RequiresApi
import android.support.v4.app.NotificationCompat
import android.text.TextUtils
import android.util.Log
import com.example.sunkai.heritage.Activity.LoginActivity.LoginActivity
import com.example.sunkai.heritage.Activity.UserCommentDetailActivity
import com.example.sunkai.heritage.Activity.UserOwnTieziActivity
import com.example.sunkai.heritage.ConnectWebService.HandlePush
import com.example.sunkai.heritage.Data.PushMessageData
import com.example.sunkai.heritage.R
import com.example.sunkai.heritage.tools.ThreadPool
import com.example.sunkai.heritage.tools.runOnUiThread
import com.example.sunkai.heritage.value.HOST_IP
import com.example.sunkai.heritage.value.PUSH_PORT
import com.google.gson.Gson
import java.io.*
import java.lang.ref.WeakReference
import java.net.ConnectException
import java.net.Socket


class PushService : Service() {
    private lateinit var mNM: NotificationManager
    private val NOTIFICATION = R.string.app_name
    private val mBinder = LocalBinder()
    private var pushChannelID = -1
    private var socketRef: WeakReference<Socket>? = null
    private var bufferInputStream:BufferedInputStream?=null

    inner class LocalBinder : Binder() {
        internal val service: PushService
            get() = this@PushService
    }

    override fun onCreate() {
        mNM = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createAndroidONotificationChannel(mNM)
        }
        //第一次启动服务的时候，获取所有未读取的推送
        getPushMessage()
        ThreadPool.execute {
            val socket = try {
                Socket(HOST_IP, PUSH_PORT)
            } catch (e: ConnectException) {
                e.printStackTrace()
                return@execute
            }
            runOnUiThread(Runnable {
                socketRef = WeakReference(socket)
                handleSocketPushChannel(socket)
            })

        }

    }

    private fun getPushMessage() {
        val userID = LoginActivity.userID
        if (userID == 0) return
        ThreadPool.execute {
            val resultList = HandlePush.GetPush(userID)
            if (resultList.isNotEmpty()) {
                val notifiCationStringList=ArrayList<String>()
                resultList.forEach {
                    notifiCationStringList.add(String.format("%s:%s\n",it.userName,it.replyContent))
                }
                runOnUiThread(Runnable {
                    val pendingIntent=PendingIntent.getActivity(this,0,Intent(this,UserOwnTieziActivity::class.java),0)
                    Log.d("notification",notifiCationStringList.toString())
                    showNotification(notifiCationStringList.toTypedArray(),pendingIntent)
                })
            }
        }
    }

    private fun handleSocketPushChannel(socket: Socket?) {
        socket?:return
        ThreadPool.execute {
            if (LoginActivity.userID == 0) {
                socket.close()
                return@execute
            }
            if (socket.isConnected) {
                val bufferedReader = socket.getInputStream().bufferedReader()
                val getSocketID = bufferedReader.readLine()
                pushChannelID = getSocketID.toInt()
                val writer = socket.getOutputStream().bufferedWriter()
                writer.write(LoginActivity.userID.toString())
                writer.flush()
            }
            socket.keepAlive = true
            var emtyTime = 0
            bufferInputStream=BufferedInputStream(socket.getInputStream())
            val buf=ByteArray(1024)
            while (socket.isConnected && emtyTime < 100) {
                try {
                    val count=bufferInputStream?.read(buf)
                    if(count?:continue<0){
                        continue
                    }
                    val content=String(buf,0,count)
                    Log.d("content",content)
                    if (!TextUtils.isEmpty(content)) {
                        val pushMessageData = try {
                            Gson().fromJson<PushMessageData>(content, PushMessageData::class.java)
                        } catch (e: Exception) {
                            e.printStackTrace()
                            continue
                        }
                        runOnUiThread(Runnable {
                            val intent = Intent(this@PushService, UserCommentDetailActivity::class.java)
                            intent.putExtra("id", pushMessageData.replyCommentID)
                            //这个flag非常重要，不添加的话将不会传递intent的信息
                            val pendingIntent = PendingIntent.getActivity(this@PushService, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
                            showNotification(arrayOf(String.format("%s:%s",pushMessageData.userName,pushMessageData.replyContent)), pendingIntent)
                        })
                    } else {
                        emtyTime++
                    }
                }catch (e:IOException){
                    e.printStackTrace()
                    emtyTime++
                }
            }
        }
    }


    private fun showNotification(content: Array<String>,contentIntent:PendingIntent) {
        val text = getString(R.string.new_reply)
        val inboxStyle=NotificationCompat.InboxStyle()
        inboxStyle.setBigContentTitle(text)
        content.forEach { inboxStyle.addLine(it) }
        val notificaiton = NotificationCompat.Builder(this, getString(R.string.push_channel))
                .setSmallIcon(R.mipmap.app_logo_image)
                .setTicker(text)
                .setWhen(System.currentTimeMillis())
                .setContentIntent(contentIntent)
                .setAutoCancel(true)
                .setStyle(inboxStyle)
                .build()

        mNM.notify(NOTIFICATION, notificaiton)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_NOT_STICKY
    }


    override fun onBind(intent: Intent): IBinder? {
        return mBinder
    }


    override fun onUnbind(intent: Intent?): Boolean {
        socketRef?.get()?.shutdownOutput()
        socketRef?.get()?.shutdownInput()
        if(socketRef?.get()?.isConnected == true) {
            socketRef?.get()?.close()
        }
        bufferInputStream?.close()
        bufferInputStream=null
        mNM.cancel(NOTIFICATION)
        return super.onUnbind(intent)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createAndroidONotificationChannel(mNM: NotificationManager) {
        val channel = NotificationChannel(getString(R.string.push_channel), getString(R.string.push_channel_name), NotificationManager.IMPORTANCE_DEFAULT)
        channel.enableLights(true)
        channel.lightColor = getColor(R.color.colorPrimary)
        channel.setShowBadge(true)
        mNM.createNotificationChannel(channel)
    }

}