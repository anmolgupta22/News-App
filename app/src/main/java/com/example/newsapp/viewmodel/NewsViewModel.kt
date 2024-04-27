package com.example.newsapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.newsapp.database.RoomRepository
import com.example.newsapp.model.Articles
import com.example.newsapp.model.HeaderType
import com.example.newsapp.model.NewsArticleModel
import com.example.newsapp.model.NewsItems
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*
import javax.inject.Inject

@HiltViewModel
class NewsViewModel @Inject constructor(private val repository: RoomRepository) : ViewModel() {

    // Default sorting order
    private var sort = "DESC"

    // HashMap for filtering articles
    val filterHm: HashMap<String?, Articles> = hashMapOf()

    // Function to extract date from publishedAt string
    private fun getScheduledDate(publishedAt: String?): String? {
        var date: String? = publishedAt
        if (publishedAt != null) {
            if (publishedAt.contains("T")) {
                date = publishedAt.split("T").toTypedArray()[0]
            }
        }
        return date
    }

    // Coroutine function to fetch news articles
    suspend fun fetchNewsArticle(): NewsArticleModel? {
        return withContext(viewModelScope.coroutineContext + Dispatchers.IO) {
            repository.fetchAllNewsArticle()
        }
    }

    // Coroutine function to insert news article
    fun insertNewsArticle(newsArticleModel: NewsArticleModel) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertNewsArticle(newsArticleModel)
        }
    }

    // Function to set sorting order
    fun setSort(sort: String?) {
        if (sort != null) {
            this.sort = sort
        }
    }

    // Function to get current sorting order
    fun getSort(): String {
        return sort
    }

    // Function to filter and group data into NewsItems
    fun getFilteredData(data: List<Articles>): MutableList<NewsItems> {
        val newsItems = mutableListOf<NewsItems>()
        val groupedHashMap = groupDataIntoTreeMap(data)

        // Iterate over grouped data and create NewsItems
        groupedHashMap.forEach { (date, articlesList) ->
            newsItems.add(NewsItems(HeaderType.DATE, date))
            newsItems.addAll(articlesList.map { NewsItems(role = HeaderType.NEWS, item = it) })
        }
        return newsItems
    }

    // Function to group data into TreeMap based on date
    private fun groupDataIntoTreeMap(articlesList: List<Articles>): TreeMap<String, List<Articles>> {
        // Determine sorting order for TreeMap
        val groupedHashMap = if (getSort().equals("DESC", ignoreCase = true)) {
            TreeMap<String, List<Articles>>(Collections.reverseOrder())
        } else {
            TreeMap()
        }

        // Iterate over articles and group them by date
        for (article in articlesList) {
            val date: String? = getScheduledDate(article.publishedAt)
            if (date != null) {
                if (groupedHashMap.containsKey(date)) {
                    groupedHashMap[date] = articlesList // Assigning entire list here, should be changed
                } else {
                    val list: MutableList<Articles> = mutableListOf()
                    list.add(article)
                    groupedHashMap[date] = list
                }
            }
        }
        return groupedHashMap
    }
}