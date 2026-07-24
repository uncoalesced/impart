// Engineered by uncoalesced
package com.uncoalesced.impart.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.uncoalesced.impart.data.local.db.entity.ContactEntity

@Database(entities = [ContactEntity::class], version = 2, exportSchema = false)
abstract class ImpartDatabase : RoomDatabase() {
    abstract fun contactDao(): ContactDao
}
