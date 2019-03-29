package com.beta.yihao.translite.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.beta.yihao.translite.R
import com.beta.yihao.translite.databinding.FragmentVoiceRoleLayoutBinding
import com.beta.yihao.translite.utils.ViewModelFactory
import com.beta.yihao.translite.viewmodels.VoiceRoleViewModel

/**
 * 发声人选择界面
 */
class VoiceRoleFragment : Fragment() {
    private lateinit var binding: FragmentVoiceRoleLayoutBinding
    private lateinit var viewModel: VoiceRoleViewModel
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentVoiceRoleLayoutBinding.inflate(inflater, container, false)
        val factory = ViewModelFactory.buildVoiceRoleViewModelFactory(context!!)
        viewModel = ViewModelProviders.of(this, factory).get(VoiceRoleViewModel::class.java)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun initView() {
        binding.voiceGroup.setOnCheckedChangeListener { _, checkedId ->
            val mode = when (checkedId) {
                R.id.voiceMode0 -> 0
                R.id.voiceMode1 -> 1
                R.id.voiceMode2 -> 2
                R.id.voiceMode3 -> 3
                R.id.voiceMode4 -> 4
                else -> 0
            }
            viewModel.setVoiceMode(mode)
        }
    }

    override fun onResume() {
        super.onResume()
        binding.voiceGroup.check(binding.voiceGroup.getChildAt(viewModel.getVoiceMode()).id)
    }
}