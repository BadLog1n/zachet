package com.example.universitysystem

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import authCheck.AuthCheck
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.*

private lateinit var database: DatabaseReference
private lateinit var db: MyDatabase
private val chatName = ChatName()


class SettingsFragment : Fragment(R.layout.fragment_settings) {
    private val authCheck = AuthCheck()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        authCheck.check(view, this@SettingsFragment.context)

        super.onViewCreated(view, savedInstanceState)







    }



    fun sendMessage(
        sendUser: String,
        text: String,
        type: String,
        chatName: String
    ): Boolean {
        database = FirebaseDatabase.getInstance().getReference("chatMessages")
        val message = mapOf(
            "text" to text,
            "type" to type,
            "username" to sendUser,
        )
        val currentTimestamp = System.currentTimeMillis().toString()
        database.child(chatName).child(currentTimestamp).updateChildren(message)
        return true
    }

    @SuppressLint("SimpleDateFormat")
    fun getMessage(sendUser: String, getUser: String, timestamp: String) {
        val requestToDatabase = database.get()
        val chatName = chatName.getChatName(sendUser, getUser)
        database = FirebaseDatabase.getInstance().getReference("chatMessages/$chatName/$timestamp")

        requestToDatabase.addOnSuccessListener {
            val username = "username"
            val text = "text"
            val type = "type"
            for (i in it.children) {
                val dt = SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(Date(i.key!!.toLong()))
                    .toString()
                when (i.child(type).value.toString()) {
                    "text" -> {
                        val tx = i.child(text).value.toString()

                        if (i.child(username).value.toString() == sendUser) {
                            // adapter.add(ChatToItem(tx, dt))
                        } else {
                            // adapter.add(ChatFromItem(tx, dt))
                        }
                    }
                }
            }
        }
    }
   /*public fun getDbInfo(un:String,nameET: EditText,surnameET: EditText,loginET: EditText,passwordET: EditText)
   //public fun getDbInfo( db:MyDatabase,un:String,nameET: EditText,surnameET: EditText,loginET: EditText,passwordET: EditText)
    {
        val dbref= FirebaseDatabase.getInstance().getReference("users/$un")
        db = MyDatabase(dbref)
        db.getSettingsInfo(nameET,surnameET,loginET,passwordET)
        //db.getSettingsInfo("users/$un",nameET,surnameET,loginET,passwordET)

    }

    public fun getDb():MyDatabase{
        return db
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
*/
}


//Только английские буквы или цифры
//Только русские буквы


//В будущем:
//При изменении пароля придётся заново авторизовываться.