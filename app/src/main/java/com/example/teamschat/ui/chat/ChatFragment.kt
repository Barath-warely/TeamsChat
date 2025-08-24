package com.example.teamschat.ui.chat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.teamschat.databinding.FragmentChatBinding
import com.example.teamschat.vm.ChatViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


class ChatFragment: Fragment() {
    private var _b: FragmentChatBinding? = null
    private val b get() = _b!!
    private val vm: ChatViewModel by viewModels()
    private val adapter = ChatAdapter()


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _b = FragmentChatBinding.inflate(inflater, container, false)
        return b.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        b.recycler.layoutManager = LinearLayoutManager(requireContext()).apply { stackFromEnd = true }
        b.recycler.adapter = adapter


        fun sendNow(msg: String){ vm.send(msg) { b.input.setText("") } }


        b.btnSend.setOnClickListener { val t = b.input.text.toString().trim(); if (t.isNotEmpty()) sendNow(t) }
        b.btnLoggedIn.setOnClickListener { sendNow("Logged in") }
        b.btnLoggedOut.setOnClickListener { sendNow("Logged out") }
        b.btnBreak.setOnClickListener { sendNow("Out for break") }
        b.btnBackToWork.setOnClickListener { sendNow("Back to work") }


        b.swipe.setOnRefreshListener { vm.load() }


        viewLifecycleOwner.lifecycleScope.launch {
            vm.chats.collectLatest { list ->
                adapter.submit(list)
                b.recycler.scrollToPosition(adapter.itemCount - 1)
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            vm.loading.collectLatest { b.swipe.isRefreshing = it }
        }
    }


    override fun onResume() {
        super.onResume()
        vm.load()
    }


    override fun onDestroyView() { super.onDestroyView(); _b = null }
}