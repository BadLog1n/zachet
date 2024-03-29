package com.oneseed.zachet.fragments

import android.content.Context
import android.content.DialogInterface
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.activity.addCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat.recreate
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import authCheck.AuthCheck
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.oneseed.zachet.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.jsoup.Connection
import org.jsoup.Jsoup
import ratingUniversity.InfoOfStudent


private lateinit var database: DatabaseReference

class SettingsFragment : Fragment(R.layout.fragment_settings) {
    private val authCheck = AuthCheck()
    private val infoOfStudent = InfoOfStudent()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        authCheck.check(view, this@SettingsFragment.context)

        super.onViewCreated(view, savedInstanceState)
        activity?.findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar1)?.title =
            "Настройки"
        val loginEditText = view.findViewById<EditText>(R.id.textLgn)
        val nameEditText = view.findViewById<EditText>(R.id.nameText)
        val surnameEditText = view.findViewById<EditText>(R.id.surnameText)
        val loginWebInput = view.findViewById<EditText>(R.id.lWebInput)
        val passwordWebInput = view.findViewById<EditText>(R.id.pWebInput)
        val updateBtn = view.findViewById<Button>(R.id.updateBtn)
        val switch = view.findViewById<SwitchMaterial>(R.id.loadFromServerWeb)
        val autoTheme = view.findViewById<SwitchMaterial>(R.id.autoTheme)
        val darkTheme = view.findViewById<SwitchMaterial>(R.id.darkTheme)
        val loadImages = view.findViewById<SwitchMaterial>(R.id.loadImages)
        val saveOnServerWebCheckBox = view.findViewById<CheckBox>(R.id.saveOnServerWebCheckBox)
        val emailInputText = view.findViewById<EditText>(R.id.emailInputText)
        val emailHelpBtn = view.findViewById<ImageButton>(R.id.emailHelpBtn)
        val menuMove = view.findViewById<SwitchMaterial>(R.id.menuMove)

        val sharedPrefGrades: SharedPreferences? = activity?.getSharedPreferences(
            getString(R.string.gradesShared), Context.MODE_PRIVATE
        )
        val sharedPrefSettings: SharedPreferences? = activity?.getSharedPreferences(
            getString(R.string.settingsShared), Context.MODE_PRIVATE
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
            loginEditText.isEnabled = true
            nameEditText.isEnabled = true
            surnameEditText.isEnabled = true
            emailInputText.isEnabled = true
        }

        val menuIsRight =
            sharedPrefSettings?.getBoolean(getString(R.string.menuIsRight), false) == true
        if (menuIsRight) menuMove.isChecked = true

        menuMove.setOnCheckedChangeListener { _, _ ->

            if (menuMove.isChecked && !menuIsRight) {
                sharedPrefSettings?.edit()?.putBoolean(
                    getString(R.string.menuIsRight), true
                )?.apply()
                recreate(requireActivity())
            } else if (menuIsRight) {
                sharedPrefSettings?.edit()?.putBoolean(
                    getString(R.string.menuIsRight), false
                )?.apply()
                recreate(requireActivity())

            }
        }

        val isTeacher = sharedPrefSettings?.getBoolean(getString(R.string.isTeacher), false)

        when (sharedPrefSettings?.getString(getString(R.string.darkThemeShared), "Light")) {
            "Auto" -> {
                autoTheme.isChecked = true
                darkTheme.isEnabled = false
            }
            "Night" -> {
                darkTheme.isChecked = true
                autoTheme.isEnabled = false
            }
        }
        if (sharedPrefSettings?.getBoolean(getString(R.string.loadImages), true) == true) {
            loadImages.isChecked = true
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            if (isTeacher == true) {
                findNavController().navigate(R.id.chatsFragment)
            } else {
                findNavController().navigate(R.id.gradesFragment)
            }
        }

        /**
         * Действия при смене положения switch элемента - "загрузить с сервера"
         */
        switch.setOnCheckedChangeListener { _, _ ->
            if (switch.isChecked) {
                loginWebInput.isEnabled = false
                passwordWebInput.isEnabled = false
                loginWebInput.setText("")
                passwordWebInput.setText("")
                database = FirebaseDatabase.getInstance().getReference("users/$uid")
                database.get().addOnSuccessListener {
                    if (it.child("loginWeb").exists()) {
                        loginWebInput.setText(it.child("loginWeb").value.toString())
                        passwordWebInput.setText(it.child("passwordWeb").value.toString())
                    }
                }
            } else {
                loginWebInput.isEnabled = true
                passwordWebInput.isEnabled = true
                val loginWebSwitch =
                    sharedPrefGrades?.getString(getString(R.string.loginWebShared), "").toString()
                val passwordWebSwitch =
                    sharedPrefGrades?.getString(getString(R.string.passwordWebShared), "")
                        .toString()
                loginWebInput.setText(loginWebSwitch)
                passwordWebInput.setText(passwordWebSwitch)
            }

        }

        view.findViewById<Button>(R.id.changePasswordBtn).setOnClickListener {
            val builder = AlertDialog.Builder(
                requireActivity()
            )
            builder.setTitle("Изменение пароля").setView(R.layout.dialog_change_password)
                .setPositiveButton("OK", null).setNeutralButton("Отмена", null).create()
            val alertDialog = builder.create()
            alertDialog.show()

            val autoBtn = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE)
            val cancelBtn = alertDialog.getButton(DialogInterface.BUTTON_NEUTRAL)
            with(autoBtn) {
                setTextColor(Color.BLACK)
                setOnClickListener {
                    val oldPassword =
                        alertDialog.findViewById<EditText>(R.id.oldPasswordEt)?.text.toString()
                    val newPassword =
                        alertDialog.findViewById<EditText>(R.id.newPasswordEt)?.text.toString()
                    val password =
                        sharedPrefSettings?.getString(getString(R.string.savePassword), "null")
                            .toString()
                    if (newPassword.isEmpty()) {
                        Toast.makeText(
                            activity, "Новый пароль не может быть пустым", Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        if (password == oldPassword) {
                            sharedPrefSettings?.edit()?.putString(
                                getString(R.string.savePassword), newPassword
                            )?.apply()
                            user!!.updatePassword(newPassword)
                            Toast.makeText(activity, "Успешно обновлено", Toast.LENGTH_SHORT).show()

                            alertDialog.cancel()
                        } else {
                            Toast.makeText(
                                activity, "Предыдущий пароль не совпадает", Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
            with(cancelBtn) {
                setTextColor(Color.BLACK)
            }

        }

        emailHelpBtn.setOnClickListener {
            Toast.makeText(
                requireContext(),
                "Почта необходима для возможности восстановления аккаунта",
                Toast.LENGTH_SHORT
            ).show()
        }

        view.findViewById<Button>(R.id.saveSettingsBtn).setOnClickListener {

            val builder = AlertDialog.Builder(requireContext())
            builder.setMessage("Сохранить изменения?")
            builder.setPositiveButton("Да") { _, _ ->
                while (true) {
                    val login =
                        sharedPrefSettings?.getString(getString(R.string.loginShared), "null")
                            .toString()
                    val loginText = loginEditText.text.toString().lowercase()
                    if (loginText.toIntOrNull()?.let { true } == true) {
                        Toast.makeText(
                            requireContext(),
                            "Логин не должен состоять только из цифр",
                            Toast.LENGTH_SHORT
                        ).show()
                        break
                    }
                    if (loginText.isEmpty()) {
                        Toast.makeText(
                            requireContext(), "Логин не может быть пустым", Toast.LENGTH_SHORT
                        ).show()
                        break
                    }
                    val surname = surnameEditText.text.toString()
                    val name = nameEditText.text.toString()
                    if (surname.isNotEmpty()) {
                        database.child("surname").setValue(surname)
                    }
                    if (name.isNotEmpty()) {
                        database.child("name").setValue(name)
                    }
                    if (android.util.Patterns.EMAIL_ADDRESS.matcher(emailInputText.text.toString())
                            .matches()
                    ) {
                        user!!.updateEmail(emailInputText.text.toString())
                        sharedPrefSettings?.edit()?.putString(
                            getString(R.string.emailShared), emailInputText.text.toString()
                        )?.apply()
                    } else {
                        Toast.makeText(
                            requireContext(), "Ввденная почта некорректна", Toast.LENGTH_SHORT
                        ).show()
                        break
                    }

                    val databaseRef =
                        FirebaseDatabase.getInstance().getReference("login").child(loginText).get()
                    databaseRef.addOnSuccessListener { item ->
                        if (!item.exists()) {
                            FirebaseDatabase.getInstance().getReference("login").child(loginText)
                                .setValue(uid)
                            FirebaseDatabase.getInstance().getReference("login").child(login)
                                .removeValue()
                            database.child("login").setValue(loginText)
                            sharedPrefSettings?.edit()?.putString(
                                getString(R.string.loginShared), loginText
                            )?.apply()

                        } else if (login != loginText) {
                            Toast.makeText(requireContext(), "Логин уже занят", Toast.LENGTH_SHORT)
                                .show()
                        } else {
                            Toast.makeText(
                                requireContext(), "Успешно сохранено", Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                    if (login != loginText) {
                        Toast.makeText(
                            requireContext(), "Успешно сохранено", Toast.LENGTH_SHORT
                        ).show()
                    }
                    break
                }
            }
            builder.setNeutralButton("Нет") { _, _ ->
            }
            val alertDialog = builder.create()
            alertDialog.show()
            alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(Color.BLACK)
            alertDialog.getButton(DialogInterface.BUTTON_NEUTRAL).setTextColor(Color.BLACK)


        }

        /**
         * Действия по кнопке "Подтвердить" данные аккаунта в брс
         */
        view.findViewById<Button>(R.id.submitBtn).setOnClickListener {
            val builder = AlertDialog.Builder(requireContext())
            builder.setMessage("Подтвердить аккаунт?")
            builder.setPositiveButton("Да") { _, _ ->
                Toast.makeText(
                    requireContext(), "Ожидайте подтверждения", Toast.LENGTH_SHORT
                ).show()
                lifecycleScope.launch {
                    try {
                        withContext(Dispatchers.IO) {

                            val loginWebInputString = loginWebInput.text.toString()
                            val passwordWebInputString = passwordWebInput.text.toString()
                            val sitePath =
                                "https://info.swsu.ru/scripts/student_diplom/auth.php?act=auth&login=$loginWebInputString&password=$passwordWebInputString&type=array"
                            val response: Connection.Response = Jsoup.connect(sitePath)
                                .userAgent("Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/535.21 (KHTML, like Gecko) Chrome/19.0.1042.0 Safari/535.21")
                                .timeout(30000).execute()
                            val statusCode: Int = response.statusCode()
                            val document =
                                if (statusCode == 200) Jsoup.connect(sitePath).get().text() else ""
                            withContext(Dispatchers.Main) {

                                if (document != "") {
                                    sharedPrefGrades?.edit()?.putString(
                                        getString(R.string.loginWebShared), loginWebInputString
                                    )?.apply()
                                    sharedPrefGrades?.edit()?.putString(
                                        getString(R.string.passwordWebShared),
                                        passwordWebInputString
                                    )?.apply()
                                    getDataOfStudent(
                                        sharedPrefGrades,
                                        loginWebInputString,
                                        passwordWebInputString,
                                        true
                                    )

                                    if (saveOnServerWebCheckBox.isChecked) {
                                        database =
                                            FirebaseDatabase.getInstance()
                                                .getReference("users/$uid")
                                        val loginPassWeb = mapOf(
                                            "loginWeb" to loginWebInputString,
                                            "passwordWeb" to passwordWebInputString,
                                        )
                                        database.updateChildren(loginPassWeb)

                                    }
                                } else Toast.makeText(
                                    requireContext(),
                                    "Не удается авторизоваться на сайте," + " проверьте вводимый логин и пароль",
                                    Toast.LENGTH_SHORT
                                ).show()

                            }
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

        /**
         * Действия по кнопке "Обновить список семестров"
         */
        updateBtn.setOnClickListener {
            val builder = AlertDialog.Builder(requireContext())
            builder.setMessage("Обновить данные о семестрах?")
            builder.setPositiveButton("Да") { _, _ ->
                try {
                    lifecycleScope.launch {
                        getDataOfStudent(
                            sharedPrefGrades, loginWeb, passwordWeb, false
                        )
                        Toast.makeText(
                            requireContext(), "Ожидайте обновления", Toast.LENGTH_SHORT
                        ).show()

                    }

                } catch (e: Exception) {
                    Toast.makeText(
                        requireContext(),
                        "Не удается обновить данные с сайта." + "Пожалуйста, попробуйте позже.",
                        Toast.LENGTH_SHORT
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
        autoTheme.setOnCheckedChangeListener { _, _ ->
            darkTheme.isEnabled = false

            val isSupportDarkTheme = sharedPrefSettings?.getString(
                getString(R.string.darkThemeShared), "Light"
            )
            darkTheme.isEnabled = !autoTheme.isChecked

            if (autoTheme.isChecked && isSupportDarkTheme == "Light") {
                sharedPrefSettings.edit()?.putString(getString(R.string.darkThemeShared), "Auto")
                    ?.apply()
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                onDestroy()
            } else if (isSupportDarkTheme != "Night") {
                sharedPrefSettings?.edit()?.putString(getString(R.string.darkThemeShared), "Light")
                    ?.apply()
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                onDestroy()

            }


            //exitProcess(0)
        }

        darkTheme.setOnCheckedChangeListener { _, _ ->

            val isSupportDarkTheme = sharedPrefSettings?.getString(
                getString(R.string.darkThemeShared), "Light"
            )
            autoTheme.isEnabled = !autoTheme.isChecked

            if (darkTheme.isChecked && isSupportDarkTheme == "Light") {
                sharedPrefSettings.edit()?.putString(getString(R.string.darkThemeShared), "Night")
                    ?.apply()
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                onDestroy()
            } else if (isSupportDarkTheme != "Auto") {
                sharedPrefSettings?.edit()?.putString(getString(R.string.darkThemeShared), "Light")
                    ?.apply()
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                onDestroy()
            }
        }

        loadImages.setOnCheckedChangeListener { _, _ ->
            if (loadImages.isChecked) {
                sharedPrefSettings?.edit()?.putBoolean(getString(R.string.loadImages), true)
                    ?.apply()

            } else sharedPrefSettings?.edit()?.putBoolean(getString(R.string.loadImages), false)
                ?.apply()
        }
    }

    private fun getDataOfStudent(
        sharedPref: SharedPreferences?, login: String, password: String, isUpdate: Boolean
    ) {
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                val infoOfStudent = getSemester(login, password)
                withContext(Dispatchers.Main) {
                    if (infoOfStudent != null) {
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
                        val stringSemester = semester.joinToString(separator = ",")

                        sharedPref?.edit()
                            ?.putString(getString(R.string.listOfSemester), stringSemester)?.apply()
                        sharedPref?.edit()
                            ?.putString(getString(R.string.listOfSemesterToChange), stringSemester)
                            ?.apply()
                        sharedPref?.edit()
                            ?.putString(getString(R.string.groupOfStudent), infoOfStudent[0][1])
                            ?.apply()
                        sharedPref?.edit()
                            ?.putString(getString(R.string.formOfStudent), infoOfStudent[0][0])
                            ?.apply()
                        sharedPref?.edit()?.putString(getString(R.string.actualGrades), "")?.apply()
                        /*              Toast.makeText(requireContext(), item, Toast.LENGTH_SHORT)
                                      .show()*/

                        if (isUpdate) Toast.makeText(
                            requireContext(), "Подтверждено!", Toast.LENGTH_SHORT
                        ).show()
                        else Toast.makeText(
                            requireContext(), "Успешно обновлено!", Toast.LENGTH_SHORT
                        ).show()

                    }
                }
            }
        }
    }

    private fun getFormAndGroup(login: String, password: String): ArrayList<String>? {
        try {
            val arrayToReturn = arrayListOf("", "")
            val document: String
            val sitePath =
                "https://info.swsu.ru/scripts/student_diplom/auth.php?act=auth&login=$login&password=$password&type=array"

            val response: Connection.Response = Jsoup.connect(sitePath)
                .userAgent("Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/535.21 (KHTML, like Gecko) Chrome/19.0.1042.0 Safari/535.21")
                .timeout(30000).execute()

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
            val sitePath = arrayOf(
                "https://info.swsu.ru/scripts/student_diplom/auth.php?act=semestr&group=${infoStudent[1]}&status=false&type=json",
                "https://info.swsu.ru/scripts/student_diplom/auth.php?act=semestr&group=${infoStudent[1]}&status=true&type=json"
            )
            for (item in sitePath) {
                val response: Connection.Response = Jsoup.connect(item)
                    .userAgent("Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/535.21 (KHTML, like Gecko) Chrome/19.0.1042.0 Safari/535.21")
                    .timeout(30000).execute()
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