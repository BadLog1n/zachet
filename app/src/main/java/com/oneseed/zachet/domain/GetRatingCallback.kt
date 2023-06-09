package com.oneseed.zachet.domain

import com.oneseed.zachet.domain.models.StudentRating

interface GetRatingCallback {
    fun onRatingLoad(studentRating: StudentRating) //в параметрах тип данных рейтинга (что возвращает реализация?)
}