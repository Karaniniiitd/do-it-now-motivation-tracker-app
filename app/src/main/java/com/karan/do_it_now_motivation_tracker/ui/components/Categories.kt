package com.karan.do_it_now_motivation_tracker.ui.components

import androidx.compose.ui.graphics.Color

object Categories {
    val all = listOf("Mind", "Body", "Code", "Work", "Finance", "Create", "Spirit", "Mission")

    fun emoji(category: String): String = when (category) {
        "Mind"    -> "🧠"
        "Body"    -> "💪"
        "Code"    -> "💻"
        "Work"    -> "💼"
        "Finance" -> "💰"
        "Create"  -> "🎨"
        "Spirit"  -> "🌱"
        "Mission" -> "🎯"
        // legacy names
        "Health"    -> "💪"
        "Study"     -> "🧠"
        "Fitness"   -> "💪"
        "Personal"  -> "🌱"
        "Creative"  -> "🎨"
        "General"   -> "🎯"
        else        -> "🎯"
    }

    fun color(category: String): Color = Color.White

    fun label(category: String): String = when (category) {
        "Mind"    -> "MIND"
        "Body"    -> "BODY"
        "Code"    -> "CODE"
        "Work"    -> "WORK"
        "Finance" -> "FINANCE"
        "Create"  -> "CREATE"
        "Spirit"  -> "SPIRIT"
        "Mission" -> "MISSION"
        else      -> category.uppercase()
    }
}
