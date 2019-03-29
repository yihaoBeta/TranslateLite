package com.beta.yihao.translite.repository

import android.content.Context
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LiveData
import com.beta.yihao.translite.data.MainDatabase
import com.beta.yihao.translite.data.RecognizeResult
import com.beta.yihao.translite.data.TTSState
import com.beta.yihao.translite.data.entity.LanguageEntity
import com.beta.yihao.translite.data.entity.TranslateEntity
import com.beta.yihao.translite.lifecycler.MainFragmentLifeCycle
import io.reactivex.disposables.CompositeDisposable

/**
 * @Author yihao
 * @Description 从各个不同的Repository收集数据，供ViewModel使用
 * @Date 2019/1/14-16:36
 * @Email yihaobeta@163.com
 */
class MainRepository private constructor() {
    lateinit var languageRepository: LanguageRepository
    lateinit var settingsRepository: SettingsRepository
    lateinit var translateRepository: TranslateRepository
    lateinit var collectRepository: CollectRepository
    lateinit var ttsRepository: TtsRepository
    lateinit var recognizeRepository: RecognizeRepository


    companion object {
        @Volatile
        private var instance: MainRepository? = null

        fun getInstance(context: Context, lifecycleObserver: LifecycleObserver?): MainRepository {

            return instance ?: synchronized(this) {
                instance
                    ?: MainRepository().apply {
                        instance = this
                        languageRepository = LanguageRepository.getInstance(
                            MainDatabase.getInstance(context).langDao()
                        )
                        settingsRepository = SettingsRepository.getInstance(
                            MainDatabase.getInstance(context).settingDao()
                        )
                        translateRepository = TranslateRepository.getInstance()
                        collectRepository = CollectRepository.getInstance(
                            MainDatabase.getInstance(context).collectDao()
                        )
                        if (lifecycleObserver != null && lifecycleObserver is MainFragmentLifeCycle) {
                            ttsRepository = TtsRepository.getInstance().also {
                                lifecycleObserver.setTtsRepos(it)
                            }

                            recognizeRepository = RecognizeRepository.getInstance().also {
                                lifecycleObserver.setRecognizeRepos(it)
                            }
                        } else {
                            IllegalArgumentException("LifeCycle is wrong,must be instance of MainFragmentLifecycle")
                        }
                    }
            }
        }
    }

    fun getSourceLanguage(): LiveData<LanguageEntity> {
        return settingsRepository.getSourceLanguage()
    }

    suspend fun setSourceLanguage(languageEntity: LanguageEntity) {
        settingsRepository.setSourceLang(languageEntity)
    }

    fun getTargetLanguage(): LiveData<LanguageEntity> {
        return settingsRepository.getTargetLanguage()
    }

    suspend fun setTargetLanguage(languageEntity: LanguageEntity) {
        settingsRepository.setTargetLanguage(languageEntity)
    }

    fun translate(
        co: CompositeDisposable,
        query: String,
        sourceLang: LanguageEntity,
        targetLang: LanguageEntity,
        callback: (result: TranslateEntity) -> Unit
    ) {
        translateRepository.translate(this.languageRepository, co, query, sourceLang, targetLang, callback)
    }

    fun speak(str: String, callback: (TTSState) -> Unit) {
        ttsRepository.speak(str, callback)
    }

    fun speakWithProgress(str: String, callback: (String, Int) -> Unit) {
        ttsRepository.speakWithProgress(str, callback)
    }

    suspend fun collected(translateEntity: TranslateEntity) {
        collectRepository.collected(translateEntity)
    }

    suspend fun unCollect(id: String) {
        collectRepository.unCollect(id)
    }

    suspend fun getCollectionById(id: String): TranslateEntity? {
        return collectRepository.getCollectById(id)
    }

    fun recognizeStart(callback: (RecognizeResult) -> Unit) {
        recognizeRepository.start(callback)
    }

    fun recognizeStop() {
        recognizeRepository.stop()
    }

    fun onCleared() {

    }
}