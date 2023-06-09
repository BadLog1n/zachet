package com.oneseed.zachet.domain.models

data class StudentRating(
    val getSubjectName: String,
    val ratingScore: Int,
    val subjectType: String,
    val rating: List<Int>,
    val tutorName: String,
    val tutorId: String, // todo: Подумать
)