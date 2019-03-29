package com.beta.yihao.translite.engine.tts

import android.content.Context
import android.os.Build
import android.os.Handler
import android.os.HandlerThread
import android.os.Message

import com.beta.yihao.translatelite.engine.recognize.InitConfig
import com.beta.yihao.translite.utils.ZLogger

/**
 * 在新线程中调用initTTs方法。防止UI柱塞
 *
 *
 * Created by fujiayi on 2017/5/24.
 */

class NonBlockSynthesizer(context: Context, initConfig: InitConfig) : TTSSynthesizer(context, null) {
    private var hThread: HandlerThread? = null
    private var tHandler: Handler? = null


    init {
        initThread()
        runInHandlerThread(INIT, initConfig)
    }

    companion object {

        private const val INIT = 1

        private const val RELEASE = 11

        private const val TAG = "NonBlockSynthesizer"
    }

    private fun initThread() {
        hThread = HandlerThread("NonBlockSynthesizer-thread")
        hThread!!.start()
        tHandler = object : Handler(hThread!!.looper) {
            override fun handleMessage(msg: Message) {
                super.handleMessage(msg)
                when (msg.what) {
                    INIT -> {
                        val config = msg.obj as InitConfig
                        val isSuccess = init(config)
                        if (isSuccess) {
                            // speak("初始化成功");
                            sendToUiThread("NonBlockSynthesizer 初始化成功")
                        } else {
                            sendToUiThread("合成引擎初始化失败, 请查看日志")
                        }
                    }
                    RELEASE -> {
                        super@NonBlockSynthesizer.release()
                        sendToUiThread("NonBlockSynthesizer Released")
                        if (Build.VERSION.SDK_INT < 18) {
                            looper.quit()
                        }
                    }
                    else -> {
                        ZLogger.d("${msg.what}:${msg.obj}")
                    }
                }

            }
        }
    }

    override fun release() {
        runInHandlerThread(RELEASE)
        if (Build.VERSION.SDK_INT >= 18) {
            hThread!!.quitSafely()
        }
    }

    private fun runInHandlerThread(action: Int, obj: Any? = null) {
        val msg = Message.obtain()
        msg.what = action
        msg.obj = obj
        tHandler!!.sendMessage(msg)
    }


}
