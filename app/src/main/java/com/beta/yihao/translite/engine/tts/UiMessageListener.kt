package com.beta.yihao.translite.engine.tts

import android.os.Handler
import android.os.Message
import com.baidu.tts.client.SpeechError
import com.baidu.tts.client.SpeechSynthesizerListener
import com.beta.yihao.translite.utils.ZLogger

/**
 * 在 MessageListener的基础上，和UI配合。
 * Created by fujiayi on 2017/9/14.
 */

class UiMessageListener(private val mainHandler: Handler?) : SpeechSynthesizerListener,
    MainHandlerConstant {
    override fun onSynthesizeStart(serialId: String?) {
        sendMessage(serialId ?: "NULL", false, MainHandlerConstant.TTS_SYNTHESIZE_BEGIN)
    }

    override fun onSpeechFinish(serialId: String?) {
        sendMessage(serialId ?: "NULL", false, MainHandlerConstant.TTS_PLAY_COMPLETED)
    }

    override fun onSynthesizeFinish(serialId: String?) {
        sendMessage(serialId ?: "NULL", false, MainHandlerConstant.TTS_SYNTHESIZE_COMPLETED)
    }

    override fun onSpeechStart(serialId: String?) {
        sendMessage(serialId ?: "NULL", false, MainHandlerConstant.TTS_PLAY_BEGIN)
    }

    override fun onError(serialId: String?, error: SpeechError?) {
        sendMessage(serialId ?: "NULL", true, MainHandlerConstant.TTS_ERROR)
    }

    /**
     * 合成数据和进度的回调接口，分多次回调。
     * 注意：progress表示进度，与播放到哪个字无关
     * @param utteranceId
     * @param data 合成的音频数据。该音频数据是采样率为16K，2字节精度，单声道的pcm数据。
     * @param progress 文本按字符划分的进度，比如:你好啊 进度是0-3
     */
    override fun onSynthesizeDataArrived(utteranceId: String, data: ByteArray, progress: Int) {
        mainHandler!!.sendMessage(
            mainHandler.obtainMessage(
                MainHandlerConstant.UI_CHANGE_SYNTHESIZE_TEXT_SELECTION,
                progress.toString()
            )
        )
    }

    /**
     * 播放进度回调接口，分多次回调
     * 注意：progress表示进度，与播放到哪个字无关
     *
     * @param utteranceId
     * @param progress 文本按字符划分的进度，比如:你好啊 进度是0-3
     */
    override fun onSpeechProgressChanged(utteranceId: String, progress: Int) {
        mainHandler!!.sendMessage(
            mainHandler.obtainMessage(
                MainHandlerConstant.UI_CHANGE_INPUT_TEXT_SELECTION,
                progress.toString()
            )
        )
    }

    private fun sendMessage(message: String, isError: Boolean, action: Int) {
        if (isError) {
            ZLogger.d(message)
        }
        if (mainHandler != null) {
            val msg = Message.obtain()
            msg.what = action
            msg.obj = message + "\n"
            mainHandler.sendMessage(msg)
        }
    }
}
