package com.example.universitysystem

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.drawerlayout.widget.DrawerLayout.LOCK_MODE_LOCKED_CLOSED
import androidx.drawerlayout.widget.DrawerLayout.LOCK_MODE_UNLOCKED
import androidx.navigation.findNavController
import com.google.android.material.navigation.NavigationView


class MainActivity : AppCompatActivity() {
    //private val checkSettings = "check_settings"
    private val checkLogin = "check_login"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<DrawerLayout>(R.id.drawer).setDrawerLockMode(LOCK_MODE_LOCKED_CLOSED)
        val sharedPref: SharedPreferences? = this.getPreferences(Context.MODE_PRIVATE)
        if (sharedPref?.getBoolean(checkLogin, false) == true){
            findViewById<DrawerLayout>(R.id.drawer).setDrawerLockMode(LOCK_MODE_UNLOCKED)
        }
        if (intent.getBooleanExtra("from_chats",false)==true){
            findViewById<DrawerLayout>(R.id.drawer).openDrawer(GravityCompat.START)
        }
        findViewById<ImageButton>(R.id.menuButton).setOnClickListener {
            findViewById<DrawerLayout>(R.id.drawer).openDrawer(GravityCompat.START)
        }

        if (findNavController(R.id.nav_host_fragment).currentDestination ==
            findNavController(R.id.nav_host_fragment).findDestination(R.id.gradesFragment)){
            findViewById<TextView>(R.id.header_tv).text = "Мои баллы"
        }

        /**
         * Навигация по нажатию в боковом меню
         */
        findViewById<NavigationView>(R.id.navViewGrades).setNavigationItemSelectedListener {
            when (it.itemId){
                R.id.grades_menu-> {
                    findViewById<TextView>(R.id.header_tv).text = "Мои баллы"
                    findNavController(R.id.nav_host_fragment).navigate(R.id.gradesFragment)
                }
                R.id.chats_menu->{
                    //findViewById<TextView>(R.id.header_tv).text = "Чаты"
                    //findNavController(R.id.nav_host_fragment).navigate(R.id.chatsFragment)
                    val intent = Intent(this,ChatsActivity::class.java)
                    startActivity(intent)
                }
                R.id.settings_menu-> {
                    findViewById<TextView>(R.id.header_tv).text = "Настройки"
                    findNavController(R.id.nav_host_fragment).navigate(R.id.settingsFragment)
                }
                R.id.help_menu->{
                    findViewById<TextView>(R.id.header_tv).text = "Помощь"
                    findNavController(R.id.nav_host_fragment).navigate(R.id.helpFragment)
                }

                R.id.logout_menu-> {
                    LogoutFragment().show(
                        this.supportFragmentManager, LogoutFragment.TAG)
                    //val sharedPref: SharedPreferences? = this.getPreferences(Context.MODE_PRIVATE)
                    //haredPref?.edit()?.putBoolean(checkSettings, false)?.apply()
                    //findNavController(R.id.nav_host_fragment).navigate(R.id.loginFragment)
                }

            }
            findViewById<DrawerLayout>(R.id.drawer).closeDrawer(GravityCompat.START)
            true
        }
    }



}
