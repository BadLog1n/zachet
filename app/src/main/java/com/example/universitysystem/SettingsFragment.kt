package com.example.universitysystem

import android.annotation.SuppressLint
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
import org.json.JSONArray
import org.jsoup.Connection
import org.jsoup.Jsoup
import ratingUniversity.InfoOfStudent
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

private lateinit var database: DatabaseReference

class SettingsFragment : Fragment(R.layout.fragment_settings) {
    private val authCheck = AuthCheck()
    private val infoOfStudent = InfoOfStudent()

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
        val updateBtn = view.findViewById<Button>(R.id.updateBtn)
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
                GlobalScope.launch {
                    try {

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
                                getDataOfStudent(
                                    sharedPref,
                                    loginWebInputString,
                                    passwordWebInputString
                                )
                                Toast.makeText(
                                    requireContext(),
                                    "Подтверждено!",
                                    Toast.LENGTH_SHORT
                                ).show()
                                updateBtn.visibility = View.VISIBLE
                            } else Toast.makeText(
                                requireContext(), "Не удается авторизоваться на сайте," +
                                        " проверьте вводимый логин и пароль", Toast.LENGTH_SHORT
                            ).show()

                        }

                    } catch (e: Exception) {
                        Log.d("tag", e.toString())
                        // Toast.makeText(requireContext(), e.toString(), Toast.LENGTH_SHORT).show()

                    }
                }
            }
            builder.setNeutralButton("Нет") { _, _ ->
            }
            val alertDialog = builder.create()
            alertDialog.show()

            val autoBtn = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE)
            with(autoBtn) {
                setTextColor(Color.BLACK)
            }
            val userBtn = alertDialog.getButton(DialogInterface.BUTTON_NEUTRAL)
            with(userBtn) {
                setTextColor(Color.BLACK)
            }
        }
        updateBtn.setOnClickListener {
            val builder = AlertDialog.Builder(requireContext())
            builder.setMessage("Обновить данные о семестрах?")
            builder.setPositiveButton("Да") { _, _ ->
                try {
                    GlobalScope.launch {

                        val loginWebInputString = loginWebInput.text.toString()
                        val passwordWebInputString = passwordWebInput.text.toString()

                        withContext(Dispatchers.Main) {
                            getDataOfStudent(
                                sharedPref,
                                loginWebInputString,
                                passwordWebInputString
                            )
                            Toast.makeText(
                                requireContext(),
                                "Успешно обновлено!",
                                Toast.LENGTH_SHORT
                            ).show()


                        }
                    }
                } catch (e: Exception) {
                    Toast.makeText(
                        requireContext(), "Не удается обновить данные с сайта." +
                                "Пожалуйста, попробуйте позже.", Toast.LENGTH_SHORT
                    ).show()

                }
            }
            builder.setNeutralButton("Нет") { _, _ ->
            }
            val alertDialog = builder.create()
            alertDialog.show()

            val autoBtn = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE)
            with(autoBtn) {
                setTextColor(Color.BLACK)
            }
            val userBtn = alertDialog.getButton(DialogInterface.BUTTON_NEUTRAL)
            with(userBtn) {
                setTextColor(Color.BLACK)

            }

        }


    }

    @SuppressLint("SimpleDateFormat")
    @OptIn(DelicateCoroutinesApi::class)
    private fun getDataOfStudent(
        sharedPref: SharedPreferences?,
        login: String,
        password: String,
    ) {
        GlobalScope.launch {

            val infoOfStudent = getSemester(login, password)
            withContext(Dispatchers.Main) {
                if (infoOfStudent != null) {

                    val dateFormat: DateFormat = SimpleDateFormat("MM")
                    val date = Date()
                    val month = dateFormat.format(date)
                    val arrayOfSemester = arrayOf("1", "9", "10", "11", "12")
                    val semesterCurrent = month in arrayOfSemester


                    val semester = (infoOfStudent[1] + infoOfStudent[2]).toMutableList()
                    semester.sort()
                    semester.reverse()
                    sharedPref?.edit()?.putInt("lastSemester", semester.first().toInt())
                        ?.apply()
                    semester.forEachIndexed { index, element ->
                        val correctSemester = element.toInt() - 1
                        semester[index] = "Семестр $correctSemester"
                    }
                    if (semesterCurrent) semester.removeFirst()
                    val stringSemester = semester.joinToString(separator = ",")


                    sharedPref?.edit()?.putString("listOfSemester", stringSemester)
                        ?.apply()
                    sharedPref?.edit()?.putString("groupOfStudent", infoOfStudent[0][1])
                        ?.apply()
                    sharedPref?.edit()?.putString("formOfStudent", infoOfStudent[0][0])
                        ?.apply()
                    /*              Toast.makeText(requireContext(), item, Toast.LENGTH_SHORT)
                                      .show()*/


                }
            }
        }
    }


    /*                           returnRating(
                                login,
                                infoOfStudent[0][1],
                                semester.first(),
                                infoOfStudent[0][0],
                                "false"
                            )*/


    private fun getFormAndGroup(login: String, password: String): ArrayList<String>? {
        try {
            val arrayToReturn = arrayListOf("", "")
            val document: String
            val sitePath =
                "https://info.swsu.ru/scripts/student_diplom/auth.php?act=auth&login=$login&password=$password&type=array"

            val response: Connection.Response = Jsoup.connect(sitePath)
                .userAgent("Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/535.21 (KHTML, like Gecko) Chrome/19.0.1042.0 Safari/535.21")
                .timeout(30000)
                .execute()

            val statusCode: Int = response.statusCode()

            if (statusCode == 200) {
                document = Jsoup.connect(sitePath).get().text()
            } else return null
            arrayToReturn[0] = infoOfStudent.getFormOfStudy(document)
            arrayToReturn[1] = infoOfStudent.getGroupOfStudent(document)
            return arrayToReturn

        } catch (e: Exception) {
            Log.d("getFormAndGroup", e.toString())


            return null
        }
    }


    private fun getSemester(login: String, password: String): ArrayList<ArrayList<String>>? {
        try {
            val arrayToReturn = arrayListOf<ArrayList<String>>()
            val infoStudent = getFormAndGroup(login, password) ?: return null
            arrayToReturn.add(infoStudent)
            var document: String
            val sitePath =
                arrayOf(
                    "https://info.swsu.ru/scripts/student_diplom/auth.php?act=semestr&group=${infoStudent[1]}&status=false&type=json",
                    "https://info.swsu.ru/scripts/student_diplom/auth.php?act=semestr&group=${infoStudent[1]}&status=true&type=json"
                )
            for (item in sitePath) {
                val response: Connection.Response = Jsoup.connect(item)
                    .userAgent("Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/535.21 (KHTML, like Gecko) Chrome/19.0.1042.0 Safari/535.21")
                    .timeout(30000)
                    .execute()
                val statusCode: Int = response.statusCode()
                if (statusCode == 200) {
                    document = Jsoup.connect(item).get().text()
                } else return null
                val jsonArray = JSONArray(document)
                arrayToReturn.add(infoOfStudent.getSemesterOfStudent(jsonArray))
            }
            return arrayToReturn
        } catch (e: Exception) {
            Log.d("getFormAndGroup", e.toString())


        }
        return null
    }


}


//Только английские буквы или цифры
//Только русские буквы


//В будущем:
//При изменении пароля придётся заново авторизовываться.