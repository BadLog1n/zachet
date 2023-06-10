package com.oneseed.zachet.domain.repository

import com.oneseed.zachet.domain.models.SubjectGrades

interface Repository {

    interface GetRating {
        fun getRating(getRatingCallback: (subjectGrades: List<SubjectGrades>) -> Unit)
    }

}