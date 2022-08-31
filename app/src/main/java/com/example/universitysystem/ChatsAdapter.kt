package com.example.universitysystem

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.universitysystem.databinding.ListOfChatsItemBinding

class ChatsAdapter:RecyclerView.Adapter<ChatsAdapter.ChatsHolder> (){
    var chatsList = ArrayList<ChatPreview>()
    class ChatsHolder (item: View):RecyclerView.ViewHolder (item){
        private val binding = ListOfChatsItemBinding.bind(item)
        fun bind(chatPreview: ChatPreview) = with(binding){
            receiverName.text = chatPreview.receiverName
            latestMsgTimeTv.text=chatPreview.latestMsgTime
            latestMsgTv.text=chatPreview.latestMsg
            latestMsgTv.typeface = if (!chatPreview.newMsg) Typeface.DEFAULT_BOLD else Typeface.DEFAULT
            getUser.text = chatPreview.getUser
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatsHolder {

        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_of_chats_item,parent,false)
        view.findViewById<LinearLayout>(R.id.linearLayout).setOnClickListener {
            val intent = Intent(parent.context,IndividualChatActivity::class.java)
            intent.putExtra("getUser",view.findViewById<TextView>(R.id.getUser).text)
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
    @SuppressLint("NotifyDataSetChanged")
    fun addChatPreview(chatPreview: ChatPreview){
        chatsList.add(chatPreview)
        notifyDataSetChanged()
    }

    fun clearRecords(){
        chatsList.removeAll(chatsList.toSet())
    }

    fun removeObject(chatPreview: ChatPreview){
        chatsList.remove(chatPreview)
    }
}