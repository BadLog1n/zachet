package com.oneseed.zachet.ui.grades

import android.content.Context
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
import kotlin.properties.Delegates

class GradesFragmentViewModel : ViewModel() {
    private val _listToObserve: MutableLiveData<StudentState> =
        MutableLiveData()
    val listToObserve: LiveData<StudentState> get() = _listToObserve
//    private var _semNumSpinner: MutableLiveData<Int> = MutableLiveData()
//    val semNumSpinner: LiveData<Int> get() = _semNumSpinner
   // private lateinit var _context: Context
    fun getGrades(context: Context, semester: Int) {
        val getRatingImpl = GetRatingImpl(context)
        val getRating = GetRatingUseCase(getRatingImpl)
        viewModelScope.launch(Dispatchers.IO) {
            getRating.invoke(semester, callback = { it: ArrayList<SubjectGrades> ->
                _listToObserve.postValue(StudentState.Success(it))
            })
        }
    }
}