package com.example.checkin

import android.content.Context
import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase

class LocalDataSource(var context: Context) {
    private var db: RecordsDatabase? = null
    fun getDatabase(): com.example.checkin.RecordsDatabase {
        if(db == null) {
            db = Room.databaseBuilder(context, RecordsDatabase::class.java, "db").build()
        }
        return db as RecordsDatabase
    }
}

@Entity(tableName = "records")
data class Records(
    @PrimaryKey var id: String,
    @ColumnInfo(name = "date") var date: String,
    @ColumnInfo(name = "time_in") var timeIn: Long,
    @ColumnInfo(name = "time_out") var timeOut: Long,
    @ColumnInfo(name = "userAccessKey") var accessKey: String,
    @ColumnInfo(name = "new") var new: Boolean
) {
    constructor() : this("", "", 0L, 0L, "", true)
}


@Database(entities = [Records::class], version = 1)
abstract class RecordsDatabase(): RoomDatabase() {
    abstract fun userDao(): UserDao

}

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun storeAllRecords(vararg records: Records)

    @Query("SELECT EXISTS(SELECT * FROM records WHERE id = :entryId)")
    fun recordExists(entryId: String) : Boolean



}


