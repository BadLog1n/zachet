package com.oneseed.zachet.domain.repository

import com.oneseed.zachet.domain.models.BackPressedState
import com.oneseed.zachet.domain.models.SubjectGrades

interface Repository {

    interface GetRating {
        suspend fun getRating(
            semNumSpinner: Int, getRatingCallback: (subjectGrades: ArrayList<SubjectGrades>) -> Unit
        )
    }

    interface BackPressed {
        suspend fun backPressed(callback: (result: BackPressedState) -> Unit)
    }

}