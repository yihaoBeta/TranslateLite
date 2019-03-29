package com.beta.yihao.translite.data.entity

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * @Author yihao
 * @Description 设置实体类
 * @Date 2019/1/15-16:09
 * @Email yihaobeta@163.com
 */
@Entity(tableName = "setting")
data class SettingsEntity(

    @ColumnInfo(name = "source")
    var sourceLang: LanguageEntity,

    @ColumnInfo(name = "target")
    var targetLang: LanguageEntity,

    @ColumnInfo(name = "voice")
    var voiceMode: Int = 0,

    @PrimaryKey @NonNull
    var id: Int = 1
)