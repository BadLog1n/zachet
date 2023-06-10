package com.oneseed.zachet.domain

import com.oneseed.zachet.domain.models.StudentRating
import com.oneseed.zachet.domain.repository.Repository

class GetRatingUseCase(
    private val repository: Repository.GetRating,
) {

    fun invoke(callback: (studentRating: StudentRating) -> Unit) {
        repository.getRating(callback)
    }

}