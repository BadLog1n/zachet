package com.oneseed.zachet.data

import com.oneseed.zachet.domain.models.StudentRating
import com.oneseed.zachet.domain.repository.Repository


class GetRatingImpl : Repository.GetRating {
    override fun getRating(getRatingCallback: (studentRating: StudentRating) -> Unit) {
        val array = ArrayList<Int>()
        array.add(2)

        val studentRating = StudentRating("f", 5, "ff", array, "f", "f")
        getRatingCallback(studentRating)
    }
}
