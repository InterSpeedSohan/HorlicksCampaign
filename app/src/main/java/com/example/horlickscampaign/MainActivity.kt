package com.example.horlickscampaign

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.horlickscampaign.attendance.AttendanceActivity
import com.example.horlickscampaign.databinding.ActivityMainBinding
import com.example.horlickscampaign.login.loginResponse.School
import com.example.horlickscampaign.login.loginResponse.SessionData
import com.google.gson.Gson

class MainActivity: AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    var sessionData: SessionData? = null
    lateinit var sharedPreferences: SharedPreferences
    var school: School? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        sharedPreferences = getSharedPreferences("user", Context.MODE_PRIVATE)
        sessionData = Gson().fromJson(sharedPreferences.getString("loginInfo",""), SessionData::class.java)

        binding.userName.text = sessionData!!.full_name
        binding.team.text = sessionData!!.team_name

        binding.activityBtn.setOnClickListener {
            startActivity(Intent(applicationContext, SchoolListActivity::class.java))
        }

        binding.attendanceBtn.setOnClickListener {
            startActivity(Intent(applicationContext, AttendanceActivity::class.java))
        }
    }
}