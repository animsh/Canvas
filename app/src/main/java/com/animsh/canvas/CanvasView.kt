package com.animsh.canvas

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

/**
 * Created by animsh on 6/2/2021.
 */
class CanvasView(
    context: Context,
    attributeSet: AttributeSet
) : View(context, attributeSet) {

    private var mDrawPath: CustomPath? = null
    private var mCanvasBitmap: Bitmap? = null
    private var mDrawPaint: Paint? = null
    private var mCanvasPaint: Paint? = null
    private var mBrushSize: Float = 0.toFloat()
    private var mColor = Color.BLACK
    private var canvas: Canvas? = null
    private var mPathList = ArrayList<CustomPath>()

    init {
        setupCanvasView()
    }

    private fun setupCanvasView() {
        mDrawPaint = Paint()
        mDrawPath = CustomPath(mColor, mBrushSize)
        mDrawPaint.apply {
            this!!
            color = mColor
            style = Paint.Style.STROKE
            strokeJoin = Paint.Join.ROUND
            strokeCap = Paint.Cap.ROUND
        }
        mCanvasPaint = Paint(Paint.DITHER_FLAG)
        mBrushSize = 20.toFloat()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mCanvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        canvas = Canvas(mCanvasBitmap!!)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawBitmap(mCanvasBitmap!!, 0f, 0f, mCanvasPaint)
        for (availablePath in mPathList) {
            mDrawPaint!!.strokeWidth = availablePath.brushThickness
            mDrawPaint!!.color = availablePath.color
            canvas.drawPath(availablePath, mDrawPaint!!)
        }
        if (!mDrawPath!!.isEmpty) {
            mDrawPaint!!.strokeWidth = mDrawPath!!.brushThickness
            mDrawPaint!!.color = mDrawPath!!.color
            canvas.drawPath(mDrawPath!!, mDrawPaint!!)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        val touchX = event?.x
        val touchY = event?.y
        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                mDrawPath!!.color = mColor
                mDrawPath!!.brushThickness = mBrushSize
                mDrawPath!!.reset()
                mDrawPath!!.moveTo(touchX!!, touchY!!)
            }
            MotionEvent.ACTION_MOVE -> {
                mDrawPath!!.lineTo(touchX!!, touchY!!)
            }
            MotionEvent.ACTION_UP -> {
                mPathList.add(mDrawPath!!)
                mDrawPath = CustomPath(mColor, mBrushSize)
            }
            else -> return false
        }
        invalidate()
        return true

    }

    internal inner class CustomPath(
        var color: Int,
        var brushThickness: Float
    ) : Path() {

    }
}