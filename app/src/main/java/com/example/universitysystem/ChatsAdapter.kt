package com.example.universitysystem

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.universitysystem.databinding.ListOfChatsItemBinding
import com.example.universitysystem.databinding.SubjectGradesItemBinding

class ChatsAdapter:RecyclerView.Adapter<ChatsAdapter.ChatsHolder> (){
    var chatsList = ArrayList<ChatPreview>()
    class ChatsHolder (item: View):RecyclerView.ViewHolder (item){
        private val binding = ListOfChatsItemBinding.bind(item)
        fun bind(chatPreview: ChatPreview) = with(binding){
            receiverName.text = chatPreview.receiverName
            latestMsgTimeTv.text=chatPreview.latestMsgTime
            latestMsgTv.text=chatPreview.latestMsg
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatsHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_of_chats_item,parent,false)
        return ChatsAdapter.ChatsHolder(view)
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
}