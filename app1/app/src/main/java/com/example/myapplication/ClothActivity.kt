package com.example.myapplication

import android.Manifest
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.layout_cloth.*
import org.json.JSONObject
import java.lang.Exception
import java.net.URL
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Executors
import kotlin.math.abs
import kotlin.math.pow


class ClothActivity : AppCompatActivity() {
    val myExecutor = Executors.newSingleThreadExecutor()
    val myHandler = Handler(Looper.getMainLooper())

    private val allEntries : MutableMap<String, *> = MyApplication.prefs.getAll()
    var factor =0.0
    var user_factor=0.0

    val long_now = System.currentTimeMillis()
    val cur_date = SimpleDateFormat("yyyy년 MM월 dd일", Locale.KOREAN).format(long_now)
    val cal:Calendar = Calendar.getInstance()
    val cal2 = cal.add(Calendar.DATE,-1)
    val yesterday = cal.time
    var dNX = 0.0
    var dNY = 0.0

    val resultBasetime = { t : String ->
        if(t.toInt() < 300) "2300"
        else if (t.toInt() >= 300 && t.toInt() < 600) "0200"
        else if (t.toInt() >= 600 && t.toInt() < 900) "0500"
        else if (t.toInt() >= 900 && t.toInt() < 1200) "0800"
        else if (t.toInt() >= 1200 && t.toInt() < 1500) "1100"
        else if (t.toInt() >= 1500 && t.toInt() < 1800) "1400"
        else if (t.toInt() >= 1800 && t.toInt() < 2100) "1700"
        else if (t.toInt() >= 2100 && t.toInt() < 2400) "2000"
        else "2300"
    }
    val resultBasedate = { t : String->
        if( t == "2300") SimpleDateFormat("yyyyMMdd", Locale.KOREAN).format(yesterday)
        else SimpleDateFormat("yyyyMMdd", Locale.KOREAN).format(long_now)
    }

    val KEY: String = "zermksAIRj%2BjSA4qXmTMtwB56UEA7mUA4tK5YYJMqT5ff26baEyM8Xs57%2BbcDUK%2Bb4pQ25rdEE2b33Lhy6CeZw%3D%3D"
    var num_of_rows = 50 // 몇개 데이터 행을 가져올지
    var page_no = 1
    var base_time = resultBasetime((SimpleDateFormat("HH", Locale.KOREAN).format(long_now))+"00")
    var base_date = resultBasedate(base_time).toInt()
    var nx = "55" // 위도값(동적으로 받아올 예정)
    var ny = "127" // 경도값(동적으로 받아올 예정)
    var timenow = (SimpleDateFormat("HH", Locale.KOREAN).format(long_now))+"00"
    var today = (SimpleDateFormat("yyyyMMdd", Locale.KOREAN).format(long_now)).toInt()

    lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    private var PERMISSION_ID = 1000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_cloth)

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        getLastLocation() // 위치 받아오는 함수

        current_date.text = cur_date

    }

    //여기부터 위치 받아오는데 필요한 함수들
    @SuppressLint("MissingPermission")
    private fun getLastLocation(){
        if(CheckPerm()){
            if(isLocatioonEnabled()){
                //실제로 위치 받는곳
                fusedLocationProviderClient.lastLocation.addOnCompleteListener { task->
                    var location=task.result
                    if(location == null){
                        GetNewLocation()
                    }else{
                        nx = abs(location.latitude.toInt()).toString()
                        ny = abs(location.longitude.toInt()).toString()
                        dNX = location.latitude
                        dNY = location.longitude
                        weatherTask() // 날씨 받아오는 함수
                    }
                }
            }else{
                Toast.makeText(this, "권한을 허용에 주십시요", Toast.LENGTH_SHORT).show()
            }
        }else{
            Toast.makeText(this, "권한을 허용에 주십시요", Toast.LENGTH_SHORT).show()
        }
    }

    @SuppressLint("MissingPermission")
    private fun GetNewLocation() {
        var locationRequest = com.google.android.gms.location.LocationRequest.create()
        locationRequest.priority = com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = 0
        locationRequest.fastestInterval = 0
        locationRequest.numUpdates = 1
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        fusedLocationProviderClient.requestLocationUpdates(
            locationRequest,locationCallback,Looper.myLooper()!!
        )
    }

    private val locationCallback = object : LocationCallback(){
        override fun onLocationResult(locationResult: LocationResult) {
            var lastLocation: Location = locationResult.lastLocation
            Log.d("Debug : ", "마지막 위치 : " + lastLocation.longitude.toString())
            nx = abs(lastLocation.latitude.toInt()).toString()
            ny = abs(lastLocation.longitude.toInt()).toString()
            dNX = lastLocation.latitude
            dNY = lastLocation.longitude
            weatherTask() // 날씨 받아오는 함수
        }
    }

    //허용권한 확인
    private fun CheckPerm():Boolean{
        if(
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED ||
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)==PackageManager.PERMISSION_GRANTED
        ){
            return true
        }
        return false
    }

    //locationService가 사용가능한지 확인
    private fun isLocatioonEnabled():Boolean{

        var locationManager:LocationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    //permission result checking
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == PERMISSION_ID){
            if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Log.d("Debug:", "권한이 있습니다")
            }
        }
    }

    //여기부터 날씨데이터 받아오는 함수들
    fun weatherTask() {
        myExecutor.execute {
            var response:String?
            try {
                response = URL("http://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getVilageFcst?serviceKey=$KEY&pageNo=$page_no&numOfRows=$num_of_rows&dataType=JSON&base_date=$base_date&base_time=$base_time&nx=$nx&ny=$ny")
                    .readText(Charsets.UTF_8)
            }
            catch (e: Exception) {
                response = null
            }
            myHandler.post {
                try {
                    val jsonObj = JSONObject(response)
                    val jsonObj2 = jsonObj.getJSONObject("response")
                    val jsonObj3 = jsonObj2.getJSONObject("body")
                    val jsonObj4 = jsonObj3.getJSONObject("items")
                    val jsonObj5 = jsonObj4.getJSONArray("item")
                    var num=0
                    for(i in 0 until num_of_rows){
                        val item_list=jsonObj5.getJSONObject(i)
                        val list_time=item_list.getString("fcstTime")
                        val list_date=item_list.getInt("fcstDate")
                        if(list_time == timenow && list_date == today){
                            num=i
                            break
                        }
                    }
                    var tmp:String? = null
                    var wsd:String? = null
                    var sky:String? = null
                    var pty:String? = null
                    for(i in num until num_of_rows){
                        val item = jsonObj5.getJSONObject(num)
                        when(item.getString("category")){
                            "TMP" -> tmp = item.getString("fcstValue")
                            "WSD" -> wsd = item.getString("fcstValue")
                            "SKY" -> sky = item.getString("fcstValue")
                            "PTY" -> pty = item.getString("fcstValue")
                        }
                        val list_time=item.getString("fcstTime")
                        if(list_time != timenow)
                            break
                        num++
                    }

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

                    factor = 13.12 + 0.6215 * (tmp!!.toDouble()) - 11.37 * (wsd!!.toDouble() / 3.6).pow(0.16) + 0.3965 * (wsd.toDouble() / 3.6).pow(0.16) * (tmp.toDouble())
                    user_factor = factor - 3 * user_sens

                    val mGeoCoder =  Geocoder(applicationContext, Locale.KOREAN)
                    var mResultList = mGeoCoder.getFromLocation(dNX,dNY,1)
                    if(mResultList != null){
                        val testString = mResultList!!.get(0).getAddressLine(0).toString()
                        val splitArray = testString.split(" ")
                        val resultString = splitArray[1].plus(" ".plus(splitArray[2].plus(" ".plus(splitArray[3]))))
                        current_space.text = resultString
                    }

                    temp.text = tmp.plus("℃")                                    // 기온
                    Factor.text = factor.toInt().toString().plus("℃")            // 체감온도
                    UserFactor.text = user_factor.toInt().toString().plus("℃")   // 사용자 체감온도

                    val skyState = sky!!.toInt()
                    val ptyState = pty!!.toInt()
                    var sunORmoon = 0
                    val weatherImageStartId = R.drawable.weather_00_moon_clear
                    val clothImageStarId = R.drawable.n1_28_cloth1

                    if((timenow.toInt()) >= 600 && (timenow.toInt()) <= 1700) sunORmoon +=1

                    when(ptyState){
                        0-> {
                            when(skyState){
                                1-> WeatherImage.setImageResource(weatherImageStartId + sunORmoon)    //맑음 일출,일몰 버전
                                3-> WeatherImage.setImageResource(weatherImageStartId + 2 + sunORmoon) // 구름많음 일출,일몰 버전
                                4-> WeatherImage.setImageResource(weatherImageStartId + 4) // 흐림
                            }
                        }
                        1-> WeatherImage.setImageResource(weatherImageStartId + 5) // 비
                        2-> WeatherImage.setImageResource(weatherImageStartId + 6) // 비눈
                        3-> WeatherImage.setImageResource(weatherImageStartId + 7) // 눈
                        4-> WeatherImage.setImageResource(weatherImageStartId + 8) // 소나기
                    }

                    if(user_factor >= 28){                              //28도 ~
                        ClothImage1.setImageResource(clothImageStarId)
                        Cloth1_name.text="반팔"
                        ClothImage2.setImageResource(clothImageStarId + 1)
                        Cloth2_name.text="반바지"
                        ClothImage3.setImageResource(clothImageStarId + 2)
                        Cloth3_name.text="민소매"
                    }
                    else if (user_factor >= 23 && user_factor <28){     //23도 ~ 27도
                        ClothImage1.setImageResource(clothImageStarId + 3)
                        Cloth1_name.text="면바지"
                        ClothImage2.setImageResource(clothImageStarId + 4)
                        Cloth2_name.text="얇은 셔츠"
                        ClothImage3.setImageResource(clothImageStarId + 5)
                        Cloth3_name.text="반바지"
                    }
                    else if (user_factor >= 20 && user_factor <23){     //20도 ~ 22도
                        ClothImage1.setImageResource(clothImageStarId + 6)
                        Cloth1_name.text="긴팔 티"
                        ClothImage2.setImageResource(clothImageStarId + 7)
                        Cloth2_name.text="블라우스"
                        ClothImage3.setImageResource(clothImageStarId + 8)
                        Cloth3_name.text="면바지"
                    }
                    else if (user_factor >= 17 && user_factor <20){     //17도 ~ 19도
                        ClothImage1.setImageResource(clothImageStarId + 9)
                        Cloth1_name.text="가디건"
                        ClothImage2.setImageResource(clothImageStarId + 10)
                        Cloth2_name.text="맨투맨"
                        ClothImage3.setImageResource(clothImageStarId + 11)
                        Cloth3_name.text="긴 바지"
                    }
                    else if (user_factor >= 12 && user_factor < 17){    //12도 ~ 16도
                        ClothImage1.setImageResource(clothImageStarId + 12)
                        Cloth1_name.text="자켓"
                        ClothImage2.setImageResource(clothImageStarId + 13)
                        Cloth2_name.text="니트"
                        ClothImage3.setImageResource(clothImageStarId + 14)
                        Cloth3_name.text="청바지"
                    }
                    else if (user_factor >= 9 && user_factor < 12){     //9도 ~ 11도
                        ClothImage1.setImageResource(clothImageStarId + 15)
                        Cloth1_name.text="야상"
                        ClothImage2.setImageResource(clothImageStarId + 16)
                        Cloth2_name.text="기모바지"
                        ClothImage3.setImageResource(clothImageStarId + 17)
                        Cloth3_name.text="트렌치 코트"
                    }
                    else if (user_factor >= 5 && user_factor <9){       //5도 ~ 8도
                        ClothImage1.setImageResource(clothImageStarId + 18)
                        Cloth1_name.text="기모"
                        ClothImage2.setImageResource(clothImageStarId + 19)
                        Cloth2_name.text="가죽 옷"
                        ClothImage3.setImageResource(clothImageStarId + 20)
                        Cloth3_name.text="히트텍"
                    }
                    else{                                               // ~4도도
                        ClothImage1.setImageResource(clothImageStarId + 21)
                        Cloth1_name.text="패딩"
                        ClothImage2.setImageResource(clothImageStarId + 22)
                        Cloth2_name.text="목도리"
                        ClothImage3.setImageResource(clothImageStarId + 23)
                        Cloth3_name.text="두꺼운 자켓"
                    }

                }
                catch (e: Exception) { // 에러처리
                }
            }
        }


    }
}