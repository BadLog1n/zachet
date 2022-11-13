package com.example.universitysystem

import android.widget.EditText
import com.google.android.gms.tasks.Task
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

open class MyDatabase (var dbref:DatabaseReference){
//open class MyDatabase (){
    //public val dbref:DatabaseReference
    var isInfoGot:Boolean = false
    var settingsMap: MutableMap<String,String> = mutableMapOf("name" to "","surname" to "","login" to "","password" to "")
    //open public fun getSettingsInfo(db: DatabaseReference, path:String, nameET: EditText, surnameET: EditText, loginET: EditText, passwordET: EditText) {
    open public fun getSettingsInfo(nameET: EditText, surnameET: EditText, loginET: EditText, passwordET: EditText) {
        //val db= FirebaseDatabase.getInstance().getReference(path)
        val requestToDatabase = dbref.get()

       requestToDatabase.addOnSuccessListener {
            if (it.child("name").value.toString() != "null")
            {nameET.setText(it.child("name").value.toString())
            }
            else nameET.setText("")
            if (it.child("surname").value.toString() != "null"){
                surnameET.setText(it.child("surname").value.toString())
            }
            else  surnameET.setText("")
            loginET.setText(it.child("login").value.toString())
            passwordET.setText(it.child("password").value.toString())
            isInfoGot = true
       }

    }
    open public fun extractInfo() {
        //val db= FirebaseDatabase.getInstance().getReference(path)
        val requestToDatabase = dbref.get()

        requestToDatabase.addOnSuccessListener {
            if (it.child("name").value.toString() != "null")
            {
                settingsMap["name"] = it.child("name").value.toString()}
            else settingsMap["name"] = ""
            if (it.child("surname").value.toString() != "null"){

                settingsMap["surname"] = it.child("surname").value.toString()
            }
            else  settingsMap["surname"] =""
            settingsMap["login"] = it.child("login").value.toString()
            settingsMap["password"] = it.child("password").value.toString()

        }

    }

    open fun changePassword(password:String):Boolean{
        dbref.child("password").setValue(password)
        settingsMap["password"] = password
        return true
    }
    open fun savePassword(password:String){
        dbref.child("password").setValue(password)

    }

    open fun saveName(name:String){
        dbref.child("name").setValue(name)

    }

    open fun saveSurname(surname:String){
        dbref.child("surname").setValue(surname)

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


    private val chatName = ChatName()


    private lateinit var database: DatabaseReference

    fun sendMessage(
        sendUser: String,
        getUser: String,
        text: String,
        type: String,
    ): Boolean {
        val chatNameString = chatName.getChatName(sendUser, getUser)
        database = FirebaseDatabase.getInstance().getReference("chatMessages")
        val message = mapOf(
            "text" to text,
            "type" to type,
            "username" to sendUser,
        )
        val currentTimestamp = System.currentTimeMillis().toString()
        database.child(chatNameString).child(currentTimestamp).updateChildren(message)
        return true
    }
}