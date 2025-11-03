package com.example.mandirinewsapp.ui

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mandirinewsapp.R
import com.example.mandirinewsapp.adapters.RecyclerViewAdapter
import com.example.mandirinewsapp.api.ConfigNetwork
import com.example.mandirinewsapp.databinding.ActivityAllNewsBinding
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

class AllNews : AppCompatActivity() {
    private var binding: ActivityAllNewsBinding? = null
    private var adapter: RecyclerViewAdapter? = null

    // 'articleList' adalah master list dari API
    private val articleList: MutableList<Article> = ArrayList()
    // 'filteredList' adalah list yang dilihat oleh adapter
    private val filteredList: MutableList<Article> = ArrayList()

    private var isLoading = false
    private var currentPage = 1
    private val pageSize = Constants.QUERY_PAGE_SIZE
    private var loadingDialog: AlertDialog? = null
    private var currentCategoryQuery = "indonesia" // Default query

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAllNewsBinding.inflate(layoutInflater)
        setContentView(binding!!.root)

        // Tombol kembali
        binding!!.backButton.setOnClickListener {
            finish()
        }

        enableEdgeToEdge()

        val layoutManager = LinearLayoutManager(this)
        binding!!.recyclerNews.layoutManager = layoutManager

        // Penting: Inisialisasi adapter dengan filteredList
        adapter = RecyclerViewAdapter(filteredList)
        binding!!.recyclerNews.adapter = adapter

        // Setup listener kategori
        setupCategoryClickListeners()

        // Muat berita awal
        loadNews(currentPage)

        // Infinite scrolling
        binding!!.recyclerNews.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val totalItemCount = layoutManager.itemCount
                val lastVisibleItem = layoutManager.findLastVisibleItemPosition()

                if (!isLoading && totalItemCount <= (lastVisibleItem + 3)) {
                    currentPage++
                    loadNews(currentPage)
                }
            }
        })

        // Search listener
        binding!!.searchEdit.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterNews(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun loadNews(page: Int, query: String = currentCategoryQuery) {
        if (isLoading) return
        showLoading()
        isLoading = true

        // Jika ini adalah halaman pertama (awal atau ganti kategori), bersihkan list
        if (page == 1) {
            articleList.clear()
            // Kita akan membersihkan filteredList di dalam filterNews()
        }

        val cal = Calendar.getInstance()
        cal.add(Calendar.DATE, -1)
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val fromDate = dateFormat.format(cal.time)

        ConfigNetwork.api.getNews(
            query, // Gunakan query dari kategori
            fromDate,
            "publishedAt",
            "en",
            Constants.API_KEY, // Pastikan API KEY Anda ada di Constants.kt
            page,
            pageSize
        ).enqueue(object : Callback<ResponseData?> {
            override fun onResponse(call: Call<ResponseData?>, response: Response<ResponseData?>) {
                isLoading = false
                hideLoading()

                val newArticles = response.body()?.articles ?: emptyList()
                if (newArticles.isNotEmpty()) {
                    articleList.addAll(newArticles) // Tambah ke master list
                    filterNews(binding?.searchEdit?.text.toString()) // Terapkan filter pencarian
                }
            }

            override fun onFailure(call: Call<ResponseData?>, t: Throwable) {
                isLoading = false
                hideLoading()
                Log.d("error", t.localizedMessage ?: "Unknown error")
            }
        })
    }

    // --- PERBAIKAN DI SINI ---
    /**
     * Memfilter 'articleList' ke dalam 'filteredList' dan memberi tahu adapter.
     */
    private fun filterNews(query: String) {
        filteredList.clear() // Bersihkan list yang sedang ditampilkan

        if (query.isEmpty()) {
            // Jika query kosong, tampilkan semua dari master list
            filteredList.addAll(articleList)
        } else {
            // Jika ada query, filter master list dan masukkan ke filteredList
            filteredList.addAll(articleList.filter {
                it.title.contains(query, ignoreCase = true) ||
                        (it.description?.contains(query, ignoreCase = true) ?: false)
            })
        }

        // Langsung beri tahu adapter bahwa datanya berubah, JANGAN panggil updateData()
        adapter?.notifyDataSetChanged()
    }

    // --- Fungsi Kategori (Pindahan dari NewsActivity) ---

    private fun setupCategoryClickListeners() {
        val categories = listOf(
            binding!!.categoryAll,
            binding!!.categorySports,
            binding!!.categoryHealth,
            binding!!.categoryTechnology,
            binding!!.categoryScience,
            binding!!.categoryBusiness
        )

        for (category in categories) {
            category.setOnClickListener {
                val categoryText = category.text.toString().lowercase()
                currentCategoryQuery = if (categoryText == "all") "indonesia" else "indonesia AND $categoryText"

                updateCategoryUI(category, categories)

                // Muat ulang berita dengan kategori baru
                currentPage = 1
                loadNews(currentPage, currentCategoryQuery)
            }
        }
    }

    private fun updateCategoryUI(selectedTextView: TextView, allTextViews: List<TextView>) {
        for (textView in allTextViews) {
            if (textView == selectedTextView) {
                textView.setTypeface(ResourcesCompat.getFont(this, R.font.poppins_bold))
                textView.setTextColor(ContextCompat.getColor(this, R.color.dark_blue))
            } else {
                textView.setTypeface(ResourcesCompat.getFont(this, R.font.poppins_reg))
                textView.setTextColor(ContextCompat.getColor(this, R.color.black))
            }
        }
    }

    // --- Fungsi Loading ---

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