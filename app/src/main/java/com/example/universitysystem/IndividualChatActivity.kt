package com.example.universitysystem

import android.os.Bundle
import android.view.Gravity
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity

class IndividualChatActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_individual_chat)
        getSupportActionBar()?.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM)
        getSupportActionBar()?.setCustomView(R.layout.chat_action_bar)
        supportActionBar?.show()
        supportActionBar?.title = "Имя получателя"
        //var p = ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        //p.gravity = Gravity.CENTER
        //supportActionBar?.setLogo(R.drawable.ic_arrow_back_)
        findViewById<ImageButton>(R.id.backButton).setOnClickListener {
            onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
       // menuInflater.inflate(menuBack,menu)
        return super.onCreateOptionsMenu(menu)
    }
}