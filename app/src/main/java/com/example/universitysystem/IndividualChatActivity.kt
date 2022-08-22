package com.example.universitysystem

import android.annotation.SuppressLint
import android.content.Context

import android.content.SharedPreferences
import android.os.AsyncTask
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
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*


class IndividualChatActivity : AppCompatActivity() {

    var countMessages = 0
    private lateinit var database: DatabaseReference
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
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
        addPostEventListener()


        findViewById<ImageButton>(R.id.sendButton).setOnClickListener {

            val text = findViewById<EditText>(R.id.messageEditText).text.toString()
            sendMessage(un, "19-06-0109", text, "text", getChatName(un, "19-06-0109"))
/*            adapter.add(ChatToItem( text,getTime()))
            rcView.adapter= adapter
            rcView.scrollToPosition(adapter.itemCount-1)*/

            findViewById<EditText>(R.id.messageEditText).text.clear()
        }

    }




    private fun addPostEventListener() {

        // [START post_value_event_listener]
        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Get Post object and use the values to update the UI
                val post = dataSnapshot.value
                Log.w("T", "$post")

                // ...
                val rcView = findViewById<RecyclerView>(R.id.messagesRcView)
                rcView.layoutManager = LinearLayoutManager(this@IndividualChatActivity)
                val adapter = initRcView()
                rcView.adapter= adapter
                rcView.scrollToPosition(countMessages-1)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w("T", "loadPost:onCancelled", databaseError.toException())
            }
        }
        database = FirebaseDatabase.getInstance().getReference("chatMessages")
        database.addValueEventListener(postListener)
    }


    private fun initRcView():GroupAdapter<GroupieViewHolder>{
        var adapter = GroupAdapter<GroupieViewHolder>()
        val sharedPref: SharedPreferences? = this.getSharedPreferences("Settings", MODE_PRIVATE)
        val un = sharedPref?.getString("save_userid", "").toString()
        adapter = getChatMessages(un, "19-06-0109", false, adapter)
        //findViewById<RecyclerView>(R.id.messagesRcView)


       /* */


        return adapter
        //adapter
        //rcView.layoutManager = LinearLayoutManager(this)
        //rcView.adapter = adapter
    }

    private fun getTime(): String {

        val currentDate = Date()
        val timeFormat: DateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        return timeFormat.format(currentDate)
    }


//        fun onChildAdded(dataSnapshot: DataSnapshot, prevChildKey: String) {
//            val newPost: Post? = dataSnapshot.getValue(Post::class.java)
//            System.out.println("Author: " + newPost.author)
//            System.out.println("Title: " + newPost.title)
//            println("Previous Post ID: $prevChildKey")
//        }





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


    /**
     * Функция, которая получает список чатов, в которых состоит пользователь [userName].
     * */
    private fun getUsersChats(userName: String) {
        database = FirebaseDatabase.getInstance().getReference("chatMembers")
        database.child(userName).get().addOnSuccessListener {
            if (it.exists()) {
                for (i in it.children) {
                    //getChatMessages(userName, i.key.toString(), true)
                }
            }
        }

    }


    /**
     * Функция которая получает одно последнее или все сообщения между пользователем приложения [sendUser]
     * и собеседником пользователя [getUser]. Параметр [needLast]
     * отвечает за то, нужно ли получить только последнее сообщение.
     * @param needLast
     * True - только последнее сообщение
     * False - все сообщения
     * */
    private fun getChatMessages(sendUser: String, getUser: String, needLast: Boolean,
                                adapter: GroupAdapter<GroupieViewHolder>) :  GroupAdapter<GroupieViewHolder> {
        val username = "username"
        val text = "text"
        val type = "type"
        val dataTime = "dataTime"
        val chatName = getChatName(sendUser, getUser)
        updateChat(sendUser, getUser, false)
        database = FirebaseDatabase.getInstance().getReference("chatMessages/$chatName")
        var requestToDatabase = database.get()
        if (needLast) {
            requestToDatabase = database.limitToLast(1).get()
        }
        requestToDatabase.addOnSuccessListener {
            countMessages = it.children.count()
            for (i in it.children) {
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
                            if (needLast){
                                Log.d("Message", "Новый файл")
                            }
                            else{
                                //download(i.child(text).value.toString(), chatName)

                            }
                        }
                        "photo" -> {
                            if(needLast){
                                Log.d("Message", "Новая фотография")
                            }
                            else{
                                Log.d("Message", "Скачать файл")
                                //displayImage(i.child(text).value.toString(), chatName)
                            }

                    }
                }
            }
        }
    return adapter
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

class DoAsync(val handler: () -> Unit) : AsyncTask<Void, Void, Void>() {
    init {
        execute()
    }

    @Deprecated("Deprecated in Java")
    override fun doInBackground(vararg params: Void?): Void? {
        handler()
        return null
    }
}
