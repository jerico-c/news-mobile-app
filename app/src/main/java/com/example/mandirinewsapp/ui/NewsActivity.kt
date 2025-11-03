package com.example.mandirinewsapp.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mandirinewsapp.R
import com.example.mandirinewsapp.adapters.HeadlineAdapter // Adapter baru untuk headline
import com.example.mandirinewsapp.adapters.RecyclerViewAdapter
import com.example.mandirinewsapp.api.ConfigNetwork
import com.example.mandirinewsapp.databinding.ActivityNewsBinding
import com.example.mandirinewsapp.models.Article
import com.example.mandirinewsapp.models.ResponseData
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.app.AlertDialog
import android.view.LayoutInflater
import com.example.mandirinewsapp.util.Constants
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class NewsActivity : AppCompatActivity() {
    private var binding: ActivityNewsBinding? = null
    private var adapter: RecyclerViewAdapter? = null // Adapter untuk "Recommendation"
    private var headlineAdapter: HeadlineAdapter? = null // Adapter untuk "Breaking News"
    private val articleList: MutableList<Article> = ArrayList() // List untuk "Recommendation"
    private var isLoading = false
    private var currentPage = 1
    private val pageSize = 10 // Ukuran halaman lebih kecil untuk "Recommendation"
    private var loadingDialog: AlertDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewsBinding.inflate(layoutInflater)
        setContentView(binding!!.root)

        enableEdgeToEdge()

        // Klik "See all"
        binding!!.seeAll.setOnClickListener {
            val intent = Intent(this, AllNews::class.java)
            startActivity(intent)
        }

        // Klik Ikon Pencarian
        binding!!.searchIcon.setOnClickListener {
            val intent = Intent(this, AllNews::class.java)
            startActivity(intent)
        }

        // --- Setup Recommendation List (Vertical) ---
        val layoutManager = LinearLayoutManager(this)
        binding!!.recyclerNews.layoutManager = layoutManager
        adapter = RecyclerViewAdapter(articleList)
        binding!!.recyclerNews.adapter = adapter

        // --- Setup Headline List (Horizontal) ---
        val headlineLayoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding!!.headlineRecycler.layoutManager = headlineLayoutManager

        // Muat data awal
        loadNews(currentPage)
        loadHeadlines()
        
        binding!!.recyclerNews.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val totalItemCount = layoutManager.itemCount
                val lastVisibleItem = layoutManager.findLastVisibleItemPosition()

                // Kriteria infinite scroll
                if (!isLoading && totalItemCount <= (lastVisibleItem + 3)) {
                    currentPage++
                    loadNews(currentPage)
                }
            }
        })
    }

    /**
     * Memuat berita untuk daftar "Recommendation" dengan infinite scrolling.
     */
    private fun loadNews(page: Int) {
        if (isLoading) return
        showLoading()
        isLoading = true

        // Mengambil berita dari 1 hari yang lalu
        val cal = Calendar.getInstance()
        cal.add(Calendar.DATE, -1)
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val fromDate = dateFormat.format(cal.time)

        ConfigNetwork.api.getNews(
            "indonesia", // Query default untuk "Recommendation"
            fromDate,
            "publishedAt",
            "en",
            Constants.API_KEY, // Pastikan API_KEY Anda terisi di Constants.kt
            page,
            pageSize
        ).enqueue(object : Callback<ResponseData?> {
            override fun onResponse(call: Call<ResponseData?>, response: Response<ResponseData?>) {
                isLoading = false
                hideLoading()
                val newArticles = response.body()?.articles ?: emptyList()
                if (newArticles.isNotEmpty()) {
                    // Gunakan addData untuk menambahkan ke list (Infinite Scroll)
                    adapter?.addData(newArticles)
                }
            }

            override fun onFailure(call: Call<ResponseData?>, t: Throwable) {
                isLoading = false
                hideLoading()
                Log.d("error", t.localizedMessage ?: "Unknown error")
            }
        })
    }

    /**
     * Memuat berita untuk daftar "Breaking News" horizontal.
     */
    private fun loadHeadlines(category: String = "general") {
        showLoading()
        // Menggunakan "us" untuk headlines internasional, sesuai kode asli
        ConfigNetwork.api.getHeadlines("us", Constants.API_KEY, category)
            .enqueue(object : Callback<ResponseData?> {
                override fun onResponse(call: Call<ResponseData?>, response: Response<ResponseData?>) {
                    hideLoading()
                    if (response.isSuccessful && response.body() != null) {
                        val dataNews: List<Article> = response.body()!!.articles
                        if (dataNews.isNotEmpty()) {
                            // Set adapter untuk horizontal recycler view
                            headlineAdapter = HeadlineAdapter(dataNews)
                            binding!!.headlineRecycler.adapter = headlineAdapter
                        }
                    }
                }

                override fun onFailure(call: Call<ResponseData?>, t: Throwable) {
                    hideLoading()
                    Log.d("error", t.localizedMessage ?: "Unknown error")
                }
            })
    }

    // Fungsi kategori (setupHeadlineCategoryClickListeners dan updateHeadlineCategory)
    // telah dihapus dari file ini karena dipindah ke AllNews.kt

    private fun showLoading() {
        if (loadingDialog != null && loadingDialog!!.isShowing) return
        val builder = AlertDialog.Builder(this)
        val inflater = LayoutInflater.from(this)
        val view = inflater.inflate(R.layout.dialog_loading, null)
        builder.setView(view)
        builder.setCancelable(false)
        loadingDialog = builder.create()
        loadingDialog?.show()
    }

    private fun hideLoading() {
        loadingDialog?.dismiss()
        loadingDialog = null
    }
}