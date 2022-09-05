package com.example.universitysystem

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.MotionEvent
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toolbar
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.drawable.toDrawable
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.drawerlayout.widget.DrawerLayout.LOCK_MODE_LOCKED_CLOSED
import androidx.drawerlayout.widget.DrawerLayout.LOCK_MODE_UNLOCKED
import androidx.navigation.findNavController
import com.google.android.material.navigation.NavigationView

//internal lateinit var mainActionBar:androidx.appcompat.app.ActionBar private set

class MainActivity : AppCompatActivity() {
    //private val checkSettings = "check_settings"
    private val checkLogin = "check_login"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar:androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar1)
        toolbar.title = "Мои баллы"
        setSupportActionBar(toolbar)

        val toggle = ActionBarDrawerToggle(this,findViewById<DrawerLayout>(R.id.drawer),toolbar,R.string.drawer_open,R.string.drawer_closed)
        toggle.onDrawerOpened(findViewById<DrawerLayout>(R.id.drawer)).apply {
            supportActionBar?.hide()
        }

        findViewById<DrawerLayout>(R.id.drawer).addDrawerListener(toggle)
        toggle.syncState()

        //mainActionBar = supportActionBar!!
        findViewById<DrawerLayout>(R.id.drawer).setDrawerLockMode(LOCK_MODE_LOCKED_CLOSED)

        supportActionBar?.hide()
        val sharedPref: SharedPreferences? = this.getPreferences(Context.MODE_PRIVATE)

        if (sharedPref?.getBoolean(checkLogin, false) == true){
            findViewById<DrawerLayout>(R.id.drawer).setDrawerLockMode(LOCK_MODE_UNLOCKED)
            supportActionBar?.show()

        }
        if (intent.getBooleanExtra("from_chats",false)==true){
            findViewById<DrawerLayout>(R.id.drawer).openDrawer(GravityCompat.START)
        }
        /*findViewById<ImageButton>(R.id.menuButton).setOnClickListener {
            findViewById<DrawerLayout>(R.id.drawer).openDrawer(GravityCompat.START)
        }*/

        if (findNavController(R.id.nav_host_fragment).currentDestination ==
            findNavController(R.id.nav_host_fragment).findDestination(R.id.gradesFragment)){
            toolbar.title = "Мои баллы"
            supportActionBar?.show()
            findViewById<DrawerLayout>(R.id.drawer)?.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
        }

        /**
         * Навигация по нажатию в боковом меню
         */
        findViewById<NavigationView>(R.id.navViewGrades).setNavigationItemSelectedListener {
            when (it.itemId){
                R.id.grades_menu-> {
                    toolbar.title = "Мои баллы"
                    supportActionBar?.show()
                    findNavController(R.id.nav_host_fragment).navigate(R.id.gradesFragment)
                }
                R.id.chats_menu->{
                    findNavController(R.id.nav_host_fragment).navigate(R.id.chatsFragment)
                    toolbar.title = "Чаты"
                    supportActionBar?.show()

                }
                R.id.settings_menu-> {
                    findNavController(R.id.nav_host_fragment).navigate(R.id.settingsFragment)
                    toolbar.title = "Настройки"
                    supportActionBar?.show()
                }
                R.id.help_menu->{
                    toolbar.title = "О приложении"
                    supportActionBar?.show()
                    findNavController(R.id.nav_host_fragment).navigate(R.id.helpFragment)
                }

                R.id.logout_menu-> {
                    LogoutFragment().show(
                        this.supportFragmentManager, LogoutFragment.TAG)
                    //val sharedPref: SharedPreferences? = this.getPreferences(Context.MODE_PRIVATE)
                    //haredPref?.edit()?.putBoolean(checkSettings, false)?.apply()
                    //findNavController(R.id.nav_host_fragment).navigate(R.id.loginFragment)
                    //findViewById<DrawerLayout>(R.id.drawer).setDrawerLockMode(LOCK_MODE_LOCKED_CLOSED)
                    //supportActionBar?.hide()

                    sharedPref?.edit()?.putBoolean(checkLogin, false)?.apply()
                }
                R.id.schedule_menu->{
                    toolbar.title = "Расписание"
                    supportActionBar?.show()
                    findNavController(R.id.nav_host_fragment).navigate(R.id.scheduleFragment)
                }

            }
            findViewById<DrawerLayout>(R.id.drawer).closeDrawer(GravityCompat.START)
            true
        }
    }


    override fun onResume() {
        super.onResume()
        /**
         * Меняем заголовок экрана в toolbar при восстановлении фрагментов
         */
        val toolbar:androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar1)
        when (findNavController(R.id.nav_host_fragment).currentDestination){
            findNavController(R.id.nav_host_fragment).findDestination(R.id.gradesFragment) -> toolbar.title = "Мои баллы"
            findNavController(R.id.nav_host_fragment).findDestination(R.id.chatsFragment) -> toolbar.title = "Чаты"
            findNavController(R.id.nav_host_fragment).findDestination(R.id.helpFragment) -> toolbar.title = "О приложении"
            findNavController(R.id.nav_host_fragment).findDestination(R.id.settingsFragment) -> toolbar.title = "Настройки"
        }
        supportActionBar?.show()
    }

}


