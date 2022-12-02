package com.example.horlickscampaign.login.loginResponse

data class SessionData(
    val full_name: String,
    val picture_name: String,
    val schoolList: List<School>,
    val team_id: String,
    val team_name: String,
    val user_id: String,
    val user_name: String,
    val user_type_id: String,
    val user_type_name: String,
    val employee_id: String,
)