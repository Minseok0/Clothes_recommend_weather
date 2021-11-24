package com.example.myapplication


import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        start_button.setOnClickListener{
            val intent = Intent(this@MainActivity,MenuActivity::class.java)
            startActivity(intent)
        }
    }
}