package com.example.electriccarsapp.data.local

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.electriccarsapp.data.local.CarrosContract.SQL_DELETE_ENTRIES
import com.example.electriccarsapp.data.local.CarrosContract.TABLE_CAR

class CarsDbHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
       db.execSQL(TABLE_CAR)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL(SQL_DELETE_ENTRIES)
        onCreate(db)

    }

    override fun onDowngrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        onDowngrade(db, oldVersion, newVersion)
    }

    companion object {

        const val DATABASE_VERSION = 1
        const val DATABASE_NAME = "dbCar.db"
    }

}