package com.oneseed.zachet

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.addCallback
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.util.concurrent.Executors
import kotlin.system.exitProcess


class LoginFragment : Fragment(R.layout.fragment_login) {

    private var un = ""
    private var pw = ""
    private var clickBack = false
    private lateinit var database: DatabaseReference
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val sharedPref: SharedPreferences? = activity?.getSharedPreferences(getString(R.string.settingsShared), MODE_PRIVATE)
        activity?.findViewById<DrawerLayout>(R.id.drawer)
            ?.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
        sharedPref?.edit()?.putBoolean(getString(R.string.checkLogin), false)?.apply()

        //activity?.findViewById<DrawerLayout>(R.id.drawer)?.setDrawerLockMode(LOCK_MODE_LOCKED_CLOSED)
        //requireActivity().actionBar?.hide()

        if (sharedPref?.getBoolean(getString(R.string.checkSettings), false) == true) {
            loadSettings()
            view.hideKeyboard()
            sharedPref.edit()?.putBoolean(getString(R.string.checkLogin), true)?.apply()
            //activity?.findViewById<DrawerLayout>(R.id.drawer)?.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
            val isTeacher = sharedPref.getBoolean(getString(R.string.isTeacher), false)
            findNavController().navigate(R.id.gradesFragment)
            if (isTeacher)
            {
                findNavController().navigate(R.id.chatsFragment)
            }
        }

        view.findViewById<Button>(R.id.enterButton).setOnClickListener {
            un = (view.findViewById<TextInputLayout>(R.id.layoutLogin))?.editText?.text.toString()
            pw = (view.findViewById<TextInputLayout>(R.id.layoutPassword))?.editText?.text.toString()
            database = FirebaseDatabase.getInstance().getReference("users/$un")
            val requestToDatabase = database.get()
            requestToDatabase.addOnSuccessListener {
                if (pw == it.child("password").value.toString()){
                    saveSettings(un, pw)
                    view.hideKeyboard()
                    sharedPref?.edit()?.putBoolean(getString(R.string.checkLogin), true)?.apply()
                    activity?.findViewById<DrawerLayout>(R.id.drawer)?.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
                    //mainActionBar.show()
                    sharedPref?.edit()?.putBoolean(getString(R.string.isTeacher), false)?.apply()

                    if (it.child("teacher").value.toString() != "null")
                    {
                        sharedPref?.edit()?.putBoolean(getString(R.string.isTeacher), true)?.apply()
                        findNavController().navigate(R.id.chatsFragment)
                    }
                    else {
                        findNavController().navigate(R.id.gradesFragment)
                    }
                    //activity?.findViewById<TextView>(R.id.header_tv)?.text = "Мои баллы"

                }
                else{
                    Toast.makeText(activity, "Логин или пароль введён неверно.", Toast.LENGTH_SHORT).show()
                }
            }

        }

        view.findViewById<TextView>(R.id.forgot_passw_btn).setOnClickListener {
            val openTelegram =
                Intent(Intent.ACTION_VIEW, Uri.parse("https://t.me/+R5UnUTwVUEI1MjVi"))
            startActivity(openTelegram)
        }
        
        /*В случае нажатия кнопки назад просит повторить действие для успешного выхода из приложения
* В случае если в течение следующих 2-х секунд пользователь не нажал кнопку назад, в следующий раз,
* при нажатии кнопки "назад", пользователю это будеты предложено вновь*/
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

    private fun saveSettings(un: String, pw: String) {
        val sharedPref: SharedPreferences? = activity?.getSharedPreferences("Settings", MODE_PRIVATE)
        sharedPref?.edit()?.putString(getString(R.string.saveUserId), un)?.apply()
        sharedPref?.edit()?.putString(getString(R.string.savePassword), pw)?.apply()
        sharedPref?.edit()?.putBoolean(getString(R.string.checkSettings), true)?.apply()

    }

    private fun loadSettings() {
        val sharedPref: SharedPreferences? = activity?.getSharedPreferences("Settings", MODE_PRIVATE)
        un = sharedPref?.getString(getString(R.string.saveUserId), "").toString()
        pw = sharedPref?.getString(getString(R.string.savePassword), "").toString()
    }


    private fun View.hideKeyboard() {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(windowToken, 0)
    }

}
