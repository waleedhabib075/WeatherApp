package com.waleed.weatherapp


import android.content.ContentValues.TAG
import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.room.util.query
import com.waleed.weatherapp.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Tag
import java.util.Date
import java.util.Locale

//13ca2cd63132456f638c529774fb14e2

class MainActivity : AppCompatActivity() {
    private  val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        fetchWeatherData(cityName = "karachi")
        SearchCity()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }




    }

    private fun SearchCity() {
        val searchView  = binding.searchView
        searchView.setOnQueryTextListener(object :SearchView.OnQueryTextListener,
            android.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null){
                    fetchWeatherData(query)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }


        })
    }

    private fun fetchWeatherData(cityName: String) {
        val retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .build().create(apiInterface::class.java)
        val response  =  retrofit.getWeatherData(cityName, appid = "13ca2cd63132456f638c529774fb14e2" , units ="metric")
        response.enqueue(object :  Callback<weatherApp>{
            override fun onResponse(call: Call<weatherApp>, response: Response<weatherApp>) {
                val responseBody = response.body()
                if (response.isSuccessful && response != null){
                  val temperature = responseBody?.main?.temp.toString()
                    val humidity = responseBody?.main?.humidity
                    val weather= responseBody?.weather
                val windspeed = responseBody?.wind?.speed
                    val sunRise = responseBody?.sys?.sunrise
                    val sunSet = responseBody?.sys?.sunset
                    val seaLevel = responseBody?.main?.pressure
                    val condition= responseBody?.weather?.firstOrNull()?.main?: "unknown"
                    val maxTemp = responseBody?.main?.temp_max
                    val minTemp = responseBody?.main?.temp_min

                    binding.weather.text = condition
                    binding.temp.text = temperature
                  binding.condition.text = condition
                    binding.humidity.text ="$humidity %"

                    binding.MaxTemp.text= "$maxTemp"
                    binding.minTemp.text = "$minTemp"
                    binding.windSpeed.text = "$windspeed m/s"
                    binding.sunset.text = "$sunSet"
                    binding.Rise.text = "$sunRise"
                    binding.Sea.text ="$seaLevel nPa"
                    binding.Day.text = dayName(System.currentTimeMillis())
                        binding.date.text=date()
                        binding.CityName.text ="$cityName"
//                    Log.d(TAG, "onResponse: $temperature")
                }
            }

            override fun onFailure(call: Call<weatherApp>, t: Throwable) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun date(): String{
        val sdf =SimpleDateFormat("dd mm yyyy", Locale.getDefault())
        return      sdf.format(Date())
    }

fun dayName (timestamp: Long): String{
    val sdf =SimpleDateFormat("EEE", Locale.getDefault())
    return      sdf.format(Date())
}
}
