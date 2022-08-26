package com.example.universitysystem

import android.app.Activity
import android.content.DialogInterface
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.ortiz.touchview.TouchImageView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
class ImageActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        setContentView(R.layout.activity_image)
        val chatName:String = intent.getStringExtra("chatName").toString()
        val fileName:String = intent.getStringExtra("fileName").toString()
        super.onCreate(savedInstanceState)
        displayImage(fileName, chatName)
       findViewById<ImageButton>(R.id.backFromImgBtn).setOnClickListener {
            this.onBackPressed()
        }
        findViewById<Button>(R.id.saveBtn).setOnClickListener {
            Toast.makeText(this,"Сохранение..",Toast.LENGTH_SHORT).show()
        }
    }


    /**
     * Функция, которая отображает фотографии с сервера в приложении. [filename] - имя файла с расширением,
     * [chatName] - имя чата между пользователями.
     * */
    private fun displayImage(filename: String, chatName: String) = CoroutineScope(Dispatchers.IO).launch {
        try {
            val imageRef = Firebase.storage.reference
            val imageView = findViewById<TouchImageView>(R.id.bigImg)
            val maxDownloadSize = 5L * 1024 * 1024 * 1024
            val bytes = imageRef.child("$chatName/$filename").getBytes(maxDownloadSize).await()
            val bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
            withContext(Dispatchers.Main) {
                imageView?.setImageBitmap(bmp)
            }
        } catch(e: Exception) {
            withContext(Dispatchers.Main) {
                Toast.makeText(this@ImageActivity, e.message, Toast.LENGTH_LONG).show()
            }
        }
    }
}