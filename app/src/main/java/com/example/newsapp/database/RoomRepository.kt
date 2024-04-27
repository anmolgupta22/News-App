package com.example.newsapp.database

import com.example.newsapp.database.dao.NewsDao
import com.example.newsapp.model.NewsArticleModel
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
open class RoomRepository @Inject constructor(
    private val newsDao: NewsDao,
) {

    suspend fun fetchAllNewsArticle(): NewsArticleModel? {
        return newsDao.fetchAllNewsArticle()
    }

    fun insertNewsArticle(newsArticleModel: NewsArticleModel) {
        newsDao.insert(newsArticleModel)
    }

}