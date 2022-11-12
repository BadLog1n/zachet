package com.example.universitysystem

import android.annotation.SuppressLint
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.*

open class Message(var dbref: DatabaseReference? = null) {
    private val chatName = ChatName()


    private lateinit var database: DatabaseReference

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
}
