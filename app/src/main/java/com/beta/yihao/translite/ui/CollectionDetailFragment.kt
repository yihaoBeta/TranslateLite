package com.beta.yihao.translite.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.beta.yihao.translite.databinding.FragmentCollectDetailLayoutBinding
import com.beta.yihao.translite.utils.ViewModelFactory
import com.beta.yihao.translite.utils.ZLogger
import com.beta.yihao.translite.viewmodels.CollectionDetailViewModel

/**
 * @Author yihao
 * @Description 收藏详情Fragment
 * @Date 2019/1/22-17:19
 * @Email yihaobeta@163.com
 */
class CollectionDetailFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = FragmentCollectDetailLayoutBinding.inflate(inflater, container, false)
        val id = CollectionDetailFragmentArgs.fromBundle(arguments!!).id
        val factory = ViewModelFactory.buildCollectionDetailViewModelFactory(id, context!!)
        val viewModel = ViewModelProviders.of(this, factory).get(CollectionDetailViewModel::class.java)
        bindAndObserverData(binding, viewModel)
        return binding.root
    }

    //绑定数据
    private fun bindAndObserverData(binding: FragmentCollectDetailLayoutBinding, viewModel: CollectionDetailViewModel) {
        viewModel.getCollectionDetail().observe(this, Observer {
            if (it == null) {
                ZLogger.d("non data")
                this.findNavController().popBackStack()
            } else {
                binding.isCollect = true
                binding.collectionDetail = it
                binding.eventHandler = CollectionFragmentEventHandler(viewModel, binding)
                binding.sourceLangId = it.sourLang.id
                binding.targetLangId = it.targetLang.id
            }
        })

        viewModel.sourceSpeakingState().observe(this, Observer {
            binding.isSourceSpeaking = it
        })

        viewModel.targetSpeakingState().observe(this, Observer {
            binding.isTargetSpeaking = it
        })
    }
}