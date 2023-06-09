package com.oneseed.zachet.domain.repository

import com.oneseed.zachet.domain.models.StudentRating

interface Repository {

    interface GetRating {
        fun getRating(getRatingCallback: (studentRating: StudentRating) -> Unit)
    }

}