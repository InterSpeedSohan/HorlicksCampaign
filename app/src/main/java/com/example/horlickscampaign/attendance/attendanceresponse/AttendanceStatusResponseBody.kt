package com.example.horlickscampaign.attendance.attendanceresponse

data class AttendanceStatusResponseBody(
    val message: String,
    val resultList: List<Result>,
    val success: Boolean
)