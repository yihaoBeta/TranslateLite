package com.beta.yihao.translite.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.beta.yihao.translite.engine.translate.TransErrCode
import com.beta.yihao.translite.utils.MD5
import java.util.*

/**
 * @Author yihao
 * @Description 翻译结果/收藏实体类
 * @Date 2019/1/16-14:07
 * @Email yihaobeta@163.com
 */
@Entity(tableName = "collect")
data class TranslateEntity(
    @ColumnInfo(name = "sourceText")
    var sourceText: String,
    @ColumnInfo(name = "transText")
    var transText: String,
    @ColumnInfo(name = "sourceLang")
    var sourLang: LanguageEntity,
    @ColumnInfo(name = "targetLang")
    var targetLang: LanguageEntity,
    @ColumnInfo(name = "date")
    var date: Calendar,
    @PrimaryKey
    var id: String = ""
) {
    @Ignore
    private var transErrCode: TransErrCode = TransErrCode.SUCCESS

    fun setTransErrCode(transErrCode: TransErrCode) {
        this.transErrCode = transErrCode
    }

    fun getTransErrCode(): TransErrCode {
        return this.transErrCode
    }

    fun generateId(): String? {
        return MD5.md5(toString())
    }

    override fun toString(): String {
        return "$sourceText + $transText + $sourLang+$targetLang"
    }
}

