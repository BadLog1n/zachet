package com.oneseed.zachet.data

import com.oneseed.zachet.domain.models.SubjectGrades
import com.oneseed.zachet.domain.repository.Repository


class GetRatingImpl : Repository.GetRating {
    override fun getRating(getRatingCallback: (List<SubjectGrades>) -> Unit) {
        val array = ArrayList<Int>()
        array.add(2)
        val result: ArrayList<SubjectGrades> = arrayListOf()
        val subjectGrades = SubjectGrades("f", 5, "ff", array, "f", "f")
        result.add(subjectGrades)
        getRatingCallback(result)
    }
}
