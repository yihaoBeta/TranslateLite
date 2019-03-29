package com.beta.yihao.translite.repository

import androidx.lifecycle.LiveData
import com.beta.yihao.translite.data.dao.SettingDao
import com.beta.yihao.translite.data.entity.LanguageEntity

/**
 * @Author yihao
 * @Description 设置相关Repository
 * @Date 2019/1/15-16:52
 * @Email yihaobeta@163.com
 */
class SettingsRepository private constructor(private val settingDao: SettingDao) {

    companion object {
        @Volatile
        private var instance: SettingsRepository? = null

        fun getInstance(dao: SettingDao): SettingsRepository {
            return instance ?: synchronized(this) {
                instance
                    ?: SettingsRepository(dao).also {
                        instance = it
                    }
            }
        }
    }

    fun getSourceLanguage(): LiveData<LanguageEntity> {
        return settingDao.getSourceLang()
    }

    suspend fun setSourceLang(languageEntity: LanguageEntity) {
        settingDao.setSourceLang(languageEntity)
    }

    fun getTargetLanguage(): LiveData<LanguageEntity> {
        return settingDao.getTargetLang()
    }

    suspend fun setTargetLanguage(languageEntity: LanguageEntity) {
        settingDao.setTargetLang(languageEntity)
    }

    fun getVoiceMode(): Int {
        return settingDao.getVoiceMode()
    }

    fun setVoiceMode(mode: Int) {
        settingDao.setVoiceMode(mode)
    }
}