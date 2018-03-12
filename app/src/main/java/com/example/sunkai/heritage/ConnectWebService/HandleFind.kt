package com.example.sunkai.heritage.ConnectWebService

import android.text.TextUtils
import android.util.Log
import com.example.sunkai.heritage.Data.CommentReplyInformation
import com.example.sunkai.heritage.Data.UserCommentData
import com.example.sunkai.heritage.value.COMMENT_REPLY
import com.example.sunkai.heritage.value.MINI_REPLY
import com.google.gson.Gson
import okhttp3.FormBody
import org.kobjects.base64.Base64

/*
 * Created by sunkai on 2018/2/1.
 */
object HandleFind:BaseSetting() {

    fun Add_User_Comment_Information(user_id: Int, comment_title: String, comment_content: String, comment_image: String): Boolean {
        val postUrl= "$URL/AddUserCommentInformation"
        val form=FormBody.Builder()
                .add("userID",user_id.toString())
                .add("commentTitle",comment_title)
                .add("commentContent",comment_content)
                .add("commentImage",comment_image)
                .build()
        val result=PutPost(postUrl,form)
        return SUCCESS==result
    }

    fun GetUserCommentInformation(userID: Int,start:Int=0):List<UserCommentData>{
        val getUrl="$URL/GetUserCommentInformation?userID=$userID&start=$start"
        val result=PutGet(getUrl)
        Log.d("GetUserCommentInfo",result)
        if(result== ERROR){
            return arrayListOf()
        }else{
            try{
                val datas=Gson().fromJsonToList(result,Array<UserCommentData>::class.java)
                for(data in datas){
                    data.miniReplys= GetUserCommentReply(data.id, MINI_REPLY)
                }
                return datas
            }catch (e:Exception){
                e.printStackTrace()
            }
        }
        return arrayListOf()
    }

    fun GetUserCommentInformationByUser(userID: Int,start: Int=0):List<UserCommentData>{
        val getUrl="$URL/GetUserCommentInformationByUser?userID=$userID&start=$start"
        val result=PutGet(getUrl)
        Log.d("GetUserCommentInfoByUsr",result)
        if(result== ERROR){
            return arrayListOf()
        }else{
            try{
                val datas= Gson().fromJsonToList(result,Array<UserCommentData>::class.java)
                for(data in datas){
                    data.miniReplys= GetUserCommentReply(data.id, MINI_REPLY)
                }
                return datas
            }catch (e:Exception){
                e.printStackTrace()
            }
        }
        return arrayListOf()
    }

    fun GetUserCommentInformaitonByOwn(userID:Int,start: Int=0):List<UserCommentData>{
        val getUrl="$URL/GetUserCommentInformaitonByOwn?userID=$userID&start=$start"
        val result=PutGet(getUrl)
        return if(result== ERROR){
            arrayListOf()
        }else{
            try{
                Gson().fromJsonToList(result,Array<UserCommentData>::class.java)
            }catch (e:Exception){
                e.printStackTrace()
                arrayListOf<UserCommentData>()
            }

        }
    }

    fun GetUserCommentIdByUser(userID: Int):IntArray{
        val getUrl="$URL/GetUserCommentIdByUser?userID=$userID"
        val result=PutGet(getUrl)
        return if(ERROR==result){
            IntArray(0)
        }else{
            Gson().fromJson(result,IntArray::class.java)
        }
    }

    fun DeleteUserCommentByID(commentID:Int):Boolean{
        val getUrl="$URL/DeleteUserCommentByID?id=$commentID"
        val result=PutGet(getUrl)
        return result==SUCCESS
    }

    fun UpdateUserCommentImage(commentID:Int,image:ByteArray):Boolean{
        val imgString=Base64.encode(image)
        val formBody=FormBody.Builder().add("id",commentID.toString()).add("image",imgString).build()
        val postURL="$URL/UpdateUserCommentImage"
        val result=PutPost(postURL,formBody)
        return result== SUCCESS
    }

