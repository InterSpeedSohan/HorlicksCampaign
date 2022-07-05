package com.example.horlickscampaign.utils

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.location.LocationManager
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.provider.Settings
import android.util.Base64
import androidx.appcompat.app.AlertDialog
import cn.pedant.SweetAlert.SweetAlertDialog
import java.io.ByteArrayOutputStream
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt


object CustomUtility {
    fun imageToString(bitmap: Bitmap): String {
        val byteArrayOutputStream = ByteArrayOutputStream()
        val aspectRatio: Float = bitmap.width.toFloat() / bitmap.height.toFloat()
        val width = 1080
        val height = (width / aspectRatio).roundToInt()
        var bitm = Bitmap.createScaledBitmap(bitmap, width, height, false)
        bitm.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream)
        val imgBytes = byteArrayOutputStream.toByteArray()
        return Base64.encodeToString(imgBytes, Base64.DEFAULT)
    }

    fun deleteFile(file: File) {
        if (file.exists()) {
            if (file.delete()) {
                println("file Deleted")
            } else {
                println("file not Deleted")
            }
        }
    }

    fun showAlert(context: Context?, message: String?, title: String?) {
        val builder = AlertDialog.Builder(
                context!!
        )
        builder.setMessage(message)
        builder.setPositiveButton("Ok", null)
        builder.setTitle(title)
        builder.show()
    }

    fun showWarning(context: Context?, mess: String?, tittle: String?) {
        SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
            .setTitleText(tittle)
            .setContentText(mess)
            .setConfirmText("Ok")
            .show()
    }

    fun showError(context: Context?, mess: String?, tittle: String?) {
        SweetAlertDialog(context, SweetAlertDialog.ERROR_TYPE)
            .setTitleText(tittle)
            .setContentText(mess)
            .setConfirmText("Ok")
            .show()
    }

    fun showSuccess(context: Context?, mess: String?, tittle: String?) {
        SweetAlertDialog(context, SweetAlertDialog.SUCCESS_TYPE)
            .setTitleText(tittle)
            .setContentText(mess)
            .setConfirmText("Ok")
            .show()
    }

    fun getTimeStamp(format: String?): String {
        val s = SimpleDateFormat(format)
        return s.format(Date())
    }

    fun haveNetworkConnection(con: Context): Boolean {
        var haveConnectedWifi = false
        var haveConnectedMobile = false
        try {
            val cm: ConnectivityManager =
                con.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val netInfo: Array<NetworkInfo> = cm.getAllNetworkInfo()
            for (ni in netInfo) {
                if (ni.getTypeName()
                        .equals("WIFI", ignoreCase = true)
                ) if (ni.isConnected()) haveConnectedWifi = true
                if (ni.getTypeName()
                        .equals("MOBILE", ignoreCase = true)
                ) if (ni.isConnected()) haveConnectedMobile = true
            }
        } catch (e: Exception) {
        }
        return haveConnectedWifi || haveConnectedMobile
    }

    fun haveGpsEnabled(context: Context) {
        val lm: LocationManager =
            context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        var gps_enabled = false
        var network_enabled = false
        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER)
        } catch (ex: Exception) {
        }
        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        } catch (ex: Exception) {
        }
        if (!gps_enabled && !network_enabled) {
            // notify user
            var s = SweetAlertDialog(context, SweetAlertDialog.ERROR_TYPE)
            s.titleText = "GPS not enabled"
            s.setCancelable(false)
            s.setConfirmButton("GPS Setting", SweetAlertDialog.OnSweetClickListener {
                context.startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                s.dismissWithAnimation()
            })
            s.show()


        }
    }

    fun getDeviceDate(): String? {
        //val myFormat = "yyyy-MM-dd H:m:s" //In which you need put here
        val myFormat = "yyyy-MM-dd"
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        val date = Date()
        return sdf.format(date)
    }


}