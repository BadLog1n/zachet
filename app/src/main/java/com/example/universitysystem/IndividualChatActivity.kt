package com.example.universitysystem

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class IndividualChatActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_individual_chat)
        supportActionBar?.title = "Имя получателя"
    }
}