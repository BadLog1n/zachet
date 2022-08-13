package com.example.universitysystem

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.view.GravityCompat
import androidx.core.view.isVisible
import androidx.drawerlayout.widget.DrawerLayout
import androidx.drawerlayout.widget.DrawerLayout.LOCK_MODE_LOCKED_CLOSED
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.universitysystem.databinding.ActivityMainBinding
import com.google.android.material.navigation.NavigationView
import com.google.android.material.textfield.TextInputLayout


public lateinit var currentFragment:Fragment
class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private val checkSettings = "check_settings"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
       /* if (findNavController(R.id.nav_host_fragment).currentDestination != findNavController(R.id.nav_host_fragment).findDestination(R.id.loginFragment)){
            findViewById<DrawerLayout>(R.id.drawer).setDrawerLockMode(LOCK_MODE_LOCKED_CLOSED)

        }*/
        findViewById<DrawerLayout>(R.id.drawer).setDrawerLockMode(LOCK_MODE_LOCKED_CLOSED)

        /*this.supportFragmentManager.addFragmentOnAttachListener { fragmentManager, fragment ->
            if (fragment == R.id.loginFragment)
        }*/
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
