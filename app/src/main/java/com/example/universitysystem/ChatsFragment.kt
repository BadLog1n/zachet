package com.example.universitysystem

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import chatsPackage.ChatsPackage
import com.google.firebase.database.*
import java.text.SimpleDateFormat
import java.util.*


class ChatsFragment : Fragment(R.layout.fragment_chats) {

    private val chatsPackage = ChatsPackage()


    private lateinit var database: DatabaseReference


    private var rcAdapter = ChatsAdapter()
    private var list = ArrayList<ChatPreview>()

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        rcAdapter.clearRecords()
        val sharedPref: SharedPreferences? = activity?.getSharedPreferences(
            "Settings",
            AppCompatActivity.MODE_PRIVATE
        )
        val userName = sharedPref?.getString("save_userid", "").toString()
        val recyclerView: RecyclerView = view.findViewById(R.id.chatsRcView)
        recyclerView.layoutManager = LinearLayoutManager(this@ChatsFragment.context)

        /*supportActionBar?.displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM
    supportActionBar?.setCustomView(R.layout.chats_action_bar)
    supportActionBar?.show()*/

        rcAdapter.clearRecords()
        rcAdapter.chatsList = ArrayList()
        rcAdapter.notifyDataSetChanged()
        recyclerView.adapter = rcAdapter
        recyclerView.layoutManager = LinearLayoutManager(this@ChatsFragment.context)

        activity?.findViewById<ImageButton>(R.id.menuFromChatsBtn)?.setOnClickListener {
            activity?.onBackPressed()
        }

