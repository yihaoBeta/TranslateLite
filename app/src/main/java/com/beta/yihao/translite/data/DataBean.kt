package com.beta.yihao.translite.data

import com.google.gson.annotations.SerializedName
import java.util.*

/**
 * @Author yihao
 * @Date 2018/12/11-09:31
 * @Email yihaobeta@163.com
 */

enum class TTSState(description: String) {
    TTS_IDLE("tts idle"),
    TTS_INIT_COMPLETE("tts init complete"),
    TTS_SYNTHESIS_BEGIN("tts synthesis begin"),
    TTS_SYNTHESIS_COMPLETE("tts synthesis complete"),
    TTS_SPEAK_BEGIN("tts speak begin"),
    TTS_SPEAK_PROGRESS("tts speak progress"),
    TTS_SPEAK_COMPLETE("tts speak complete"),
    TTS_ERROR("tts error"),
}

enum class RecognizeState(description: String) {
    ASR_IDLE("asr.idle"),
    ASR_BEGIN("asr.begin"),
    ASR_VOLUME("asr.volume"),
    ASR_PARTIAL("asr.partial"),
    ASR_STOP("asr.stop"),
    ASR_READY("asr.ready"),
    ASR_EXIT("asr.exit"),
    ASR_FINISH("asr.finish")
}

data class TTSResult(
    var state: TTSState = TTSState.TTS_IDLE,
    val serialNum: String? = null,
    var progress: Int = 0,
    var errCode: Int = 0,
    var errMsg: String? = null
) {
    override fun toString(): String {
        return "[state:${state.name}]-[serial:$serialNum]-[progress:$progress]-[errno:$errCode]-[errMsg:$errMsg]"
    }
}

data class RecognizeResult(
    var state: RecognizeState,
    var recognizeText: RecognizeText? = null,
    var recognizeVolume: RecognizeVolume? = null,
    var error: Int = 0
)

//"results_recognition":["你"],"result_type":"partial_result","best_result":"你","origin_result":{"corpus_no":6633589658154459059,"err_no":0,"result":{"word":["你"]},"sn":"c5a29e8b-3d4d-450c-918f-cd3417143b5f"},"error":0}
data class RecognizeText(
    var results_recognition: ArrayList<String>,
    var result_type: String,
    var best_result: String,
    var error: Int
)

//{"volume":13,"volume-percent":0}


data class RecognizeVolume(var volume: Int, @SerializedName("volume-percent") var volumePercent: Int)
//data class Language(var id: String, var name: String) : Serializable
//data class LanguageList(var list: ArrayList<Language>)
