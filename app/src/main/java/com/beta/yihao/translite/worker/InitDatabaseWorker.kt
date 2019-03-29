package com.beta.yihao.translite.worker

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.beta.yihao.translite.data.MainDatabase
import com.beta.yihao.translite.data.entity.LanguageEntity
import com.beta.yihao.translite.data.entity.SettingsEntity
import com.beta.yihao.translite.utils.ASSET_JSON_FILE_LANGUAGE
import com.beta.yihao.translite.utils.ZLogger
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader

/**
 * @Author yihao
 * @Description 初始化数据库的工作流
 * @Date 2019/1/14-14:06
 * @Email yihaobeta@163.com
 */
class InitDatabaseWorker(val context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {

    private val jsonType = object : TypeToken<List<LanguageEntity>>() {}.type
    private var reader: JsonReader? = null
    override fun doWork(): Result {
        return try {

            //读取Asset中的语言列表
            val inputStream = applicationContext.assets.open(ASSET_JSON_FILE_LANGUAGE)
            reader = JsonReader(inputStream.reader())
            val list: List<LanguageEntity> = Gson().fromJson(reader, jsonType)
            if (list.isEmpty()) {
                throw IllegalStateException("asset file [$ASSET_JSON_FILE_LANGUAGE] is empty")
            }

            //将所有语言写入数据库中
            MainDatabase.getInstance(applicationContext).langDao().insertList(list)

            val defaultLang = LanguageEntity("auto", "自动")
            val settingsEntity = SettingsEntity(defaultLang, defaultLang)
            MainDatabase.getInstance(applicationContext).settingDao().insert(settingsEntity)
            ZLogger.logAll("Database init..")
            Result.success()
        } catch (e: Exception) {
            ZLogger.d(e)
            Result.failure()
        }
    }
}