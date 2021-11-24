package com.example.myapplication

import android.app.Application
import android.content.Context

class MyApplication : Application() {
    companion object {
        lateinit var prefs: PrefsManager
    }

    override fun onCreate() {
        prefs = PrefsManager(applicationContext)
        super.onCreate()
    }


}

class PrefsManager(context: Context) {
    private val prefs = context.getSharedPreferences("EnstimationActivity", Context.MODE_PRIVATE)

    fun getFloat(key: String, defValue: Float) : Float {
        return prefs.getFloat(key, defValue)
    }

    fun setFloat(key: String, value: Float) {
        prefs.edit().putFloat(key, value).apply()
    }

    fun getAll() : MutableMap<String, *>{
        return prefs.all
    }

    fun clearAll() {
        prefs.edit().clear().apply()
    }
}