package com.beta.yihao.translite.utils

import android.content.Context
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * @Author yihao
 * @Description preference代理类
 * @Date 2018/12/18-15:50
 * @Email yihaobeta@163.com
 */
class PreferenceProxy<T> constructor(
    val context: Context,
    val key: String,
    val default: T,
    private val prefName: String = "default"
) : ReadWriteProperty<Any?, T> {

    private val preference by lazy {
        context.getSharedPreferences(prefName, Context.MODE_PRIVATE)
    }

    override fun getValue(thisRef: Any?, property: KProperty<*>): T {
        return get(key)
    }

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        save(key, value)
    }


    private fun save(key: String, value: T) {
        with(preference.edit()) {
            when (value) {
                is String -> putString(key, value)
                is Boolean -> putBoolean(key, value)
                is Int -> putInt(key, value)
                is Long -> putLong(key, value)
                else -> throw IllegalArgumentException("unsupported argument")
            }
            apply()
        }
    }

    @Suppress("UNCHECKED_CAST", "IMPLICIT_CAST_TO_ANY")
    private fun get(key: String): T {
        return when (default) {
            is String -> preference.getString(key, default)
            is Boolean -> preference.getBoolean(key, default)
            is Int -> preference.getInt(key, default)
            is Long -> preference.getLong(key, default)
            else -> {
                throw IllegalArgumentException("unSupported type")
            }
        } as T
    }

}