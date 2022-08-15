package com.example.universitysystem

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.drawerlayout.widget.DrawerLayout.LOCK_MODE_LOCKED_CLOSED
import androidx.drawerlayout.widget.DrawerLayout.LOCK_MODE_UNLOCKED
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
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

        findViewById<NavigationView>(R.id.navViewGrades).setNavigationItemSelectedListener {
            when (it.itemId){
                R.id.grades_menu->
                    findNavController(R.id.nav_host_fragment).navigate(R.id.gradesFragment)
                R.id.settings_menu->
                    findNavController(R.id.nav_host_fragment).navigate(R.id.settingsFragment)
                R.id.help_menu->
                    findNavController(R.id.nav_host_fragment).navigate(R.id.helpFragment)
                R.id.logout_menu-> {
                    findNavController(R.id.nav_host_fragment).navigate(R.id.logoutFragment)
                    /*LogoutFragment().show(
                        this.supportFragmentManager, LogoutFragment.TAG)*/
                    val modalBottomSheet = LogoutFragment()
                    modalBottomSheet.show(supportFragmentManager, LogoutFragment.TAG)
                    val sharedPref: SharedPreferences? = this.getPreferences(Context.MODE_PRIVATE)
                    //findViewById<DrawerLayout>(R.id.drawer).setDrawerLockMode(LOCK_MODE_LOCKED_CLOSED)
                    sharedPref?.edit()?.putBoolean(checkSettings, false)?.apply()
                    //findNavController(R.id.nav_host_fragment).navigate(R.id.loginFragment)
                }

            }
            findViewById<DrawerLayout>(R.id.drawer).closeDrawer(GravityCompat.START)
            true
        }
    }
}
