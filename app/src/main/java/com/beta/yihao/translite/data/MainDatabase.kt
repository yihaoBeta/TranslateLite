package com.beta.yihao.translite.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.beta.yihao.translite.data.dao.CollectDao
import com.beta.yihao.translite.data.dao.LanguageDao
import com.beta.yihao.translite.data.dao.SettingDao
import com.beta.yihao.translite.data.entity.LanguageEntity
import com.beta.yihao.translite.data.entity.SettingsEntity
import com.beta.yihao.translite.data.entity.TranslateEntity
import com.beta.yihao.translite.utils.DATABASE_NAME
import com.beta.yihao.translite.utils.ZLogger
import com.beta.yihao.translite.worker.InitDatabaseWorker

/**
 * @Author yihao
 * @Description 数据库
 * @Date 2019/1/14-14:25
 * @Email yihaobeta@163.com
 */

@Database(
    entities = [TranslateEntity::class, LanguageEntity::class, SettingsEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(LanguageConverter::class, DateConverter::class)
abstract class MainDatabase : RoomDatabase() {
    abstract fun langDao(): LanguageDao
    abstract fun settingDao(): SettingDao
    abstract fun collectDao(): CollectDao

    companion object {
        @Volatile
        private var instance: MainDatabase? = null

        fun getInstance(context: Context): MainDatabase {
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also { instance = it }
            }
        }

        private fun buildDatabase(context: Context): MainDatabase {
            ZLogger.d("buildDatabase")
            return Room.databaseBuilder(context, MainDatabase::class.java, DATABASE_NAME)
                .addCallback(object : RoomDatabase.Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        ZLogger.d("on DB create")
                        val workRequest = OneTimeWorkRequestBuilder<InitDatabaseWorker>().build()
                        WorkManager.getInstance().enqueue(workRequest)
                        super.onCreate(db)
                    }

                    override fun onOpen(db: SupportSQLiteDatabase) {
                        ZLogger.d("onOpen")
                        super.onOpen(db)
                    }
                })
                .allowMainThreadQueries()
                .build()
        }
    }
}