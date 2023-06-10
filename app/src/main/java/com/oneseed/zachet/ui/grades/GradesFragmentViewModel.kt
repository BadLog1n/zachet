package com.oneseed.zachet.ui.grades

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.oneseed.zachet.data.GetRatingImpl
import com.oneseed.zachet.domain.GetRatingUseCase
import com.oneseed.zachet.domain.models.SubjectGrades
import com.oneseed.zachet.domain.models.StudentState

class GradesFragmentViewModel : ViewModel() {

    private var _listToObserve: MutableLiveData<StudentState> = MutableLiveData()
    val listToObserve: LiveData<StudentState> get() = _listToObserve

    fun getGrades() {
        val getRatingImpl = GetRatingImpl()
        val getRating = GetRatingUseCase(getRatingImpl)
        getRating.invoke(callback = { it: List<SubjectGrades> ->
            //  viewModelScope.launch(Dispatchers.IO) { 
            _listToObserve.postValue(StudentState.Success(it))
            // }
        })
    }
}