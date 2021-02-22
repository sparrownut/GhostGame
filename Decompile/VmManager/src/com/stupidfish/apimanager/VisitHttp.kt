package com.stupidfish.apimanager

import org.jsoup.Jsoup


class VisitHttp {
    fun visitWithPost(url:String,postData:String): String {/* 需要新建线程调用 防止网络卡线程 */
        val connect = Jsoup.connect(url)
        connect.data()
        connect.data(postData)
        return connect.post().toString()
    }
    fun visitWithGet(url: String,vararg payload: String): String {
        return if(payload.isNotEmpty())//如果有payload
                    Jsoup.connect(url+payload).get().toString()
                else//无payload
                    Jsoup.connect(url).get().toString()
    }
}