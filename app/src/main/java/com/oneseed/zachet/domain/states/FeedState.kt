package com.oneseed.zachet.domain.states

import com.oneseed.zachet.dataClasses.FeedRecord

sealed class FeedState {
    data class Success(
        val feedData: ArrayList<FeedRecord>,
    ) : FeedState()

    class Error(val error: Throwable) : FeedState()
    object Loading : FeedState()
}