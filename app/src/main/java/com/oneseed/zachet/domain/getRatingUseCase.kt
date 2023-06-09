package com.oneseed.zachet.domain

import com.oneseed.zachet.domain.repository.Repository

class GetRatingUseCase(
    private val repository: Repository.GetRating,
) {

    fun execute(callback: GetRatingCallback) {
        repository.getRating(callback)
    }

}