    fun UpdateUserCommentInformaiton(id:Int,title:String,content:String,image: String):String{
        val formBody=FormBody.Builder()
                .add("id",id.toString())
                .add("title",title)
                .add("content",content)
                .add("image",image).build()
        val postUrl="$URL/UpdateUserCommentInformaiton"
        return PutPost(postUrl,formBody)
    }


    fun GetUserCommentImageUrl(commentID: Int):String{
        val getUrl="$URL/GetUserCommentImageUrl?id=$commentID"
        val url= PutGet(getUrl)
        Log.d("GetUserCommentImageUrl",url)
        if(TextUtils.isEmpty(url)||url== ERROR){
            return ERROR
        }
        return URL+url
    }

    fun GetAllUserCommentInfoByID(userID: Int): UserCommentData?{
        val getUrl="$URL/GetAllUserCommentInfoByID?user=$userID"
        val result=PutGet(getUrl)
        return if(result== ERROR){
            null
        }else {
            try {
                Gson().fromJson(result, UserCommentData::class.java)
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }

    fun GetCommentLikeNumber(commentID: Int):String{
        val getUrl="$URL/GetCommentLikeNumber?commentID=$commentID"
        val result=PutGet(getUrl)
        return if(result== ERROR){
            "0"
        }else{
            result
        }
    }

    fun GetUserCommentCount(commentID: Int,miniReply: Int= COMMENT_REPLY):String{
        val getUrl="$URL/GetUserCommentCount?commentID=$commentID&miniReply=$miniReply"
        val result=PutGet(getUrl)
        return if(result== ERROR){
            "0"
        }else{
            result
        }
    }

    fun GetUserCommentReply(commentID: Int,miniReply:Int= COMMENT_REPLY):List<CommentReplyInformation>{
        val getUrl="$URL/GetUserCommentReply?commentID=$commentID&miniReply=$miniReply"
        val result=PutGet(getUrl)
        return if(result== ERROR){
            arrayListOf()
        }else{
            try {
                Gson().fromJsonToList(result,Array<CommentReplyInformation>::class.java)
            }catch (e:Exception){
                e.printStackTrace()
                arrayListOf<CommentReplyInformation>()
            }

        }
    }

    fun SetUserLike(userID: Int,commentID: Int):Boolean{
        val getUrl="$URL/SetUserLike?userID=$userID&commentID=$commentID"
        return PutGet(getUrl)== SUCCESS
    }

    fun CancelUserLike(userID: Int,commentID: Int):Boolean{
        val getUrl="$URL/CancelUserLike?userID=$userID&commentID=$commentID"
        return PutGet(getUrl)== SUCCESS
    }

    fun AddUserCommentReply(userID: Int,commentID: Int,reply:String,intentString:String):String{
        val postUrl="$URL/AddUserCommentReply"
        val formBody=FormBody.Builder()
                .add("userID",userID.toString())
                .add("commentID",commentID.toString())
                .add("reply",reply)
                .add("intentString",intentString)
                .build()
        return PutPost(postUrl,formBody)
    }

    fun UpdateUserCommentReply(replyID:Int,reply:String):Boolean{
        val postUrl="$URL/UpdateUserCommentReply"
        val formBody=FormBody.Builder()
                .add("replyID",replyID.toString())
                .add("reply",reply).build()
        return PutPost(postUrl,formBody)== SUCCESS
    }

    fun DeleteUserCommentReply(replyID: Int):Boolean{
        val getUrl="$URL/DeleteUserCommentReply?replyID=$replyID"
        return PutGet(getUrl)== SUCCESS
    }

    fun GetUserLikeComment(userID: Int):List<UserCommentData>{
        val getUrl="$URL/GetUserLikeComment?userID=$userID"
        val result= PutGet(getUrl)
        if(result== ERROR){
            return arrayListOf()
        }
        try{
            return Gson().fromJsonToList(result,Array<UserCommentData>::class.java)
        }catch (e:Exception){
            e.printStackTrace()
        }
        return arrayListOf()
    }
}
