package com.karan.do_it_now_motivation_tracker.ui.components

import androidx.compose.ui.graphics.Color
import com.karan.do_it_now_motivation_tracker.ui.theme.CatCreative
import com.karan.do_it_now_motivation_tracker.ui.theme.CatFinance
import com.karan.do_it_now_motivation_tracker.ui.theme.CatFitness
import com.karan.do_it_now_motivation_tracker.ui.theme.CatGeneral
import com.karan.do_it_now_motivation_tracker.ui.theme.CatHealth
import com.karan.do_it_now_motivation_tracker.ui.theme.CatPersonal
import com.karan.do_it_now_motivation_tracker.ui.theme.CatStudy
import com.karan.do_it_now_motivation_tracker.ui.theme.CatWork

object Categories {
    val all = listOf("General", "Health", "Study", "Work", "Fitness", "Finance", "Personal", "Creative")

    fun emoji(category: String): String = when (category) {
        "Health"   -> "🌿"
        "Study"    -> "📚"
        "Work"     -> "💼"
        "Fitness"  -> "⚡"
        "Finance"  -> "💎"
        "Personal" -> "🌱"
        "Creative" -> "🎨"
        else       -> "📋"
    }

    fun color(category: String): Color = when (category) {
        "Health"   -> CatHealth
        "Study"    -> CatStudy
        "Work"     -> CatWork
        "Fitness"  -> CatFitness
        "Finance"  -> CatFinance
        "Personal" -> CatPersonal
        "Creative" -> CatCreative
        else       -> CatGeneral
    }
}
