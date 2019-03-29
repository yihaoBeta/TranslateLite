package com.beta.yihao.translite.utils

import android.util.Log
import com.beta.yihao.translite.BuildConfig

/**
 * @Description Logger 辅助类
 * @Author yihaobeta
 * @Date 2019/1/3-15:55
 * @Email yihaobeta@163.com
 */
object ZLogger {

    private const val TAG = "myLog"

    enum class EXT_INFO {
        NONE,
        METHOD,
        CLAZZ,
        FILE,
        JUMP,
        ALL,
        SIMPLE
    }

    fun log(message: Any, ext: EXT_INFO, tag: String = TAG, level: Int = Log.DEBUG) {
        if (!BuildConfig.DEBUG) return
        var realMessage = message.toString()
        val stackItem = Thread.currentThread().stackTrace.filter {
            !it.isNativeMethod
        }.filter {
            it.className != this.javaClass.name
        }.first {
            it.className != Thread::class.java.name
        }
        if (stackItem != null) {
            when (ext) {
                EXT_INFO.NONE -> {
                    realMessage = message.toString()
                }
                EXT_INFO.METHOD -> {
                    realMessage = "[${stackItem.methodName}] : $message"
                }
                EXT_INFO.CLAZZ -> {
                    realMessage = "[${stackItem.className.substringAfterLast(".")}] : $message"
                }
                EXT_INFO.JUMP -> {
                    realMessage = "(${stackItem.fileName}:${stackItem.lineNumber}) : $message"
                }
                EXT_INFO.FILE -> {
                    realMessage = "[${stackItem.fileName}] : $message"
                }
                EXT_INFO.ALL -> {
                    realMessage =
                        "[${stackItem.className.substringAfterLast(".")}:${stackItem.methodName}](${stackItem.fileName}:${stackItem.lineNumber}) : $message"
                }
                EXT_INFO.SIMPLE -> {
                    realMessage =
                        "[(${stackItem.fileName}:${stackItem.lineNumber})@${stackItem.methodName}] : $message"
                }
            }
        }

        realMessage.let {
            when (level) {
                Log.DEBUG -> Log.d(tag, it)
                Log.INFO -> Log.i(tag, it)
                Log.ERROR -> Log.e(tag, it)
                Log.WARN -> Log.w(tag, it)
                Log.VERBOSE -> Log.v(tag, it)
                else -> Log.d(tag, it)
            }
        }
    }

    fun d(message: Any, ext: EXT_INFO = EXT_INFO.NONE, tag: String = TAG) {
        log(message, ext, tag, Log.DEBUG)
    }

    fun e(message: Any, ext: EXT_INFO = EXT_INFO.NONE, tag: String = TAG) {
        log(message, ext, tag, Log.ERROR)
    }

    fun logAll(message: Any, tag: String = TAG) {
        log(message, EXT_INFO.ALL, tag)
    }

    fun e(e: Exception, tag: String = TAG) {
        val sb = StringBuffer()
        val name = Thread.currentThread().stackTrace.filter {
            it.isNativeMethod.not()
        }.filter {
            it.className != Thread::class.java.name
        }.first {
            it.className != this.javaClass.name
        }.className
        val sts = e.stackTrace

        if (name != null) {
            sb.append("$name - $e\r\n")
        } else {
            sb.append("$e\r\n")
        }
        if (sts != null && sts.isNotEmpty()) {
            for (st in sts) {
                if (st != null) {
                    sb.append("[ ${st.fileName}:${st.lineNumber}]\r\n")
                }
            }
        }

        log(sb.toString(), EXT_INFO.NONE, tag)
    }

}