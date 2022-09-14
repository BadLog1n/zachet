package com.example.universitysystem

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.universitysystem.databinding.FeedItemBinding

class FeedAdapter:RecyclerView.Adapter<FeedAdapter.RecordHolder> (){
    var recordsList = ArrayList<FeedRecord>()
    class RecordHolder(item: View) : RecyclerView.ViewHolder(item){
        private val binding = FeedItemBinding.bind(item)
        fun bind(feedItem:FeedRecord) = with(binding){
            whoPostedTv.text = feedItem.poster
            timeOfPostTv.text = feedItem.time
            recordTv.text = feedItem.record_text
            sponsoredTv.text = "Спонсировано"
            if (feedItem.isSponsored == true){
                sponsoredTv.visibility = View.VISIBLE
            }
            else {
                sponsoredTv.visibility = View.INVISIBLE
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecordHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.feed_item,parent,false)

        view.findViewById<ImageButton>(R.id.replyToMsgBtn).setOnClickListener {
            Toast.makeText(parent.context,"Добавить открытие чата",Toast.LENGTH_SHORT).show()

        }

        return RecordHolder(view)
    }

    override fun onBindViewHolder(holder: RecordHolder, position: Int) {
        holder.bind(recordsList[position])
    }

    override fun getItemCount(): Int {
        return recordsList.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun addFeedRecord(feedRecord: FeedRecord){
        recordsList.add(feedRecord)
        notifyDataSetChanged()
    }

    fun clearRecords(){
        recordsList.removeAll(recordsList.toSet())
    }
}