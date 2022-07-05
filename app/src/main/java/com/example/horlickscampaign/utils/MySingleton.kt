package com.example.horlickscampaign.utils

import android.content.Context
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley

class MySingleton(private var mCtx: Context) {
    private var requestQueue: RequestQueue?
    private fun getRequestQueue(): RequestQueue? {
        if (requestQueue == null) requestQueue =
            Volley.newRequestQueue(mCtx.applicationContext)
        return requestQueue
    }

    fun <T> addToRequestQue(request: Request<T>?) {
        getRequestQueue()!!.add(request)
    }

    companion object {
        //private lateinit var mCtx: Context
        private var mInstance: MySingleton? = null
        @Synchronized
        fun getInstance(context: Context): MySingleton? {
            if (mInstance == null) {
                mInstance = MySingleton(context)
            }
            return mInstance
        }
    }

    init {
        requestQueue = getRequestQueue()
    }
}