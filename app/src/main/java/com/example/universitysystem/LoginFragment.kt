package com.example.universitysystem

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.textfield.TextInputLayout

class LoginFragment : Fragment(R.layout.fragment_login) {
    private val saveUserid = "save_userid"
    private val savePassword = "save_password"
    private val checkSettings = "check_settings"
    private var un = ""
    private var pw = ""
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val sharedPref: SharedPreferences? = activity?.getPreferences(Context.MODE_PRIVATE)

        if (sharedPref?.getBoolean(checkSettings, false) == true) {
            loadSettings()
            findNavController().navigate(R.id.gradesFragment)
        }
        view.findViewById<Button>(R.id.enterButton).setOnClickListener {
            //findNavController().navigate(R.id.action_feedFragment_to_detailFragment2)
            //Toast.makeText(activity, "FFF", Toast.LENGTH_SHORT).show()
            saveSettings()
            findNavController().navigate(R.id.gradesFragment)
        }
    }

    private fun saveSettings() {
        un = (view?.findViewById<EditText>(R.id.editTextLogin))?.text.toString()
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

}