package com.oneseed.zachet

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import authCheck.AuthCheck
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class HelpFragment : Fragment(R.layout.fragment_help) {
    private val authCheck = AuthCheck()
    private lateinit var database: DatabaseReference

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        authCheck.check(view, this@HelpFragment.context)

        activity?.findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar1)?.title =
            "О приложении"
        
        view.findViewById<Button>(R.id.getSupportBtn).setOnClickListener {
            database = FirebaseDatabase.getInstance().getReference("login")
            database.child("support").get().addOnSuccessListener { itLogin ->
                val support =
                    if (itLogin.exists()) itLogin.value.toString() else return@addOnSuccessListener
                val intent = Intent(this@HelpFragment.context, IndividualChatActivity::class.java)
                intent.putExtra(getString(R.string.getUser), support)
                startActivity(intent)
            }
        }

        view.findViewById<Button>(R.id.telegramBtn).setOnClickListener {
            val openTelegram = Intent(Intent.ACTION_VIEW, Uri.parse("https://t.me/zacheet"))
            startActivity(openTelegram)
        }

        var clickCount = 0
        val imageMain = view.findViewById<ImageView>(R.id.imageView2)
        imageMain.setOnClickListener {
            if (clickCount == 5) imageMain.rotation += 5f
            else clickCount += 1
        }

        view.findViewById<TextView>(R.id.rustoreBtn).setOnClickListener {
            val openRuStore = Intent(
                Intent.ACTION_VIEW, Uri.parse("https://apps.rustore.ru/app/com.oneseed.zachet")
            )
            startActivity(openRuStore)
        }

        view.findViewById<TextView>(R.id.githubBtn).setOnClickListener {
            val openGithub =
                Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/BadLog1n/zachet"))
            startActivity(openGithub)
        }

        view.findViewById<TextView>(R.id.nemGithub).setOnClickListener {
            val openGithub = Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/BadLog1n"))
            startActivity(openGithub)
        }

        view.findViewById<TextView>(R.id.nemTelegram).setOnClickListener {
            val openGithub = Intent(Intent.ACTION_VIEW, Uri.parse("https://t.me/fogtoad"))
            startActivity(openGithub)
        }

        view.findViewById<TextView>(R.id.sofGithub).setOnClickListener {
            val openGithub = Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/sssofi0101"))
            startActivity(openGithub)
        }

        view.findViewById<TextView>(R.id.sofTeleram).setOnClickListener {
            val openGithub = Intent(Intent.ACTION_VIEW, Uri.parse("https://t.me/sssofi01"))
            startActivity(openGithub)
        }

        val sharedPref: SharedPreferences? = activity?.getSharedPreferences(
            "Settings", AppCompatActivity.MODE_PRIVATE
        )
        val isTeacher = sharedPref?.getBoolean(getString(R.string.isTeacher), false)

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            if (isTeacher == true) {
                findNavController().navigate(R.id.chatsFragment)
            } else {
                findNavController().navigate(R.id.gradesFragment)
            }
        }


        val versionName = getAppVersion(requireContext())
        view.findViewById<TextView>(R.id.version).text = versionName

        Log.d("version", versionName)
        view.findViewById<Button>(R.id.checkVersionsBtn).setOnClickListener {
            database =
                FirebaseDatabase.getInstance().getReference("versionInfo").child("actualVersion")
            database.get().addOnSuccessListener {
                if (versionName < it.value.toString()) {
                    Log.d("it.value.toString()", it.value.toString())
                    val newVersion = it.value.toString()
                    val openDownloadFile = Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("https://github.com/BadLog1n/zachet/releases/download/$newVersion/$newVersion.apk")
                    )
                    startActivity(openDownloadFile)
                    Toast.makeText(
                        this@HelpFragment.context, "Загрузка началась", Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Toast.makeText(
                        this@HelpFragment.context,
                        "Установлена последняя версия",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

        }
    }

    private fun getAppVersion(context: Context?): String {
        var version = ""
        try {
            val pInfo = if (Build.VERSION.SDK_INT >= 33) {
                context?.packageManager?.getPackageInfo(
                    requireContext().packageName, PackageManager.PackageInfoFlags.of(0)
                )
            } else {
                @Suppress("DEPRECATION") context?.packageManager?.getPackageInfo(
                    requireContext().packageName, 0
                )
            }
            version = pInfo?.versionName ?: "0"
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }

        return version
    }

}