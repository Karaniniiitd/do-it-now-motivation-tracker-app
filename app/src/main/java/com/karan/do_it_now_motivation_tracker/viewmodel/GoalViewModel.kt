package com.karan.do_it_now_motivation_tracker.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.karan.do_it_now_motivation_tracker.model.Goal

class GoalViewModel : ViewModel() {

    private val _goals = mutableStateListOf<Goal>()
    val goals: List<Goal> = _goals

    fun addGoal(goal: Goal) {
        _goals.add(goal)
    }
}