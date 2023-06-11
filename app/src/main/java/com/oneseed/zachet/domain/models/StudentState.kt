package com.oneseed.zachet.domain.models

sealed class StudentState {
    data class Success(
        val ratingData: ArrayList<SubjectGrades>,
    ) : StudentState()

    class Error(val error: Throwable) : StudentState()
    object Loading : StudentState()
}