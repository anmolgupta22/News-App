package com.example.newsapp.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy.Companion.REPLACE
import androidx.room.Update


@Dao
interface BaseDao<T> {

    @Insert(onConflict = REPLACE)
    fun insertAll(newsModel: List<T>)

    @Insert(onConflict = REPLACE)
    fun insert(newsModel: T): Long

    @Update(onConflict = REPLACE)
    fun update(newsModel: T): Int

    @Delete
    fun delete(newsModel: T): Int
}