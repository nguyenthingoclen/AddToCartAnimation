package com.example.addtocartanimation

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private var count = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val fLowerAdapter = FLowerAdapter(this, createData(), object : FLowerAdapter.OnClickListener{
            override fun onItemClickListener(imageView: ImageView) {
//                Toast.makeText(this@MainActivity, "OnItemClickListener", Toast.LENGTH_SHORT)
//                    .show()
//                scale(imageView)
                makeFlyAnimation(imageView)
//                val scaleAnimatorY: Animator = ObjectAnimator.ofFloat(imageView, View.SCALE_Y, 1.0f,1.0f,0.5f,0.5f)
//                val scaleAnimatorX: Animator = ObjectAnimator.ofFloat(imageView, View.SCALE_X, 1.0f,1.0f,0.5f,0.5f)
//                val animatorCircleSet = AnimatorSet()
//                animatorCircleSet.duration = 2000
//                animatorCircleSet.playTogether(scaleAnimatorX, scaleAnimatorY)
//                animatorCircleSet.start()
            }
        })
        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false)
        rvFlower.layoutManager = layoutManager
        rvFlower.adapter = fLowerAdapter
    }

    private fun createData(): MutableList<Int>{
        val flowers = mutableListOf<Int>()
        flowers.add(R.drawable.one)
        flowers.add(R.drawable.two)
        flowers.add(R.drawable.three)
        flowers.add(R.drawable.four)
        return flowers
    }

    private fun makeFlyAnimation(image: ImageView){

        val animationUtil = CircleAnimationUtil()
            .attachActivity(this)
            .setTargetView(image)
            .setMoveDuration(1000)
            .setDestView(cartImg)
            .setAnimationListener(object : Animator.AnimatorListener{
                override fun onAnimationRepeat(animation: Animator?) {

                }

                override fun onAnimationEnd(animation: Animator?) {
                    Toast.makeText(this@MainActivity, "onAnimationEnd", Toast.LENGTH_SHORT)
                        .show()

                }

                override fun onAnimationCancel(animation: Animator?) {

                }

                override fun onAnimationStart(animation: Animator?) {
                    Toast.makeText(this@MainActivity, "onAnimationStart", Toast.LENGTH_SHORT)
                        .show()

                }

            })
        animationUtil.startAnimation()

    }

    private fun scale(view: ImageView){
        val mScale = AnimatorSet()
        val sX = ObjectAnimator.ofFloat(view,View.SCALE_X,1.0f,1.0f,0.5f,0.5f)
        val sY = ObjectAnimator.ofFloat(view,View.SCALE_Y,1.0f,1.0f,0.5f,0.5f)
        mScale.playTogether(sX,sY)
        mScale.start()
    }
}