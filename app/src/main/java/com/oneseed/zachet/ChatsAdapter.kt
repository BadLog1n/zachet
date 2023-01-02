package com.oneseed.zachet

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.oneseed.zachet.databinding.ListOfChatsItemBinding
import java.text.SimpleDateFormat
import java.util.*

class ChatsAdapter : RecyclerView.Adapter<ChatsAdapter.ChatsHolder>() {
    var chatsList = ArrayList<ChatPreview>()

    class ChatsHolder(item: View) : RecyclerView.ViewHolder(item) {

        private val binding = ListOfChatsItemBinding.bind(item)

        @SuppressLint("SimpleDateFormat")
        fun bind(chatPreview: ChatPreview) = with(binding) {
            val dt =
                SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(Date(chatPreview.latestMsgTime))
                    .toString()
            receiverName.text = chatPreview.receiverName
            latestMsgTimeTv.text = dt
            latestMsgTv.text = chatPreview.latestMsg
            latestMsgTv.typeface =
                if (!chatPreview.isRead) Typeface.DEFAULT_BOLD else Typeface.DEFAULT
            getUser.text = chatPreview.getUser
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatsHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.list_of_chats_item, parent, false)
        view.findViewById<LinearLayout>(R.id.linearLayout).setOnClickListener {
            val intent = Intent(parent.context, IndividualChatActivity::class.java)
            intent.putExtra("getUser", view.findViewById<TextView>(R.id.getUser).text)
            parent.context.startActivity(intent)
        }
        return ChatsHolder(view)
    }

    override fun onBindViewHolder(holder: ChatsHolder, position: Int) {
        holder.bind(chatsList[position])
    }

    override fun getItemCount(): Int {
        return chatsList.size
    }

    fun addChatPreview(chatPreview: ChatPreview) {
        chatsList.add(chatPreview)
    }

    fun clearRecords() {
        chatsList.removeAll(chatsList.toSet())
    }

}