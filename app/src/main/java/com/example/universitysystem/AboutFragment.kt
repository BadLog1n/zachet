package com.example.universitysystem

import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import authCheck.AuthCheck
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class HelpFragment : Fragment(R.layout.fragment_help) {
    private val authCheck = AuthCheck()
    private lateinit var database: DatabaseReference

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        authCheck.check(view, this@HelpFragment.context)

        activity?.findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar1)?.title =
            "О приложении"


        view.findViewById<Button>(R.id.getSupportBtn).setOnClickListener {
            val intent =
                Intent(this@HelpFragment.context, IndividualChatActivity::class.java)
            intent.putExtra("getUser", "11")
            startActivity(intent)
        }

        database = FirebaseDatabase.getInstance().getReference("version")
        val requestToDatabase = database.get()
        val versionName = getAppVersion(requireContext())
        view.findViewById<TextView>(R.id.version).text = versionName

        Log.d("version", versionName)
        view.findViewById<Button>(R.id.checkVersionsBtn).setOnClickListener {
            requestToDatabase.addOnSuccessListener {
                if (versionName < it.value.toString()) {
                    download(it.value.toString(), requireContext())
                    Toast.makeText(this@HelpFragment.context, "Загрузка началась", Toast.LENGTH_SHORT).show()
                }
                else {
                    Toast.makeText(this@HelpFragment.context, "Установлена последняя версия", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


    private fun getAppVersion(context: Context?): String {
        var version = ""
        try {
            val pInfo = context?.packageManager?.getPackageInfo(requireContext().packageName, 0)
            version = pInfo!!.versionName
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }

        return version
    }


    private fun download(version: String, context: Context) {
        Log.d("version", version)

        val storageRef = Firebase.storage.reference

        val fileRef = storageRef.child("app").child("$version.apk")
        Log.d("version", fileRef.toString())
        fileRef.downloadUrl
            .addOnSuccessListener { uri ->
                val url = uri.toString()
                Toast.makeText(context, "Загрузка файла началась", Toast.LENGTH_SHORT).show()
                downloadFile(context, "$version.apk", Environment.DIRECTORY_DOWNLOADS, url)
            }.addOnFailureListener { }


    }

    /**
     * Расширение функции загрузки фото
     * */
    private fun downloadFile(
        context: Context,
        fileName: String,
        destinationDirectory: String?,
        url: String?
    ) {
        val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val uri = Uri.parse(url)
        val request = DownloadManager.Request(uri)
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        request.setDestinationInExternalPublicDir(
            destinationDirectory,
            fileName
        )
        downloadManager.enqueue(request)
    }
}