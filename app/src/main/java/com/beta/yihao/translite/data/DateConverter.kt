package com.beta.yihao.translite.data

import androidx.room.TypeConverter
import java.util.*

/**
 * @Author yihao
 * @Description 用于Room数据库的日期数据格式转换类
 * @Date 2019/1/17-16:42
 * @Email yihaobeta@163.com
 */

class DateConverter {
    @TypeConverter
    fun toDB(calendar: Calendar): Long {
        return calendar.timeInMillis
    }

    @TypeConverter
    fun toCalendar(dbValue: Long): Calendar {
        return Calendar.getInstance().apply {
            timeInMillis = dbValue
        }
    }
}