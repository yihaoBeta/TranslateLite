package com.beta.yihao.translite.repository

import com.beta.yihao.translatelite.engine.RecognizeEngine
import com.beta.yihao.translite.AppContext
import com.beta.yihao.translite.data.RecognizeResult
import com.beta.yihao.translite.utils.RECOGNIZE_STATE_INIT

/**
 * @Author yihao
 * @Description 语音识别Repository
 * @Date 2019/1/18-14:18
 * @Email yihaobeta@163.com
 */
class RecognizeRepository private constructor() {
    private val recognizeEngine: RecognizeEngine by lazy {
        RecognizeEngine(AppContext)
    }

    companion object {
        @Volatile
        private var instance: RecognizeRepository? = null

        fun getInstance(): RecognizeRepository {
            return instance ?: synchronized(this) {
                instance ?: RecognizeRepository().also {
                    instance = it
                }
            }
        }
    }

    /**
     * 初始化语音识别引擎
     */
    fun init() {
        recognizeEngine.init()
    }

    /**
     * 开始语音识别
     */
    fun start(callback: (RecognizeResult) -> Unit) {
        if (recognizeEngine.hasState(RECOGNIZE_STATE_INIT).not()) {
            init()
        }
        recognizeEngine.start(callback)
    }

    fun stop() {
        recognizeEngine.stop()
    }

    fun cancel() {
        recognizeEngine.canlel()
    }

    fun release() {
        recognizeEngine.release()
    }
}