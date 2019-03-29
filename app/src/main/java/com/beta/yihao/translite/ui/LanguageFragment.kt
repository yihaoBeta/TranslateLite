package com.beta.yihao.translite.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.beta.yihao.translite.adapters.LanguageAdapter
import com.beta.yihao.translite.databinding.LanguageLayoutBinding
import com.beta.yihao.translite.utils.ViewModelFactory
import com.beta.yihao.translite.viewmodels.TransMainViewModel

/**
 * @Author yihao
 * @Description 语言界面
 * @Date 2018/12/11-18:22
 * @Email yihaobeta@163.com
 */

class LanguageFragment : Fragment() {

    private lateinit var viewModel: TransMainViewModel
    private lateinit var binding: LanguageLayoutBinding
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = LanguageLayoutBinding.inflate(inflater, container, false)
        val factory = ViewModelFactory.buildTransMainViewModelFactory(null, this.context!!)
        viewModel = ViewModelProviders.of(this, factory).get(TransMainViewModel::class.java)
        val adapter = LanguageAdapter(LanguageFragmentArgs.fromBundle(arguments!!).isSource, viewModel)
        binding.mLanguageListView.adapter = adapter
        viewModel.getAllLanguage().observe(this, Observer {
            if (it != null) {
                adapter.submitList(it)
            }
        })
        return binding.root
    }
}

