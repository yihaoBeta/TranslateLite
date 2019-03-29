package com.beta.yihao.translite.data.entity

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * @Author yihao
 * @Description 语言实体类，用于标识翻译内容的源语言和目标语言
 * @Date 2019/1/13-20:49
 * @Email yihaobeta@163.com
 */
@Entity(tableName = "language")
class LanguageEntity(
    @PrimaryKey @NonNull val id: String,
    @ColumnInfo var name: String
) {
    override fun toString(): String {
        return "$id:$name"
    }
}