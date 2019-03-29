package com.beta.yihao.translite.ui.view

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.Gravity
import com.beta.yihao.translite.R
import kotlinx.android.synthetic.main.recognizing_layout.*

/**
 * @Author yihao
 * @Description 语音识别提示框
 * @Date 2018/12/7-16:51
 * @Email yihaobeta@163.com
 */
class RecognizingDialog(context: Context) : AlertDialog(context, R.style.dialog) {

    private lateinit var mSoundWaveView: SoundWaveView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.recognizing_layout)
        mSoundWaveView = soundwaveView
        val window = this.window
        window!!.setGravity(Gravity.CENTER_HORIZONTAL)
        this.setCanceledOnTouchOutside(false)//设置点击Dialog外部任意区域关闭Dialog
    }

    fun updateValue(value: Int) {
        mSoundWaveView.setVolumeLevel(value)
    }

}
