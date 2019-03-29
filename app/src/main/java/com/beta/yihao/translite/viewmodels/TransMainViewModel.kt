package com.beta.yihao.translite.viewmodels

import androidx.lifecycle.*
import com.beta.yihao.translite.R
import com.beta.yihao.translite.data.RecognizeState
import com.beta.yihao.translite.data.TTSState
import com.beta.yihao.translite.data.entity.LanguageEntity
import com.beta.yihao.translite.data.entity.TranslateEntity
import com.beta.yihao.translite.engine.translate.TransErrCode
import com.beta.yihao.translite.lifecycler.MainFragmentLifeCycle
import com.beta.yihao.translite.repository.MainRepository
import com.beta.yihao.translite.utils.SUPPORT_TTS_LIST
import com.beta.yihao.translite.utils.ZLogger
import com.beta.yihao.translite.utils.getStringById
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import java.util.*

/**
 * @Author yihao
 * @Description 主ViewModel 主要负责翻译主界面的数据管理
 * @Date 2019/1/14-16:55
 * @Email yihaobeta@163.com
 */
class TransMainViewModel(private val lifecycle: LifecycleObserver?, private val mainRepository: MainRepository) :
    ViewModel() {

    private var transResult = MutableLiveData<TranslateEntity>()
    private var sourceLangLiveData: LiveData<LanguageEntity> = mainRepository.getSourceLanguage()
    private var targetLangLiveData: LiveData<LanguageEntity> = mainRepository.getTargetLanguage()
    private var sourceLang: LanguageEntity? = null
    private var targetLang: LanguageEntity? = null
    private var toastMessage = MutableLiveData<String>()
    private var isSpeaking = MutableLiveData<Boolean>()
    private var isCollected = MutableLiveData<Boolean>()
    private var job = Job()

    //用于协程的管理
    private val coroutineManager = CoroutineScope(job)
    private var isCollectedForUI = false

    init {
        isSpeaking.value = false
        isCollected.value = false
        sourceLangLiveData.observeForever {
            sourceLang = it
            translate(transResult.value?.sourceText ?: "")
        }

        targetLangLiveData.observeForever {
            targetLang = it
            translate(transResult.value?.sourceText ?: "")
        }

        transResult.observeForever {
            updateCollectState()
        }
        isCollected.observeForever {
            isCollectedForUI = it
        }
    }

    //主界面重新加载时刷新收藏的状态，用于在收藏界面取消收藏后主界面的同步
    fun updateCollectState() {
        val deferred = coroutineManager.async {
            withContext(IO) {
                mainRepository.getCollectionById(transResult.value?.id ?: "")
            }
        }
        coroutineManager.launch {
            withContext(Main) {
                val entity = deferred.await()
                isCollected.value = entity != null
            }
        }
    }

    fun getAllLanguage(): LiveData<List<LanguageEntity>> {
        return mainRepository.languageRepository.getAll()
    }

    //收集各种信息/错误状态，用于toast显示
    fun getToastMessage(): LiveData<String> = Transformations.map(toastMessage) {
        it
    }

    fun getSourceLanguage(): LiveData<LanguageEntity> = sourceLangLiveData
    fun getTargetLanguage(): LiveData<LanguageEntity> = targetLangLiveData

    fun setSourceLanguage(languageEntity: LanguageEntity) {
        coroutineManager.launch {
            withContext(IO) {
                mainRepository.setSourceLanguage(languageEntity)
            }
        }
    }


    /**
     * 翻译
     */
    fun translate(text: String) {
        if (text.isNotEmpty()) {
            if (lifecycle is MainFragmentLifeCycle) {
                mainRepository.translate(lifecycle.getCompositeDisposable(), text, sourceLang!!, targetLang!!) {
                    if (it.getTransErrCode() != TransErrCode.SUCCESS) {
                        setToastMessage(it.getTransErrCode().getMessage())
                    } else {
                        transResult.value = it
                    }
                }
            } else {
                IllegalArgumentException("LifeCycle is wrong,must be instance of MainFragmentLifecycle")
            }
        }
    }

    fun setTargetLanguage(languageEntity: LanguageEntity) {
        coroutineManager.launch {
            withContext(IO) {
                mainRepository.setTargetLanguage(languageEntity)
            }
        }
    }


    fun getTranslateResult(): LiveData<TranslateEntity> = Transformations.map(transResult) {
        it
    }

    /**
     * 朗读tts
     */
    fun speak() {
        val textToSpeak = transResult.value?.transText ?: ""
        if (textToSpeak.isEmpty()) {
            setToastMessage(getStringById(R.string.not_find_text_to_tts))
            return
        }
        if (checkIfSupportTts()) {
            mainRepository.speak(textToSpeak) {
                when (it) {
                    TTSState.TTS_SPEAK_BEGIN -> {
                        coroutineManager.launch {
                            withContext(Main) {
                                isSpeaking.value = true
                            }
                        }
                    }
                    TTSState.TTS_SPEAK_COMPLETE -> {
                        coroutineManager.launch {
                            withContext(Main) {
                                isSpeaking.value = false
                            }
                        }
                    }
                    TTSState.TTS_ERROR -> {
                        ZLogger.e("TTS_ERROR:${it.name}")
                        coroutineManager.launch {
                            withContext(Main) {
                                isSpeaking.value = false
                                setToastMessage(getStringById(R.string.error_retry))
                            }
                        }
                    }
                    else -> {
                        ZLogger.d("speak callback -> ${it.name}")
                    }
                }
            }
        } else {
            setToastMessage(getStringById(R.string.not_support))
        }
    }

    fun speakWithProgress(str: String, callback: (String, Int) -> Unit) {
        if (checkIfSupportTts()) {
            mainRepository.speakWithProgress(str, callback)
        } else {
            setToastMessage(getStringById(R.string.not_support))
        }
    }

    fun exchangeLanguage() {
        val tempSourceLang = sourceLang
        setSourceLanguage(targetLang!!)
        setTargetLanguage(tempSourceLang!!)
    }

    //判断当前语言是否支持tts
    private fun checkIfSupportTts(): Boolean {
        return targetLang?.id in SUPPORT_TTS_LIST
    }

    fun isSpeakingLiveData(): LiveData<Boolean> = Transformations.map(isSpeaking) {
        it
    }

    fun isCollectedLiveData(): LiveData<Boolean> = Transformations.map(isCollected) {
        it
    }

    fun isCollectedForUI() = isCollectedForUI

    //收藏
    fun setCollected(flag: Boolean) {
        if (flag) {
            val entity = transResult.value
            if (entity != null) {
                entity.date = Calendar.getInstance()
                coroutineManager.launch {
                    withContext(IO) {
                        mainRepository.collected(entity)
                    }
                }
                ZLogger.d("collected success")
                setToastMessage(getStringById(R.string.collect_success))
                isCollected.value = true
            } else {
                setToastMessage(getStringById(R.string.collect_fail))
            }
        } else {
            coroutineManager.launch {
                withContext(IO) {
                    mainRepository.unCollect(transResult.value?.id ?: "")
                }
            }
            setToastMessage(getStringById(R.string.collect_cancel))
            isCollected.value = false
        }
    }

    //清理工作
    override fun onCleared() {
        super.onCleared()
        job.cancel()
        mainRepository.onCleared()
    }

    /**
     * 开始语音识别
     */
    fun recognizeStart(callback: (String, Int) -> Unit) {
        var tempRecognizeResult = ""
        var finalRecognizeResult = ""
        var result = ""

        var volume = 0
        mainRepository.recognizeStart {
            if (it.error == 0) {
                when (it.state) {
                    RecognizeState.ASR_BEGIN,
                    RecognizeState.ASR_READY -> {
                    }
                    RecognizeState.ASR_PARTIAL -> {
                        when (it.recognizeText?.result_type) {
                            "partial_result" -> {
                                tempRecognizeResult = it.recognizeText?.best_result ?: ""
                            }
                            "final_result" -> {
                                tempRecognizeResult = ""
                                finalRecognizeResult += it.recognizeText?.best_result
                            }
                        }
                        result = finalRecognizeResult + tempRecognizeResult
                    }
                    RecognizeState.ASR_VOLUME -> {
                        volume = it.recognizeVolume?.volumePercent ?: 0
                    }
                    RecognizeState.ASR_EXIT,
                    RecognizeState.ASR_STOP -> {
                    }
                    else -> {
                    }
                }
                callback.invoke(result, volume)
            } else {
                setToastMessage(getStringById(R.string.recognize_error))
            }
        }
    }

    fun recognizeStop() {
        mainRepository.recognizeStop()
    }

    private fun setToastMessage(msg: String) {
        toastMessage.value = msg
    }

    fun clearTransText() {
        transResult.value = null
    }

    fun clearToastMessage() {
        toastMessage.value = null
    }
}

