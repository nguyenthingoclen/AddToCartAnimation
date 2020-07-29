package com.example.addtocartanimation

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
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
                makeFlyAnimation(imageView)
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

        val destView= findViewById<RelativeLayout>(R.id.cartImg)
        val animationUtil = CircleAnimationUtil()
            .attachActivity(this)
            .setTargetView(image)
            .setMoveDuration(1000)
            .setDestView(destView)
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
}