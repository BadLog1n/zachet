package com.example.universitysystem

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.addCallback
import androidx.drawerlayout.widget.DrawerLayout
import androidx.drawerlayout.widget.DrawerLayout.LOCK_MODE_LOCKED_CLOSED
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.textfield.TextInputLayout
import java.util.concurrent.Executors
import kotlin.system.exitProcess


class LoginFragment : Fragment(R.layout.fragment_login) {
    private val saveUserid = "save_userid"
    private val savePassword = "save_password"
    private val checkSettings = "check_settings"
    private val checkLogin = "check_login"
    private var un = ""
    private var pw = ""
    private var clickBack = false
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val sharedPref: SharedPreferences? = activity?.getSharedPreferences("Settings", MODE_PRIVATE)
        sharedPref?.edit()?.putBoolean(checkLogin, false)?.apply()

        activity?.findViewById<DrawerLayout>(R.id.drawer)?.setDrawerLockMode(LOCK_MODE_LOCKED_CLOSED)

        if (sharedPref?.getBoolean(checkSettings, false) == true) {
            loadSettings()
            view.hideKeyboard()
            sharedPref.edit()?.putBoolean(checkLogin, true)?.apply()
            activity?.findViewById<DrawerLayout>(R.id.drawer)?.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
            findNavController().navigate(R.id.gradesFragment)
        }
        view.findViewById<Button>(R.id.enterButton).setOnClickListener {
            saveSettings()
            view.hideKeyboard()
            sharedPref?.edit()?.putBoolean(checkLogin, true)?.apply()
            activity?.findViewById<DrawerLayout>(R.id.drawer)?.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
            activity?.findViewById<TextView>(R.id.header_tv)?.text = "Мои баллы"
            findNavController().navigate(R.id.gradesFragment)

        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            if (!clickBack) {
                Toast.makeText(activity, "Нажмите ещё раз, чтобы выйти", Toast.LENGTH_SHORT).show()
                clickBack = true
                val executor = Executors.newSingleThreadExecutor()
                executor.execute {
                    Thread.sleep(2000)
                    clickBack = false
                }
            } else {
                Thread.sleep(150)
                exitProcess(0)
            }
        }
    }

    private fun saveSettings() {
        un = (view?.findViewById<TextInputLayout>(R.id.layoutLogin))?.editText?.text.toString()
        pw = (view?.findViewById<TextInputLayout>(R.id.layoutPassword))?.editText?.text.toString()
        val sharedPref: SharedPreferences? = activity?.getSharedPreferences("Settings", MODE_PRIVATE)
        sharedPref?.edit()?.putString(saveUserid, un)?.apply()
        sharedPref?.edit()?.putString(savePassword, pw)?.apply()
        sharedPref?.edit()?.putBoolean(checkSettings, true)?.apply()

    }

    private fun loadSettings() {
        val sharedPref: SharedPreferences? = activity?.getSharedPreferences("Settings", MODE_PRIVATE)
        un = sharedPref?.getString(saveUserid, "").toString()
        pw = sharedPref?.getString(savePassword, "").toString()
        message()
    }

    private fun message() {
        Toast.makeText(activity, "$un\n$pw", Toast.LENGTH_SHORT).show()
    }

    private fun View.hideKeyboard() {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(windowToken, 0)
    }

}

