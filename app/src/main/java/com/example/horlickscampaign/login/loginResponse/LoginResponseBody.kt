package com.example.horlickscampaign.login.loginResponse

data class LoginResponseBody(
    val message: String,
    val sessionData: SessionData,
    val success: Boolean
)