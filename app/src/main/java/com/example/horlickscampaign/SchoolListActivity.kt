package com.example.horlickscampaign

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.*
import com.example.horlickscampaign.databinding.ActivitySchoolListBinding
import com.example.horlickscampaign.login.loginResponse.School
import com.example.horlickscampaign.login.loginResponse.SessionData
import com.google.gson.Gson
import java.util.*


class SchoolListActivity : AppCompatActivity() {
    var recyclerView: RecyclerView? = null
    lateinit var mAdapter: SchoolListDataAdapter
    private val dataList: java.util.ArrayList<School> = java.util.ArrayList<School>()
    private val mainOutletList: java.util.ArrayList<School> = java.util.ArrayList<School>()
    lateinit var binding: ActivitySchoolListBinding
    var sharedPreferences: SharedPreferences? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)



        binding = ActivitySchoolListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sharedPreferences = getSharedPreferences("user", Context.MODE_PRIVATE)

        binding.searchSchool.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                getNewSearchedList(charSequence)
            }

            override fun afterTextChanged(editable: Editable) {}
        })

        dataList.clear()
        recyclerView = binding.schoolListRecycler
        recyclerView!!.setHasFixedSize(true)

        val linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        recyclerView!!.layoutManager = linearLayoutManager

        mAdapter = SchoolListDataAdapter(dataList,this)
        recyclerView!!.adapter = mAdapter

        getSchoolList()

        if(sharedPreferences!!.contains("savedSchool"))
        {
            startActivity(Intent(applicationContext, FormActivity::class.java))
            finish()
        }

    }
    private fun getSchoolList() {
        dataList.clear()
        mainOutletList.clear()
        val s = sharedPreferences!!.getString("loginInfo",null)
        val loginInfo = Gson().fromJson(s, SessionData::class.java)
        dataList.addAll(loginInfo.schoolList)
        Log.d("datalist size",mAdapter.itemCount.toString())
        mainOutletList.addAll(dataList)
        mAdapter.notifyDataSetChanged()
    }

    private fun getNewSearchedList(s: CharSequence) {
        val newList: ArrayList<School> = ArrayList<School>()
        if (s == "") {
            dataList.clear()
            dataList.addAll(mainOutletList)
        } else {
            for (i in mainOutletList.indices) {
                if (mainOutletList[i].name.lowercase(Locale.getDefault())
                        .contains(s.toString().lowercase(Locale.getDefault()))
                ) {
                    newList.add(mainOutletList[i])
                }
            }
        }
        dataList.clear()
        dataList.addAll(newList)
        mAdapter.notifyDataSetChanged()
    }
    inner class SchoolListDataAdapter(dataList: java.util.ArrayList<School>, context: Context) :
        RecyclerView.Adapter<SchoolListDataAdapter.MyViewHolder>() {
        var mc = context

        private val dataList: java.util.ArrayList<School> = dataList
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val itemView: View = LayoutInflater.from(parent.context)
                .inflate(R.layout.school_row_layout, parent, false)
            return MyViewHolder(itemView)
        }

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            val data = dataList[position]
            holder.schoolName.text = data.name
            holder.schoolId.text = "Outlet Id: " + data.id

            holder.rowLayout.setOnClickListener {
                val sharedPreferences =
                    getSharedPreferences("user", AppCompatActivity.MODE_PRIVATE)
                val editor = sharedPreferences.edit()
                editor.putString("savedSchool",Gson().toJson(data))
                editor.apply()
                startActivity(Intent(applicationContext, FormActivity::class.java))
                finish()
            }
        }

        override fun getItemCount(): Int {
            return dataList.size
        }

        inner class MyViewHolder(convertView: View) : RecyclerView.ViewHolder(convertView) {
            var schoolName: TextView = convertView.findViewById(R.id.schoolName)
            var schoolId: TextView = convertView.findViewById(R.id.schoolId)

            var rowLayout: ConstraintLayout = convertView.findViewById(R.id.retail_row_layout)
        }

    }
}