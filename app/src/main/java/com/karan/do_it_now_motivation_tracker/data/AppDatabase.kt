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
import com.karan.do_it_now_motivation_tracker.model.AiChatSession
import com.karan.do_it_now_motivation_tracker.model.AiChatMessage
import com.karan.do_it_now_motivation_tracker.model.AiChatDao
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [Goal::class, DailyLog::class, UserStats::class, DailyQuest::class, WeeklyReport::class, AiChatSession::class, AiChatMessage::class],
    version = 7,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun goalDao(): GoalDao
    abstract fun dailyLogDao(): DailyLogDao
    abstract fun userStatsDao(): UserStatsDao
    abstract fun dailyQuestDao(): DailyQuestDao
    abstract fun weeklyReportDao(): WeeklyReportDao
    abstract fun aiChatDao(): AiChatDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            val MIGRATION_4_5 = object : Migration(4, 5) {
                override fun migrate(db: SupportSQLiteDatabase) {
                    db.execSQL("CREATE TABLE IF NOT EXISTS `ai_chat_sessions` (`id` TEXT NOT NULL, `title` TEXT NOT NULL, `timestamp` INTEGER NOT NULL, PRIMARY KEY(`id`))")
                    db.execSQL("CREATE TABLE IF NOT EXISTS `ai_chat_messages` (`id` TEXT NOT NULL, `sessionId` TEXT NOT NULL, `text` TEXT NOT NULL, `isUser` INTEGER NOT NULL, `isError` INTEGER NOT NULL, `timestamp` INTEGER NOT NULL, PRIMARY KEY(`id`))")
                }
            }
            
            val MIGRATION_5_6 = object : Migration(5, 6) {
                override fun migrate(db: SupportSQLiteDatabase) {
                    db.execSQL("ALTER TABLE `goals` ADD COLUMN `hasTime` INTEGER NOT NULL DEFAULT 0")
                }
            }

            val MIGRATION_6_7 = object : Migration(6, 7) {
                override fun migrate(db: SupportSQLiteDatabase) {
                    db.execSQL("ALTER TABLE `goals` ADD COLUMN `isFailed` INTEGER NOT NULL DEFAULT 0")
                    db.execSQL("ALTER TABLE `goals` ADD COLUMN `failedDate` INTEGER")
                    db.execSQL("ALTER TABLE `daily_logs` ADD COLUMN `goalsFailedCount` INTEGER NOT NULL DEFAULT 0")
                }
            }

            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "do_it_now_database"
                )
                    .addMigrations(MIGRATION_4_5, MIGRATION_5_6, MIGRATION_6_7)
                    .fallbackToDestructiveMigration(dropAllTables = true)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
