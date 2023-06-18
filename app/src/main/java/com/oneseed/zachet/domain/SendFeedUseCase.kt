package com.oneseed.zachet.domain

import com.oneseed.zachet.domain.models.FeedRecord
import com.oneseed.zachet.domain.repository.Repository

class SendFeedUseCase(private val repository: Repository.SendFeedList) {
    fun invoke(callback: (result: ArrayList<FeedRecord>) -> Unit) {
        repository.sendFeedList(callback)
    }
}