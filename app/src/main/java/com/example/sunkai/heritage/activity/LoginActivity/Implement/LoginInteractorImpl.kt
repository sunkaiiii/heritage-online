package com.example.sunkai.heritage.activity.loginActivity.Implement

import android.content.Context
import android.os.Handler
import android.os.Looper
import androidx.core.content.edit
import com.example.sunkai.heritage.activity.loginActivity.Interface.Logininteractor
import com.example.sunkai.heritage.activity.loginActivity.LoginActivity
import com.example.sunkai.heritage.connectWebService.HandleUser
import com.example.sunkai.heritage.tools.GlobalContext
import com.example.sunkai.heritage.tools.infoToRSA
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

/**
 * 处理登录页数据的interactor
 * Created by sunkai on 2018/3/8.
 */
class LoginInteractorImpl:Logininteractor {
    override fun login(username: String, password: String, listner: Logininteractor.OnLoginFinishedListner) {
        GlobalScope.launch {
            val encryptedPassword= infoToRSA(password)
            if(encryptedPassword==null){
                listner.onEncryError()
                return@launch
            }
            val result=HandleUser.Sign_In(username,encryptedPassword)
            Handler(Looper.getMainLooper()).post {
                if(result){
                    LoginActivity.userName=username
                    //登陆成功，写入sharePrefrece自动登录信息
                    writeSharePrefrece(username,password)
                    listner.onSuccess()
                }else{
                    listner.onPasswordError()
                }
            }
        }
    }

    private fun writeSharePrefrece(userName: String, userPassword:String){
        val userNames=loadUserNamesFromSharePrefrece().toMutableSet()
        userNames.add(userName)
        GlobalContext.instance.getSharedPreferences("data", Context.MODE_PRIVATE).edit {
            putInt("user_id", LoginActivity.userID)
            putString("user_name", userName)
            putString("user_password", userPassword)
        }
        GlobalContext.instance.getSharedPreferences("userNames", Context.MODE_PRIVATE).edit {
            putStringSet("userNames",userNames)
        }
    }

    override fun loadUserNamesFromSharePrefrece(): Set<String> {
        return GlobalContext.instance.getSharedPreferences("userNames", Context.MODE_PRIVATE).getStringSet("userNames", mutableSetOf())?: mutableSetOf()
    }
}