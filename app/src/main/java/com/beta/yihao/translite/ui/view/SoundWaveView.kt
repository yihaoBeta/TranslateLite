package com.beta.yihao.translite.ui.view

import android.animation.ValueAnimator
import android.content.Context
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PointF
import android.util.AttributeSet
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import com.beta.yihao.translite.R

/**
 * @Author yihao
 * @Description 用于语音识别的自定义view，根据用户的音量大小改变动画效果
 * @Date 2018/10/17-16:01
 * @Email yihaobeta@163.com
 */

class SoundWaveView : View {
    private lateinit var mPaint: Paint
    private lateinit var mPaintWave: Paint

    private var mRadius: Float = 0.0F
    private var mColor: Int = 0xffffff
    private lateinit var mContext: Context
    private var mPoint: PointF = PointF(0F, 0F)
    private var mVolumeLevel: Int = 0
    private var mLastVolume: Int = 0
    private var mMaxWaveRadius = mRadius

    private lateinit var valueAnimator: ValueAnimator
    private var mAnimatorValue: Int = 0
    private lateinit var mUpdateListener: ValueAnimator.AnimatorUpdateListener
    private val defaultDuration = 100


    constructor(context: Context) : super(context) {
        init(context)
    }

    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet) {
        init(context, attributeSet)
    }

    private fun init(context: Context, attributeSet: AttributeSet? = null) {
        mContext = context
        if (attributeSet != null) {
            val ta: TypedArray = mContext.obtainStyledAttributes(attributeSet, R.styleable.SoundWaveView)
            mRadius = ta.getFloat(R.styleable.SoundWaveView_radius, 50.0f)
            mMaxWaveRadius = mRadius * 4
            mColor = ta.getColor(R.styleable.SoundWaveView_color, 0x34ff44)
            ta.recycle()
        }

        mPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mPaint.color = Color.RED
        mPaint.style = Paint.Style.FILL

        mPaintWave = Paint(Paint.ANTI_ALIAS_FLAG)
        mPaintWave.color = Color.YELLOW
        mPaintWave.alpha = 200
        mPaintWave.style = Paint.Style.FILL

        valueAnimator = ValueAnimator.ofInt(0, 1)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mPoint.set(w.toFloat() / 2, h.toFloat() / 2)
        mMaxWaveRadius = mPoint.x
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        setMeasuredDimension(measureSpec(widthMeasureSpec), measureSpec(heightMeasureSpec))
    }

    override fun draw(canvas: Canvas?) {
        super.draw(canvas)


        //2.绘制声波圆
        canvas!!.drawCircle(mPoint.x, mPoint.y, mAnimatorValue * mMaxWaveRadius / 100, mPaintWave)
        //1.绘制中心圆
        canvas.drawCircle(mPoint.x, mPoint.y, mRadius, mPaint)
    }

    private fun measureSpec(heightMeasureSpec: Int): Int {
        var result: Int
        val specSize = View.MeasureSpec.getSize(heightMeasureSpec) //获取高的高度 单位 为px
        val specMode = View.MeasureSpec.getMode(heightMeasureSpec)//获取测量的模式
        //如果是精确测量，就将获取View的大小设置给将要返回的测量值
        if (specMode == View.MeasureSpec.EXACTLY) {
            result = specSize
        } else {
            result = 500
            //如果设置成wrap_content时，给高度指定一个值
            if (specMode == View.MeasureSpec.AT_MOST) {
                result = Math.min(result, specSize)
            }
        }
        return result
    }

    fun setVolumeLevel(value: Int) {
        this.mLastVolume = this.mVolumeLevel
        this.mVolumeLevel = value
        if (valueAnimator != null)
            valueAnimator.cancel()
        initAnimator(mLastVolume, mVolumeLevel)

    }


    private fun initAnimator(from: Int, to: Int) {
        valueAnimator = ValueAnimator.ofInt(from, to).setDuration(defaultDuration.toLong())
        valueAnimator.interpolator = AccelerateDecelerateInterpolator()
        mUpdateListener = ValueAnimator.AnimatorUpdateListener { animation ->
            mAnimatorValue = animation.animatedValue as Int
            //更改透明度
            mPaintWave.alpha = 235 * (100 - mAnimatorValue) / 100
            invalidate()
        }
        valueAnimator.addUpdateListener(mUpdateListener)
        valueAnimator.start()
    }
}
