package com.oneseed.zachet

import android.annotation.SuppressLint
import android.content.*
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import authCheck.AuthCheck
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
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

    @SuppressLint("UseSwitchCompatOrMaterialCode")
    @OptIn(DelicateCoroutinesApi::class)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        authCheck.check(view, this@SettingsFragment.context)

        super.onViewCreated(view, savedInstanceState)
        activity?.findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar1)?.title =
            "Настройки"
        val loginEditText = view.findViewById<EditText>(R.id.loginText)
        val nameEditText = view.findViewById<EditText>(R.id.nameText)
        val surnameEditText = view.findViewById<EditText>(R.id.surnameText)
        val loginWebInput = view.findViewById<EditText>(R.id.lWebInput)
        val passwordWebInput = view.findViewById<EditText>(R.id.pWebInput)
        val updateBtn = view.findViewById<Button>(R.id.updateBtn)
        val switch = view.findViewById<Switch>(R.id.loadFromServerWeb)
        val saveOnServerWebCheckBox = view.findViewById<CheckBox>(R.id.saveOnServerWebCheckBox)

        val emailInputText = view.findViewById<EditText>(R.id.emailInputText)
        val emailHelpBtn = view.findViewById<ImageButton>(R.id.emailHelpBtn)


        val sharedPrefGrades: SharedPreferences? = activity?.getSharedPreferences(
            getString(R.string.gradesShared),
            Context.MODE_PRIVATE
        )
        val sharedPrefSettings: SharedPreferences? = activity?.getSharedPreferences(
            getString(R.string.settingsShared),
            Context.MODE_PRIVATE
        )
        val loginWeb =
            sharedPrefGrades?.getString(getString(R.string.loginWebShared), "").toString()
        loginWebInput.setText(loginWeb)
        val passwordWeb =
            sharedPrefGrades?.getString(getString(R.string.passwordWebShared), "").toString()
        passwordWebInput.setText(passwordWeb)
        if (loginWebInput.text.toString() != "") {
            updateBtn.visibility = View.VISIBLE
        }
        val user = Firebase.auth.currentUser

        val uid = user?.uid

        database = FirebaseDatabase.getInstance().getReference("users/$uid")
        val requestToDatabase = database.get()
        requestToDatabase.addOnSuccessListener {
            nameEditText.setText(if (it.child("name").value.toString() != "null") it.child("name").value.toString() else "")
            surnameEditText.setText(if (it.child("surname").value.toString() != "null") it.child("surname").value.toString() else "")
            loginEditText.setText(it.child("login").value.toString())
            emailInputText.setText(user?.email.toString())
            nameEditText.isEnabled = true
            surnameEditText.isEnabled = true
            emailInputText.isEnabled = true

        }



        switch.setOnCheckedChangeListener { _, _ ->
            database = FirebaseDatabase.getInstance().getReference("users/$uid")
            database.get().addOnSuccessListener {
                val switchState: Boolean = switch.isChecked

                if (switchState) {
                    loginWebInput.setText("")
                    passwordWebInput.setText("")
                    loginWebInput.isEnabled = false
                    passwordWebInput.isEnabled = false
                    if (it.child("loginWeb").exists()) {
                        loginWebInput.setText(it.child("loginWeb").value.toString())
                        passwordWebInput.setText(it.child("passwordWeb").value.toString())
                    }

                } else {
                    loginWebInput.isEnabled = true
                    passwordWebInput.isEnabled = true
                    val loginWebSwitch =
                        sharedPrefGrades?.getString(getString(R.string.loginWebShared), "")
                            .toString()
                    val passwordWebSwitch =
                        sharedPrefGrades?.getString(getString(R.string.passwordWebShared), "")
                            .toString()
                    loginWebInput.setText(loginWebSwitch)
                    passwordWebInput.setText(passwordWebSwitch)
                }
            }
        }

        view.findViewById<Button>(R.id.changePasswordBtn).setOnClickListener {
            val builder = AlertDialog.Builder(
                requireActivity()
            )
            builder
                .setTitle("Изменение пароля")
                .setView(R.layout.dialog_change_password)
                .setPositiveButton("OK", null)
                .setNeutralButton("Отмена", null)
                .create()
            val alertDialog = builder.create()
            alertDialog.show()

            val autoBtn = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE)
            val cancelBtn = alertDialog.getButton(DialogInterface.BUTTON_NEUTRAL)
            with(autoBtn) {
                setTextColor(Color.BLACK)
                setOnClickListener {
                    val oldPassword = alertDialog.findViewById<EditText>(R.id.oldPasswordEt)
                    val newPassword = alertDialog.findViewById<EditText>(R.id.newPasswordEt)
                    val password =
                        sharedPrefSettings?.getString(getString(R.string.savePassword), "null")
                            .toString()
                    if (password == oldPassword?.text.toString()) {
                        sharedPrefSettings?.edit()
                            ?.putString(
                                getString(R.string.savePassword),
                                newPassword?.text.toString()
                            )
                            ?.apply()
                        user!!.updatePassword(newPassword?.text.toString())
                        Toast.makeText(activity, "Успешно обновлено", Toast.LENGTH_SHORT).show()

                        alertDialog.cancel()
                    } else {
                        Toast.makeText(
                            activity,
                            "Предыдущий пароль не совпадает",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
            with(cancelBtn) {
                setTextColor(Color.BLACK)
            }

        }



        emailHelpBtn.setOnClickListener {
            Toast.makeText(
                requireContext(), "Почта необходима для возможности " +
                        "восстановления аккаунта", Toast.LENGTH_SHORT
            ).show()
        }

        view.findViewById<Button>(R.id.saveSettingsBtn).setOnClickListener {

            val builder = AlertDialog.Builder(requireContext())
            builder.setMessage("Сохранить изменения?")
            builder.setPositiveButton("Да") { _, _ ->
                val login = sharedPrefSettings?.getString(getString(R.string.loginShared), "null")
                    .toString()
                val loginText =
                    loginEditText.text.toString().lowercase()
                if (loginText.toIntOrNull()?.let { true } == true) {
                    Toast.makeText(
                        requireContext(),
                        "Логин не должен состоять только из цифр",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setPositiveButton
                }

                val databaseRef = FirebaseDatabase.getInstance().getReference("login")
                    .child(loginEditText.text.toString()).get()
                databaseRef.addOnSuccessListener { item ->
                    if (!item.exists()) {
                        FirebaseDatabase.getInstance().getReference("login")
                            .child(loginEditText.text.toString()).setValue(uid)
                        FirebaseDatabase.getInstance().getReference("login")
                            .child(login).removeValue()
                        database.child("login").setValue(loginEditText.text.toString())
                        sharedPrefSettings?.edit()
                            ?.putString(
                                getString(R.string.loginShared),
                                loginEditText.text.toString()
                            )
                            ?.apply()

                    } else if (login != loginEditText.text.toString()) {
                        Toast.makeText(requireContext(), "Логин уже занят", Toast.LENGTH_SHORT)
                            .show()
                    }


                }

                val surname = surnameEditText.text.toString()
                val name = surnameEditText.text.toString()
                if (surname.isNotEmpty() && surname.isNotBlank()) {
                    database.child("surname").setValue(surname)

                }
                if (name.isNotEmpty() && name.isNotBlank()) {
                    database.child("name").setValue(name)

                }
                if (android.util.Patterns.EMAIL_ADDRESS.matcher(emailInputText.text.toString())
                        .matches()
                ) {
                    user!!.updateEmail(emailInputText.text.toString())
                    sharedPrefSettings?.edit()
                        ?.putString(getString(R.string.emailShared), emailInputText.text.toString())
                        ?.apply()
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Ввденная почта некорректна",
                        Toast.LENGTH_SHORT
                    ).show()


                }

                Toast.makeText(
                    this@SettingsFragment.context,
                    "Сохранено",
                    Toast.LENGTH_SHORT
                ).show()


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
                                sharedPrefGrades?.edit()?.putString(
                                    getString(R.string.loginWebShared),
                                    loginWebInputString
                                )
                                    ?.apply()
                                sharedPrefGrades?.edit()
                                    ?.putString(
                                        getString(R.string.passwordWebShared),
                                        passwordWebInputString
                                    )
                                    ?.apply()
                                getDataOfStudent(
                                    sharedPrefGrades,
                                    loginWebInputString,
                                    passwordWebInputString
                                )
                                Toast.makeText(
                                    requireContext(),
                                    "Подтверждено!",
                                    Toast.LENGTH_SHORT
                                ).show()
                                if (saveOnServerWebCheckBox.isChecked) {
                                    database =
                                        FirebaseDatabase.getInstance().getReference("users/$uid")
                                    val loginPassWeb = mapOf(
                                        "loginWeb" to loginWebInputString,
                                        "passwordWeb" to passwordWebInputString,
                                    )
                                    database.updateChildren(loginPassWeb)

                                }
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
                        withContext(Dispatchers.Main) {
                            getDataOfStudent(
                                sharedPrefGrades,
                                loginWeb,
                                passwordWeb
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
                    sharedPref?.edit()
                        ?.putInt(getString(R.string.lastSemester), semester.first().toInt())
                        ?.apply()
                    semester.forEachIndexed { index, element ->
                        val correctSemester = element.toInt() - 1
                        semester[index] = "Семестр $correctSemester"
                    }
                    if (semesterCurrent) semester.removeFirst()
                    val stringSemester = semester.joinToString(separator = ",")


                    sharedPref?.edit()
                        ?.putString(getString(R.string.listOfSemester), stringSemester)
                        ?.apply()
                    sharedPref?.edit()
                        ?.putString(getString(R.string.groupOfStudent), infoOfStudent[0][1])
                        ?.apply()
                    sharedPref?.edit()
                        ?.putString(getString(R.string.formOfStudent), infoOfStudent[0][0])
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