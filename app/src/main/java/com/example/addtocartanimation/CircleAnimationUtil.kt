package com.example.addtocartanimation

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.TimeInterpolator
import android.annotation.SuppressLint
import android.app.Activity
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.BitmapDrawable
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.view.animation.LinearInterpolator
import android.widget.FrameLayout
import android.widget.ImageView
import java.lang.ref.WeakReference
import kotlin.math.pow

class CircleAnimationUtil() {
    companion object {
        private const val DEFAULT_DURATION = 1000
        private const val DEFAULT_DURATION_DISAPPEAR = 200
    }

    private var mTarget: View? = null
    private var mDest: View? = null

    private var originX = 0f
    private var originY = 0f
    private var destX = 0f
    private var destY = 0f

    private val mCircleDuration = DEFAULT_DURATION
    private var mMoveDuration = DEFAULT_DURATION
    private val mDisappearDuration = DEFAULT_DURATION_DISAPPEAR

    private var mContextReference: WeakReference<Activity>? = null
    private val mBorderWidth = 4
    private val mBorderColor = Color.BLACK

    //    private CircleLayout mCircleLayout;
    private var mBitmap: Bitmap? = null
//    private var mImageView: CircleImageView? = null
    private var mImageView: ImageView? = null
    private var mAnimationListener: Animator.AnimatorListener? = null

    fun attachActivity( activity: Activity): CircleAnimationUtil{
        mContextReference = WeakReference<Activity>(activity)
        return this
    }

    fun setTargetView(view: View): CircleAnimationUtil{
        mTarget = view
        mTarget?.let {
            setOriginRect(it.width.toFloat(), it.height.toFloat())
        }
        return this
    }

    fun setOriginRect(x: Float, y: Float): CircleAnimationUtil{
        originX = x
        originY = y
        return this
    }

    fun setDestRect(x: Float, y: Float): CircleAnimationUtil{
        destX = x
        destY = y
        return this
    }

    fun setDestView(view: View):CircleAnimationUtil{
        mDest = view
        mDest?.let {
            setDestRect(it.width.toFloat(), it.width.toFloat())
        }

        return this
    }

    fun setMoveDuration(duration: Int): CircleAnimationUtil{
        mMoveDuration = duration
        return this
    }

    fun startAnimation(){
//        getAvatarRevealAnimator().start()
        if (prepare()) {
            mTarget!!.visibility = View.VISIBLE
            getAvatarRevealAnimator().start()
        }
    }

    private fun prepare(): Boolean{
        if (mContextReference!!.get() != null) {
            val decoreView =
                mContextReference!!.get()!!.window.decorView as ViewGroup
            mTarget?.let {
                mBitmap = drawViewToBitmap(it, it.layoutParams.width, it.layoutParams.height)
            }
            if (mImageView == null) mImageView = ImageView(mContextReference!!.get()!!)
            mImageView?.let {
                it.setImageBitmap(mBitmap)
//                it.setBorderWidth(mBorderWidth)
//                it.setBorderColor(mBorderColor)
            }
            val src = IntArray(2)
            mTarget!!.getLocationOnScreen(src)
            val params =
                FrameLayout.LayoutParams(mTarget!!.width, mTarget!!.height)
            params.setMargins(src[0], src[1], 0, 0)
            if (mImageView?.parent == null) decoreView.addView(mImageView, params)
        }
        return true
    }

