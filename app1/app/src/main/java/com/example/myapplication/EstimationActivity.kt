package com.example.myapplication

import android.icu.text.SimpleDateFormat
import android.os.Build
import android.os.Bundle
import android.widget.RatingBar
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.layout_estimation.*
import java.util.*

class EstimationActivity : AppCompatActivity() {
    val now = System.currentTimeMillis()
    @RequiresApi(Build.VERSION_CODES.N)
    val simpleDateFormat = SimpleDateFormat("yyyy.MM.dd", Locale.KOREAN).format(now)!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_estimation)

        star_ratingBar.onRatingBarChangeListener = MyRatingBarChangListener()
        star_ratingBar.rating = 3.0F

        estimation_button.setOnClickListener{
            if(star_ratingBar.rating == 0F){
                Toast.makeText(applicationContext,"다시 평가해주세요",Toast.LENGTH_SHORT).show()
            }
            else{
                var num:Float = (star_ratingBar.rating * 0.5F) - 1.5F
                MyApplication.prefs.setFloat(simpleDateFormat,num)
                Toast.makeText(applicationContext,"평가 완료 되었습니다",Toast.LENGTH_SHORT).show()
            }
        }

    }
    inner class MyRatingBarChangListener:RatingBar.OnRatingBarChangeListener{
        override fun onRatingChanged(ratingBar: RatingBar?, rating: Float, fromUser: Boolean) {
            when(star_ratingBar.rating){
                0F -> star_text.text = "별점을 주세요"
                1.0F-> star_text.text = "매우 더움"
                2.0F-> star_text.text = "더움"
                3.0F-> star_text.text = "적당함"
                4.0F-> star_text.text = "추움"
                5.0F-> star_text.text = "매우 추움"
            }
        }
    }
}
