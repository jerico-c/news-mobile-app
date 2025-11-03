package com.example.mandirinewsapp.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mandirinewsapp.R
import com.example.mandirinewsapp.models.Article
import com.example.mandirinewsapp.ui.NewsDetail

class HeadlineAdapter(private val articles: List<Article>) :
    RecyclerView.Adapter<HeadlineAdapter.HeadlineViewHolder>() {

    class HeadlineViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val image: ImageView = itemView.findViewById(R.id.h_image)
        val title: TextView = itemView.findViewById(R.id.h_title)
        val source: TextView = itemView.findViewById(R.id.h_source)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HeadlineViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_headline, parent, false)
        return HeadlineViewHolder(view)
    }

    override fun getItemCount(): Int = articles.size

    override fun onBindViewHolder(holder: HeadlineViewHolder, position: Int) {
        val article = articles[position]

        holder.title.text = article.title
        holder.source.text = article.source.name

        Glide.with(holder.itemView.context)
            .load(article.urlToImage)
            .placeholder(R.drawable.no_images)
            .into(holder.image)

        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, NewsDetail::class.java)
            intent.putExtra("article", article)
            holder.itemView.context.startActivity(intent)
        }
    }
}