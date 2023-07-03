package com.oneseed.zachet.data.feed

import com.oneseed.zachet.domain.models.FeedRecord
import com.oneseed.zachet.domain.repository.Repository

class SendFeedListImpl: Repository.SendFeedList {
    override fun sendFeedList(callback: (result: ArrayList<FeedRecord>) -> Unit) {
        TODO("Not yet implemented")
    }
}