package com.example.universitysystem

import android.widget.EditText
import com.google.firebase.database.DatabaseReference

open class MyDatabase(var dbref: DatabaseReference) {
    //open class MyDatabase (){
    //public val dbref:DatabaseReference
    var isInfoGot: Boolean = false
    var settingsMap: MutableMap<String, String> =
        mutableMapOf("name" to "", "surname" to "", "login" to "", "password" to "")

    //open public fun getSettingsInfo(db: DatabaseReference, path:String, nameET: EditText, surnameET: EditText, loginET: EditText, passwordET: EditText) {
    open fun getSettingsInfo(
        nameET: EditText,
        surnameET: EditText,
        loginET: EditText,
        passwordET: EditText
    ) {
        //val db= FirebaseDatabase.getInstance().getReference(path)
        val requestToDatabase = dbref.get()

        requestToDatabase.addOnSuccessListener {
            if (it.child("name").value.toString() != "null") {
                nameET.setText(it.child("name").value.toString())
            } else nameET.setText("")
            if (it.child("surname").value.toString() != "null") {
                surnameET.setText(it.child("surname").value.toString())
            } else surnameET.setText("")
            loginET.setText(it.child("login").value.toString())
            passwordET.setText(it.child("password").value.toString())
            isInfoGot = true
        }

    }

    open fun extractInfo() {
        //val db= FirebaseDatabase.getInstance().getReference(path)
        val requestToDatabase = dbref.get()

        requestToDatabase.addOnSuccessListener {
            if (it.child("name").value.toString() != "null") {
                settingsMap["name"] = it.child("name").value.toString()
            } else settingsMap["name"] = ""
            if (it.child("surname").value.toString() != "null") {

                settingsMap["surname"] = it.child("surname").value.toString()
            } else settingsMap["surname"] = ""
            settingsMap["login"] = it.child("login").value.toString()
            settingsMap["password"] = it.child("password").value.toString()

        }

    }

    open fun changePassword(password: String): Boolean {
        dbref.child("password").setValue(password)
        settingsMap["password"] = password
        return true
    }

    open fun savePassword(password: String) {
        dbref.child("password").setValue(password)

    }

    open fun saveName(name: String) {
        dbref.child("name").setValue(name)

    }

    open fun saveSurname(surname: String) {
        dbref.child("surname").setValue(surname)

    }

}