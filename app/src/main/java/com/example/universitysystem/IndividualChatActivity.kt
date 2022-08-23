package com.example.universitysystem

import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


class IndividualChatActivity : AppCompatActivity() {

    private lateinit var database: DatabaseReference
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        val arguments = intent.extras
        val name = arguments!!["getUser"].toString()
        val sharedPref: SharedPreferences? = this.getSharedPreferences("Settings", MODE_PRIVATE)
        val un = sharedPref?.getString("save_userid", "").toString()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_individual_chat)
        supportActionBar?.displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM
        supportActionBar?.setCustomView(R.layout.chat_action_bar)
        supportActionBar?.show()

        val rcView = findViewById<RecyclerView>(R.id.messagesRcView)
        rcView.layoutManager = LinearLayoutManager(this)

        findViewById<TextView>(R.id.receiverName_tv).text = "Петр Иванович"
        findViewById<ImageButton>(R.id.backButton).setOnClickListener {
            onBackPressed()
        }
        findViewById<ImageButton>(R.id.clipButton).setOnClickListener {
            Toast.makeText(this, "Здесь будет диалог для выбора вложения", Toast.LENGTH_SHORT).show()
        }
        addPostEventListener(un, name)
        findViewById<ImageButton>(R.id.sendButton).setOnClickListener {

            val text = findViewById<EditText>(R.id.messageEditText).text.toString()
            sendMessage(un, "19-06-0109", text, "text", getChatName(un, "19-06-0109"))
            findViewById<EditText>(R.id.messageEditText).text.clear()
        }
    }

    private fun addPostEventListener(sendUser: String, getUser: String) {
        val chatName = getChatName(sendUser, getUser)
        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val rcView = findViewById<RecyclerView>(R.id.messagesRcView)
                rcView.layoutManager = LinearLayoutManager(this@IndividualChatActivity)
                val adapter = GroupAdapter<GroupieViewHolder>()
                val username = "username"
                val text = "text"
                val type = "type"
                val dataTime = "dataTime"
                updateChat(sendUser, getUser, false)
                // Get Post object and use the values to update the UI
                val post = dataSnapshot.value
                Log.w("T", "$post")

                for (i in dataSnapshot.children) {
                    when (i.child(type).value.toString()) {
                        "text" -> {
                            if (i.child(username).value.toString() == sendUser){
                                adapter.add(ChatToItem(i.child(text).value.toString(),i.child(dataTime).value.toString()))
                            }
                            else {
                                adapter.add(ChatFromItem(i.child(text).value.toString(),i.child(dataTime).value.toString()))
                            }
                        }
                        "file" -> {
                            Log.d("Message", "Новый файл")
                        }
                        "photo" -> {
                            Log.d("Message", "Скачать файл")
                        }
                    }
                }
                rcView.adapter= adapter
                rcView.scrollToPosition(adapter.itemCount-1)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.w("T", "loadPost:onCancelled", databaseError.toException())
            }
        }
        database = FirebaseDatabase.getInstance().getReference("chatMessages/$chatName")
        database.addValueEventListener(postListener)
    }

    private fun getChatName(sendUser: String, getUser: String): String {
        return if (sendUser > getUser) "$getUser$sendUser" else "$sendUser$getUser"
    }

    /**
     * Функция, которая отправляет новое сообщение.
     * [sendUser] пользователь приложения, [getUser] собеседник пользователя,
     * [text] - текст сообщения, [type] - тип сообщения, [chatName] - название чата между собеседниками(
     * подробнее в функции getChatName)
     * */
    @RequiresApi(Build.VERSION_CODES.O)
    private fun sendMessage(
        sendUser: String,
        getUser: String,
        text: String,
        type: String,
        chatName: String
    ) {
        val dateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("M/d/y H:m:ss"))

        updateChat(sendUser, getUser, true)
        database = FirebaseDatabase.getInstance().getReference("chatMessages")
        val message = mapOf(
            "text" to text,
            "type" to type,
            "username" to sendUser,
            "dataTime" to dateTime.toString(),
        )
        val currentTimestamp = System.currentTimeMillis().toString()
        database.child(chatName).child(currentTimestamp).updateChildren(message)
    }


    /**
     *  Функция, которая создаёт запись о существовании чата между пользователем [sendUser]
     *  и собеседником пользователя [getUser]. Параметр [isSend] определяет, было ли это
     *  отправлением сообщения или сообщение просто прочитали.
     *  @param isSend
     *  True - сообщение отправлено и у собеседника новое непрочитанное.
     *  False - чат просто открыт или сообщение прочитано.
     * */
    private fun updateChat(sendUser: String, getUser: String, isSend: Boolean) {
        database = FirebaseDatabase.getInstance().getReference("chatMembers")
        database.child(sendUser).child(getUser).setValue("true")
        if (isSend) {
            database.child(getUser).child(sendUser).setValue("false")
        }
    }

}

class ChatFromItem(val text:String, private val time:String): Item<GroupieViewHolder>(){
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.findViewById<TextView>(R.id.from_ms_tv).text = text
        viewHolder.itemView.findViewById<TextView>(R.id.from_ms_time_tv).text = time
    }

    override fun getLayout(): Int {
        return R.layout.from_ms_item
    }

}

class ChatToItem(val text: String, private val time:String): Item<GroupieViewHolder>(){
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.findViewById<TextView>(R.id.to_ms_tv).text = text
        viewHolder.itemView.findViewById<TextView>(R.id.to_ms_time_tv).text = time
    }

    override fun getLayout(): Int {
        return R.layout.to_message_item
    }

}

