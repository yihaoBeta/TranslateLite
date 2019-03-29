package com.beta.yihao.translatelite.engine

import android.content.Context
import android.util.Log
import com.baidu.speech.EventListener
import com.baidu.speech.EventManager
import com.baidu.speech.EventManagerFactory
import com.baidu.speech.asr.SpeechConstant
import com.beta.yihao.translite.data.RecognizeResult
import com.beta.yihao.translite.data.RecognizeState
import com.beta.yihao.translite.data.RecognizeText
import com.beta.yihao.translite.data.RecognizeVolume
import com.beta.yihao.translite.utils.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.json.JSONObject


/**
 * 集成文档： http://ai.baidu.com/docs#/ASR-Android-SDK/top 集成指南一节
 * demo目录下doc_integration_DOCUMENT
 * ASR-INTEGRATION-helloworld  ASR集成指南-集成到helloworld中 对应 ActivityMiniRecog
 * ASR-INTEGRATION-TTS-DEMO ASR集成指南-集成到合成DEMO中 对应 ActivityRecog
 */

class RecognizeEngine(private var context: Context) : EventListener {


    private var asr: EventManager? = null

    private val logTime = false

    private lateinit var recognizeCallback: (RecognizeResult) -> Unit

    private var recognizeState = 0L

    private val recognizeResult = RecognizeResult(RecognizeState.ASR_IDLE)

    /**
     *
     * 点击开始按钮
     * 测试参数填在这里
     */
    fun start(callback: (RecognizeResult) -> Unit) {

        if (!StateController.hasState(recognizeState, RECOGNIZE_STATE_INIT)) {
            return
        }
        recognizeCallback = callback
        val params = LinkedHashMap<String, Any>()
        val event: String?
        event = SpeechConstant.ASR_START // 替换成测试的event

        params[SpeechConstant.ACCEPT_AUDIO_VOLUME] = false
        params[SpeechConstant.VAD_ENDPOINT_TIMEOUT] = 0 // 长语音
        params[SpeechConstant.PID] = 15372// 中文输入法模型，有逗号
        params[SpeechConstant.ACCEPT_AUDIO_VOLUME] = true //获取音量回调，用于动画显示
        params[SpeechConstant.APP_KEY] = BAIDU_TTS_KEY
        params[SpeechConstant.APP_ID] = BAIDU_TTS_APPID
        params[SpeechConstant.SECRET] = BAIDU_TTS_SECRET_KEY
        val json: String?
        json = JSONObject(params).toString()
        asr!!.send(event, json, null, 0, 0)
        printLog("输入参数：$json")

        recognizeState = StateController.addState(recognizeState, RECOGNIZE_STATE_START)
    }

    /**
     * 点击停止按钮
     */
    fun stop() {
        printLog("停止识别：ASR_STOP")
        if (StateController.hasState(recognizeState, RECOGNIZE_STATE_START)) {
            asr!!.send(SpeechConstant.ASR_STOP, null, null, 0, 0)
            recognizeState = StateController.delState(recognizeState, RECOGNIZE_STATE_START)
        } else {
            ZLogger.e("engine is not started!")
        }
    }


    fun init() {
        if (!StateController.hasState(recognizeState, RECOGNIZE_STATE_INIT)) {
            asr = EventManagerFactory.create(context, "asr")
            asr!!.registerListener(this) //  EventListener 中 onEvent方法
            recognizeState = StateController.addState(recognizeState, RECOGNIZE_STATE_INIT)
        }
    }

    fun canlel() {
        if (StateController.hasState(recognizeState, RECOGNIZE_STATE_START)) {
            asr!!.send(SpeechConstant.ASR_CANCEL, "{}", null, 0, 0)
        } else {
            ZLogger.e("engine is not started!")
        }
        Log.i("ActivityMiniRecog", "On pause")
    }


    fun release() {
        asr!!.send(SpeechConstant.ASR_CANCEL, "{}", null, 0, 0)

        // 必须与registerListener成对出现，否则可能造成内存泄露
        asr!!.unregisterListener(this)
        recognizeState = 0L
    }

    //   EventListener  回调方法
    override fun onEvent(name: String, params: String?, data: ByteArray?, offset: Int, length: Int) {
        var logTxt = "name: $name"


        if (params != null && !params.isEmpty()) {
            logTxt += ("; params :$params")
        }
        when (name) {

            SpeechConstant.CALLBACK_EVENT_ASR_BEGIN -> {
                recognizeResult.state = RecognizeState.ASR_BEGIN
            }

            SpeechConstant.CALLBACK_EVENT_ASR_READY -> {
                recognizeResult.state = RecognizeState.ASR_READY
            }
            SpeechConstant.CALLBACK_EVENT_ASR_FINISH -> {
                recognizeResult.state = RecognizeState.ASR_FINISH
            }
            SpeechConstant.CALLBACK_EVENT_ASR_PARTIAL -> {
                if (params!!.contains("\"results_recognition\"")) {
                    val textJson = Gson().fromJson<RecognizeText>(params, object : TypeToken<RecognizeText>() {}.type)
                    recognizeResult.state = RecognizeState.ASR_PARTIAL
                    recognizeResult.recognizeText = textJson
                }
            }
            SpeechConstant.CALLBACK_EVENT_ASR_EXIT -> {
                recognizeResult.state = RecognizeState.ASR_EXIT
            }
            SpeechConstant.CALLBACK_EVENT_ASR_VOLUME -> {
                val volumeJson =
                    Gson().fromJson<RecognizeVolume>(params!!, object : TypeToken<RecognizeVolume>() {}.type)
                recognizeResult.state = RecognizeState.ASR_VOLUME
                recognizeResult.recognizeVolume = volumeJson
            }
            else -> {
                if (data != null) {
                    logTxt += (" ;data length=$data.size")
                }
            }
        }
        if (StateController.hasState(recognizeState, RECOGNIZE_STATE_START)) {
            recognizeCallback(recognizeResult)
        }
        printLog(logTxt)
    }

    private fun printLog(text: String) {
        var mText = text
        if (logTime) {
            mText += "  ;time=" + System.currentTimeMillis()
        }
        mText += "\n"
        ZLogger.d(text)
    }

    fun hasState(state: Long): Boolean {
        return StateController.hasState(recognizeState, state)
    }

}
