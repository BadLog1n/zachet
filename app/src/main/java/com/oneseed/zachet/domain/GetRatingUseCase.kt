package com.oneseed.zachet.domain

import com.oneseed.zachet.domain.models.SubjectGrades
import com.oneseed.zachet.domain.repository.Repository

class GetRatingUseCase(
    private val repository: Repository.GetRating,
) {

    suspend fun invoke(
        semNumSpinner: Int,
        callback: (subjectGrades: ArrayList<SubjectGrades>) -> Unit,
    ) {
        repository.getRating(semNumSpinner, callback,)
    }

}