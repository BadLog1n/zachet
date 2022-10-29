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
import authCheck.AuthCheck
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

private lateinit var database: DatabaseReference
private lateinit var db: MyDatabase

class SettingsFragment : Fragment(R.layout.fragment_settings) {
    private val authCheck = AuthCheck()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        authCheck.check(view, this@SettingsFragment.context)

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
        val dbref= FirebaseDatabase.getInstance().getReference("users/$un")
        db = MyDatabase(dbref)

        getDbInfo(db,nameEditText,surnameEditText,loginEditText,passwordEditText)
        nameEditText.isEnabled = true
        surnameEditText.isEnabled = true
        passwordEditText.isEnabled = true

            view.findViewById<Button>(R.id.saveSettingsBtn).setOnClickListener {
            /*if (passwordEditText.text.isNotBlank() && passwordEditText.text.isNotEmpty()){
                database.child("password").setValue(passwordEditText.text.toString())

            }
            if (nameEditText.text.isNotBlank() && nameEditText.text.isNotEmpty()){
                database.child("name").setValue(nameEditText.text.toString())

            }
            if (surnameEditText.text.isNotBlank() && surnameEditText.text.isNotEmpty()){
                database.child("surname").setValue(surnameEditText.text.toString())
            }*/
                saveSettings(passwordEditText,nameEditText,surnameEditText)
            Toast.makeText(this@SettingsFragment.context, "Успешно сохранено", Toast.LENGTH_SHORT).show()
            findNavController().navigate(R.id.gradesFragment)

        }


    }


   public fun getDbInfo(db:MyDatabase,nameET: EditText,surnameET: EditText,loginET: EditText,passwordET: EditText)
   //public fun getDbInfo( db:MyDatabase,un:String,nameET: EditText,surnameET: EditText,loginET: EditText,passwordET: EditText)
    {

       db.getSettingsInfo(nameET,surnameET,loginET,passwordET)
        //db.getSettingsInfo("users/$un",nameET,surnameET,loginET,passwordET)

    }

    fun changePassword(passwordEditText: EditText){
        if (passwordEditText.text.isNotBlank() && passwordEditText.text.isNotEmpty()){
            db.changePassword(passwordEditText.text.toString())
            //database.child("password").setValue(passwordEditText.text.toString())

        }
    }
    fun saveSettings(passwordEditText: EditText, nameEditText: EditText, surnameEditText: EditText) {
        if (passwordEditText.text.isNotBlank() && passwordEditText.text.isNotEmpty()){
            db.savePassword(passwordEditText.text.toString())
            //database.child("password").setValue(passwordEditText.text.toString())

        }
        if (nameEditText.text.isNotBlank() && nameEditText.text.isNotEmpty()){
            db.saveName(nameEditText.text.toString())
            //database.child("name").setValue(nameEditText.text.toString())

        }
        if (surnameEditText.text.isNotBlank() && surnameEditText.text.isNotEmpty()){
            db.saveSurname(surnameEditText.text.toString())
            //database.child("surname").setValue(surnameEditText.text.toString())
        }

    }

}


//Только английские буквы или цифры
//Только русские буквы


//В будущем:
//При изменении пароля придётся заново авторизовываться.