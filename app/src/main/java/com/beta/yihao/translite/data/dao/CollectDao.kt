package com.beta.yihao.translite.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.beta.yihao.translite.data.entity.TranslateEntity

/**
 * @Author yihao
 * @Description 收藏接口
 * @Date 2019/1/17-16:35
 * @Email yihaobeta@163.com
 */
@Dao
interface CollectDao {

    @Insert
    fun insert(translate: TranslateEntity)

    @Query("SELECT * FROM collect")
    fun getAll(): LiveData<List<TranslateEntity>>

    @Query("SELECT * FROM collect WHERE id = :id")
    fun getById(id: String): TranslateEntity?

    @Query("DELETE from collect where id=:id")
    fun delById(id: String)
}