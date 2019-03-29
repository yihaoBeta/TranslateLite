package com.beta.yihao.translite.repository

import androidx.lifecycle.LiveData
import com.beta.yihao.translite.data.dao.LanguageDao
import com.beta.yihao.translite.data.entity.LanguageEntity

/**
 * @Author yihao
 * @Description 语言数据仓库
 * @Date 2019/1/14-16:26
 * @Email yihaobeta@163.com
 */

class LanguageRepository private constructor(private val languageDao: LanguageDao) {
    companion object {
        @Volatile
        private var instance: LanguageRepository? = null

        fun getInstance(dao: LanguageDao): LanguageRepository {
            return instance ?: synchronized(this) {
                instance
                    ?: LanguageRepository(dao).also { instance = it }
            }
        }
    }

    fun getAll(): LiveData<List<LanguageEntity>> {
        return languageDao.getLanguages()
    }

    fun getLanguage(id: String): LanguageEntity {
        return languageDao.getById(id)
    }
}