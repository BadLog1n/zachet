package com.oneseed.zachet.domain

import com.oneseed.zachet.domain.models.SubjectGrades
import com.oneseed.zachet.domain.repository.Repository

class GetRatingUseCase(
    private val repository: Repository.GetRating,
) {

    fun invoke(callback: (subjectGrades: List<SubjectGrades>) -> Unit) {
        repository.getRating(callback)
    }

}