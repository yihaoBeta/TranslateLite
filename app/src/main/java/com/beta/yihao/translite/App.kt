package com.beta.yihao.translite

import android.app.Application
import android.content.ContextWrapper

/**
 * @Author yihao
 * @Description Application
 * @Date 2019/1/15-15:04
 * @Email yihaobeta@163.com
 */
class App : Application() {

    override fun onCreate() {
        super.onCreate()
        INSTANCE = this
    }
}

lateinit var INSTANCE: App

object AppContext : ContextWrapper(INSTANCE)