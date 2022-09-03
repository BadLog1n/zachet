package com.example.universitysystem

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.fragment.findNavController
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

private lateinit var database: DatabaseReference

class SettingsFragment : Fragment(R.layout.fragment_settings) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)
        activity?.findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar1)?.title = "Настройки"
        val loginEditText = view.findViewById<EditText>(R.id.loginText)
        val passwordEditText = view.findViewById<EditText>(R.id.passText)
        val nameEditText = view.findViewById<EditText>(R.id.nameText)
        val surnameEditText = view.findViewById<EditText>(R.id.surnameText)
        val sharedPref: SharedPreferences? = activity?.getSharedPreferences("Settings",
            Context.MODE_PRIVATE
        )
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
            loginEditText.isEnabled = true
            passwordEditText.isEnabled = true
        }


        view.findViewById<Button>(R.id.saveBtn).setOnClickListener {
            if (passwordEditText.text.isNotBlank() && passwordEditText.text.isNotEmpty()){
                database.child("password").setValue(passwordEditText.text.toString())

            }
            if (nameEditText.text.isNotBlank() && nameEditText.text.isNotEmpty()){
                database.child("name").setValue(nameEditText.text.toString())

            }
            if (surnameEditText.text.isNotBlank() && surnameEditText.text.isNotEmpty()){
                database.child("surname").setValue(surnameEditText.text.toString())
            }
            Toast.makeText(this@SettingsFragment.context, "Успешно сохранено", Toast.LENGTH_SHORT).show()
            findNavController().navigate(R.id.gradesFragment)

        }


    }


}


//Только английские буквы или цифры
//Только русские буквы


//В будущем:
//При изменении пароля придётся заново авторизовываться.