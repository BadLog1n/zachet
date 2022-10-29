package com.example.universitysystem

import android.widget.EditText
import com.google.android.gms.tasks.Task
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference

open class MyDatabase (var dbref:DatabaseReference){
//open class MyDatabase (){
    //public val dbref:DatabaseReference

    //open public fun getSettingsInfo(db: DatabaseReference, path:String, nameET: EditText, surnameET: EditText, loginET: EditText, passwordET: EditText) {
    open public fun getSettingsInfo(nameET: EditText, surnameET: EditText, loginET: EditText, passwordET: EditText) {
        //val db= FirebaseDatabase.getInstance().getReference(path)
        val requestToDatabase = dbref.get()

       requestToDatabase.addOnSuccessListener {
            if (it.child("name").value.toString() != "null")
            {nameET.setText(it.child("name").value.toString())}
            else nameET.setText("")
            if (it.child("surname").value.toString() != "null") surnameET.setText(it.child("surname").value.toString()) else  surnameET.setText("")
            loginET.setText(it.child("login").value.toString())
            passwordET.setText(it.child("password").value.toString())
        }
    }

    private fun requestListener(requestToDatabase: Task<DataSnapshot>, arr: Array<String>) {
        requestToDatabase.addOnSuccessListener {
            if (it.child("name").value.toString() != "null") {
                arr[0] = it.child("name").value.toString()
            } else arr[0] = ""
            if (it.child("surname").value.toString() != "null") arr[1] =
                it.child("surname").value.toString() else arr[1] = ""
            arr[2] = it.child("login").value.toString()
            arr[3] = it.child("password").value.toString()
        }
    }
}