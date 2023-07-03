package com.oneseed.zachet.domain

import com.oneseed.zachet.domain.models.FeedRecord
import com.oneseed.zachet.domain.repository.Repository

class GetFeedUseCase(private val repository: Repository.GetFeedList) {
    fun invoke(count: Long) : ArrayList<FeedRecord> {
        return repository.getFeedsList(count)
    }
}