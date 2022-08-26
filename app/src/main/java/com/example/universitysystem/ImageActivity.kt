package com.example.universitysystem

import android.app.Activity
import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast

class ImageActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image)
        findViewById<ImageButton>(R.id.backFromImgBtn).setOnClickListener {
            this.onBackPressed()
        }
        findViewById<Button>(R.id.saveBtn).setOnClickListener {
            Toast.makeText(this,"Сохранение..",Toast.LENGTH_SHORT)
        }
    }
}