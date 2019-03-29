package com.beta.yihao.translite.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.beta.yihao.translite.data.entity.LanguageEntity
import com.beta.yihao.translite.data.entity.SettingsEntity

/**
 * @Author yihao
 * @Description 设置接口
 * @Date 2019/1/15-16:16
 * @Email yihaobeta@163.com
 */
@Dao
interface SettingDao {
    @Query("SELECT source FROM setting")
    fun getSourceLang(): LiveData<LanguageEntity>

    @Query("SELECT target FROM setting")
    fun getTargetLang(): LiveData<LanguageEntity>

    @Query("UPDATE setting SET source = :sourceLang WHERE id = 1")
    fun setSourceLang(sourceLang: LanguageEntity)

    @Query("UPDATE setting SET target = :sourceLang WHERE id = 1")
    fun setTargetLang(sourceLang: LanguageEntity)

    @Insert
    fun insert(settingsEntity: SettingsEntity)

    @Query("SELECT * FROM setting WHERE id = 1")
    fun getSetting(): SettingsEntity

    @Query("UPDATE setting SET voice = :voiceMode")
    fun setVoiceMode(voiceMode: Int)

    @Query("SELECT voice FROM SETTING")
    fun getVoiceMode(): Int
}