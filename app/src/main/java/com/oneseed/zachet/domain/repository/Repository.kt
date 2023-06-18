package com.oneseed.zachet.domain.repository

import com.oneseed.zachet.domain.models.FeedRecord
import com.oneseed.zachet.domain.models.SubjectGrades

interface Repository {

    interface GetRating {
        suspend fun getRating(
            semNumSpinner: Int, getRatingCallback: (subjectGrades: ArrayList<SubjectGrades>) -> Unit
        )
    }

    interface GetSemesterList {
        fun getSemesterList(getSemesterListCallback: (result: Array<String>?) -> Unit)
    }

    interface ChangeGradList {
        fun changeGradList(semNumSpinner: String)
    }

    interface GetFeedList {
        fun getFeedsList(callback: (result: ArrayList<FeedRecord>) -> Unit)
    }

    interface SendFeedList {
        fun sendFeedList(callback: (result: ArrayList<FeedRecord>) -> Unit)
    }
}