package com.oneseed.zachet.fragments

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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.oneseed.zachet.R
import java.util.concurrent.Executors


class LoginFragment : Fragment(R.layout.fragment_login) {

    /**Электронная почта*/
    private var email = ""
    /**Пароль пользователя.*/
    private var pw = ""
    /**Проверка на нажатие кнопки назад.*/
    private var clickBack = false
    private lateinit var database: DatabaseReference
    private lateinit var firebaseAuth: FirebaseAuth
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val sharedPref: SharedPreferences? =
            activity?.getSharedPreferences(getString(R.string.settingsShared), MODE_PRIVATE)
        activity?.findViewById<DrawerLayout>(R.id.drawer)
            ?.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
        activity?.findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar1)?.isEnabled = false
        super.onViewCreated(view, savedInstanceState)

        //Если пользователь уже авторизован в приложении то его сразу перенаправляют в gradesFragment
        if (sharedPref?.getBoolean(getString(R.string.checkSettings), false) == true) {
            view.hideKeyboard()
            val isTeacher = sharedPref.getBoolean(getString(R.string.isTeacher), false)
            findNavController().navigate(R.id.gradesFragment)
            if (isTeacher) {
                findNavController().navigate(R.id.chatsFragment)
            }
        }
        firebaseAuth = FirebaseAuth.getInstance()
        view.findViewById<Button>(R.id.enterButton).setOnClickListener {
            email =
                (view.findViewById<TextInputLayout>(R.id.layoutEmail))?.editText?.text.toString()
            pw =
                (view.findViewById<TextInputLayout>(R.id.layoutPassword))?.editText?.text.toString()
            if (email.isNotEmpty() && pw.isNotEmpty()) {
                firebaseAuth.signInWithEmailAndPassword(email, pw).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(activity, "Успешный вход", Toast.LENGTH_SHORT).show()
                        database = FirebaseDatabase.getInstance()
                            .getReference("users/${firebaseAuth.uid.toString()}")
                        val requestToDatabase = database.get()
                        view.hideKeyboard()
                        sharedPref?.edit()?.putBoolean(getString(R.string.isTeacher), false)
                            ?.apply()
                        requestToDatabase.addOnSuccessListener {
                            activity?.findViewById<DrawerLayout>(R.id.drawer)
                                ?.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
                            if (it.child("isTeacher").value.toString() != "null") {
                                sharedPref?.edit()?.putBoolean(getString(R.string.isTeacher), true)
                                    ?.apply()
                                findNavController().navigate(R.id.chatsFragment)

                            } else {
                                findNavController().navigate(R.id.gradesFragment)

                            }
                            saveSettings(
                                email,
                                pw,
                                firebaseAuth.uid.toString(),
                                it.child("login").value.toString()
                            )
                        }
                    } else {
                        Toast.makeText(
                            activity, "Данные для входа неправильные", Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            } else {
                Toast.makeText(
                    activity, "Поля для ввода не должны быть пустыми", Toast.LENGTH_SHORT
                ).show()
            }

        }

        view.findViewById<TextView>(R.id.forgot_passw_btn).setOnClickListener {
            email =
                (view.findViewById<TextInputLayout>(R.id.layoutEmail))?.editText?.text.toString()
            if (email.isNotEmpty()) {
                firebaseAuth.sendPasswordResetEmail(email).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(
                            requireContext(),
                            "На почту отправлено письмо с восстановлением пароля",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        Toast.makeText(
                            requireContext(), "Аккаунт с этой почтой не найден", Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            } else {
                Toast.makeText(
                    requireContext(), "Почта не может быть пустой", Toast.LENGTH_SHORT
                ).show()
            }
        }

        view.findViewById<TextView>(R.id.registerOfferTw).setOnClickListener {
            findNavController().navigate(R.id.registrationFragment)
        }

        view.findViewById<TextView>(R.id.moveTelegram).setOnClickListener {
            val openTelegram = Intent(Intent.ACTION_VIEW, Uri.parse("https://t.me/zacheet"))
            startActivity(openTelegram)
        }

        view.findViewById<TextView>(R.id.moveGithub).setOnClickListener {
            val openGithub =
                Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/BadLog1n/zachet"))
            startActivity(openGithub)
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
                activity?.finish()
            }
        }
    }

    /**Сохраняет пользовательские данные в памяти.
     * @param email электронная почта
     * @param pw пароль пользователя
     * @param saveUserId логин пользователя
     * @param uid уникальный идентификатор пользователя
     * */
    private fun saveSettings(email: String, pw: String, uid: String, saveUserId: String) {
        val sharedPref: SharedPreferences? =
            activity?.getSharedPreferences("Settings", MODE_PRIVATE)
        sharedPref?.edit()?.putString(getString(R.string.emailShared), email)?.apply()
        sharedPref?.edit()?.putString(getString(R.string.savePassword), pw)?.apply()
        sharedPref?.edit()?.putString(getString(R.string.uid), uid)?.apply()
        sharedPref?.edit()?.putString(getString(R.string.loginShared), saveUserId)?.apply()
        sharedPref?.edit()?.putBoolean(getString(R.string.checkSettings), true)?.apply()

    }


    /**Скрывает клавиатуру.*/
    private fun View.hideKeyboard() {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(windowToken, 0)
    }

}

