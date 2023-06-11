package com.oneseed.zachet.domain

import com.oneseed.zachet.domain.models.SubjectGrades
import com.oneseed.zachet.domain.repository.Repository

class GetRatingUseCase(
    private val repository: Repository.GetRating,
) {

    suspend fun invoke(
        login: String,
        group: String,
        semester: String,
        form: String,
        status: String,
        callback: (subjectGrades: ArrayList<SubjectGrades>) -> Unit,
    ) {
        repository.getRating(
            login,
            group,
            semester,
            form,
            status,
            callback,
        )
    }

}