package com.example.universitysystem

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import chatsPackage.chatsPackage
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase


class ChatsFragment : Fragment(R.layout.fragment_chats) {

    private val chatsPackage = chatsPackage()

    private lateinit var database: DatabaseReference

    var sendName = ""
    private var rcAdapter = ChatsAdapter()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        rcAdapter.clearRecords()
        val sharedPref: SharedPreferences? = activity?.getSharedPreferences("Settings",
            AppCompatActivity.MODE_PRIVATE
        )
        sendName = sharedPref?.getString("save_userid", "").toString()
        val recyclerView: RecyclerView = view.findViewById(R.id.chatsRcView)
        recyclerView.layoutManager = LinearLayoutManager(this@ChatsFragment.context)

        /*supportActionBar?.displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM
        supportActionBar?.setCustomView(R.layout.chats_action_bar)
        supportActionBar?.show()*/

        rcAdapter.clearRecords()
        rcAdapter.chatsList = ArrayList()
        rcAdapter.notifyDataSetChanged()
        recyclerView.adapter = rcAdapter
        initChatsRc()
        recyclerView.adapter = rcAdapter
        recyclerView.layoutManager = LinearLayoutManager(this@ChatsFragment.context)

        activity?.findViewById<ImageButton>(R.id.menuFromChatsBtn)?.setOnClickListener {
            activity?.onBackPressed()
        }

        view.findViewById<Button>(R.id.button).setOnClickListener {

            val intent = Intent(this@ChatsFragment.context,IndividualChatActivity::class.java)
            intent.putExtra("getUser",view.findViewById<EditText>(R.id.searchTxtInput).text)
            startActivity(intent)
            //("Сделать проверку на существование аккаунта")
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_chats, container, false)
    }



}