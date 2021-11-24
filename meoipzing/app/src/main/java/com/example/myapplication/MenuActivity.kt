package com.example.myapplication

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import kotlinx.android.synthetic.main.layout_menu.*

class MenuActivity : AppCompatActivity() {
    private var PERMISSION_ID = 1000
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_menu)

        if(CheckPerm()){
            //허용권한 확인
        }
        else{
            RequestPerm()
        }

        cloth_recommendation.setOnClickListener {
            val intent = Intent(this,ClothActivity::class.java)
            startActivity(intent)
        }
        estimation.setOnClickListener{
            val intent = Intent(this,EstimationActivity::class.java)
            startActivity(intent)
        }
        user_sensitivity.setOnClickListener{
            val intent = Intent(this,UserActivity::class.java)
            startActivity(intent)
        }
    }
    //허용권한 확인
    private fun CheckPerm():Boolean{
        if(
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED ||
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)== PackageManager.PERMISSION_GRANTED
        ){
            return true
        }
        return false
    }
    //허용요청
    private fun RequestPerm(){
        ActivityCompat.requestPermissions(
            this, arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION),
            PERMISSION_ID
        )
    }
}

