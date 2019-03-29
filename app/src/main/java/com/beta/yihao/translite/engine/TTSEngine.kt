/**
 * Copyright (C) 2015 Baidu, Inc. All Rights Reserved.
 */
package com.beta.yihao.translite.engine

import android.content.Context
import android.text.TextUtils
import android.util.Pair
import com.baidu.tts.chainofresponsibility.logger.LoggerProxy
import com.baidu.tts.client.SpeechSynthesizer
import com.baidu.tts.client.TtsMode
import com.beta.yihao.translatelite.engine.recognize.InitConfig
import com.beta.yihao.translite.data.TTSResult
import com.beta.yihao.translite.data.TTSState
import com.beta.yihao.translite.engine.tts.*
import com.beta.yihao.translite.utils.BAIDU_TTS_APPID
import com.beta.yihao.translite.utils.BAIDU_TTS_KEY
import com.beta.yihao.translite.utils.BAIDU_TTS_SECRET_KEY
import com.beta.yihao.translite.utils.ZLogger

import java.io.IOException
import java.util.*

/**
 * 语音合成。含在线和离线，没有纯离线功能。
 * 根据网络状况优先走在线，在线时访问服务器失败后转为离线。
 */
class TTSEngine(private var mContext: Context) {

    // ================== 初始化参数设置开始 ==========================

    private var appId = BAIDU_TTS_APPID

    private var appKey = BAIDU_TTS_KEY

    private var secretKey = BAIDU_TTS_SECRET_KEY

    // TtsMode.MIX; 离在线融合，在线优先； TtsMode.ONLINE 纯在线； 没有纯离线
    private var ttsMode = TtsMode.ONLINE

    private var voiceMode: VoiceMode = VoiceMode.VOICE_FEMALE

    // 离线发音选择，VOICE_FEMALE即为离线女声发音。
    // assets目录下bd_etts_common_speech_m15_mand_eng_high_am-mix_v3.0.0_20170505.dat为离线男声模型；
    // assets目录下bd_etts_common_speech_f7_mand_eng_high_am-mix_v3.0.0_20170512.dat为离线女声模型
    private var offlineVoice = OfflineVoiceMode.VOICE_FEMALE

    private var bInitialStatus: Boolean = false

    // ===============初始化参数设置完毕，更多合成参数请至getParams()方法中设置 =================

    // 主控制类，所有合成控制方法从这个类开始
    private lateinit var synthesizer: TTSSynthesizer

    private var mTTSCallback: ((TTSResult) -> Unit)? = null

    private var mTTSResult = TTSResult()

    /**
     * 合成的参数，可以初始化时填写，也可以在合成前设置。
     *
     * @return params
     */
    // 以下参数均为选填
    // 设置在线发声音人： 0 普通女声（默认） 1 普通男声 2 特别男声 3 情感男声<度逍遥> 4 情感儿童声<度丫丫>
    // 设置合成的音量，0-9 ，默认 5
    // 设置合成的语速，0-9 ，默认 5
    // 设置合成的语调，0-9 ，默认 5
    // 该参数设置为TtsMode.MIX生效。即纯在线模式不生效。
    // MIX_MODE_DEFAULT 默认 ，wifi状态下使用在线，非wifi离线。在线状态下，请求超时6s自动转离线
    // MIX_MODE_HIGH_SPEED_SYNTHESIZE_WIFI wifi状态下使用在线，非wifi离线。在线状态下， 请求超时1.2s自动转离线
    // MIX_MODE_HIGH_SPEED_NETWORK ， 3G 4G wifi状态下使用在线，其它状态离线。在线状态下，请求超时1.2s自动转离线
    // MIX_MODE_HIGH_SPEED_SYNTHESIZE, 2G 3G 4G wifi状态下使用在线，其它状态离线。在线状态下，请求超时1.2s自动转离线
    // 离线资源文件， 从assets目录中复制到临时目录，需要在initTTs方法前完成
    // 声学模型文件路径 (离线引擎使用), 请确认下面两个文件存在

    private fun getParams(): HashMap<String, String> {
        val params = HashMap<String, String>()
        params[SpeechSynthesizer.PARAM_SPEAKER] = "0"
        params[SpeechSynthesizer.PARAM_VOLUME] = "9"
        params[SpeechSynthesizer.PARAM_SPEED] = "5"
        params[SpeechSynthesizer.PARAM_PITCH] = "5"

        params[SpeechSynthesizer.PARAM_MIX_MODE] = SpeechSynthesizer.MIX_MODE_DEFAULT
        val offlineResource = createOfflineResource(offlineVoice)
        params[SpeechSynthesizer.PARAM_TTS_TEXT_MODEL_FILE] = offlineResource!!.textFilename!!
        params[SpeechSynthesizer.PARAM_TTS_SPEECH_MODEL_FILE] = offlineResource.modelFilename!!
        return params
    }

    fun initial(listener: (TTSResult) -> Unit) {
        mTTSCallback = listener
        initialTts(listener) // 初始化TTS引擎
    }


