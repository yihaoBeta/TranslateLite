package com.beta.yihao.translite.engine.tts


import com.baidu.tts.client.SpeechError
import com.baidu.tts.client.SpeechSynthesizerListener
import com.beta.yihao.translite.data.TTSResult
import com.beta.yihao.translite.data.TTSState
import com.beta.yihao.translite.utils.ZLogger

/**
 * SpeechSynthesizerListener 简单地实现，仅仅记录日志
 * Created by fujiayi on 2017/5/19.
 */

open class MessageListener(val listener: (TTSResult) -> Unit) : SpeechSynthesizerListener,
    MainHandlerConstant {

    /**
     * 播放开始，每句播放开始都会回调
     *
     * @param utteranceId
     */
    override fun onSynthesizeStart(utteranceId: String) {
        sendMessage("准备开始合成,序列号:$utteranceId")
        sendToCallback(TTSState.TTS_SYNTHESIS_BEGIN, serialId = utteranceId)
    }

    /**
     * 语音流 16K采样率 16bits编码 单声道 。
     *
     * @param utteranceId
     * @param bytes       二进制语音 ，注意可能有空data的情况，可以忽略
     * @param progress    如合成“百度语音问题”这6个字， progress肯定是从0开始，到6结束。 但progress无法和合成到第几个字对应。
     */
    override fun onSynthesizeDataArrived(utteranceId: String, bytes: ByteArray, progress: Int) {
        //  Log.i(TAG, "合成进度回调, progress：" + progress + ";序列号:" + utteranceId );
    }

    /**
     * 合成正常结束，每句合成正常结束都会回调，如果过程中出错，则回调onError，不再回调此接口
     *
     * @param utteranceId
     */
    override fun onSynthesizeFinish(utteranceId: String) {
        sendMessage("合成结束回调, 序列号:$utteranceId")
        sendToCallback(TTSState.TTS_SYNTHESIS_COMPLETE, serialId = utteranceId)
    }

    override fun onSpeechStart(utteranceId: String) {
        sendMessage("播放开始回调, 序列号:$utteranceId")
        sendToCallback(TTSState.TTS_SPEAK_BEGIN, serialId = utteranceId)
    }

    /**
     * 播放进度回调接口，分多次回调
     *
     * @param utteranceId
     * @param progress    如合成“百度语音问题”这6个字， progress肯定是从0开始，到6结束。 但progress无法保证和合成到第几个字对应。
     */
    override fun onSpeechProgressChanged(utteranceId: String, progress: Int) {
        sendMessage("播放进度回调, progress：$progress;序列号:$utteranceId")
        sendToCallback(TTSState.TTS_SPEAK_PROGRESS, serialId = utteranceId, progress = progress)
    }

    /**
     * 播放正常结束，每句播放正常结束都会回调，如果过程中出错，则回调onError,不再回调此接口
     *
     * @param utteranceId
     */
    override fun onSpeechFinish(utteranceId: String) {
        sendMessage("播放结束回调, 序列号:$utteranceId")
        sendToCallback(TTSState.TTS_SPEAK_COMPLETE, serialId = utteranceId)
    }

    /**
     * 当合成或者播放过程中出错时回调此接口
     *
     * @param utteranceId
     * @param speechError 包含错误码和错误信息
     */
    override fun onError(utteranceId: String, speechError: SpeechError) {
        sendErrorMessage(
            "错误发生：" + speechError.description + "，错误编码："
                    + speechError.code + "，序列号:" + utteranceId
        )

        sendToCallback(
            TTSState.TTS_ERROR,
            serialId = utteranceId,
            errno = speechError.code,
            errMsg = speechError.description
        )
    }

    private fun sendErrorMessage(message: String) {
        sendMessage(message, true)
    }


    private fun sendMessage(message: String) {
        sendMessage(message, false)
    }

    open fun sendMessage(message: String, isError: Boolean) {
        if (isError) {
            ZLogger.d(message)
        } else {
            ZLogger.d(message)
        }

    }


    private fun sendToCallback(
        state: TTSState,
        serialId: String? = null,
        progress: Int = 0,
        errno: Int = 0,
        errMsg: String? = null
    ) {
        val ttsResult = TTSResult(state, serialId, progress, errno, errMsg)
        listener.invoke(ttsResult)
    }
}
