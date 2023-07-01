package com.oneseed.zachet.ui.feed

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.oneseed.zachet.domain.states.FeedState

class FeedFragmentViewModel : ViewModel() {
    private val _listToObserve: MutableLiveData<FeedState> = MutableLiveData()
    val listToObserve: LiveData<FeedState> get() = _listToObserve

    fun warningRecord(record: String) {
    }

    fun deleteRecord(record: String) {
    }

    fun sendFeed(record: String) {

    }
}