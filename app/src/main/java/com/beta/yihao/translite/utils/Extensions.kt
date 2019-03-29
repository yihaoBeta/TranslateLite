package com.beta.yihao.translite.utils

import android.annotation.SuppressLint
import android.content.Context
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.beta.yihao.translite.AppContext
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

/**
 * @Author yihao
 * @Description Kotlin扩展方法集合
 * @Date 2019/1/13-20:47
 * @Email yihaobeta@163.com
 */

fun Disposable.addTo(c: CompositeDisposable) {
    c.add(this)
}

fun Context.toastLong(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_LONG).show()
}

fun Context.toastShort(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

fun Any.getStringById(id: Int): String {
    return AppContext.getString(id)
}

@SuppressLint("CheckResult")
fun <T> MutableLiveData<T>.setValueOnMainThread(value: T) {
    Observable.just(this)
        .subscribeOn(Schedulers.trampoline())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe {
            it.value = value
        }
}


@SuppressLint("CheckResult")
fun <T> T.runInIOThread(block: T.() -> Unit) {
    Observable.just(this)
        .subscribeOn(Schedulers.trampoline())
        .observeOn(Schedulers.io())
        .subscribe {
            block()
        }
}

@SuppressLint("CheckResult")
fun <T> T.runInUIThread(block: T.() -> Unit) {
    Observable.just(this)
        .subscribeOn(Schedulers.trampoline())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe {
            block()
        }
}
