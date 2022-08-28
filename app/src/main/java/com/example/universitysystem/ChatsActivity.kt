package com.example.universitysystem

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.ActionBar

class ChatsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chats)
        supportActionBar?.displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM
        supportActionBar?.setCustomView(R.layout.chats_action_bar)
        supportActionBar?.show()

        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        findViewById<android.widget.SearchView>(R.id.chats_searchView).apply {
            setSearchableInfo(searchManager.getSearchableInfo(componentName))
            setIconifiedByDefault(false)
        }
        findViewById<ImageButton>(R.id.menuFromChatsBtn).setOnClickListener {
            val intent =Intent(this,MainActivity::class.java)
            intent.putExtra("from_chats",true)
            startActivity(intent)
        }

    }
}