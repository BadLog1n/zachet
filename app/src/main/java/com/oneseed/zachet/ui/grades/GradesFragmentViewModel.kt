package com.oneseed.zachet.ui.grades

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.oneseed.zachet.data.GetRatingImpl
import com.oneseed.zachet.domain.GetRatingUseCase
import com.oneseed.zachet.domain.models.StudentState
import com.oneseed.zachet.domain.models.SubjectGrades
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class GradesFragmentViewModel : ViewModel() {

    private var _listToObserve: MutableLiveData<StudentState> = MutableLiveData()
    val listToObserve: LiveData<StudentState> get() = _listToObserve

    fun getGrades() {
        val getRatingImpl = GetRatingImpl()
        val getRating = GetRatingUseCase(getRatingImpl)
        viewModelScope.launch(Dispatchers.IO) {
            getRating.invoke(callback = { it: ArrayList<SubjectGrades> ->
                _listToObserve.postValue(StudentState.Success(it))
            })
        }
    }
}