package com.beta.yihao.translite.ui

import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.text.Editable
import android.view.View
import com.beta.yihao.translite.AppContext
import com.beta.yihao.translite.R
import com.beta.yihao.translite.databinding.FragmentMainLayoutBinding
import com.beta.yihao.translite.viewmodels.TransMainViewModel

/**
 * @Author yihao
 * @Description 主界面事件处理
 * @Date 2019/1/16-17:04
 * @Email yihaobeta@163.com
 */

class TransMainFragmentEventHandler(
    private var binding: FragmentMainLayoutBinding,
    private val viewModel: TransMainViewModel
) {
    private var exchangeAnim: AnimatorSet

    init {
        binding.isNoText = true
        exchangeAnim = AnimatorInflater.loadAnimator(AppContext, R.animator.animatorset_exchange) as AnimatorSet
        exchangeAnim.setTarget(binding.includeTransHeaderLayout.mExchangeButton)
    }

    fun handleClearBtnClick(view: View) {
        binding.includeTransMainLayout.mTransTextEditText.text.clear()
    }

    fun handleEtOnChange(s: CharSequence, start: Int, before: Int, count: Int) {
        if (s.isEmpty()) {
            viewModel.clearTransText()
        } else {
            viewModel.translate(s.toString())
        }
    }

    fun handleEtAfterChange(s: Editable?) {
        binding.isNoText = s.toString().isEmpty()
    }

    fun handleCollectBtn(view: View) {
        viewModel.setCollected(!viewModel.isCollectedForUI())
    }

    fun handleReadBtn(view: View) {
        viewModel.speak()
    }

    fun handleExchangeBtn(view: View) {
        binding.includeTransMainLayout.mTransTextEditText.text.clear()
        exchangeAnim.start()
        viewModel.exchangeLanguage()
    }
}