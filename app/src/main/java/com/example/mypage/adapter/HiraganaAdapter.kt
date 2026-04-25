package com.example.mypage.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mypage.R
import com.example.mypage.model.HiraganaChar

class HiraganaAdapter(
    private val chars: List<HiraganaChar>,
    private val onCharClick: (HiraganaChar, Int) -> Unit
) : RecyclerView.Adapter<HiraganaAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvHiragana: TextView  = view.findViewById(R.id.tvHiragana)
        val tvRomaji: TextView    = view.findViewById(R.id.tvRomaji)
        val tvHint: TextView      = view.findViewById(R.id.tvHint)
        val ivSound: ImageView    = view.findViewById(R.id.ivSound)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.item_hiragana_card, parent, false)
    )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = chars[position]
        holder.tvHiragana.text = item.character
        holder.tvRomaji.text   = item.romaji
        holder.tvHint.text     = item.hint

        holder.itemView.setOnClickListener {
            onCharClick(item, position)
        }

        holder.ivSound.setOnClickListener {
            // Sound button logic
        }
    }

    override fun getItemCount() = chars.size
}
