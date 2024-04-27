package com.example.newsapp.model


enum class HeaderType {
    DATE,
    NEWS
}

data class NewsItems(
    val role: HeaderType,
    val date: String? = null,
    val item: Articles? = null,
) {

    private fun getHeaderType(role: HeaderType): Int {
        return role.ordinal
    }

    val getHeaderType: Int = getHeaderType(role)
}


