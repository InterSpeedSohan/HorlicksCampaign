package com.example.horlickscampaign


import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.RadioGroup
import android.widget.Toast
import cn.pedant.SweetAlert.SweetAlertDialog
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.horlickscampaign.databinding.ActivityStudentFormBinding
import com.example.horlickscampaign.login.loginResponse.School
import com.example.horlickscampaign.login.loginResponse.SessionData
import com.example.horlickscampaign.utils.CustomUtility
import com.example.horlickscampaign.utils.GPSLocation
import com.example.horlickscampaign.utils.StaticTags
import com.google.gson.Gson
import org.json.JSONException
import org.json.JSONObject
import java.util.*

class StudentFormActivity : AppCompatActivity() {
    var doubleBackToExitPressedOnce = false
    lateinit var binding: ActivityStudentFormBinding
    lateinit var sharedPreferences: SharedPreferences
    var school:School? = null
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
        binding = ActivityStudentFormBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPreferences = getSharedPreferences("user",Context.MODE_PRIVATE)
        if(sharedPreferences.contains("savedSchool")){
            school = Gson().fromJson(sharedPreferences.getString("savedSchool",""),School::class.java)
            sessionData = Gson().fromJson(sharedPreferences.getString("loginInfo",""),SessionData::class.java)

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

            binding.sexRadioGroup.setOnCheckedChangeListener { radioGroup: RadioGroup, i: Int ->
                when (i) {
                    binding.male.id -> {
                        studentSex = "Male"
                    }
                    binding.female.id -> {
                        studentSex = "Female"
                    }

                }
            }

            binding.typeRadioClass.setOnCheckedChangeListener { radioGroup: RadioGroup, i: Int ->
                when (i) {
                    binding.one.id -> {
                        studentClass = "One"
                    }
                    binding.two.id -> {
                        studentClass = "Two"
                    }
                    binding.three.id -> {
                        studentClass = "Three"
                    }
                    binding.four.id -> {
                        studentClass = "Four"
                    }
                    binding.five.id -> {
                        studentClass = "Five"
                    }
                    binding.six.id -> {
                        studentClass = "Six"
                    }
                    binding.seven.id -> {
                        studentClass = "Seven"
                    }
                    binding.eight.id -> {
                        studentClass = "Eight"
                    }
                    binding.nine.id -> {
                        studentClass = "Nine"
                    }
                    binding.ten.id -> {
                        studentClass = "Ten"
                    }
                }
            }


            binding.contactPersonMobileNumber.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable) {}
                override fun beforeTextChanged(
                    s: CharSequence, start: Int,
                    count: Int, after: Int
                ) {
                }

                override fun onTextChanged(
                    s: CharSequence, start: Int,
                    before: Int, count: Int
                ) {
                    if(s.toString() == "")
                    {
                        isCorrectPrimaryNumber = false
                    }
                    else if (!isCorrectPhoneNumber(s.toString())) {
                        binding.contactPersonMobileNumber.error = "Number must be correct and unique"
                        isCorrectPrimaryNumber = false
                        //setCallGone()
                    }

                    else {
                        //isCorrectPrimaryNumber = true
                        guardianNumber = s.toString()
                        checkDuplicate(guardianNumber)

                    }

                }
            })


            binding.submitBtn.setOnClickListener{
                if(checkFields())
                {
                    sweetAlertDialog = SweetAlertDialog(this,SweetAlertDialog.WARNING_TYPE)
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
        binding.sexRadioGroup.clearCheck()
        studentSex = ""
        binding.typeRadioClass.clearCheck()
        studentClass = ""
        binding.studentName.setText("")
        studentName = ""
        binding.contactPersonMobileNumber.setText("")
        isCorrectPrimaryNumber = false
        guardianNumber = ""
    }

    private fun upload() {

        val sweetAlertDialog = SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE)
        sweetAlertDialog.titleText = "Loading"
        sweetAlertDialog.setCancelable(false)
        sweetAlertDialog.show()
        val queue = Volley.newRequestQueue(this)
        val url = StaticTags.BASE_URL + "activities/insert_activity.php"

        val sr: StringRequest = object : StringRequest(Method.POST, url,
            Response.Listener {
                sweetAlertDialog.dismiss()
                Log.d("response:", it)
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

                school = Gson().fromJson(sharedPreferences.getString("savedSchool",""),School::class.java)
                sessionData = Gson().fromJson(sharedPreferences.getString("loginInfo",""),SessionData::class.java)

                params["UserId"] = sessionData!!.user_id
                params["AppVersion"] = getString(R.string.version)
                params["Name"] = binding.studentName.text.toString()
                params["Class"] = studentClass
                params["Sex"] = studentSex
                params["SchoolId"] = school!!.id

                params["Mobile"] = guardianNumber

                params["LatValue"] = presentLat!!
                params["LonValue"] = presentLon!!
                params["Accuracy"] = presentAcc!!


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


    private fun isCorrectPhoneNumber(phone: String): Boolean {
        if ((phone == "") || (phone.length != 11)) {
            return false
        }
        val code2 = phone.substring(0, 3)
        for (op: String in operatorList) {
            if ((op == code2)) {
                return true
            }
        }
        return false
    }
    private fun checkDuplicate(number: String) {
        if (CustomUtility.haveNetworkConnection(this)) {
            val sweetAlertDialog = SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE)
            sweetAlertDialog.titleText = "Loading"
            sweetAlertDialog.setCancelable(false)
            sweetAlertDialog.show()
            val queue = Volley.newRequestQueue(this)
            var url = StaticTags.BASE_URL
            url+="activities/check_mobile_exist.php"

            val sr: StringRequest = object : StringRequest(
                Method.POST, url,
                Response.Listener {
                    sweetAlertDialog.dismiss()
                    Log.d("response:", it)
                    val jsonObject = JSONObject(it)
                    if(jsonObject.getBoolean("success"))
                    {
                        binding.contactPersonMobileNumber.error = "Number must be correct and unique"
                        isCorrectPrimaryNumber = false
                        CustomUtility.showError(this, "Failed!", jsonObject.getString("message"))
                    }
                    else
                    {
                        binding.contactPersonMobileNumber.error = null
                        isCorrectPrimaryNumber = true
                    }

                },
                Response.ErrorListener {
                    sweetAlertDialog.dismiss()
                    CustomUtility.showError(this, "Network problem, try again", "Failed")
                }) {
                override fun getParams(): Map<String, String> {
                    val params: MutableMap<String, String> = HashMap()
                    params["Mobile"] = guardianNumber
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
        else
        {
            Toast.makeText(this,"No internet connection!",Toast.LENGTH_SHORT).show()
        }
    }



    private fun checkFields(): Boolean {
        if (CustomUtility.haveNetworkConnection(this)) {
            if (!CustomUtility.haveNetworkConnection(this)) {
                Toast.makeText(this, "No internet connection!", Toast.LENGTH_SHORT)
                    .show()
                return false
            } else if (binding.studentName.text.toString() == "") {
                Toast.makeText(this, "Please fill required fields!", Toast.LENGTH_SHORT)
                    .show()
                binding.studentName.error = "Required Field!"
                return false
            }  else if (studentSex == "") {
                Toast.makeText(this, "Please select student's sex type!", Toast.LENGTH_SHORT)
                    .show()
                return false
            } else if (studentClass == "") {
                Toast.makeText(this, "Please select student's class!", Toast.LENGTH_SHORT)
                    .show()
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