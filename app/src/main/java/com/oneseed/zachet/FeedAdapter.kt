package com.oneseed.zachet

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.FirebaseDatabase
import com.oneseed.zachet.databinding.FeedItemBinding


private lateinit var userId: String

class FeedAdapter : RecyclerView.Adapter<FeedAdapter.RecordHolder>() {

    var recordsList = ArrayList<FeedRecord>()

    class RecordHolder(item: View) : RecyclerView.ViewHolder(item) {

        private val binding = FeedItemBinding.bind(item)

        fun bind(feedItem: FeedRecord) = with(binding) {
            whoPostedTv.text =
                if (feedItem.displayLogin != "null") "${feedItem.author} (${feedItem.displayLogin})"
                else feedItem.author
            timeOfPostTv.text = feedItem.time
            recordTv.text = feedItem.record_text
            record.text = feedItem.record
            authorIdChat.text = feedItem.authorIdChat
            userId = feedItem.userId
            val isAdmin = feedItem.authorIdChat == "10"
            deleteSign.visibility = if (feedItem.authorIdChat != userId) View.GONE else View.VISIBLE
            if (isAdmin || feedItem.authorIdChat == userId) {
                replyToMsgBtn.visibility = View.GONE
                warningSign.visibility = View.GONE
            } else {
                replyToMsgBtn.visibility = View.VISIBLE
                warningSign.visibility = View.VISIBLE
            }
            sponsoredTv.text = "Спонсировано"
            sponsoredTv.visibility = if (feedItem.isSponsored) View.VISIBLE else View.INVISIBLE
            if (isAdmin) sponsoredTv.visibility = View.GONE
            if (authorIdChat.text.toString() == "10") {
                whoPostedTv.text = feedItem.author
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecordHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.feed_item, parent, false)

        val database = FirebaseDatabase.getInstance()
        view.findViewById<ImageButton>(R.id.replyToMsgBtn).setOnClickListener {
            val authorIdChat = view.findViewById<TextView>(R.id.authorIdChat).text.toString()

            val intent = Intent(parent.context, IndividualChatActivity::class.java)
            intent.putExtra("getUser", authorIdChat)
            parent.context.startActivity(intent)

        }

        view.findViewById<ImageButton>(R.id.warningSign).setOnClickListener {
            val record = view.findViewById<TextView>(R.id.record).text.toString()
            val myRef = database.getReference("warnings").child(record)
            myRef.setValue(userId)
            Toast.makeText(parent.context, "Жалоба отправлена", Toast.LENGTH_SHORT)
                .show()
        }

        view.findViewById<ImageButton>(R.id.deleteSign).setOnClickListener {
            val record = view.findViewById<TextView>(R.id.record).text.toString()
            val myRef = database.getReference("feed").child(record)
            myRef.removeValue()
            val item: FeedRecord = recordsList.single { (record == it.record) }
            removeObject(item)
        }

        return RecordHolder(view)
    }

    override fun onBindViewHolder(holder: RecordHolder, position: Int) {
        holder.bind(recordsList[position])
    }

    override fun getItemCount(): Int {
        return recordsList.size
    }

    fun addFeedRecord(feedRecord: FeedRecord) {
        recordsList.add(feedRecord)
    }

    fun clearRecords() {
        recordsList.removeAll(recordsList.toSet())
    }

    private fun removeObject(feedItem: FeedRecord) {
        val position = recordsList.indexOf(feedItem)
        recordsList.remove(feedItem)
        notifyItemRangeChanged(position, itemCount)

    }
}