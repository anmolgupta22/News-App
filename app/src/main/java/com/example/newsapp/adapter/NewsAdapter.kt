package com.example.newsapp.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.newsapp.R
import com.example.newsapp.databinding.HeaderItemsBinding
import com.example.newsapp.databinding.NewsItemsBinding
import com.example.newsapp.model.Articles
import com.example.newsapp.model.NewsItems
import java.text.SimpleDateFormat
import java.util.*

class NewsAdapter(
    private val context: Context,
    private val viewMoreListener: ViewMoreListener,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    // List to hold news items
    private var itemList: MutableList<NewsItems> = mutableListOf()

    // Position of expanded item
    private var expandedPosition = RecyclerView.NO_POSITION

    // Function to set items in the adapter
    @SuppressLint("NotifyDataSetChanged")
    fun setItems(items: MutableList<NewsItems>) {
        itemList = items
        notifyDataSetChanged()
    }

    // Function to clear all items from the adapter
    fun clearItems() = itemList.clear()

    // Function to create view holder based on view type
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            1 -> NewsViewHolder(
                NewsItemsBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
            0 -> DateViewHolder(
                HeaderItemsBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    // Function to bind data to view holder
    @SuppressLint("NotifyDataSetChanged")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is NewsViewHolder -> {
                holder.onBind(itemList[position].item, context, viewMoreListener)
                val isExpanded = position == expandedPosition
                with(holder.binding) {
                    if (isExpanded) {
                        fullDetails.visibility = View.VISIBLE
                    } else {
                        fullDetails.visibility = View.GONE
                    }

                    titleCard.setOnClickListener {
                        expandedPosition = if (isExpanded) RecyclerView.NO_POSITION else position
                        notifyDataSetChanged()
                    }
                }
            }
            is DateViewHolder -> holder.onBind(itemList[position].date)
            else -> throw IllegalArgumentException("Unexpected view holder type")
        }
    }

    // Function to get total item count
    override fun getItemCount() = itemList.size

    // View holder for news items
    inner class NewsViewHolder(val binding: NewsItemsBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun onBind(articles: Articles?, context: Context, viewMoreListener: ViewMoreListener) {
            with(binding) {
                // Bind news data
                headLine.text = articles?.title
                Glide.with(context).load(articles?.urlToImage).apply(
                    RequestOptions.placeholderOf(R.drawable.ic_news)
                        .error(R.drawable.ic_news)
                ).into(image)

                // Format author and description text
                val authorText = "Author: ${articles?.author}"
                val descriptionText = "Description: ${articles?.description}"

                // Create spannable for author text
                val spannableAuthor = SpannableString(authorText)
                spannableAuthor.setSpan(
                    StyleSpan(Typeface.BOLD),
                    0,
                    7,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )

                // Combine description text with view more
                val combinedText = "$descriptionText view more"

                // Create spannable for combined text
                val spannableString = SpannableString(combinedText)

                // Set ClickableSpan for "view more"
                val clickableSpan = object : ClickableSpan() {
                    override fun onClick(p0: View) {
                        viewMoreListener.viewMoreClicked(articles?.url)
                        println("Opening URL: ${articles?.url}")
                    }
                }

                // Set ClickableSpan for "view more" text
                val startIndex = descriptionText.length + 1
                val endIndex = combinedText.length
                spannableString.setSpan(
                    clickableSpan,
                    startIndex,
                    endIndex,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )

                // Apply blue color to "view more" text
                val blueColor = Color.BLUE
                spannableString.setSpan(
                    ForegroundColorSpan(blueColor),
                    startIndex,
                    endIndex,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )

                // Set the text to TextView
                val spannableDescription = SpannableString(spannableString)
                spannableDescription.setSpan(
                    StyleSpan(Typeface.BOLD),
                    0,
                    12,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )

                // Set description text and make it clickable
                description.text = spannableDescription
                description.movementMethod = LinkMovementMethod.getInstance()

                // Set author text
                author.text = spannableAuthor

                // Load full image
                Glide.with(context).load(articles?.urlToImage).apply(
                    RequestOptions.placeholderOf(R.drawable.ic_news)
                        .error(R.drawable.ic_news)
                ).into(fullImage)
            }
        }
    }

    // View holder for date items
    inner class DateViewHolder(private val binding: HeaderItemsBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun onBind(dates: String?) {
            // Convert date format and bind
            val date = convertToRequiredFormat(dates)
            binding.tvHeader.text = date
        }
    }

    // Function to get view type for an item
    override fun getItemViewType(position: Int): Int {
        return itemList[position].getHeaderType
    }

    // Function to convert date format
    private fun convertToRequiredFormat(strDate: String?): String {
        return try {
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
            val currentDate = strDate?.let { sdf.parse(it) }
            val sdf2 = SimpleDateFormat("dd MMM yyyy, E", Locale.ENGLISH)
            currentDate?.let { sdf2.format(it) } ?: ""
        } catch (e: Exception) {
            e.printStackTrace()
            ""
        }
    }

    // Interface for handling "view more" click
    interface ViewMoreListener {
        fun viewMoreClicked(url: String?)
    }
}
