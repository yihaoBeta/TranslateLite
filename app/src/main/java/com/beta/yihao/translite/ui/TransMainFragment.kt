package com.beta.yihao.translite.ui

import android.annotation.SuppressLint
import android.app.Service
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import com.beta.yihao.translite.databinding.FragmentMainLayoutBinding
import com.beta.yihao.translite.lifecycler.MainFragmentLifeCycle
import com.beta.yihao.translite.ui.view.RecognizingDialog
import com.beta.yihao.translite.utils.ViewModelFactory
import com.beta.yihao.translite.utils.ZLogger
import com.beta.yihao.translite.viewmodels.TransMainViewModel

/**
 * @Author yihao
 * @Description 主界面
 * @Date 2019/1/13-17:08
 * @Email yihaobeta@163.com
 */
class TransMainFragment : Fragment() {
    private lateinit var binding: FragmentMainLayoutBinding
    private lateinit var viewModel: TransMainViewModel
    private val recognizeDialog by lazy {
        RecognizingDialog(context!!)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        ZLogger.d("onCreateView")
        val lifecycleObserver = MainFragmentLifeCycle()
        lifecycle.addObserver(lifecycleObserver)
        binding = FragmentMainLayoutBinding.inflate(inflater, container, false)
        val factory = ViewModelFactory.buildTransMainViewModelFactory(lifecycleObserver, context!!)
        viewModel = ViewModelProviders.of(this, factory).get(TransMainViewModel::class.java)
        initView()
        bindData()
        return binding.root
    }

    private fun bindData() {
        viewModel.updateCollectState()
        viewModel.getToastMessage().observe(this, Observer {
            if (TextUtils.isEmpty(it).not()) {
                Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                viewModel.clearToastMessage()
            }
        })

        viewModel.getSourceLanguage().observe(this, Observer {
            if (it != null)
                binding.sourceLanguage = it
        })

        viewModel.getTargetLanguage().observe(this, Observer {
            if (it != null) {
                binding.targetLanguage = it
            }
        })

        viewModel.getTranslateResult().observe(this, Observer {
            binding.transResult = it
        })

        viewModel.isSpeakingLiveData().observe(this, Observer {
            binding.isSpeaking = it
        })

        viewModel.isCollectedLiveData().observe(this, Observer {
            ZLogger.d("isCollect : $it")
            binding.isCollected = it
        })

    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initView() {
        binding.event = TransMainFragmentEventHandler(binding, viewModel)
        binding.includeTransHeaderLayout.mSourceLayout.setOnClickListener {
            val args = TransMainFragmentDirections.mainToLanguage(true)
            Navigation.findNavController(view!!).navigate(args)
        }

        binding.includeTransHeaderLayout.mTargetLayout.setOnClickListener {
            val args = TransMainFragmentDirections.mainToLanguage(false)
            Navigation.findNavController(view!!).navigate(args)
        }


        binding.includeTransBottomLayout.mRecognizeBtn.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    recognizeDialog.show()
                    viewModel.recognizeStart { result, volume ->
                        var finalVolume = volume * 2
                        if (finalVolume > 100) {
                            finalVolume = 100
                        }
                        recognizeDialog.updateValue(finalVolume)
                        if (result.isNotEmpty()) {
                            binding.includeTransMainLayout.mTransTextEditText.setText(result)
                        }
                    }
                    true
                }
                MotionEvent.ACTION_CANCEL,
                MotionEvent.ACTION_UP -> {
                    viewModel.recognizeStop()
                    if (recognizeDialog.isShowing)
                        recognizeDialog.dismiss()
                    true
                }
                else -> {
                    false
                }
            }


        }

        binding.includeTransMainLayout.mTransTextEditText.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                val inputMethod = context!!.getSystemService(Service.INPUT_METHOD_SERVICE) as InputMethodManager
                if (inputMethod.isActive) {
                    inputMethod.hideSoftInputFromWindow(v.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
                }
            }
        }
    }
}
