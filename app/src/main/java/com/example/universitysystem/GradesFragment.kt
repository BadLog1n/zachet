package com.example.universitysystem

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.addCallback
import androidx.core.view.GravityCompat
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.universitysystem.databinding.ActivityMainBinding
import com.google.android.material.navigation.NavigationView
import kotlin.system.exitProcess


class GradesFragment : Fragment(R.layout.fragment_grades) {


    var clickBack = false
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            if (!clickBack) {
                Toast.makeText(activity, "Нажмите ещё раз, чтобы выйти", Toast.LENGTH_SHORT).show()
                clickBack = true
            } else {
                Thread.sleep(150)
                exitProcess(0)
            }
        }
        /*binding = DataBindingUtil.setContentView(this@GradesFragment,R.layout.activity_main)
        findViewById<NavigationView>(R.id.navViewGrades).setNavigationItemSelectedListener {
            when (it.itemId){
                R.id.grades_menu->
                    findNavController().navigate(R.id.gradesFragment)
                R.id.settings_menu->
                    findNavController().navigate(R.id.settingsFragment)
                R.id.help_menu->
                    findNavController().navigate(R.id.helpFragment)
            }
            findViewById<DrawerLayout>(R.id.drawer).closeDrawer(GravityCompat.START)
            true
        }

        view.apply {


        }*/
    }


}