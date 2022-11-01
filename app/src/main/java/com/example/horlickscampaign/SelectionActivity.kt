package com.example.horlickscampaign

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.horlickscampaign.databinding.ActivitySelectionBinding
import com.example.horlickscampaign.login.loginResponse.School
import com.example.horlickscampaign.login.loginResponse.SessionData
import com.google.gson.Gson

class SelectionActivity: AppCompatActivity() {

    lateinit var binding: ActivitySelectionBinding
    var sessionData: SessionData? = null
    lateinit var sharedPreferences: SharedPreferences
    var school:School? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySelectionBinding.inflate(layoutInflater)
        setContentView(binding.root)


        sharedPreferences = getSharedPreferences("user", Context.MODE_PRIVATE)
        sessionData = Gson().fromJson(sharedPreferences.getString("loginInfo",""),SessionData::class.java)
        school = Gson().fromJson(sharedPreferences.getString("savedSchool",""), School::class.java)
        binding.schoolName.text = school!!.name

        binding.userName.text = sessionData!!.full_name
        binding.team.text = sessionData!!.team_name

        binding.studentInfoBtn.setOnClickListener {
            startActivity(Intent(applicationContext, StudentFormActivity::class.java))
        }

        binding.guardianInfoBtn.setOnClickListener {
            startActivity(Intent(applicationContext, GuardianFormActivity::class.java))
        }
    }
}