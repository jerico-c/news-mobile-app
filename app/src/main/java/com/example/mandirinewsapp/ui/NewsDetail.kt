package com.example.mandirinewsapp.ui

import android.content.Intent
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.mandirinewsapp.R
import com.example.mandirinewsapp.databinding.ActivityNewsDetailBinding
import com.example.mandirinewsapp.models.Article

class NewsDetail : AppCompatActivity() {
    private lateinit var binding: ActivityNewsDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityNewsDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val article = intent.getParcelableExtra<Article>("article")

        article?.let {
            Glide.with(this).load(it.urlToImage).placeholder(R.drawable.no_images).into(binding.image)
            binding.title.text = it.title

            binding.author.text = if (!it.author.isNullOrEmpty()) it.author else it.source.name

            binding.date.text = it.publishedAt?.split("T")?.get(0) ?: "Unknown Date"

            binding.desc.text = it.content?.replace("\n", " ") ?: "No content available."

            // --- PERBAIKAN DI SINI ---
            // ID 'toolbar_title' dan 'backButton' sekarang diakses langsung dari 'binding'

            // Teks "Details" sudah di-set di XML, jadi baris ini bisa dihapus atau biarkan
            binding.toolbarTitle.text = "Details"

            // Gunakan ID 'backButton' dari XML
            binding.backButton.setOnClickListener {
                finish()
            }
            // --- AKHIR PERBAIKAN ---

            Log.d("NewsDetail", "Content: ${article.content}")
        }
    }
}