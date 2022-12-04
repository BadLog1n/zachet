package com.oneseed.zachet

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.oneseed.zachet.databinding.FeedItemBinding
import com.google.firebase.database.FirebaseDatabase


private lateinit var userId: String

class FeedAdapter : RecyclerView.Adapter<FeedAdapter.RecordHolder>() {

    var recordsList = ArrayList<FeedRecord>()

    class RecordHolder(item: View) : RecyclerView.ViewHolder(item) {

        private val binding = FeedItemBinding.bind(item)

        @SuppressLint("SetTextI18n")
        fun bind(feedItem: FeedRecord) = with(binding) {

            whoPostedTv.text = "${feedItem.author} (${feedItem.authorIdChat})"
            timeOfPostTv.text = feedItem.time
            recordTv.text = feedItem.record_text
            record.text = feedItem.record
            authorIdChat.text = feedItem.authorIdChat
            userId = feedItem.userId
            sponsoredTv.text = "Спонсировано"
            if (feedItem.isSponsored) {
                sponsoredTv.visibility = View.VISIBLE
            }

            if (authorIdChat.text.toString() == "10") {
                replyToMsgBtn.visibility = View.GONE
                whoPostedTv.text = feedItem.author
            }

        }
    }

    @SuppressLint("SetTextI18n")
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecordHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.feed_item, parent, false)

        val database = FirebaseDatabase.getInstance()
        view.findViewById<ImageButton>(R.id.replyToMsgBtn).setOnClickListener {
            val authorIdChat = view.findViewById<TextView>(R.id.authorIdChat).text.toString()

            val intent = Intent(parent.context, IndividualChatActivity::class.java)
            intent.putExtra("getUser", authorIdChat)
            parent.context.startActivity(intent)

        }

        view.findViewById<LinearLayout>(R.id.recordItemLayout).setOnClickListener {
            val authorIdChat = view.findViewById<TextView>(R.id.authorIdChat).text.toString()
            val record = view.findViewById<TextView>(R.id.record).text.toString()
            if (authorIdChat != "10") {

                val builder = AlertDialog.Builder(parent.context)
                if (userId == authorIdChat) {
                    builder.setNeutralButton("Удалить") { _, _ ->
                        val myRef = database.getReference("feed").child(record)
                        myRef.removeValue()
                        val item: FeedRecord =
                            recordsList.single { (record == it.record) }
                        removeObject(item)
                    }
                } else {
                    builder.setNeutralButton("Пожаловаться") { _, _ ->
                        val myRef = database.getReference("warnings").child(record)
                        myRef.setValue(userId)
                        Toast.makeText(parent.context, "Жалоба отправлена", Toast.LENGTH_SHORT)
                            .show()
                    }
                }

                val alertDialog = builder.create()
                alertDialog.show()
                val autoBtn = alertDialog.getButton(DialogInterface.BUTTON_NEUTRAL)
                with(autoBtn) {
                    setTextColor(Color.BLACK)

                }
            }
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
    fun addFeedRecord(feedRecord: FeedRecord) {
        recordsList.add(feedRecord)
        notifyDataSetChanged()
    }

    fun clearRecords() {
        recordsList.removeAll(recordsList.toSet())
    }


    @SuppressLint("NotifyDataSetChanged")
    fun removeObject(feedItem: FeedRecord) {
        recordsList.remove(feedItem)
        notifyDataSetChanged()
    }
}