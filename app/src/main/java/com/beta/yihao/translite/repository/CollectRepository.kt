package com.beta.yihao.translite.repository

import androidx.lifecycle.LiveData
import com.beta.yihao.translite.data.dao.CollectDao
import com.beta.yihao.translite.data.entity.TranslateEntity

/**
 * @Author yihao
 * @Description 收藏列表数据仓库
 * @Date 2019/1/17-16:52
 * @Email yihaobeta@163.com
 */

class CollectRepository private constructor(private val collectDao: CollectDao) {
    companion object {
        @Volatile
        private var instance: CollectRepository? = null

        fun getInstance(dao: CollectDao): CollectRepository {
            return instance ?: synchronized(this) {
                instance ?: CollectRepository(dao).also {
                    instance = it
                }
            }
        }
    }

    suspend fun getCollectById(id: String): TranslateEntity? {
        return collectDao.getById(id)
    }

    fun getAllCollections(): LiveData<List<TranslateEntity>> {
        return collectDao.getAll()
    }

    suspend fun collected(translateEntity: TranslateEntity) {
        collectDao.insert(translateEntity)
    }

    suspend fun unCollect(id: String) {
        collectDao.delById(id)
    }
}