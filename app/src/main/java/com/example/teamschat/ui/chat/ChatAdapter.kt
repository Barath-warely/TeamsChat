package com.example.teamschat.ui.chat

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.teamschat.data.model.Chat
import com.example.teamschat.data.storage.TokenManager
import com.example.teamschat.databinding.ItemChatLeftBinding
import com.example.teamschat.databinding.ItemChatRightBinding


class ChatAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val items = mutableListOf<Chat>()


    fun submit(list: List<Chat>) { items.clear(); items.addAll(list); notifyDataSetChanged() }


    override fun getItemViewType(position: Int): Int {
        val chat = items[position]
        return if (chat.user_id == TokenManager.user?.id) 1 else 0
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == 1) RightVH(ItemChatRightBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        else LeftVH(ItemChatLeftBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }


    override fun getItemCount() = items.size


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val c = items[position]
        if (holder is LeftVH) holder.bind(c) else if (holder is RightVH) holder.bind(c)
    }


    class LeftVH(private val b: ItemChatLeftBinding): RecyclerView.ViewHolder(b.root){
        fun bind(c: Chat){ b.txtName.text = c.user?.name ?: c.name ?: ""; b.txtMsg.text = c.message }
    }
    class RightVH(private val b: ItemChatRightBinding): RecyclerView.ViewHolder(b.root){
        fun bind(c: Chat){ b.txtName.text = "You"; b.txtMsg.text = c.message }
    }
}