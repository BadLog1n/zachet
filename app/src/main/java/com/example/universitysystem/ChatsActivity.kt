package com.example.universitysystem

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import chatsPackage.chatsPackage
import com.google.firebase.database.*

class ChatsActivity : AppCompatActivity() {

    private val chatsPackage = chatsPackage()

    private lateinit var database: DatabaseReference

    var sendName = ""
    private var rcAdapter = ChatsAdapter()
    override fun onCreate(savedInstanceState: Bundle?) {
        rcAdapter.clearRecords()
        val sharedPref: SharedPreferences? = this.getSharedPreferences("Settings", MODE_PRIVATE)
        sendName = sharedPref?.getString("save_userid", "").toString()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chats)
        supportActionBar?.displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM
        supportActionBar?.setCustomView(R.layout.chats_action_bar)
        supportActionBar?.show()

        val recyclerView: RecyclerView = findViewById(R.id.chatsRcView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        rcAdapter.clearRecords()
        rcAdapter.chatsList = ArrayList()
        rcAdapter.notifyDataSetChanged()
        recyclerView.adapter = rcAdapter
        initChatsRc()
        recyclerView.adapter = rcAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        findViewById<ImageButton>(R.id.menuFromChatsBtn).setOnClickListener {
            onBackPressed()
        }
        findViewById<Button>(R.id.button).setOnClickListener {
            val intent =Intent(this,IndividualChatActivity::class.java)
            intent.putExtra("getUser",findViewById<EditText>(R.id.testInput).text)
            startActivity(intent)
            TODO("Сделать проверку на существование аккаунта")
        }

    }

    private fun initChatsRc() {
        getUsersChats(sendName)
    }

    private fun getChatMessages(sendUser: String, getUser: String, newMsg: String) {
        var newMsgBoolean = true
        if (newMsg == "false") newMsgBoolean = false
        val username = "username"
        val text = "text"
        val type = "type"
        val dataTime = "dataTime"
        var name = ""
        var surname = ""
        val chatName = chatsPackage.getChatName(sendUser, getUser)
        database = FirebaseDatabase.getInstance().getReference("users/$getUser")
        var requestToDatabase = database.get()
        requestToDatabase.addOnSuccessListener {
            name = it.child("name").value.toString()
            surname = it.child("surname").value.toString()
        }
        database = FirebaseDatabase.getInstance().getReference("chatMessages/$chatName")
        requestToDatabase = database.limitToLast(1).get()

        requestToDatabase.addOnSuccessListener {
            for (i in it.children) {
                val dt = i.child(dataTime).value.toString()
                val tx = i.child(text).value.toString()
                when (i.child(type).value.toString()) {

                    "text" -> {
                        val chat = ChatPreview("$name $surname", dt, tx, newMsgBoolean, getUser)
                        rcAdapter.addChatPreview(chat)

                    }
                    "file" -> {
                        val chat = ChatPreview("$name $surname", dt, "Файл", newMsgBoolean, getUser)
                        rcAdapter.addChatPreview(chat)

                    }
                    "photo" -> {
                        val chat = ChatPreview("$name $surname", dt, "Изображение", newMsgBoolean, getUser)
                        rcAdapter.addChatPreview(chat)
                    }
                }

            }
        }

    }


    /**
     * Функция, которая получает список чатов, в которых состоит пользователь [userName].
     * */
    private fun getUsersChats(userName: String) {
        database = FirebaseDatabase.getInstance().getReference("chatMembers")
        database.child(userName).get().addOnSuccessListener {
            if (it.exists()) {
                for (i in it.children) {
                    getChatMessages(userName, i.key.toString(), i.value.toString())
                }
            }
        }

    }

}