package com.beta.yihao.translite.utils

import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.math.absoluteValue

object SystemUtils {
    fun getDefaultLanguage(): String? = Locale.getDefault().language

    fun countDown(time: Int): Observable<Int> {

        return Observable.interval(0, 1, TimeUnit.SECONDS)
            .subscribeOn(AndroidSchedulers.mainThread())
            .observeOn(AndroidSchedulers.mainThread())
            .map { time - it.absoluteValue.toInt() }
            .take(time.toLong() + 1)

    }

    fun getCurrentTime(): String {
        return System.currentTimeMillis().toString()
    }
}
