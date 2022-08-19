package com.example.universitysystem

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.drawerlayout.widget.DrawerLayout.LOCK_MODE_LOCKED_CLOSED
import androidx.drawerlayout.widget.DrawerLayout.LOCK_MODE_UNLOCKED
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.universitysystem.databinding.ActivityMainBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_COLLAPSED
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.navigation.NavigationView


class MainActivity : AppCompatActivity() {


    private val checkSettings = "check_settings"
    private val checkLogin = "check_login"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<DrawerLayout>(R.id.drawer).setDrawerLockMode(LOCK_MODE_LOCKED_CLOSED)
        val sharedPref: SharedPreferences? = this.getPreferences(Context.MODE_PRIVATE)
        if (sharedPref?.getBoolean(checkLogin, false) == true){
            findViewById<DrawerLayout>(R.id.drawer).setDrawerLockMode(LOCK_MODE_UNLOCKED)
        }
        findViewById<ImageButton>(R.id.menuButton).setOnClickListener {
            findViewById<DrawerLayout>(R.id.drawer).openDrawer(GravityCompat.START)
        }
        if (findNavController(R.id.nav_host_fragment).currentDestination ==
            findNavController(R.id.nav_host_fragment).findDestination(R.id.gradesFragment)){
            findViewById<TextView>(R.id.header_tv).text = "Мои баллы"
        }
        findViewById<NavigationView>(R.id.navViewGrades).setNavigationItemSelectedListener {
            when (it.itemId){
                R.id.grades_menu-> {
                    findViewById<TextView>(R.id.header_tv).text = "Мои баллы"
                    findNavController(R.id.nav_host_fragment).navigate(R.id.gradesFragment)
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
