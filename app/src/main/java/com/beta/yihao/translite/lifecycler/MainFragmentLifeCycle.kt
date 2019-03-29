package com.beta.yihao.translite.lifecycler

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.beta.yihao.translite.repository.RecognizeRepository
import com.beta.yihao.translite.repository.TtsRepository
import com.beta.yihao.translite.utils.ZLogger
import io.reactivex.disposables.CompositeDisposable

/**
 * @Author yihao
 * @Description 生命周期管理
 * @Date 2019/1/16-15:58
 * @Email yihaobeta@163.com
 */
class MainFragmentLifeCycle : LifecycleObserver {
    private var co: CompositeDisposable = CompositeDisposable()
    private var ttsRepos: TtsRepository? = null
    private var recognizeRepository: RecognizeRepository? = null

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun initEngines() {
        ZLogger.d("ON_START")
        ttsRepos?.init()
        recognizeRepository?.init()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun destory() {

    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun releaseEngines() {
        ZLogger.d("ON_STOP")
        ttsRepos?.stop()
        ttsRepos?.release()
        recognizeRepository?.stop()
        recognizeRepository?.release()
        co.clear()
    }

    fun getCompositeDisposable(): CompositeDisposable {
        return co
    }

    fun setTtsRepos(ttsRepository: TtsRepository) {
        this.ttsRepos = ttsRepository
    }

    fun setRecognizeRepos(recognizeRepository: RecognizeRepository) {
        this.recognizeRepository = recognizeRepository
    }
}