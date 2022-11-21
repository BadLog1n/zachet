package com.example.universitysystem

import android.content.Context
import android.content.DialogInterface
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import authCheck.AuthCheck
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.*
import org.jsoup.Connection
import org.jsoup.Jsoup
import kotlin.math.log

private lateinit var database: DatabaseReference

class SettingsFragment : Fragment(R.layout.fragment_settings) {
    private val authCheck = AuthCheck()

    @OptIn(DelicateCoroutinesApi::class)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        authCheck.check(view, this@SettingsFragment.context)

        super.onViewCreated(view, savedInstanceState)
        activity?.findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar1)?.title =
            "Настройки"
        val loginEditText = view.findViewById<EditText>(R.id.loginText)
        val passwordEditText = view.findViewById<EditText>(R.id.passText)
        val nameEditText = view.findViewById<EditText>(R.id.nameText)
        val surnameEditText = view.findViewById<EditText>(R.id.surnameText)
        val loginWebInput = view.findViewById<EditText>(R.id.loginWebInput)
        val passwordWebInput = view.findViewById<EditText>(R.id.passwordWebInput)
        val sharedPref: SharedPreferences? = activity?.getSharedPreferences(
            "Settings",
            Context.MODE_PRIVATE
        )
        val loginWeb = sharedPref?.getString("loginWeb", "").toString()
        loginWebInput.setText(loginWeb)
        val passwordWeb = sharedPref?.getString("passwordWeb", "").toString()
        passwordWebInput.setText(passwordWeb)

        val un = sharedPref?.getString("save_userid", "").toString()
        database = FirebaseDatabase.getInstance().getReference("users/$un")
        val requestToDatabase = database.get()
        requestToDatabase.addOnSuccessListener {
            nameEditText.setText(if (it.child("name").value.toString() != "null") it.child("name").value.toString() else "")
            surnameEditText.setText(if (it.child("surname").value.toString() != "null") it.child("surname").value.toString() else "")
            loginEditText.setText(it.child("login").value.toString())
            passwordEditText.setText(it.child("password").value.toString())
            nameEditText.isEnabled = true
            surnameEditText.isEnabled = true
            passwordEditText.isEnabled = true
        }


        view.findViewById<Button>(R.id.saveSettingsBtn).setOnClickListener {
            val builder = AlertDialog.Builder(requireContext())
            builder.setMessage("Сохранить изменения?")
            builder.setPositiveButton("Да") { _, _ ->
                if (passwordEditText.text.isNotBlank() && passwordEditText.text.isNotEmpty()) {
                    database.child("password").setValue(passwordEditText.text.toString())

                }
                if (nameEditText.text.isNotBlank() && nameEditText.text.isNotEmpty()) {
                    database.child("name").setValue(nameEditText.text.toString())

                }
                if (surnameEditText.text.isNotBlank() && surnameEditText.text.isNotEmpty()) {
                    database.child("surname").setValue(surnameEditText.text.toString())
                }
                Toast.makeText(
                    this@SettingsFragment.context,
                    "Успешно сохранено",
                    Toast.LENGTH_SHORT
                ).show()
                findNavController().navigate(R.id.gradesFragment)
            }
            builder.setNeutralButton("Нет") { _, _ ->
            }
            val alertDialog = builder.create()
            alertDialog.show()

            val autoBtn = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE)
            with(autoBtn) {
                setTextColor(Color.GREEN)
            }
            val userBtn = alertDialog.getButton(DialogInterface.BUTTON_NEUTRAL)
            with(userBtn) {
                setTextColor(Color.RED)
            }


        }
        view.findViewById<Button>(R.id.submitBtn).setOnClickListener {
            val builder = AlertDialog.Builder(requireContext())
            builder.setMessage("Подтвердить аккаунт?")
            builder.setPositiveButton("Да") { _, _ ->
                try {
                    GlobalScope.launch {

                        val loginWebInputString = loginWebInput.text.toString()
                        val passwordWebInputString = passwordWebInput.text.toString()
                        val sitePath =
                            "https://info.swsu.ru/scripts/student_diplom/auth.php?act=auth&login=$loginWebInputString&password=$passwordWebInputString&type=array"

                        val response: Connection.Response = Jsoup.connect(sitePath)
                            .userAgent("Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/535.21 (KHTML, like Gecko) Chrome/19.0.1042.0 Safari/535.21")
                            .timeout(30000)
                            .execute()

                        val statusCode: Int = response.statusCode()
                            val document =
                                if (statusCode == 200) Jsoup.connect(sitePath).get()
                                    .text() else ""
                        withContext(Dispatchers.Main) {

                        if (document != "") {
                                sharedPref?.edit()?.putString("loginWeb", loginWebInputString)
                                    ?.apply()
                                sharedPref?.edit()
                                    ?.putString("passwordWeb", passwordWebInputString)
                                    ?.apply()
                                Toast.makeText(
                                    requireContext(),
                                    "Подтверждено!",
                                    Toast.LENGTH_SHORT
                                ).show()

                            } else Toast.makeText(
                                requireContext(), "Не удается авторизоваться на сайте," +
                                        " проверьте вводимый логин и пароль", Toast.LENGTH_SHORT
                            ).show()

                        }
                    }
                } catch (e: Exception) {
                    Log.d("tag", e.toString())
                    // Toast.makeText(requireContext(), e.toString(), Toast.LENGTH_SHORT).show()


                }
            }
            builder.setNeutralButton("Нет") { _, _ ->
            }
            val alertDialog = builder.create()
            alertDialog.show()

            val autoBtn = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE)
            with(autoBtn) {
                setTextColor(Color.GREEN)
            }
            val userBtn = alertDialog.getButton(DialogInterface.BUTTON_NEUTRAL)
            with(userBtn) {
                setTextColor(Color.RED)
            }


        }

    }


}


//Только английские буквы или цифры
//Только русские буквы


//В будущем:
//При изменении пароля придётся заново авторизовываться.