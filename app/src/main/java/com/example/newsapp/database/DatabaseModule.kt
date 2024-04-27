package com.example.newsapp.database

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    fun providesDatabase(@ApplicationContext application: Context) =
        DBHelper.getInstance(application)

    @Provides
    fun providesNewsDao(database: DBHelper) = database.newsDao()

}
