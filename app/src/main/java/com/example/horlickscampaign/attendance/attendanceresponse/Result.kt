package com.example.horlickscampaign.attendance.attendanceresponse

data class Result(
    val attendance_date: String,
    val attendance_id: String,
    val attendance_status_id: String,
    val attendance_status_name: String,
    val create_date: Any,
    val created_by: String,
    val employee_id: String,
    val employee_name: String,
    val employee_position_id: String,
    val employee_type_id: String,
    val in_remark: String,
    val in_time: String,
    val in_time_accuracy: String,
    val in_time_lat: String,
    val in_time_lon: String,
    val in_time_picture_name: String,
    val out_remark: Any,
    val out_time: String?,
    val out_time_accuracy: Any,
    val out_time_lat: Any,
    val out_time_lon: Any,
    val out_time_picture_name: Any,
    val update_date: Any,
    val updated_by: String
)