package com.beta.yihao.translite.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.beta.yihao.translite.data.entity.TranslateEntity
import com.beta.yihao.translite.repository.CollectRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch

/**
 * @Author yihao
 * @Description 收藏列表ViewModel
 * @Date 2019/1/19-19:32
 * @Email yihaobeta@163.com
 */
class CollectionViewModel(private val collectRepos: CollectRepository) : ViewModel() {


    //获取列表
    fun provideCollectionList(): LiveData<List<TranslateEntity>> {
        return collectRepos.getAllCollections()
    }

    //是否没有任何收藏，用于EmptyView显示
    fun hasCollections(): LiveData<Boolean> {
        return Transformations.map(provideCollectionList()) {
            it != null && it.isNotEmpty()
        }
    }

    //取消收藏
    fun unCollect(id: String) {
        CoroutineScope(IO).launch {
            collectRepos.unCollect(id)
        }
    }
}