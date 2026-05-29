package com.dfjk.retouch.photo

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

class MaskCanvasView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var baseBitmap: Bitmap? = null
    private var maskBitmap: Bitmap? = null
    private val maskCanvas = Canvas()

    private var brushSize = 30f
    private var isErasing = false
    private val maskPaint = Paint().apply {
        color = Color.WHITE
        style = Paint.Style.STROKE
        strokeLineCap = Paint.Cap.ROUND
        strokeLineJoin = Paint.Join.ROUND
    }

    private val eraserPaint = Paint().apply {
        color = Color.TRANSPARENT
        style = Paint.Style.STROKE
        strokeLineCap = Paint.Cap.ROUND
        strokeLineJoin = Paint.Join.ROUND
        xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
    }

    private val undoStack = mutableListOf<Bitmap>()
    private var lastX = 0f
    private var lastY = 0f

    fun setImage(bitmap: Bitmap) {
        baseBitmap = bitmap
        maskBitmap = Bitmap.createBitmap(bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)
        maskBitmap?.let { mask ->
            mask.eraseColor(Color.TRANSPARENT)
            maskCanvas.setBitmap(mask)
        }
        invalidate()
    }

    fun setBrushSize(size: Float) {
        brushSize = size
        maskPaint.strokeWidth = brushSize
        eraserPaint.strokeWidth = brushSize
    }

    fun setErasing(erasing: Boolean) {
        isErasing = erasing
    }

    fun undo() {
        if (undoStack.isNotEmpty()) {
            val prevMask = undoStack.removeAt(undoStack.size - 1)
            maskBitmap = prevMask
            maskCanvas.setBitmap(maskBitmap)
            invalidate()
        }
    }

    fun getMaskBitmap(): Bitmap? = maskBitmap

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        baseBitmap?.let {
            canvas.drawBitmap(it, 0f, 0f, null)
        }

        maskBitmap?.let {
            canvas.drawBitmap(it, 0f, 0f, null)
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.x
        val y = event.y

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                lastX = x
                lastY = y
                maskBitmap?.let {
                    undoStack.add(it.copy(it.config, false))
                }
            }
            MotionEvent.ACTION_MOVE -> {
                val paint = if (isErasing) eraserPaint else maskPaint
                maskCanvas.drawLine(lastX, lastY, x, y, paint)
                lastX = x
                lastY = y
                invalidate()
            }
            MotionEvent.ACTION_UP -> {
                // Draw completed
            }
        }

        return true
    }
}