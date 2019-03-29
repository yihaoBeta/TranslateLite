package com.beta.yihao.translite.engine.tts

import android.content.ContentValues.TAG
import android.content.Context
import android.content.res.AssetManager
import android.util.Log
import com.beta.yihao.translite.utils.FileUtil
import java.io.IOException
import java.util.*


class OfflineResource @Throws(IOException::class)
constructor(context: Context, offlineVoiceMode: OfflineVoiceMode) {

    private val assets: AssetManager
    private val destPath: String

    var textFilename: String? = null
        private set
    var modelFilename: String? = null
        private set

    init {
        var mContext = context
        mContext = mContext.applicationContext
        this.assets = mContext.applicationContext.assets
        this.destPath = FileUtil.createTmpDir(mContext, SAMPLE_DIR)
        setOfflineVoiceType(offlineVoiceMode)
    }

    @Throws(IOException::class)
    fun setOfflineVoiceType(offlineVoiceMode: OfflineVoiceMode) {
        val model: String = when (offlineVoiceMode) {
            OfflineVoiceMode.VOICE_MALE -> "bd_etts_common_speech_m15_mand_eng_high_am-mix_v3.0.0_20170505.dat"
            OfflineVoiceMode.VOICE_FEMALE -> "bd_etts_common_speech_f7_mand_eng_high_am-mix_v3.0.0_20170512.dat"
            OfflineVoiceMode.VOICE_DUXY -> "bd_etts_common_speech_yyjw_mand_eng_high_am-mix_v3.0.0_20170512.dat"
            OfflineVoiceMode.VOICE_DUYY -> "bd_etts_common_speech_as_mand_eng_high_am_v3.0.0_20170516.dat"
            else -> throw RuntimeException("voice type is not in list")
        }
        textFilename = copyAssetsFile(OFFLINE_TEXT_DAT_NAME)
        modelFilename = copyAssetsFile(model)

    }


    @Throws(IOException::class)
    private fun copyAssetsFile(sourceFilename: String): String {
        val destFilename = "$destPath/$sourceFilename"
        var recover = false
        val existed = mapInitied[sourceFilename] // 启动时完全覆盖一次
        if (existed == null || !existed) {
            recover = true
        }
        FileUtil.copyFromAssets(assets, sourceFilename, destFilename, recover)
        Log.i(TAG, "文件复制成功：$destFilename")
        return destFilename
    }

    companion object {

//        const val VOICE_FEMALE = "F"
//
//        const val VOICE_MALE = "M"
//
//        const val VOICE_DUYY = "Y"
//
//        const val VOICE_DUXY = "X"

        private const val SAMPLE_DIR = "baiduTTS"

        private const val OFFLINE_TEXT_DAT_NAME = "bd_etts_text.dat"

        private val mapInitied = HashMap<String, Boolean>()
    }
}
