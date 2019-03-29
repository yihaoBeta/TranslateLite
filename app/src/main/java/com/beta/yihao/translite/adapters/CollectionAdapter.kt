package com.beta.yihao.translite.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.beta.yihao.translite.R
import com.beta.yihao.translite.data.entity.TranslateEntity
import com.beta.yihao.translite.databinding.ListItemCollectBinding
import com.beta.yihao.translite.ui.CollectionFragmentDirections
import com.beta.yihao.translite.utils.getStringById
import com.beta.yihao.translite.viewmodels.CollectionViewModel

/**
 * @Author yihao
 * @Description 收藏列表的Adapter
 * @Date 2019/1/19-19:11
 * @Email yihaobeta@163.com
 */

class CollectionAdapter(private val viewModel: CollectionViewModel) :
    ListAdapter<TranslateEntity, CollectionAdapter.VH>(CollectionDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        return VH(ListItemCollectBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val translateEntity = getItem(position)
        holder.apply {
            bind(createClickListener(translateEntity), createLongClickListener(translateEntity), translateEntity)
        }
    }

    private fun createLongClickListener(translateEntity: TranslateEntity): View.OnLongClickListener {
        return View.OnLongClickListener {
            AlertDialog.Builder(it.context)
                .setMessage(getStringById(R.string.delete_it))
                .setPositiveButton(getStringById(R.string.ok)) { dialog, _ ->
                    viewModel.unCollect(translateEntity.id)
                    dialog.dismiss()
                }
                .setNegativeButton(getStringById(R.string.cancel)) { dialog, _ ->
                    dialog.dismiss()
                }
                .create().show()
            true
        }
    }

    private fun createClickListener(translateEntity: TranslateEntity): View.OnClickListener {
        return View.OnClickListener {
            val toDetail = CollectionFragmentDirections.collectToDetail(translateEntity.id)
            it.findNavController().navigate(toDetail)
        }
    }

    class VH(private var binding: ListItemCollectBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(
            clickListener: View.OnClickListener,
            longClickListener: View.OnLongClickListener,
            translateEntity: TranslateEntity
        ) {
            binding.transEntity = translateEntity
            binding.longClickListener = longClickListener
            binding.clickListener = clickListener
        }
    }
}

private class CollectionDiffCallback : DiffUtil.ItemCallback<TranslateEntity>() {
    override fun areItemsTheSame(oldItem: TranslateEntity, newItem: TranslateEntity): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: TranslateEntity, newItem: TranslateEntity): Boolean {
        return oldItem == newItem
    }


}
