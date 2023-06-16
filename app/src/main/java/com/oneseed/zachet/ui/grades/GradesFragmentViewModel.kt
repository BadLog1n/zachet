package com.oneseed.zachet.ui.grades

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.oneseed.zachet.data.GetRatingImpl
import com.oneseed.zachet.domain.GetRatingUseCase
import com.oneseed.zachet.domain.models.BackPressedState
import com.oneseed.zachet.domain.models.StudentState
import com.oneseed.zachet.domain.models.SubjectGrades
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
}