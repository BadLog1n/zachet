package com.example.zachet

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.drawerlayout.widget.DrawerLayout.LOCK_MODE_LOCKED_CLOSED
import androidx.drawerlayout.widget.DrawerLayout.LOCK_MODE_UNLOCKED
import androidx.navigation.findNavController
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity() {
    //private val checkSettings = "check_settings"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar:androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar1)
        setSupportActionBar(toolbar)

        val toggle = ActionBarDrawerToggle(this,findViewById(R.id.drawer),toolbar,R.string.drawer_open,R.string.drawer_closed)
        toggle.onDrawerOpened(findViewById<DrawerLayout>(R.id.drawer)).apply {
            supportActionBar?.hide()
        }

        findViewById<DrawerLayout>(R.id.drawer).addDrawerListener(toggle)
        toggle.syncState()

        //mainActionBar = supportActionBar!!
        findViewById<DrawerLayout>(R.id.drawer).setDrawerLockMode(LOCK_MODE_UNLOCKED)

        supportActionBar?.hide()
        val sharedPref: SharedPreferences? =
            this.getSharedPreferences(getString(R.string.settingsShared), MODE_PRIVATE)


        /*findViewById<ImageButton>(R.id.menuButton).setOnClickListener {
            findViewById<DrawerLayout>(R.id.drawer).openDrawer(GravityCompat.START)
        }*/

        if (findNavController(R.id.nav_host_fragment).currentDestination ==
            findNavController(R.id.nav_host_fragment).findDestination(R.id.loginFragment)
        ) {
            supportActionBar?.show()
            findViewById<DrawerLayout>(R.id.drawer)?.setDrawerLockMode(LOCK_MODE_LOCKED_CLOSED)
        }

        if (findNavController(R.id.nav_host_fragment).currentDestination ==
            findNavController(R.id.nav_host_fragment).findDestination(R.id.chatsFragment)
        ) {
            toolbar.title = "Чаты"
        }

        /**
         * Навигация по нажатию в боковом меню
         */
        findViewById<NavigationView>(R.id.navViewGrades).setNavigationItemSelectedListener {
            val isTeacher = sharedPref?.getBoolean(getString(R.string.isTeacher), false)


            when (it.itemId) {
                R.id.grades_menu -> {
                    if (isTeacher != true) {
                        toolbar.title = "Мои баллы"
                        supportActionBar?.show()
                        findNavController(R.id.nav_host_fragment).navigate(R.id.gradesFragment)
                    } else {
                        Toast.makeText(
                            this,
                            "Этот раздел закрыт для преподавателей",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                }
                R.id.chats_menu ->{
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


                }
                R.id.schedule_menu -> {
                    toolbar.title = "Расписание"
                    supportActionBar?.show()
                    findNavController(R.id.nav_host_fragment).navigate(R.id.scheduleFragment)
                }
                R.id.feed_menu -> {
                    if (isTeacher != true) {
                        toolbar.title = "Лента"
                        supportActionBar?.show()
                        findNavController(R.id.nav_host_fragment).navigate(R.id.feedFragment)
                    } else {
                        Toast.makeText(
                            this,
                            "Этот раздел закрыт для преподавателей",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
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
            findNavController(R.id.nav_host_fragment).findDestination(R.id.scheduleFragment) -> toolbar.title = "Расписание"
            findNavController(R.id.nav_host_fragment).findDestination(R.id.feedFragment) -> toolbar.title = "Лента"
        }
        supportActionBar?.show()
    }

}


