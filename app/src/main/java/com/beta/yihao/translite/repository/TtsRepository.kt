package com.beta.yihao.translite.repository

import com.beta.yihao.translite.AppContext
import com.beta.yihao.translite.data.MainDatabase
import com.beta.yihao.translite.data.TTSState
import com.beta.yihao.translite.engine.TTSEngine
import com.beta.yihao.translite.engine.tts.VoiceMode
import com.beta.yihao.translite.utils.ZLogger

/**
 * @Author yihao
 * @Description tts Repository
 * @Date 2019/1/16-20:15
 * @Email yihaobeta@163.com
 */
class TtsRepository private constructor() {
    private val ttsEngine: TTSEngine by lazy {
        TTSEngine(AppContext)
    }

    private var isStart = false
    private var isSpeaking = false
    private var ttsProgressCallback: ((String, Int) -> Unit)? = null
    private var stateCallback: ((TTSState) -> Unit)? = null

    companion object {
        @Volatile
        private var instance: TtsRepository? = null

        fun getInstance(): TtsRepository {
            return instance ?: synchronized(this) {
                instance ?: TtsRepository().also {
                    instance = it
                    it.setVoiceMode((SettingsRepository.getInstance(MainDatabase.getInstance(AppContext).settingDao()).getVoiceMode()))
                }
            }
        }
    }

    /**
     * 初始化tts引擎
     */
    fun init() {
        if (isStart.not()) {
            ttsEngine.initial {
                if (it.errCode != 0) {
                    ZLogger.d("tts engine init fail : ${it.errMsg}")
                    ttsEngine.release()
                    //return@initial
                }
                if (this.stateCallback != null) {
                    stateCallback!!.invoke(it.state)
                }
                when (it.state) {
                    TTSState.TTS_ERROR -> {
                        isSpeaking = false
                        isStart = false
                    }
                    TTSState.TTS_IDLE -> {
                    }
                    TTSState.TTS_INIT_COMPLETE -> {
                        isStart = true
                    }
                    TTSState.TTS_SPEAK_BEGIN -> {
                        isSpeaking = true
                    }
                    TTSState.TTS_SPEAK_COMPLETE -> {
                        isSpeaking = false
                        ttsProgressCallback = null
                        stateCallback = null
                    }
                    TTSState.TTS_SPEAK_PROGRESS -> {
                        if (ttsProgressCallback != null) {
                            ttsProgressCallback!!.invoke(it.serialNum ?: "0", it.progress)
                        }
                    }
                    TTSState.TTS_SYNTHESIS_BEGIN -> {
                    }
                    TTSState.TTS_SYNTHESIS_COMPLETE -> {
                    }
                }
            }
        }
    }

    fun speak(str: String, stateCallback: (TTSState) -> Unit) {
        if (isStart.not()) {
            ZLogger.d("init tts")
            init()
        }
        if (isSpeaking.not()) {
            ttsEngine.speak(str)
            this.stateCallback = stateCallback
        }
    }

    fun speakWithProgress(str: String, callback: (String, Int) -> Unit) {
        if (isSpeaking.not()) {
            ttsEngine.speak(str)
            ttsProgressCallback = callback
        }
    }

    fun stop() {
        if (isStart) {
            ttsEngine.stop()
            stateCallback?.invoke(TTSState.TTS_SPEAK_COMPLETE)
            isStart = false
            isSpeaking = false
        }
    }

    fun setVoiceMode(mode: Int) {
        when (mode) {
            0 -> ttsEngine.changeVoiceMode(VoiceMode.VOICE_FEMALE)
            1 -> ttsEngine.changeVoiceMode(VoiceMode.VOICE_MALE)
            2 -> ttsEngine.changeVoiceMode(VoiceMode.VOICE_MALE_SP)
            3 -> ttsEngine.changeVoiceMode(VoiceMode.VOICE_DUXY)
            4 -> ttsEngine.changeVoiceMode(VoiceMode.VOICE_DUYY)
            else -> ttsEngine.changeVoiceMode(VoiceMode.VOICE_FEMALE)
        }
    }

    fun release() {
        ttsEngine.release()
    }
}