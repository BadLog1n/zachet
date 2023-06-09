package com.oneseed.zachet.activities

import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.drawerlayout.widget.DrawerLayout.LOCK_MODE_LOCKED_CLOSED
import androidx.drawerlayout.widget.DrawerLayout.LOCK_MODE_UNLOCKED
import androidx.navigation.findNavController
import com.google.android.material.navigation.NavigationView
import com.oneseed.zachet.R
import com.oneseed.zachet.ui.authentication.LogoutFragment

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar1)
        setSupportActionBar(toolbar)
        val drawer = findViewById<DrawerLayout>(R.id.drawer)
        val navigationView = findViewById<NavigationView>(R.id.navViewGrades)

        val toggle = ActionBarDrawerToggle(
            this, drawer, toolbar, R.string.drawer_open, R.string.drawer_closed
        )

        val menuBtn = toolbar.findViewById<ImageButton>(R.id.menuBtn)
        //mainActionBar = supportActionBar!!
        drawer.setDrawerLockMode(LOCK_MODE_UNLOCKED)

        supportActionBar?.hide()
        val sharedPref: SharedPreferences? =
            this.getSharedPreferences(getString(R.string.settingsShared), MODE_PRIVATE)
        val menuIsRight = sharedPref?.getBoolean(getString(R.string.menuIsRight), false) == true

        if (menuIsRight) {
            val params = navigationView.layoutParams as DrawerLayout.LayoutParams
            params.gravity = GravityCompat.END
            navigationView.layoutParams = params
            menuBtn.visibility = View.VISIBLE
        } else {
            val params = navigationView.layoutParams as DrawerLayout.LayoutParams
            params.gravity = GravityCompat.START
            navigationView.layoutParams = params
            drawer.addDrawerListener(toggle)
            toggle.syncState()
        }

        fun whereToMove() {
            if (menuIsRight) {
                if (drawer.isDrawerOpen(GravityCompat.END)) {
                    drawer.closeDrawers()
                } else {
                    drawer.openDrawer(GravityCompat.END)
                }
            } else {
                if (drawer.isDrawerOpen(GravityCompat.START)) {
                    drawer.closeDrawers()
                } else {
                    drawer.openDrawer(GravityCompat.START)
                }
            }
        }
        if (sharedPref?.getBoolean(getString(R.string.checkSettings), false) == true) {
            toolbar.findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar1)
                .setOnClickListener {
                    whereToMove()
                }
            menuBtn.setOnClickListener {
                whereToMove()
            }
            toolbar.setNavigationOnClickListener {
                whereToMove()
            }
        }


        /*findViewById<ImageButton>(R.id.menuButton).setOnClickListener {
            findViewById<DrawerLayout>(R.id.drawer).openDrawer(GravityCompat.START)
        }*/

        if (findNavController(R.id.nav_host_fragment).currentDestination == findNavController(R.id.nav_host_fragment).findDestination(
                R.id.loginFragment
            )
        ) {
            supportActionBar?.show()
            drawer?.setDrawerLockMode(LOCK_MODE_LOCKED_CLOSED)
        }

        if (findNavController(R.id.nav_host_fragment).currentDestination == findNavController(R.id.nav_host_fragment).findDestination(
                R.id.chatsFragment
            )
        ) {
            toolbar.title = "Чаты"
        }


        val isSupportDarkTheme = sharedPref?.getString(
            getString(R.string.darkThemeShared), "Light"
        )

        when (isSupportDarkTheme) {
            "Light" -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
            "Auto" -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
            }
            "Night" -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            }
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
                            this, "Этот раздел закрыт для преподавателей", Toast.LENGTH_SHORT
                        ).show()
                    }

                }
                R.id.chats_menu -> {
                    findNavController(R.id.nav_host_fragment).navigate(R.id.chatsFragment)
                    toolbar.title = "Чаты"
                    supportActionBar?.show()

                }
                R.id.settings_menu -> {
                    findNavController(R.id.nav_host_fragment).navigate(R.id.settingsFragment)
                    toolbar.title = "Настройки"
                    supportActionBar?.show()
                }
                R.id.help_menu -> {
                    toolbar.title = "О приложении"
                    supportActionBar?.show()
                    findNavController(R.id.nav_host_fragment).navigate(R.id.helpFragment)
                }

                R.id.logout_menu -> {
                    LogoutFragment().show(
                        this.supportFragmentManager, LogoutFragment.TAG
                    )
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
                            this, "Этот раздел закрыт для преподавателей", Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
            if (menuIsRight) {
                drawer.closeDrawer(GravityCompat.END)
            } else {
                drawer.closeDrawer(GravityCompat.START)

            }
            true
        }
    }


    override fun onResume() {
        super.onResume()
        /**
         * Меняем заголовок экрана в toolbar при восстановлении фрагментов
         */
        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar1)
        when (findNavController(R.id.nav_host_fragment).currentDestination) {
            findNavController(R.id.nav_host_fragment).findDestination(R.id.gradesFragment) -> toolbar.title =
                "Мои баллы"
            findNavController(R.id.nav_host_fragment).findDestination(R.id.chatsFragment) -> toolbar.title =
                "Чаты"
            findNavController(R.id.nav_host_fragment).findDestination(R.id.helpFragment) -> toolbar.title =
                "О приложении"
            findNavController(R.id.nav_host_fragment).findDestination(R.id.settingsFragment) -> toolbar.title =
                "Настройки"
            findNavController(R.id.nav_host_fragment).findDestination(R.id.scheduleFragment) -> toolbar.title =
                "Расписание"
            findNavController(R.id.nav_host_fragment).findDestination(R.id.feedFragment) -> toolbar.title =
                "Лента"
        }
        supportActionBar?.show()
    }

}


