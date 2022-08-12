package com.example.universitysystem

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView
import com.google.android.material.textfield.TextInputLayout


class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private val checkSettings = "check_settings"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViewById<NavigationView>(R.id.navViewGrades).setNavigationItemSelectedListener {
            when (it.itemId){
                R.id.grades_menu->
                    findNavController(R.id.nav_host_fragment).navigate(R.id.gradesFragment)
                R.id.settings_menu->
                    findNavController(R.id.nav_host_fragment).navigate(R.id.settingsFragment)
                R.id.help_menu->
                    findNavController(R.id.nav_host_fragment).navigate(R.id.helpFragment)
                R.id.logout_menu-> {
                    val sharedPref: SharedPreferences? = this.getPreferences(Context.MODE_PRIVATE)
                    sharedPref?.edit()?.putBoolean(checkSettings, false)?.apply()
                    findNavController(R.id.nav_host_fragment).navigate(R.id.loginFragment)
                }

            }
            findViewById<DrawerLayout>(R.id.drawer).closeDrawer(GravityCompat.START)
            true
        }
    }
}