        view.findViewById<Button>(R.id.button).setOnClickListener {

            val user = view.findViewById<EditText>(R.id.searchTxtInput).text.toString()
            database = FirebaseDatabase.getInstance().getReference("users")
            database.child(user).get().addOnSuccessListener {
                if (it.exists() && user != "") {
                    val intent =
                        Intent(this@ChatsFragment.context, IndividualChatActivity::class.java)
                    intent.putExtra("getUser", user)
                    startActivity(intent)
                    view.findViewById<EditText>(R.id.searchTxtInput).setText("")

                } else {
                    Toast.makeText(activity, "Пользователя не существует", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
        addPostEventListener(userName)
    }

    var isFirsttry = true
    private fun addPostEventListener(userName: String) {
        val postListener = object : ValueEventListener {
            @SuppressLint("SimpleDateFormat")
            var count = 0

            @SuppressLint("SimpleDateFormat", "NotifyDataSetChanged")
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (member in dataSnapshot.children) {
                    val getUser = member.key.toString()

                    val isRead = member.child("isRead").value.toString().toBooleanStrict()
                    val chatName = chatsPackage.getChatName(userName, getUser)
                    val text = "text"
                    val type = "type"
                    val dataTime = "dataTime"
                    var name: String
                    var surname: String
                    database = FirebaseDatabase.getInstance().getReference("users/$getUser")
                    var requestToDatabase = database.get()
                    requestToDatabase.addOnSuccessListener { itData ->
                        name = itData.child("name").value.toString()
                        surname = itData.child("surname").value.toString()
                        database =
                            FirebaseDatabase.getInstance().getReference("chatMessages/$chatName")
                        requestToDatabase = database.limitToLast(1).get()

                        requestToDatabase.addOnSuccessListener { itNew ->
                            for (i in itNew.children) {

                                val dt =
                                    SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(Date(i.key!!.toLong()))
                                        .toString()
                                val tx = i.child(text).value.toString()
                                //val item = list.filter { it.getUser == getUser }[0]


                                when (i.child(type).value.toString()) {

                                    "text" -> {
                                        Log.d("aaaaaaaaaaa", list.toString())

                                        val chat =
                                            ChatPreview("$name $surname", dt, tx, isRead, getUser)
                                        if (!isFirsttry) {
                                            try {
                                                val item: ChatPreview =
                                                    list.single { (it.getUser == getUser && it.latestMsgTime < dt) }
                                                rcAdapter.removeObject(item)
                                                list.remove(item)
                                                list.add(chat)
                                                rcAdapter.addChatPreview(chat)
                                            } catch (e: Exception) {
                                                try {
                                                    val item: ChatPreview =
                                                        list.single { (it.getUser == getUser && it.newMsg != isRead) }
                                                    rcAdapter.removeObject(item)
                                                    list.remove(item)
                                                    list.add(chat)
                                                    rcAdapter.addChatPreview(chat)
                                                    Log.d("aaaaaaaaaaa", list.toString())

                                                } catch (e: Exception) {
                                                    try {
                                                        list.single { (it.getUser == getUser) }

                                                    } catch (e: Exception) {
                                                        list.add(chat)
                                                        rcAdapter.addChatPreview(chat)
                                                    }
                                                }
                                            }

                                        } else {
                                            list.add(chat)
                                        }


                                    }
                                    "file" -> {
                                        val chat =
                                            ChatPreview(
                                                "$name $surname",
                                                dt,
                                                "Файл",
                                                isRead,
                                                getUser
                                            )
                                        if (!isFirsttry) {
                                            try {
                                                val item: ChatPreview =
                                                    list.single { (it.getUser == getUser && it.latestMsgTime < dt) }
                                                rcAdapter.removeObject(item)
                                                list.remove(item)
                                                list.add(chat)
                                                rcAdapter.addChatPreview(chat)
                                            } catch (e: Exception) {
                                            }
                                            try {
                                                val item: ChatPreview =
                                                    list.single { (it.getUser == getUser && it.newMsg != isRead) }
                                                rcAdapter.removeObject(item)
                                                list.remove(item)
                                                list.add(chat)
                                                rcAdapter.addChatPreview(chat)
                                            } catch (e: Exception) {
                                            }
                                        } else {
                                            list.add(chat)
                                        }

                                    }
                                    "photo" -> {
                                        val chat = ChatPreview(
                                            "$name $surname",
                                            dt,
                                            "Изображение",
                                            isRead,
                                            getUser
                                        )
                                        if (!isFirsttry) {
                                            try {
                                                val item: ChatPreview =
                                                    list.single { (it.getUser == getUser && it.latestMsgTime < dt) }
                                                rcAdapter.removeObject(item)
                                                list.remove(item)
                                                list.add(chat)
                                                rcAdapter.addChatPreview(chat)
                                            } catch (e: Exception) {
                                            }
                                            try {
                                                val item: ChatPreview =
                                                    list.single { (it.getUser == getUser && it.newMsg != isRead) }
                                                rcAdapter.removeObject(item)
                                                list.remove(item)
                                                list.add(chat)
                                                rcAdapter.addChatPreview(chat)
                                            } catch (e: Exception) {
                                            }
                                        } else {
                                            list.add(chat)
                                        }
                                    }

                                }
                                count += 1
                            }
                            if (dataSnapshot.childrenCount.compareTo(count) == 0 && isFirsttry) {
                                list = ArrayList(list.sortedWith(compareBy { it.latestMsgTime }))

                                // list.reverse()
                                rcAdapter.clearRecords()
                                for (item in list) {
                                    rcAdapter.addChatPreview(item)
                                }
                                isFirsttry = false
                            }

                        }
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.w("T", "loadPost:onCancelled", databaseError.toException())
            }
        }
        database = FirebaseDatabase.getInstance().getReference("chatMembers/$userName")
        database.addValueEventListener(postListener)
    }


    /**
     * Функция, которая получает список чатов, в которых состоит пользователь [userName].
     * */
    private fun getUsersChats(userName: String) {
        database = FirebaseDatabase.getInstance().getReference("chatMembers")
        database.child(userName).get().addOnSuccessListener {
            if (it.exists()) {
                for (i in it.children) {

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