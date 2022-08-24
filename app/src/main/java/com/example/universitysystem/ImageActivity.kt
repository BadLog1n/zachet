package com.example.universitysystem

import android.app.Activity
import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton

class ImageActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image)
        findViewById<ImageButton>(R.id.closeImg_btn).setOnClickListener {
            this.onBackPressed()
        }
    }
}