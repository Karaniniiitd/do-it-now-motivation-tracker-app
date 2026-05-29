package com.karan.do_it_now_motivation_tracker.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.karan.do_it_now_motivation_tracker.model.DailyLog
import com.karan.do_it_now_motivation_tracker.model.DailyLogDao
import com.karan.do_it_now_motivation_tracker.model.DailyQuest
import com.karan.do_it_now_motivation_tracker.model.DailyQuestDao
import com.karan.do_it_now_motivation_tracker.model.Goal
import com.karan.do_it_now_motivation_tracker.model.GoalDao
import com.karan.do_it_now_motivation_tracker.model.UserStats
import com.karan.do_it_now_motivation_tracker.model.UserStatsDao
import com.karan.do_it_now_motivation_tracker.model.WeeklyReport
import com.karan.do_it_now_motivation_tracker.model.WeeklyReportDao

@Database(
    entities = [Goal::class, DailyLog::class, UserStats::class, DailyQuest::class, WeeklyReport::class],
    version = 4,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun goalDao(): GoalDao
    abstract fun dailyLogDao(): DailyLogDao
    abstract fun userStatsDao(): UserStatsDao
    abstract fun dailyQuestDao(): DailyQuestDao
    abstract fun weeklyReportDao(): WeeklyReportDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "do_it_now_database"
                )
                    .fallbackToDestructiveMigration(dropAllTables = true)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
