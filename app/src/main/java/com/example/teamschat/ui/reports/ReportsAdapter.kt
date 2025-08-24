// ReportsAdapter.kt
package com.example.teamschat.ui.reports

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.teamschat.data.model.Chat
import com.example.teamschat.databinding.ItemChatReportBinding
import java.text.SimpleDateFormat
import java.util.Locale

class ReportsAdapter : RecyclerView.Adapter<ReportsAdapter.VH>() {

    private val items = mutableListOf<Chat>()

    fun submit(list: List<Chat>) {
        items.clear()
        items.addAll(list)
        notifyDataSetChanged()
    }

    class VH(val b: ItemChatReportBinding) : RecyclerView.ViewHolder(b.root)

    private val inFmt = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US) // best-effort
    private val outFmt = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val b = ItemChatReportBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VH(b)
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: VH, position: Int) {
        val c = items[position]
        holder.b.tvUser.text = c.user?.name ?: "Unknown"
        holder.b.tvMessage.text = c.message

        // created_at like: 2025-08-23T06:25:21.000000Z → trim to seconds to parse simply
        val pretty = try {
            val trimmed = c.created_at.substring(0, 19) // yyyy-MM-ddTHH:mm:ss
            val d = inFmt.parse(trimmed)
            if (d != null) outFmt.format(d) else c.created_at
        } catch (_: Exception) { c.created_at }

        holder.b.tvTime.text = pretty
    }
}
