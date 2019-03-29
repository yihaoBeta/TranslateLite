package com.beta.yihao.translite.adapters

import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.beta.yihao.translite.AppContext
import com.beta.yihao.translite.R
import com.beta.yihao.translite.utils.SUPPORT_TTS_LIST
import java.text.SimpleDateFormat
import java.util.*

/**
 * @Author yihao
 * @Description 用于DataBinding的适配器类
 * @Date 2019/1/16-16:38
 * @Email yihaobeta@163.com
 */

@BindingAdapter("isGone")
fun bindIsGone(view: View, isGone: Boolean) {
    view.visibility = if (isGone) {
        View.GONE
    } else {
        View.VISIBLE
    }
}

@BindingAdapter("date")
fun bindDate(view: View, date: Calendar?) {
    if (date == null) return
    when (view) {
        is TextView -> {
            val format = SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.CHINA)
            view.text = view.context.getString(R.string.collect_date, format.format(date.time))
        }
        else -> {
            throw IllegalArgumentException("must be assigned to TextView")
        }
    }
}

@BindingAdapter("isSpeaking")
fun bindIsSpeaking(view: View, isSpeaking: Boolean) {
    when (view) {
        is ImageView -> {
            view.apply {
                isEnabled = if (isSpeaking) {
                    setImageDrawable(AppContext.resources.getDrawable(R.mipmap.speaking))
                    false
                } else {
                    setImageDrawable(
                        AppContext.resources.getDrawable(R.mipmap.speak)
                    )
                    true
                }
            }
        }
        is TextView -> {
            view.text = if (isSpeaking) {
                "正在朗读"
            } else {
                "朗读词条"
            }
        }
    }
}

@BindingAdapter("isCollected")
fun bindIsCollected(view: View, isCollected: Boolean) {
    when (view) {
        is ImageView -> {
            view.setImageDrawable(
                if (isCollected) {
                    AppContext.resources.getDrawable(R.mipmap.collected)
                } else {
                    AppContext.resources.getDrawable(R.mipmap.collect)
                }
            )
        }
        is TextView -> {
            view.text = if (isCollected) {
                "已收藏"
            } else {
                "收藏词条"
            }
        }
    }
}

@BindingAdapter("isSupportTts")
fun bindIsSupportTts(view: View, langId: String?) {
    when (view) {
        is ImageButton -> {
            view.visibility = if (langId in SUPPORT_TTS_LIST) {
                View.VISIBLE
            } else {
                View.GONE
            }
        }
        else -> {
        }
    }
}