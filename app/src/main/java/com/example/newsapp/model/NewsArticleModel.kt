package com.example.newsapp.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "tbl_news")
data class NewsArticleModel(
    @ColumnInfo
    @PrimaryKey
    var id: Int = 0,

    @ColumnInfo
    var status: String? = null,

    @ColumnInfo
    var articles: ArrayList<Articles> = arrayListOf(),
)

data class Source(
    var id: String? = null,
    var name: String? = null,
)

data class Articles(
    var source: Source? = Source(),
    var author: String? = null,
    var title: String? = null,
    var description: String? = null,
    var url: String? = null,
    var urlToImage: String? = null,
    var publishedAt: String? = null,
    var content: String? = null,
)