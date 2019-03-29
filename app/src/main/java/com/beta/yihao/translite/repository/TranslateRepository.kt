package com.beta.yihao.translite.repository

import com.beta.yihao.translite.data.entity.LanguageEntity
import com.beta.yihao.translite.data.entity.TranslateEntity
import com.beta.yihao.translite.engine.TranslateEngine
import com.beta.yihao.translite.engine.translate.TransErrCode
import com.beta.yihao.translite.engine.translate.getEnumValue
import com.beta.yihao.translite.utils.ZLogger
import com.beta.yihao.translite.utils.addTo
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.util.*

/**
 * @Author yihao
 * @Description 翻译相关Repository
 * @Date 2019/1/16-13:54
 * @Email yihaobeta@163.com
 */
class TranslateRepository private constructor() {
    companion object {
        @Volatile
        private var instance: TranslateRepository? = null

        fun getInstance(): TranslateRepository {
            return instance ?: synchronized(this) {
                instance ?: TranslateRepository().also { instance = it }
            }
        }
    }

    fun translate(
        languageRepository: LanguageRepository,
        co: CompositeDisposable,
        query: String,
        sourceLang: LanguageEntity,
        targetLang: LanguageEntity,
        callback: (result: TranslateEntity) -> Unit
    ) {
        TranslateEngine.translateWithSourceAndTarget(query, sourceLang.id, targetLang.id)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                ZLogger.d("onNet:${it.error_code}")
                ZLogger.d("from:${it.from},to:${it.to}")
                val errCode = getEnumValue(it.error_code)
                val result = TranslateEntity(
                    it.trans_result[0].src,
                    it.trans_result[0].dst,
                    languageRepository.getLanguage(it.from),
                    languageRepository.getLanguage(it.to),
                    Calendar.getInstance()
                )
                result.setTransErrCode(errCode)
                result.id = result.generateId()!!
                callback.invoke(result)
            }) {
                //其他错误
                ZLogger.d("other error:${it.localizedMessage}")
                val errCode = TransErrCode.OTHER_ERR
                errCode.setMessage(it.localizedMessage!!)
                val result = TranslateEntity(
                    "",
                    "",
                    sourceLang,
                    targetLang,
                    Calendar.getInstance()
                )
                result.setTransErrCode(errCode)
                result.id = result.generateId()!!
                callback.invoke(result)
            }.addTo(co)
    }
}