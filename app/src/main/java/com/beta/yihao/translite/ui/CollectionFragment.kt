package com.beta.yihao.translite.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.beta.yihao.translite.adapters.CollectionAdapter
import com.beta.yihao.translite.databinding.FragmentCollectionLayoutBinding
import com.beta.yihao.translite.utils.ViewModelFactory
import com.beta.yihao.translite.viewmodels.CollectionViewModel

/**
 * @Author yihao
 * @Description 收藏列表Fragment
 * @Date 2019/1/13-17:08
 * @Email yihaobeta@163.com
 */
class CollectionFragment : Fragment() {
    private lateinit var viewmodel: CollectionViewModel
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = FragmentCollectionLayoutBinding.inflate(inflater, container, false)
        val factory = ViewModelFactory.buildCollectionViewModel(context!!)
        viewmodel = ViewModelProviders.of(this, factory).get(CollectionViewModel::class.java)
        val adapter = CollectionAdapter(viewmodel)
        binding.collectListView.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        binding.collectListView.adapter = adapter
        bindingAndObserverData(binding, adapter)
        return binding.root
    }

    private fun bindingAndObserverData(binding: FragmentCollectionLayoutBinding, adapter: CollectionAdapter) {

        viewmodel.hasCollections().observe(this, Observer {
            binding.hasCollection = it
        })

        viewmodel.provideCollectionList().observe(this, Observer {
            if (it != null && it.isNotEmpty())
                adapter.submitList(it)
        })
    }
}
