package com.example.universitysystem

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MotionEvent
import android.view.View.OnTouchListener
import android.view.Window
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import javax.security.auth.callback.Callback


class IndividualChatActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_individual_chat)
        getSupportActionBar()?.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM)
        getSupportActionBar()?.setCustomView(R.layout.chat_action_bar)
        supportActionBar?.show()

        var rcView = findViewById<RecyclerView>(R.id.messagesRcView)
        var adapter = GroupAdapter<GroupieViewHolder>()
        rcView.layoutManager = LinearLayoutManager(this)

        findViewById<TextView>(R.id.receiverName_tv).text = "Петр Иванович"
        findViewById<ImageButton>(R.id.backButton).setOnClickListener {
            onBackPressed()
        }
        findViewById<ImageButton>(R.id.clipButton).setOnClickListener {
            Toast.makeText(this, "Здесь будет диалог для выбора вложения", Toast.LENGTH_SHORT).show()
        }
        adapter = initRcView()
        rcView.adapter= adapter
        findViewById<ImageButton>(R.id.sendButton).setOnClickListener {
            adapter.add(ChatToItem( findViewById<EditText>(R.id.messageEditText).text.toString(),getTime()))
            rcView.adapter= adapter

            findViewById<EditText>(R.id.messageEditText).text.clear()
        }



    }

    private fun initRcView():GroupAdapter<GroupieViewHolder>{
        var rcView = findViewById<RecyclerView>(R.id.messagesRcView)
        val adapter = GroupAdapter<GroupieViewHolder>()

        adapter.add(ChatFromItem("Lorem ipsum dolor sit amet, consectetur adipiscing elit. Donec fringilla quam eu faci lisis mollis. ", getTime()))
        adapter.add(ChatToItem("Lorem ipsum dolor sit amet, consectetur adipiscing elit. Donec fringilla quam eu faci lisis mollis. ",getTime()))
        adapter.add(ChatFromItem("Lorem ipsum dolor sit amet, consectetur adipiscing elit. Donec fringilla quam eu faci lisis mollis. ",getTime()))
        adapter.add(ChatToItem("Lorem ipsum dolor sit amet, consectetur adipiscing elit. Donec fringilla quam eu faci lisis mollis. ",getTime()))
        adapter.add(ChatToItem("Last message. ",getTime()))
        return adapter
        //adapter
        //rcView.layoutManager = LinearLayoutManager(this)
        //rcView.adapter = adapter
    }

    private fun getTime():String{

        val currentDate = Date()
        val timeFormat: DateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        val timeText: String = timeFormat.format(currentDate)
        return timeText
    }
}

class ChatFromItem(val text:String,val time:String): Item<GroupieViewHolder>(){
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.findViewById<TextView>(R.id.from_ms_tv).text = text
        viewHolder.itemView.findViewById<TextView>(R.id.from_ms_time_tv).text = time
    }

    override fun getLayout(): Int {
        return R.layout.from_ms_item
    }

}

class ChatToItem(val text: String,val time:String): Item<GroupieViewHolder>(){
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.findViewById<TextView>(R.id.to_ms_tv).text = text
        viewHolder.itemView.findViewById<TextView>(R.id.to_ms_time_tv).text = time
    }

    override fun getLayout(): Int {
        return R.layout.to_message_item
    }

}