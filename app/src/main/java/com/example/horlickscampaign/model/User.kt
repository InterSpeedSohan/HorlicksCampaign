package com.example.horlickscampaign.model

import android.content.SharedPreferences

class User {
    var name: String? = null
    var userName: String? = null
    var teamId: String? = null
    var userId: String? = null
    var employeeId: String? = null
    var city: String? = null
    var zone: String? = null
    var zoneId: String? = null

    var areaList: String? = null


    private constructor(
        name: String,
        teamId: String,
        userId: String,
        area: String,
        employeeId: String
    ) {
        this.name = name
        this.teamId = teamId
        this.userId = userId
        //this.tokenId = tokenId
    }

    private constructor() {}

    fun setValuesFromSharedPreference(sharedPreference: SharedPreferences) {
        name = sharedPreference.getString("name", null)
        userName = sharedPreference.getString("userName", null)
        teamId = sharedPreference.getString("id", null)
        userId = sharedPreference.getString("id", null)
        employeeId = sharedPreference.getString("employeeId", null)
        city = sharedPreference.getString("cityName", null)
        zone = sharedPreference.getString("zoneName", null)
        areaList = sharedPreference.getString("areaList",null)
        zoneId = sharedPreference.getString("zoneId",null)
    }

    fun setName(editor: SharedPreferences.Editor, id: String?, name: String?) {
        this.name = name
        saveToSharepreference(editor, id, name)
    }

    fun setUserName(editor: SharedPreferences.Editor, id: String?, userName: String?) {
        this.userName = userName
        saveToSharepreference(editor, id, userName)
    }


    fun setUserId(editor: SharedPreferences.Editor, id: String?, userId: String?) {
        this.userId = userId
        saveToSharepreference(editor, id, userId)
    }
    fun setEmployeeId(editor: SharedPreferences.Editor, id: String?, employeeId: String?) {
        this.employeeId = employeeId
        saveToSharepreference(editor, id, employeeId)
    }

    fun setZoneId(editor: SharedPreferences.Editor, id: String?, zoneId: String?) {
        this.zoneId = zoneId
        saveToSharepreference(editor, id, zoneId)
    }
    fun setCityToSf(editor: SharedPreferences.Editor, id: String?, city: String?) {
        this.city = city
        saveToSharepreference(editor, id, city)
    }
    fun setZoneToSf(editor: SharedPreferences.Editor, id: String?, zone: String?) {
        this.zone = zone
        saveToSharepreference(editor, id, zone)
    }

    fun setAreaList(editor: SharedPreferences.Editor, id: String?, areaList: String?) {
        this.areaList = areaList
        saveToSharepreference(editor, id, areaList)
    }

    fun saveToSharepreference(editor: SharedPreferences.Editor, id: String?, value: String?) {
        editor.putString(id, value)
        editor.apply()
    }

    fun isUserInSharedpreference(sharedPreferences: SharedPreferences, id: String?): Boolean {
        return sharedPreferences.contains(id)
    }

    fun clear(editor: SharedPreferences.Editor) {
        editor.clear()
        editor.apply()
    }

    companion object {
        var user: User? = null
        val instance: User?
            get() {
                if (user == null) {
                    user = User()
                }
                return user
            }

        fun createInstance(
            name: String,
            teamId: String,
            userId: String,
            area: String,
            tokenId: String
        ): User? {
            if (user == null) {
                user = User(name, teamId, userId, area, tokenId)
            }
            return user
        }
    }




}