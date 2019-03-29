package com.beta.yihao.translite.data

import android.text.TextUtils
import androidx.room.TypeConverter
import com.beta.yihao.translite.data.entity.LanguageEntity

/**
 * @Author yihao
 * @Description 用于Room数据库的语言实体数据格式转换类
 * @Date 2019/1/15-16:42
 * @Email yihaobeta@163.com
 */
class LanguageConverter {
    @TypeConverter
    fun toLanguage(dbStr: String): LanguageEntity {
        if (TextUtils.isEmpty(dbStr)) return LanguageEntity("auto", "自动")
        var id = "auto"
        var name = "自动"
        dbStr.split("@").forEachIndexed { index, s ->
            when (index) {
                0 -> id = s
                1 -> name = s
            }
        }
        return LanguageEntity(id, name)
    }

    @TypeConverter
    fun toDB(languageEntity: LanguageEntity): String {
        return "${languageEntity.id}@${languageEntity.name}"
    }

}