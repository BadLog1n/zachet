package com.oneseed.zachet.activities

import android.app.DownloadManager
import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Environment.DIRECTORY_DOWNLOADS
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.davemorrissey.labs.subscaleview.ImageSource
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.oneseed.zachet.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class ImageActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        setContentView(R.layout.activity_image)
        val r: FloatArray = floatArrayOf(00f, 00f, 00f)
        window.setBackgroundDrawable(ColorDrawable(Color.HSVToColor(80, r)))
        val chatName: String = intent.getStringExtra("chatName").toString()
        val fileName: String = intent.getStringExtra("fileName").toString()
        super.onCreate(savedInstanceState)
        displayImage(fileName, chatName)
        findViewById<ImageButton>(R.id.backFromImgBtn).setOnClickListener {
            this.onBackPressedDispatcher.onBackPressed()

        }
        findViewById<ImageButton>(R.id.saveImgBtn).setOnClickListener {
            download(fileName, chatName)
            //Toast.makeText(this,"Сохранение..",Toast.LENGTH_SHORT).show()
        }
    }


    /**
     * Функция, которая отображает фотографии с сервера в приложении. [fileName] - имя файла с расширением,
     * [chatName] - имя чата между пользователями.
     * */
    private fun displayImage(fileName: String, chatName: String) =
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val imageRef = Firebase.storage.reference
                val imageView = findViewById<SubsamplingScaleImageView>(R.id.bigImg)
                val maxDownloadSize = 5L * 1024 * 1024 * 1024
                val bytes = imageRef.child("$chatName/$fileName").getBytes(maxDownloadSize).await()
                val bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                withContext(Dispatchers.Main) {
                    imageView?.setImage(ImageSource.bitmap(bmp))
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    //Toast.makeText(this@ImageActivity, e.message, Toast.LENGTH_LONG).show()
                    Toast.makeText(this@ImageActivity, "Фото не обнаружено", Toast.LENGTH_SHORT)
                        .show()

                }
            }
        }

    /**
     * Функция, позволяющая загружать файлы напрямую в телефон. [filename] - имя файла с расширением,
     * [chatName] - имя чата между пользователями.
     * */
    private fun download(filename: String, chatName: String) {
        val storageRef = Firebase.storage.reference
        val photoRef = storageRef.child(chatName).child(filename)
        val subFileName = filename.substring(filename.lastIndexOf("/") + 1)
        photoRef.downloadUrl.addOnSuccessListener { uri ->
                val url = uri.toString()
                Toast.makeText(this, "Загрузка файла началась", Toast.LENGTH_SHORT).show()
                downloadFile(subFileName, DIRECTORY_DOWNLOADS, url)
            }.addOnFailureListener { }
    }

    /**
     * Расширение функции загрузки фото, отвечает за загрузку файла
     * @param fileName имя файла без расширения
     * @param destinationDirectory путь загрузки
     * @param url ссылка на файл
     * */
    private fun downloadFile(
        fileName: String, destinationDirectory: String?, url: String?
    ) {
        val downloadManager = this.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val uri = Uri.parse(url)
        val request = DownloadManager.Request(uri)
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        request.setDestinationInExternalPublicDir(
            destinationDirectory, fileName
        )
        downloadManager.enqueue(request)
    }
}

