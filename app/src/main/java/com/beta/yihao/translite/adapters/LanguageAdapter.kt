package com.beta.yihao.translite.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.beta.yihao.translite.data.entity.LanguageEntity
import com.beta.yihao.translite.databinding.ListItemLanguageBinding
import com.beta.yihao.translite.viewmodels.TransMainViewModel

/**
 * @Author yihao
 * @Description 语言列表的适配器类
 * @Date 2019/1/14-19:22
 * @Email yihaobeta@163.com
 */
class LanguageAdapter(private val isSource: Boolean, private val viewModel: TransMainViewModel) :
    ListAdapter<LanguageEntity, LanguageAdapter.ViewHolder>(LanguageDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ListItemLanguageBinding.inflate(
                LayoutInflater.from(
                    parent.context
                ), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val language = getItem(position)
        holder.apply {
            bind(createOnClickListener(language), language)
            itemView.tag = language
        }
    }

    private fun createOnClickListener(languageEntity: LanguageEntity): View.OnClickListener {
        return View.OnClickListener {
            languageEntity.apply {
                if (isSource)
                    viewModel.setSourceLanguage(this)
                else
                    viewModel.setTargetLanguage(this)
            }
            it.findNavController().popBackStack()
        }
    }

    class ViewHolder(private val binding: ListItemLanguageBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(listener: View.OnClickListener, item: LanguageEntity) {
            binding.apply {
                clickListener = listener
                language = item
                executePendingBindings()
            }
        }
    }
}

private class LanguageDiffCallback : DiffUtil.ItemCallback<LanguageEntity>() {
    override fun areItemsTheSame(oldItem: LanguageEntity, newItem: LanguageEntity): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: LanguageEntity, newItem: LanguageEntity): Boolean {
        return oldItem == newItem
    }

}