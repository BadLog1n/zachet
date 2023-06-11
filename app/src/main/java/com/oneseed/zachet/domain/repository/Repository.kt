package com.oneseed.zachet.domain.repository

import com.oneseed.zachet.domain.models.SubjectGrades

interface Repository {

    interface GetRating {
        suspend fun getRating(
//            login: String,
//            group: String,
//            semester: String,
//            form: String,
//            status: String,
            semNumSpinner: Int,
            getRatingCallback: (subjectGrades: ArrayList<SubjectGrades>) -> Unit
        )
    }

}