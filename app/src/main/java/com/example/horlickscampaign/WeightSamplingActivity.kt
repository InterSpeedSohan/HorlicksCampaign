package com.example.horlickscampaign

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import cn.pedant.SweetAlert.SweetAlertDialog
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.horlickscampaign.databinding.ActivityStudentFormBinding
import com.example.horlickscampaign.databinding.ActivityWeightSamplingBinding
import com.example.horlickscampaign.login.loginResponse.School
import com.example.horlickscampaign.login.loginResponse.SessionData
import com.example.horlickscampaign.utils.CustomUtility
import com.example.horlickscampaign.utils.GPSLocation
import com.example.horlickscampaign.utils.StaticTags
import com.google.gson.Gson
import org.json.JSONException
import org.json.JSONObject
import java.util.HashMap

class WeightSamplingActivity:AppCompatActivity() {
    var doubleBackToExitPressedOnce = false
    lateinit var binding: ActivityWeightSamplingBinding
    lateinit var sharedPreferences: SharedPreferences
    var school: School? = null
    var sessionData: SessionData? = null
    var sweetAlertDialog: SweetAlertDialog? = null


    private var gpsLocation: GPSLocation? = null
    private var isGPSEnabled: Boolean = false
    var presentLat: String? = null; var presentLon: String? = null; var presentAcc:String? = null


    var operatorList = arrayOf("017", "013", "019", "014", "016", "018", "015")
    var isCorrectPrimaryNumber: Boolean = false

    var studentClass: String = ""
    var studentSex: String = ""
    var guardianNumber: String = ""
    var studentName: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWeightSamplingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPreferences = getSharedPreferences("user", Context.MODE_PRIVATE)
        if(sharedPreferences.contains("savedSchool")){
            school = Gson().fromJson(sharedPreferences.getString("savedSchool",""), School::class.java)
            sessionData = Gson().fromJson(sharedPreferences.getString("loginInfo",""), SessionData::class.java)

            binding.schoolName.text = school!!.name

            binding.userName.text = sessionData!!.full_name
            binding.team.text = sessionData!!.team_name

            binding.selectSchoolBtn.setOnClickListener{
                sharedPreferences.edit().remove("savedSchool").apply()
                val intent = Intent(applicationContext, SchoolListActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(intent)
                finish()
            }

            //ask for gps
            CustomUtility.haveGpsEnabled(this)
            gpsLocation = GPSLocation(this)
            gpsLocation!!.GPS_Start()
            gpsLocation!!.setLocationChangedListener(object : GPSLocation.LocationChangedListener {
                override fun locationChangeCallback(lat: String?, lon: String?, acc: String?) {
                    presentLat = lat
                    presentLon = lon
                    presentAcc = acc
                }

            })



            binding.submitBtn.setOnClickListener{
                if(checkFields())
                {
                    sweetAlertDialog = SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                    sweetAlertDialog!!.titleText = "Are you sure"
                    sweetAlertDialog!!.setCancelButton("No", SweetAlertDialog.OnSweetClickListener {
                        sweetAlertDialog!!.dismissWithAnimation()
                    })
                    sweetAlertDialog!!.setConfirmButton("Yes", SweetAlertDialog.OnSweetClickListener {
                        sweetAlertDialog!!.dismissWithAnimation()
                        upload()
                    })
                    sweetAlertDialog!!.show()
                }
            }

        }
        else
        {
            //
        }

    }

    private fun clearForm()
    {

        binding.weightSampling.setText("")

    }

    private fun upload() {

        val sweetAlertDialog = SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE)
        sweetAlertDialog.titleText = "Loading"
        sweetAlertDialog.setCancelable(false)
        sweetAlertDialog.show()
        val queue = Volley.newRequestQueue(this)
        val url = StaticTags.BASE_URL + "activities/insert_sample_count.php"

        val sr: StringRequest = object : StringRequest(
            Method.POST, url,
            Response.Listener {
                sweetAlertDialog.dismiss()
               // Log.d("response:", it)
                try {

                    val jsonObject = JSONObject(it)
                    if (jsonObject.getBoolean("success")) {
                        CustomUtility.showSuccess(
                            this,
                            "","Success"
                        )
                        clearForm()
                    } else {
                        CustomUtility.showError(
                            this,
                            "Failed!",
                            jsonObject.getString("message")
                        )
                    }
                } catch (e: JSONException) {
                    CustomUtility.showError(this, e.message, "Failed")
                }

            },
            Response.ErrorListener {
                sweetAlertDialog.dismiss()
                CustomUtility.showError(this, "Network problem, try again", "Failed")
            }) {
            override fun getParams(): Map<String, String> {
                val params: MutableMap<String, String> = HashMap()

                school = Gson().fromJson(sharedPreferences.getString("savedSchool",""), School::class.java)
                sessionData = Gson().fromJson(sharedPreferences.getString("loginInfo",""),
                    SessionData::class.java)

                params["UserId"] = sessionData!!.user_id
                params["AppVersion"] = getString(R.string.version)
                params["WetSampleCount"] = binding.weightSampling.text.toString()
                params["SchoolId"] = school!!.id



                return params
            }

            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val params: MutableMap<String, String> = HashMap()
                params["Content-Type"] = "application/x-www-form-urlencoded"
                return params
            }
        }
        queue.add(sr)
    }





    private fun checkFields(): Boolean {
        if (CustomUtility.haveNetworkConnection(this)) {
            if (!CustomUtility.haveNetworkConnection(this)) {
                Toast.makeText(this, "No internet connection!", Toast.LENGTH_SHORT)
                    .show()
                return false
            } else if (binding.weightSampling.text.toString() == "") {
                Toast.makeText(this, "Please fill required fields!", Toast.LENGTH_SHORT)
                    .show()
                binding.weightSampling.error = "Required Field!"
                return false
            }
            /*
            else if (!isCorrectPrimaryNumber) {
                Toast.makeText(this, "Please insert guardian's phone number!", Toast.LENGTH_SHORT)
                    .show()
                return false
            }

             */
            return true
        } else {
            Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show()
            return false
        }

    }


    override fun onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed()
            return
        }
        this.doubleBackToExitPressedOnce = true
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show()
        Handler().postDelayed({ doubleBackToExitPressedOnce = false }, 2000)
    }
}