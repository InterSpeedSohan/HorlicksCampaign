package com.example.horlickscampaign.login

import android.Manifest
import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View

import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import cn.pedant.SweetAlert.SweetAlertDialog
import com.example.horlickscampaign.MainActivity
import com.example.horlickscampaign.SchoolListActivity
import com.example.horlickscampaign.databinding.ActivityLoginBinding
import com.example.horlickscampaign.login.loginResponse.LoginResponseBody
import com.example.horlickscampaign.utils.CustomUtility
import com.example.horlickscampaign.utils.StaticTags
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

open class LoginActivity : AppCompatActivity() {
    private val PERMISSIONS_LIST = arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.CAMERA,
            Manifest.permission.INTERNET
    )
    private val PERMISSION_ALL = 1
    lateinit var binding: ActivityLoginBinding
    var networkAvailable = false
    var sweetAlertDialog: SweetAlertDialog? = null
    var sharedPreferences: SharedPreferences? = null
    var userid: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if(StaticTags.isBeta)
        {
            binding.isBeta.visibility = View.VISIBLE
        }

        checkPermission()

        //binding.textUserType.text = getString(R.string.user_type)

        ObjectAnimator.ofFloat(binding.logoLayout, "translationY", -200f).apply {
            duration = 1000
            start()
        }

        val timer: Thread = object : Thread() {
            override fun run() {
                try {
                    sleep(500)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                } finally {
                    sharedPreferences = getSharedPreferences("user", Context.MODE_PRIVATE)
                    if (sharedPreferences!!.contains("loginInfo")) {
                        val intent = Intent(applicationContext, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                    else
                    {
                        runOnUiThread()
                        {
                            binding.loginLayout.alpha = 0f
                            binding.loginLayout.visibility = View.VISIBLE

                            binding.loginLayout.animate()
                                .alpha(1f)
                                .setDuration(500)
                                .setListener(null)
                        }
                        LoginButton()
                    }

                }
            }
        }
        timer.start()




    }

    private fun checkPermission() {
        if (!hasPermissions(this, *PERMISSIONS_LIST)) {
            Log.e("per", "error perm")
            ActivityCompat.requestPermissions(this, PERMISSIONS_LIST, PERMISSION_ALL)
        }
    }

    private fun LoginButton() {

        binding.btnlogin.setOnClickListener {
            networkAvailable = CustomUtility.haveNetworkConnection(this@LoginActivity)
            //checking for
            if (networkAvailable) {
                val id: String = binding.edtid.text.toString()
                userid = id
                val pass: String = binding.edtpass.text.toString()
                if ((id == "")) {
                    binding.edtid.error = "Mandatory Field!"
                }
                else if (pass == ""){
                    binding.edtpass.error = "Mandatory Field!"
                }
                else {

                    sweetAlertDialog =
                        SweetAlertDialog(this@LoginActivity, SweetAlertDialog.PROGRESS_TYPE)
                    sweetAlertDialog!!.titleText = "Loading"
                    sweetAlertDialog!!.show()
                    sweetAlertDialog!!.setCancelable(false)
                    login(id, pass)
                }
            } else {
                CustomUtility.showError(
                    this@LoginActivity,
                    "Please Check your internet connection",
                    "Network Warning !!!"
                )
            }
        }
    }

    private fun login(user_name: String, user_pass: String) {

        val retrofit = Retrofit.Builder()
            .baseUrl(StaticTags.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create(LoginApiInterface::class.java)
        val call = service.login(user_name, user_pass)

        call.enqueue(object  : Callback<LoginResponseBody>{
            override fun onResponse(
                call: Call<LoginResponseBody>?,
                response: retrofit2.Response<LoginResponseBody>?
            ) {
                sweetAlertDialog?.dismiss()
                Log.d("response", response?.body().toString())
                if (response != null) {
                    if (response.code() == 200) {
                        val loginResponseBody = response.body()!!
                        val userData = loginResponseBody.sessionData
                        if(loginResponseBody.success)
                        {
                            if(userData.user_id != null && userData.employee_id != null)
                            {
                                sharedPreferences = getSharedPreferences("user", MODE_PRIVATE)
                                val editor: SharedPreferences.Editor = sharedPreferences!!.edit()
                                editor.putString("loginInfo",Gson().toJson(userData))
                                editor.apply()
                                startActivity(Intent(applicationContext, MainActivity::class.java))
                                finish()
                            }
                            else
                            {
                                CustomUtility.showError(this@LoginActivity,"Employee not positioned","Failed")
                            }

                        }
                        else
                        {
                            CustomUtility.showError(this@LoginActivity,"Wrong login info","Failed")
                        }
                    }
                }
            }

            override fun onFailure(call: Call<LoginResponseBody>?, t: Throwable?) {
                sweetAlertDialog?.dismiss()
                Log.e("res", t.toString())
                CustomUtility.showError(
                    this@LoginActivity,
                    "Network Error, try again!",
                    "Login failed"
                )
            }
        })


    }

    fun hasPermissions(context: Context?, vararg permissions: String?): Boolean {
        if (context != null) {
            for (permission in permissions) {
                if (permission?.let {
                            ActivityCompat.checkSelfPermission(
                                    context,
                                    it
                            )
                        } != PackageManager.PERMISSION_GRANTED
                ) {
                    return false
                }
            }
        }
        return true
    }

}