@file:Suppress("UNCHECKED_CAST")

package com.beta.yihao.translite.utils

import android.content.Context
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.beta.yihao.translite.data.MainDatabase
import com.beta.yihao.translite.repository.CollectRepository
import com.beta.yihao.translite.repository.CollectionDetailRepository
import com.beta.yihao.translite.repository.MainRepository
import com.beta.yihao.translite.repository.SettingsRepository
import com.beta.yihao.translite.viewmodels.CollectionDetailViewModel
import com.beta.yihao.translite.viewmodels.CollectionViewModel
import com.beta.yihao.translite.viewmodels.TransMainViewModel
import com.beta.yihao.translite.viewmodels.VoiceRoleViewModel

/**
 * @Author yihao
 * @Description VIewModel工厂类
 * @Date 2019/1/14-16:56
 * @Email yihaobeta@163.com
 */

object ViewModelFactory {
    fun buildTransMainViewModelFactory(lifecycle: LifecycleObserver?, context: Context): TransMainVMFactory {
        return TransMainVMFactory(lifecycle, MainRepository.getInstance(context, lifecycle))
    }

    fun buildCollectionViewModel(context: Context): CollectionVMFactory {
        return CollectionVMFactory(CollectRepository.getInstance(MainDatabase.getInstance(context).collectDao()))
    }

    fun buildCollectionDetailViewModelFactory(id: String, context: Context): CollectionDetailVMFactory {
        return CollectionDetailVMFactory(id, CollectionDetailRepository(MainDatabase.getInstance(context).collectDao()))
    }

    fun buildVoiceRoleViewModelFactory(context: Context): VoiceRoleVMFactory {
        return VoiceRoleVMFactory(SettingsRepository.getInstance(MainDatabase.getInstance(context).settingDao()))
    }
}

class TransMainVMFactory(private var lifecycle: LifecycleObserver?, private var repository: MainRepository) :
    ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T = TransMainViewModel(lifecycle, repository) as T
}

class CollectionVMFactory(private var repository: CollectRepository) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return CollectionViewModel(repository) as T
    }
}

class CollectionDetailVMFactory(private var id: String, private var repository: CollectionDetailRepository) :
    ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return CollectionDetailViewModel(id, repository) as T
    }
}

class VoiceRoleVMFactory(private var resp: SettingsRepository) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return VoiceRoleViewModel(resp) as T
    }
}