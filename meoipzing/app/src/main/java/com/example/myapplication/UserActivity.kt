package com.example.myapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.layout_user.*

class UserActivity : AppCompatActivity() {
    private val allEntries : MutableMap<String, *> = MyApplication.prefs.getAll()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_user)

        var star_sum = 0F
        var star_num = 0F
        if (allEntries.isEmpty()) {
            star_num = 1F
        } else {
            for ((index, value) in allEntries) {
                star_sum += MyApplication.prefs.getFloat(index, 0F)
                star_num += 1F
            }
        }
        val user_sens = star_sum / star_num
        val userImageStarId = R.drawable.person1_hot

        if(user_sens < -0.6){
            userStatesImage.setImageResource(userImageStarId)
            userState.text = "더위를 많이 타는 체질입니다"
        }
        else if(user_sens >= -0.6 && user_sens <= -0.2){
            userStatesImage.setImageResource(userImageStarId+1)
            userState.text = "더위를 약간 타는 체질입니다"
        }
        else if(user_sens > -0.2 && user_sens < 0.2){
            userStatesImage.setImageResource(userImageStarId+2)
            userState.text = "일반적인 체질입니다"
        }
        else if(user_sens >= 0.2 && user_sens <= 0.6){
            userStatesImage.setImageResource(userImageStarId+3)
            userState.text = "추위를 약간 타는 체질입니다"
        }
        else{
            userStatesImage.setImageResource(userImageStarId+4)
            userState.text = "추위를 많이 타는 체질입니다"
        }
    }
}