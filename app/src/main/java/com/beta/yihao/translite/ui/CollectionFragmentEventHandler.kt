package com.beta.yihao.translite.ui

import android.view.View
import com.beta.yihao.translite.databinding.FragmentCollectDetailLayoutBinding
import com.beta.yihao.translite.utils.toastShort
import com.beta.yihao.translite.viewmodels.CollectionDetailViewModel

/**
 * 处理收藏界面中的事件逻辑
 */
class CollectionFragmentEventHandler(
    private val viewModel: CollectionDetailViewModel,
    private val databinding: FragmentCollectDetailLayoutBinding
) {

    fun handleFabButtonClick(view: View) {
        viewModel.unCollected()
        view.context.toastShort("已取消收藏")
    }

    fun handleSourceSpeak(view: View) {
        viewModel.speak(true)
    }

    fun handleTargetSpeak(view: View) {
        viewModel.speak(false)
    }
}