    @SuppressLint("ObjectAnimatorBinding")
    private fun getAvatarRevealAnimator(): AnimatorSet{
        val endRadius= Math.max(destX, destY) / 2
        val startRadius = Math.max(originX, originY)
        val scaleFactor = 0.5f

        val mRevealAnimator: Animator = ObjectAnimator.ofFloat(
            mImageView,
            "drawableRadius",
            startRadius,
            endRadius * 1.05f,
            endRadius * 0.9f,
            endRadius
        )
        mRevealAnimator.interpolator = AccelerateInterpolator()
        val scaleAnimatorY: Animator = ObjectAnimator.ofFloat(mImageView, View.SCALE_Y, 1.0f,1.0f,scaleFactor, scaleFactor)
        val scaleAnimatorX: Animator = ObjectAnimator.ofFloat(
            mImageView,
            View.SCALE_X,
            1.0f,
            1.0f,
            scaleFactor,
            scaleFactor
        )
        val animatorCircleSet = AnimatorSet()
        animatorCircleSet.duration = mCircleDuration.toLong()
        animatorCircleSet.playTogether(scaleAnimatorX, scaleAnimatorY)
        animatorCircleSet.addListener(object : Animator.AnimatorListener{
            override fun onAnimationRepeat(animation: Animator?) {

            }

            override fun onAnimationEnd(animation: Animator?) {
                Log.d("TAG: Util","onAnimationEnd")
                val src = IntArray(2)
                val dest = IntArray(2)
                val y = mTarget!!.y
                val x = mTarget!!.x
                Log.d("TAG: Util","onAnimationEnd x: $x, y: $y")
                mImageView!!.getLocationOnScreen(src)
                mDest!!.getLocationOnScreen(dest)
                val translatorX: Animator = ObjectAnimator.ofFloat(
                    mImageView,
                    View.X,
                    src[0].toFloat(),
                    dest[0].toFloat()
                )
                translatorX.interpolator =
                    TimeInterpolator { input ->
                        (-(input - 1.toDouble()).pow(2.0) + 1f).toFloat()
                    }
                val translatorY: Animator = ObjectAnimator.ofFloat(
                    mImageView,
                    View.Y,
                    src[1].toFloat(),
                    dest[1].toFloat()
                )
                translatorY.interpolator = LinearInterpolator()
                val animatorMoveSet = AnimatorSet()
                animatorMoveSet.playTogether(translatorX, translatorY)
                animatorMoveSet.duration = mMoveDuration.toLong()

                val animatorDisappearSet = AnimatorSet()
                val disappearAnimatorY: Animator =
                    ObjectAnimator.ofFloat(mImageView, View.SCALE_Y, scaleFactor, 0.0f)
                val disappearAnimatorX: Animator =
                    ObjectAnimator.ofFloat(mImageView, View.SCALE_X, scaleFactor, 0.0f)
                animatorDisappearSet.duration = mDisappearDuration.toLong()
                animatorDisappearSet.playTogether(disappearAnimatorX, disappearAnimatorY)

                val total = AnimatorSet()
                total.playSequentially(animatorMoveSet, animatorDisappearSet)
                total.addListener(object :Animator.AnimatorListener{
                    override fun onAnimationRepeat(animation: Animator?) {

                    }

                    override fun onAnimationEnd(animation: Animator?) {
                        Log.d("TAG: Util","onAnimationEnd2")
                        mAnimationListener?.onAnimationEnd(animation)
                        reset()

                    }

                    override fun onAnimationCancel(animation: Animator?) {

                    }

                    override fun onAnimationStart(animation: Animator?) {
                        Log.d("TAG: Util","onAnimationStart2")
                    }

                })
                total.start()
            }

            override fun onAnimationCancel(animation: Animator?) {

            }

            override fun onAnimationStart(animation: Animator?) {
                Log.d("TAG: Util","onAnimationStart")
                mAnimationListener?.onAnimationStart(animation)

            }

        })
        return animatorCircleSet
    }

    private fun reset(){
        mBitmap!!.recycle()
        mBitmap = null
        if (mImageView!!.parent != null) (mImageView!!.parent as ViewGroup).removeView(
            mImageView
        )
        mImageView = null
    }

    private fun drawViewToBitmap(view: View, width: Int, height: Int): Bitmap{
        val drawable = BitmapDrawable()
        val dest = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val c = Canvas(dest)
        drawable.bounds = Rect(0, 0, width, height)
        drawable.draw(c)
        view.draw(c)
        return dest
    }

    fun setAnimationListener(listener : Animator.AnimatorListener): CircleAnimationUtil{
        mAnimationListener = listener
        return this
    }
}