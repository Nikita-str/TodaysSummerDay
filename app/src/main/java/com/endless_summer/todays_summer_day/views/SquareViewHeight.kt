package com.endless_summer.todays_summer_day.views

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.TextView
import android.view.View.MeasureSpec
import android.widget.ImageView

class SquareViewHeight: androidx.appcompat.widget.AppCompatImageView {
    constructor(context: Context) : super(context) {}
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {}
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
            super(context, attrs, defStyleAttr) {
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec)
        val height = MeasureSpec.getSize(heightMeasureSpec)
        val size = height
        setMeasuredDimension(size, size) // make it square
    }
}