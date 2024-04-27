package com.example.newsapp.fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.newsapp.R
import com.example.newsapp.databinding.FragmentNewsArticleBinding
import com.example.newsapp.adapter.NewsAdapter
import com.example.newsapp.model.HeaderType
import com.example.newsapp.model.NewsArticleModel
import com.example.newsapp.model.NewsItems
import com.example.newsapp.utils.readJSONFromAssets
import com.example.newsapp.viewmodel.NewsViewModel
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class NewsArticleFragment : Fragment(), NewsAdapter.ViewMoreListener {

    private val viewModel: NewsViewModel by viewModels()
    private lateinit var binding: FragmentNewsArticleBinding
    private lateinit var newsAdapter: NewsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Handling back press event
        requireActivity().onBackPressedDispatcher.addCallback(this /* lifecycle owner */,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    // Back is pressed... Finishing the activity
                    requireActivity().finish()
                }
            })
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        // Inflating the layout for this fragment
        binding = FragmentNewsArticleBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initializing RecyclerView and adapter
        val linearLayoutManager = LinearLayoutManager(requireContext())
        newsAdapter = NewsAdapter(requireContext(), this)
        binding.rvNews.apply {
            layoutManager = linearLayoutManager
            adapter = newsAdapter
            itemAnimator = null
            setHasFixedSize(true)
        }

        // Reading JSON from assets
        val json = readJSONFromAssets(requireContext(), "NewsArticle.json")
        val gson = Gson()
        val data = gson.fromJson(json, NewsArticleModel::class.java)

        lifecycleScope.launch {
            // Fetching news articles asynchronously
            val newsData = viewModel.fetchNewsArticle()
            if (newsData == null) {
                // If data is not available in database, insert it
                viewModel.insertNewsArticle(data)
                newsAdapter.setItems(viewModel.getFilteredData(data.articles))
            } else {
                // If data is available, display it
                newsArticleAdd()
            }
            // Storing articles in filter hashmap
            data.articles.forEach {
                viewModel.filterHm[it.title] = it
            }
        }

        // Setting up search functionality
        binding.searchView.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val searchKeyword = s.toString()
                // Check if searchKeyword is not empty
                if (searchKeyword.isNotEmpty()) {
                    // Filter the viewModel based on searchKeyword
                    val matchingResults = viewModel.filterHm
                        .filterKeys { it?.contains(searchKeyword, ignoreCase = true) ?: false }
                        .values

                    // Create a list of matching NewsItems
                    val matchingList =
                        matchingResults.map { NewsItems(role = HeaderType.NEWS, item = it) }

                    // Set the matchingList to the newsAdapter
                    newsAdapter.setItems(matchingList.toMutableList())
                } else {
                    // If searchKeyword is empty, add all news articles
                    newsArticleAdd()
                }

            }

            override fun afterTextChanged(p0: Editable?) {}
        })

        // Setting up sort functionality
        binding.sort.setOnClickListener {
            newsAdapter.clearItems()
            if (viewModel.getSort() == getString(R.string.desc)) {
                viewModel.setSort(getString(R.string.asc))
            } else {
                viewModel.setSort(getString(R.string.desc))
            }
            newsArticleAdd()
        }
    }

    // Method to add news articles to adapter
    private fun newsArticleAdd() {
        lifecycleScope.launch {
            val newsData = viewModel.fetchNewsArticle()
            if (newsData != null) {
                newsAdapter.setItems(viewModel.getFilteredData(newsData.articles))
            }
        }
    }

    // Handling view more action
    override fun viewMoreClicked(url: String?) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.`package` = "com.android.chrome"
        startActivity(intent)
    }
}
