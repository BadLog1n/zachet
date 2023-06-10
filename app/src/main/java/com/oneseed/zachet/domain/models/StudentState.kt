package com.oneseed.zachet.domain.models

sealed class StudentState {
    data class Success(
        val ratingData: List<SubjectGrades>,
    ) : StudentState()

    class Error(val error: Throwable) : StudentState()
    object Loading : StudentState()
}
//всё супер
//сделай все импорты, тут студент рейтинга там этого класса