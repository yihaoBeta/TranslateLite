package com.beta.yihao.translite.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.beta.yihao.translite.data.entity.LanguageEntity

/**
 * @Author yihao
 * @Description 语言接口
 * @Date 2019/1/14-14:34
 * @Email yihaobeta@163.com
 */
@Dao
interface LanguageDao {
    @Query("SELECT * FROM language ORDER BY id")
    fun getLanguages(): LiveData<List<LanguageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertList(list: List<LanguageEntity>)

    @Query("SELECT * FROM language WHERE id = :id")
    fun getById(id: String): LanguageEntity
}