package com.beta.yihao.translite.viewmodels

import androidx.lifecycle.ViewModel
import com.beta.yihao.translite.repository.SettingsRepository
import com.beta.yihao.translite.repository.TtsRepository

/**
 * 更改发声角色ViewModel
 */
class VoiceRoleViewModel(private val settingsResp: SettingsRepository) : ViewModel() {


    fun getVoiceMode(): Int {
        return settingsResp.getVoiceMode()
    }

    fun setVoiceMode(mode: Int) {

        val voiceMode = if (mode > 4 || mode < 0) {
            0
        } else {
            mode
        }

        settingsResp.setVoiceMode(voiceMode)
        TtsRepository.getInstance().setVoiceMode(voiceMode)
    }
}