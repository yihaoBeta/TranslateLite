package com.beta.yihao.translite.repository

import com.beta.yihao.translite.data.TTSState
import com.beta.yihao.translite.data.dao.CollectDao
import com.beta.yihao.translite.data.entity.TranslateEntity

/**
 * @Author yihao
 * @Description 收藏详情数据仓库
 * @Date 2019/1/22-17:11
 * @Email yihaobeta@163.com
 */
class CollectionDetailRepository(private val collectDao: CollectDao) {
    private val ttsRepository by lazy {
        TtsRepository.getInstance()
    }

    companion object {
        @Volatile
        private var instance: CollectionDetailRepository? = null

        fun getInstance(dao: CollectDao): CollectionDetailRepository {
            return instance ?: synchronized(this) {
                instance ?: CollectionDetailRepository(dao).also {
                    instance = it
                }
            }
        }
    }

    suspend fun getCollectionDetail(id: String): TranslateEntity? {
        return collectDao.getById(id)
    }

    suspend fun unCollect(id: String) {
        collectDao.delById(id)
    }

    fun collected(translateEntity: TranslateEntity) {
        collectDao.insert(translateEntity)
    }

    fun speak(text: String, callback: (TTSState) -> Unit) {
        ttsRepository.speak(text, callback)
    }
}