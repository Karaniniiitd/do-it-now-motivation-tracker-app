package com.karan.do_it_now_motivation_tracker.model

data class Goal(
    val id: Int,
    val title: String,
    val startDate: Long,
    val endDate: Long,
    val difficulty: String
)