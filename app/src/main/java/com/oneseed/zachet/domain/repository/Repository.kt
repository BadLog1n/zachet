package com.oneseed.zachet.domain.repository
import com.oneseed.zachet.domain.GetRatingCallback

interface Repository {

    interface GetRating {
        fun getRating(getRatingCallback: GetRatingCallback)
    }

}