package com.example.addtocartanimation

import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import androidx.appcompat.widget.AppCompatImageView

class CircleImageView(context: Context) : AppCompatImageView(context) {
    companion object {
        private val SCALE_TYPE: ScaleType = ScaleType.CENTER_CROP
        private val BITMAP_CONFIG = Bitmap.Config.ARGB_8888
        private const val COLORDRAWABLE_DIMENSION = 2

        private const val DEFAULT_BORDER_WIDTH = 2
        private const val DEFAULT_BORDER_COLOR = Color.BLACK
    }

    private val mDrawableRect = RectF()
    private val mBorderRect = RectF()

    private val mShaderMatrix = Matrix()
    private val mBitmapPaint = Paint()
    private val mBorderPaint = Paint()
    private var mBorderColor: Int = DEFAULT_BORDER_COLOR
    private var mBorderWidth: Int = DEFAULT_BORDER_WIDTH

    private var mBitmap: Bitmap? = null
    private var mBitmapShader: BitmapShader? = null
    private var mBitmapWidth = 0
    private var mBitmapHeight = 0

    private val mDrawableRadius = 0f
    private val mBorderRadius = 0f

    private var mColorFilter: ColorFilter? = null

    private var mReady = false
    private var mSetupPending = false
    init {
        scaleType = SCALE_TYPE
        mReady = true
        if (mSetupPending) {
            setup()
            mSetupPending = false
        }
    }

    override fun getScaleType(): ScaleType {
        return SCALE_TYPE
    }

    override fun setScaleType(scaleType: ScaleType?) {
        if (scaleType != SCALE_TYPE) {
            throw IllegalArgumentException(String.format(
                "ScaleType %s not supported.",
                scaleType
            ))
        }
    }

    override fun setAdjustViewBounds(adjustViewBounds: Boolean) {
        if (adjustViewBounds) throw IllegalArgumentException("adjustViewBounds not supported.")
    }

    override fun onDraw(canvas: Canvas?) {
        if (drawable == null) {
            return
        }

        canvas?.drawCircle(
            width / 2.toFloat(),
            height / 2.toFloat(),
            mDrawableRadius,
            mBitmapPaint
        )
        if (mBorderWidth != 0) {
            canvas?.drawCircle(
                width / 2.toFloat(),
                height / 2.toFloat(),
                mBorderRadius,
                mBorderPaint
            )
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        setup()
    }

    fun getBorderColor(): Int {
        return mBorderColor
    }

    fun setBorderColor(borderColor: Int) {
        if (borderColor == mBorderColor) {
            return
        }
        mBorderColor = borderColor
        mBorderPaint.color = mBorderColor
        invalidate()
    }

    fun getBorderWidth(): Int {
        return mBorderWidth
    }

    fun setBorderWidth(borderWidth: Int) {
        if (borderWidth == mBorderWidth) {
            return
        }
        mBorderWidth = borderWidth
        setup()
    }

    override fun setImageBitmap(bm: Bitmap?) {
        super.setImageBitmap(bm)
        mBitmap = bm
        setup()
    }

    override fun setImageDrawable(drawable: Drawable?) {
        super.setImageDrawable(drawable)
        mBitmap = getBitmapFromDrawable(drawable)
        setup()
    }

    override fun setImageResource(resId: Int) {
        super.setImageResource(resId)
        mBitmap = getBitmapFromDrawable(drawable)
        setup()
    }

    override fun setImageURI(uri: Uri?) {
        super.setImageURI(uri)
        mBitmap = getBitmapFromDrawable(drawable)
        setup()
    }

    override fun setColorFilter(cf: ColorFilter?) {
        if (cf === mColorFilter) {
            return
        }

        mColorFilter = cf
        mBitmapPaint.colorFilter = mColorFilter
        invalidate()
    }

    private fun getBitmapFromDrawable(drawable: Drawable?): Bitmap? {
        if (drawable == null) {
            return null
        }
        return if (drawable is BitmapDrawable) {
            drawable.bitmap
        } else try {
            val bitmap: Bitmap = if (drawable is ColorDrawable) {
                Bitmap.createBitmap(
                    COLORDRAWABLE_DIMENSION,
                    COLORDRAWABLE_DIMENSION,
                    BITMAP_CONFIG
                )
            } else {
                Bitmap.createBitmap(
                    drawable.intrinsicWidth,
                    drawable.intrinsicHeight,
                    BITMAP_CONFIG
                )
            }
            val canvas = Canvas(bitmap)
            drawable.setBounds(0, 0, canvas.width, canvas.height)
            drawable.draw(canvas)
            bitmap
        } catch (e: OutOfMemoryError) {
            null
        }
    }

    private fun setup(){
        if (!mReady){
            mSetupPending = true
            return
        }
        mBitmap?.let {
            mBitmapShader = BitmapShader(it,Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
            mBitmapPaint.isAntiAlias = true
            mBitmapPaint.shader = mBitmapShader

            mBorderPaint.style = Paint.Style.STROKE
            mBorderPaint.isAntiAlias = true
            mBorderPaint.color = mBorderColor
            mBorderPaint.strokeWidth = mBorderWidth.toFloat()
            mBitmapHeight = it.height
            mBitmapWidth = it.width

            mBorderRect.set(0.0f,0.0f, width.toFloat(), height.toFloat())
            mDrawableRect.set(mBorderWidth.toFloat(), mBorderWidth.toFloat(), mBorderRect.width() - mBorderWidth, mBorderRect.height() - mBorderWidth)
            updateShaderMatrix()
            invalidate()
        }
    }

    private fun updateShaderMatrix(){
        var scale = 0.0f
        var dx = 0.0f
        var dy = 0.0f

        mShaderMatrix.set(null)
        if (mBitmapWidth * mDrawableRect.height() > mDrawableRect.width() * mBitmapHeight) {
            scale = mDrawableRect.height() / mBitmapHeight.toFloat()
            dx = (mDrawableRect.width() - mBitmapWidth * scale) * 0.5f
        } else {
            scale = mDrawableRect.width() / mBitmapWidth.toFloat()
            dy = (mDrawableRect.height() - mBitmapHeight * scale) * 0.5f
        }

        mShaderMatrix.setScale(scale, scale)
        mShaderMatrix.postTranslate(
            (dx + 0.5f).toInt() + mBorderWidth.toFloat(),
            (dy + 0.5f).toInt() + mBorderWidth.toFloat()
        )

        mBitmapShader!!.setLocalMatrix(mShaderMatrix)

    }

}