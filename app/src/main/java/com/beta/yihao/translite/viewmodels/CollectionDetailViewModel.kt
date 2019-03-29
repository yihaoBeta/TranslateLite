package com.beta.yihao.translite.viewmodels

import android.text.TextUtils
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.beta.yihao.translite.data.TTSState
import com.beta.yihao.translite.data.entity.TranslateEntity
import com.beta.yihao.translite.repository.CollectionDetailRepository
import com.beta.yihao.translite.utils.ZLogger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

/**
 * @Author yihao
 * @Description 收藏详情ViewModel
 * @Date 2019/1/22-17:09
 * @Email yihaobeta@163.com
 */
class CollectionDetailViewModel(private val id: String, private val repository: CollectionDetailRepository) :
    ViewModel() {

    private var detail: MutableLiveData<TranslateEntity> = MutableLiveData()
    private var isSourceSpeaking = MutableLiveData<Boolean>()
    private var isTargetSpeaking = MutableLiveData<Boolean>()

    init {
        getDetail()
        isSourceSpeaking.value = false
        isTargetSpeaking.value = false
    }

    private fun getDetail() {
        val deferred = CoroutineScope(IO).async {
            repository.getCollectionDetail(id)
        }

        CoroutineScope(Main).launch {
            detail.value = deferred.await()
        }
    }

    fun getCollectionDetail(): LiveData<TranslateEntity> {
        return Transformations.map(detail) {
            it
        }
    }

    /**
     * 取消收藏
     */
    fun unCollected() {
        CoroutineScope(IO).launch {
            repository.unCollect(id)
            getDetail()
        }
    }

    /**
     * 用于判断当前tts的是source还是target
     */
    fun sourceSpeakingState(): LiveData<Boolean> {
        return Transformations.map(isSourceSpeaking) {
            it
        }
    }

    /**
     * 用于判断当前tts的是source还是target
     */
    fun targetSpeakingState(): LiveData<Boolean> {
        return Transformations.map(isTargetSpeaking) {
            it
        }
    }

    /**
     * tts朗读
     */
    fun speak(isSource: Boolean) {
        if (isSource) {
            val text: String? = detail.value?.sourceText
            if (TextUtils.isEmpty(text).not()) {

                repository.speak(text!!) {
                    CoroutineScope(Main).launch {
                        when (it) {
                            TTSState.TTS_SPEAK_BEGIN -> {
                                isSourceSpeaking.value = true
                            }
                            TTSState.TTS_ERROR -> {
                                isSourceSpeaking.value = false
                            }
                            TTSState.TTS_SPEAK_COMPLETE -> {
                                isSourceSpeaking.value = false
                            }
                            else -> {
                                ZLogger.d("TTS STATE:${it.name}")
                            }
                        }
                    }
                }
            }
        } else {
            val text: String? = detail.value?.transText
            if (TextUtils.isEmpty(text).not()) {
                isTargetSpeaking.value = true
                repository.speak(text!!) {
                    CoroutineScope(Main).launch {
                        when (it) {
                            TTSState.TTS_SPEAK_BEGIN -> {
                                isTargetSpeaking.value = true
                            }
                            TTSState.TTS_ERROR -> {
                                ZLogger.e("TTS STATE:${it.name}")
                                isTargetSpeaking.value = false
                            }
                            TTSState.TTS_SPEAK_COMPLETE -> {
                                isTargetSpeaking.value = false
                            }
                            else -> {
                                ZLogger.d("TTS STATE:${it.name}")
                            }
                        }
                    }
                }
            }
        }
    }
}