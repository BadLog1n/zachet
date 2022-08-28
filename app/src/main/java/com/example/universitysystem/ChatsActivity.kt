package com.example.universitysystem

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.ActionBar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ChatsActivity : AppCompatActivity() {
    private var rcAdapter = ChatsAdapter()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chats)
        supportActionBar?.displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM
        supportActionBar?.setCustomView(R.layout.chats_action_bar)
        supportActionBar?.show()

        val recyclerView: RecyclerView = findViewById(R.id.chatsRcView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        rcAdapter.clearRecords()
        rcAdapter.chatsList =  ArrayList()
        rcAdapter.notifyDataSetChanged()
        recyclerView.adapter = rcAdapter
        initChatsRc()
        recyclerView.adapter = rcAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)


        findViewById<ImageButton>(R.id.menuFromChatsBtn).setOnClickListener {
            val intent =Intent(this,MainActivity::class.java)
            intent.putExtra("from_chats",true)
            startActivity(intent)
        }

    }

    private fun initChatsRc() {
        var chat = ChatPreview("Елена Петровна","5 минут назад","Здравствуйте")
        rcAdapter.addChatPreview(chat)
        rcAdapter.addChatPreview(chat)
    }
}