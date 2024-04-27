package com.example.newsapp.database.dao

import androidx.room.Dao
import androidx.room.Query
import com.example.newsapp.database.dao.BaseDao
import com.example.newsapp.model.NewsArticleModel


@Dao
interface NewsDao : BaseDao<NewsArticleModel> {

    @Query("Select * from tbl_news")
    suspend fun fetchAllNewsArticle(): NewsArticleModel?
}