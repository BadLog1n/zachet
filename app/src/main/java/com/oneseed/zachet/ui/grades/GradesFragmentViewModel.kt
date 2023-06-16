package com.oneseed.zachet.ui.grades

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

class GradesFragmentViewModel : ViewModel() {
    private val _listToObserve: MutableLiveData<StudentState> = MutableLiveData()
    private val _backPressedState: MutableLiveData<BackPressedState> = MutableLiveData()
    val backPressedState: LiveData<BackPressedState> get() = _backPressedState
    val listToObserve: LiveData<StudentState> get() = _listToObserve

    fun getGrades(context: Context, semester: Int) {
        val getRatingImpl = GetRatingImpl(context)
        val getRating = GetRatingUseCase(getRatingImpl)
        _listToObserve.postValue(StudentState.Loading)
        viewModelScope.launch(Dispatchers.IO) {
            getRating.invoke(semester) { it: ArrayList<SubjectGrades> ->
                _listToObserve.postValue(StudentState.Success(it))
            }
        }
    }

    fun onBackPressed() {
        if (_backPressedState.value == BackPressedState.Waiting) {
            _backPressedState.postValue(BackPressedState.Success)
            return
        }
        _backPressedState.postValue(BackPressedState.Waiting)
        viewModelScope.launch(Dispatchers.IO) {
            Thread.sleep(2000)
            _backPressedState.postValue(BackPressedState.Reset)
        }
    }

    fun getSemesterList(context: Context): Array<String>? {
        val getSemesterListImpl = GetSemesterListImpl(context)
        val getSemesterList = GetSemesterListUseCase(getSemesterListImpl)
        var result: Array<String>? = null
        getSemesterList.invoke {
            result = it
        }
        return result
    }

    fun sendSwipeAnalytics() {
        viewModelScope.launch(Dispatchers.IO) {
            Firebase.analytics.logEvent("grades_update") {
                param("grades_update", "")
            }
        }
    }

    fun changeGradList(context: Context, currentSemester: String) {
        val changeGradListImpl = ChangeGradListImpl(context)
        val changeList = ChangeGradesListUseCase(changeGradListImpl)
        changeList.invoke(currentSemester)
    }
}