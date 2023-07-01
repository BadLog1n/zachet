package com.oneseed.zachet.ui.feed

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.ktx.Firebase
import com.oneseed.zachet.data.ChangeGradListImpl
import com.oneseed.zachet.data.GetRatingImpl
import com.oneseed.zachet.data.GetSemesterListImpl
import com.oneseed.zachet.domain.ChangeGradesListUseCase
import com.oneseed.zachet.domain.GetRatingUseCase
import com.oneseed.zachet.domain.GetSemesterListUseCase
import com.oneseed.zachet.domain.models.SubjectGrades
import com.oneseed.zachet.domain.states.BackPressedState
import com.oneseed.zachet.domain.states.StudentState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FeedFragmentViewModel : ViewModel() {
    private val _listToObserve: MutableLiveData<StudentState> = MutableLiveData()
    private val _backPressedState: MutableLiveData<BackPressedState> = MutableLiveData()
    val backPressedState: LiveData<BackPressedState> get() = _backPressedState
    val listToObserve: LiveData<StudentState> get() = _listToObserve

    fun warningRecord(record: String) {
    }

    fun deleteRecord(record: String) {
    }
}