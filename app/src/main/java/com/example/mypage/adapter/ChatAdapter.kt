package com.example.mypage.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mypage.R
import com.example.mypage.model.ChatMessage

class ChatAdapter(
    private val messages: MutableList<ChatMessage> = mutableListOf()
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val VIEW_TYPE_AI   = 0
        private const val VIEW_TYPE_USER = 1
    }

    inner class AiViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvMessage: TextView = view.findViewById(R.id.tvAiMessage)
        val tvTime: TextView    = view.findViewById(R.id.tvAiTime)
    }

    inner class UserViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvMessage: TextView = view.findViewById(R.id.tvUserMessage)
        val tvTime: TextView    = view.findViewById(R.id.tvUserTime)
    }

    override fun getItemViewType(position: Int) =
        if (messages[position].sender == ChatMessage.Sender.AI) VIEW_TYPE_AI else VIEW_TYPE_USER

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return if (viewType == VIEW_TYPE_AI) {
            AiViewHolder(inflater.inflate(R.layout.item_chat_ai, parent, false))
        } else {
            UserViewHolder(inflater.inflate(R.layout.item_chat_user, parent, false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val msg = messages[position]
        when (holder) {
            is AiViewHolder -> {
                holder.tvMessage.text = msg.text
                holder.tvTime.text    = msg.time
            }
            is UserViewHolder -> {
                holder.tvMessage.text = msg.text
                holder.tvTime.text    = msg.time
            }
        }
    }

    override fun getItemCount() = messages.size

    fun addMessage(message: ChatMessage) {
        messages.add(message)
        notifyItemInserted(messages.size - 1)
    }

    fun setMessages(newMessages: List<ChatMessage>) {
        messages.clear()
        messages.addAll(newMessages)
        notifyDataSetChanged()
    }
}
