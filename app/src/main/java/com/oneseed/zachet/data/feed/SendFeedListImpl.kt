package com.oneseed.zachet.data.feed

import android.util.Log
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.oneseed.zachet.domain.models.FeedRecord
import com.oneseed.zachet.domain.repository.Repository

class SendFeedListImpl : Repository.SendFeedList {
    private lateinit var database: DatabaseReference

    override fun sendFeedList(record: String) {
        
    }
}
