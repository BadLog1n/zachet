package com.oneseed.zachet.ui.feed

import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.oneseed.zachet.R
import com.oneseed.zachet.activities.IndividualChatActivity
import com.oneseed.zachet.dataClasses.FeedRecord
import com.oneseed.zachet.databinding.FeedItemBinding

class FeedAdapter(
    val warningClick: (String) -> Unit,
    val deleteClick: (String) -> Unit,
) : RecyclerView.Adapter<FeedAdapter.RecordHolder>() {
    var recordsList = ArrayList<FeedRecord>()

    class RecordHolder(item: View) : RecyclerView.ViewHolder(item) {
        private val binding = FeedItemBinding.bind(item)
        fun bind(feedItem: FeedRecord) = with(binding) {
            val uidAdmin = root.context.getString(R.string.adminId)
            val isAdmin = feedItem.authorIdChat == uidAdmin
            visibilitySigns(isAdmin, feedItem)
            whoPostedTv.text =
                if (feedItem.displayLogin != "null") "${feedItem.author} (${feedItem.displayLogin})"
                else feedItem.author
            timeOfPostTv.text = feedItem.time
            recordTv.text = feedItem.record_text
            record.text = feedItem.record
            authorIdChat.text = feedItem.authorIdChat
        }

        private fun visibilitySigns(isAdmin: Boolean, feedItem: FeedRecord) {
            with(binding) {
                if (!isAdmin && feedItem.authorIdChat != feedItem.userId) {
                    replyToMsgBtn.visibility = View.VISIBLE
                    warningSign.visibility = View.VISIBLE
                } else {
                    replyToMsgBtn.visibility = View.GONE
                    warningSign.visibility = View.GONE
                    deleteSign.visibility =
                        if (feedItem.authorIdChat != feedItem.userId) View.GONE else View.VISIBLE
                    sponsoredTv.visibility =
                        if (feedItem.isSponsored) View.VISIBLE else if (isAdmin) View.GONE else View.INVISIBLE
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecordHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.feed_item, parent, false)
        view.findViewById<ImageButton>(R.id.replyToMsgBtn).setOnClickListener {
            val authorIdChat = view.findViewById<TextView>(R.id.authorIdChat).text.toString()
            val intent = Intent(parent.context, IndividualChatActivity::class.java)
            intent.putExtra("getUser", authorIdChat)
            parent.context.startActivity(intent)
        }

        view.findViewById<ImageButton>(R.id.warningSign).setOnClickListener {
            val record = view.findViewById<TextView>(R.id.record).text.toString()
            warningClick(record)
            Toast.makeText(
                parent.context, parent.context.getString(R.string.complaintSent), Toast.LENGTH_SHORT
            ).show()
        }

        view.findViewById<ImageButton>(R.id.deleteSign).setOnClickListener {
            val builder = androidx.appcompat.app.AlertDialog.Builder(parent.context)
            builder.setTitle(parent.context.getString(R.string.ConfirmDelete))
            builder.setMessage(parent.context.getString(R.string.warningDelete))
            builder.setIcon(R.drawable.ic_baseline_priority_high_24)
            builder.setPositiveButton(parent.context.getString(R.string.deleteRecord)) { _, _ ->
                val record = view.findViewById<TextView>(R.id.record).text.toString()
                deleteClick(record)
                val item: FeedRecord = recordsList.single { (record == it.record) }
                removeObject(item)
            }
            builder.setNegativeButton(parent.context.getString(R.string.Cancel)) { _, _ -> }
            val alertDialog: androidx.appcompat.app.AlertDialog = builder.create()
            alertDialog.show()
            alertDialog.getButton(androidx.appcompat.app.AlertDialog.BUTTON_POSITIVE)
                .setTextColor(Color.BLACK)
            alertDialog.getButton(androidx.appcompat.app.AlertDialog.BUTTON_NEGATIVE)
                .setTextColor(Color.BLACK)
        }
        return RecordHolder(view)
    }

    override fun onBindViewHolder(holder: RecordHolder, position: Int) {
        holder.bind(recordsList[position])
    }

    override fun getItemCount(): Int {
        return recordsList.size
    }

    /** Добавляет новую запись ([feedRecord])*/
    fun addFeedRecord(feedRecord: FeedRecord) {
        recordsList.add(feedRecord)
    }

    /**Очищает весь список из элементов записей*/
    fun clearRecords() {
        recordsList.removeAll(recordsList.toSet())
    }

    /**Удаляет конкретную запись ([feedItem])*/
    private fun removeObject(feedItem: FeedRecord) {
        val position = recordsList.indexOf(feedItem)
        recordsList.remove(feedItem)
        notifyItemRangeChanged(position, itemCount)

    }
}