    /**
     * 初始化引擎，需要的参数均在InitConfig类里
     *
     * DEMO中提供了3个SpeechSynthesizerListener的实现
     * MessageListener 仅仅用log.i记录日志，在logcat中可以看见
     * UiMessageListener 在MessageListener的基础上，对handler发送消息，实现UI的文字更新
     * FileSaveListener 在UiMessageListener的基础上，使用 onSynthesizeDataArrived回调，获取音频流
     */
    private fun initialTts(callback: (TTSResult) -> Unit) {
        LoggerProxy.printable(true) // 日志打印在logcat中
        // 设置初始化参数
        val listener = MessageListener(callback)
        val params = getParams()

        // appId appKey secretKey 网站上您申请的应用获取。注意使用离线合成功能的话，需要应用中填写您app的包名。包名在build.gradle中获取。
        val initConfig = InitConfig(appId, appKey, secretKey, ttsMode, params, listener)

        synthesizer = NonBlockSynthesizer(mContext, initConfig) // 此处可以改为MySyntherizer 了解调用过程
        bInitialStatus = true
        callback.invoke(TTSResult(TTSState.TTS_INIT_COMPLETE))
    }


    private fun createOfflineResource(voiceType: OfflineVoiceMode): OfflineResource? {
        var offlineResource: OfflineResource? = null
        try {
            offlineResource = OfflineResource(mContext, voiceType)
        } catch (e: IOException) {
            // IO 错误自行处理
            e.printStackTrace()
            sendToCallback(TTSState.TTS_ERROR, errMsg = "[error]:copy files from assets failed." + e.message)
        }

        return offlineResource
    }


    private fun checkInitial(): Boolean {
        if (!bInitialStatus) {
            ZLogger.e("请先调用init()来初始化合成引擎")
            sendToCallback(TTSState.TTS_ERROR, errMsg = "请先调用init()来初始化合成引擎")
        }

        return bInitialStatus
    }

    /**
     * speak 实际上是调用 synthesize后，获取音频流，然后播放。
     *
     * 需要合成的文本text的长度不能超过1024个GBK字节。
     */
    fun speak(text: String) {

        if (!checkInitial()) {
            return
        }

        // 合成前可以修改参数：
        //Map<String, String> params = getParams();
        // synthesizer.setParams(params);
        val params = getParams()
        params[SpeechSynthesizer.PARAM_SPEAKER] = voiceMode.ordinal.toString()
        synthesizer.setParams(params)


        if (TextUtils.isEmpty(text)) {
            //mTextToSpeak = "合成失败，请检查需要合成的文本是否正确"
            sendToCallback(TTSState.TTS_ERROR, errMsg = "合成失败，请检查需要合成的文本是否正确")
            return
        }

        val result = synthesizer.speak(text)
        checkResult(result, "speak")
    }


    /**
     * 合成但是不播放，
     * 音频流保存为文件的方法可以参见SaveFileActivity及FileSaveListener
     */
    private fun synthesize(text: String) {
        if (!checkInitial()) {
            return
        }
        var mText = text
        if (TextUtils.isEmpty(mText)) {
            mText = "合成失败，请检查需要合成的文本是否正确"
        }
        val result = synthesizer.synthesize(mText)
        checkResult(result, "synthesize")
    }

    /**
     * 批量播放
     */
    private fun batchSpeak(texts: ArrayList<Pair<String, String>>) {
        if (!checkInitial()) {
            return
        }
        if (texts.size <= 0) {
            ZLogger.e("batchSpeak:文本输入错误")
            return
        }
        val result = synthesizer.batchSpeak(texts)
        checkResult(result, "batchSpeak")
    }


    /**
     * 切换离线发音。注意需要添加额外的判断：引擎在合成时该方法不能调用
     */
    fun changeOfflineVoiceMode(mode: OfflineVoiceMode) {
        if (!checkInitial()) {
            return
        }
        offlineVoice = mode
        val offlineResource = createOfflineResource(offlineVoice)
        ZLogger.e("切换离线语音：" + offlineResource?.modelFilename)

        val result = synthesizer.loadModel(offlineResource?.modelFilename!!, offlineResource.textFilename!!)
        checkResult(result, "loadModel")
    }

    fun changeVoiceMode(mode: VoiceMode) {
        this.voiceMode = mode
    }


    private fun checkResult(result: Int, method: String) {
        if (result != 0) {
            ZLogger.e("error code :$result method:$method, 错误码文档:http://yuyin.baidu.com/docs/tts/122 ")
            sendToCallback(
                TTSState.TTS_ERROR,
                errno = result,
                errMsg = "method:$method, 错误码文档:http://yuyin.baidu.com/docs/tts/122 "
            )
        } else {
            ZLogger.d("checkResult:method($method),result($result)")
        }
    }


    /**
     * 暂停播放。仅调用speak后生效
     */
    fun pause() {
        if (!checkInitial()) {
            return
        }
        val result = synthesizer.pause()
        checkResult(result, "pause")
    }

    /**
     * 继续播放。仅调用speak后生效，调用pause生效
     */
    fun resume() {
        if (!checkInitial()) {
            return
        }
        val result = synthesizer.resume()
        checkResult(result, "resume")
    }

    /*
     * 停止合成引擎。即停止播放，合成，清空内部合成队列。
     */
    fun stop() {
        if (!checkInitial()) {
            return
        }
        val result = synthesizer.stop()
        checkResult(result, "stop")
    }

    fun release() {
        if (!checkInitial()) {
            return
        }
        synthesizer.release()
        mTTSCallback = null
        ZLogger.d("release engine")
    }

    private fun sendToCallback(
        state: TTSState,
        serialId: String? = null,
        progress: Int = 0,
        errno: Int = 0,
        errMsg: String? = null
    ) {
        val ttsResult = TTSResult(state, serialId, progress, errno, errMsg)
        mTTSCallback?.invoke(ttsResult)
    }
}
