package com.example.universitysystem

import android.content.Context
import android.content.SharedPreferences
import android.os.AsyncTask
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.Toast
import androidx.activity.addCallback
import androidx.core.view.isVisible
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.textfield.TextInputLayout
import kotlin.system.exitProcess


class LoginFragment : Fragment(R.layout.fragment_login) {
    private val saveUserid = "save_userid"
    private val savePassword = "save_password"
    private val checkSettings = "check_settings"
    private var un = ""
    private var pw = ""
    private var clickBack = false
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val sharedPref: SharedPreferences? = activity?.getPreferences(Context.MODE_PRIVATE)

        //var ma = view.findViewById<DrawerLayout>(R.id.drawer)
        //var ma=activity?.gat
       // var ma = activity?.findViewById<DrawerLayout>(R.id.drawer)
        //ma?.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
       // activity?.findViewById<DrawerLayout>(R.id.drawer)?.isEnabled=false
        //activity?.findViewById<DrawerLayout>(R.id.drawer)?.isActivated=false
        //activity?.findViewById<DrawerLayout>(R.id.drawer)?.isVisible=false
        if (sharedPref?.getBoolean(checkSettings, false) == true) {
            loadSettings()
            view.hideKeyboard()
            findNavController().navigate(R.id.gradesFragment)
        }
        view.findViewById<Button>(R.id.enterButton).setOnClickListener {
            //findNavController().navigate(R.id.action_feedFragment_to_detailFragment2)
            //Toast.makeText(activity, "FFF", Toast.LENGTH_SHORT).show()
            saveSettings()
            view.hideKeyboard()
            findNavController().navigate(R.id.gradesFragment)

        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            if (!clickBack) {
                Toast.makeText(activity, "Нажмите ещё раз, чтобы выйти", Toast.LENGTH_SHORT).show()
                clickBack = true
                DoAsync {
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
        val sharedPref: SharedPreferences? = activity?.getPreferences(Context.MODE_PRIVATE)
        sharedPref?.edit()?.putString(saveUserid, un)?.apply()
        sharedPref?.edit()?.putString(savePassword, pw)?.apply()
        sharedPref?.edit()?.putBoolean(checkSettings, true)?.apply()

    }

    private fun loadSettings() {
        val sharedPref: SharedPreferences? = activity?.getPreferences(Context.MODE_PRIVATE)
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
    class DoAsync(val handler: () -> Unit) : AsyncTask<Void, Void, Void>() {
        init {
            execute()
        }

        @Deprecated("Deprecated in Java")
        override fun doInBackground(vararg params: Void?): Void? {
            handler()
            return null
        }
    }
}

