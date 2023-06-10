package com.oneseed.zachet.domain.models

data class SubjectGrades(
    val subject: String,
    val allGradesCount: Int,
    val typeOfSubject: String,
    val grades: List<String>,
    val userChatId: String,
    val FIO: String,
    val segments: MutableList<String> = mutableListOf("1 к.т.", "2 к.т.", "3 к.т.", "4 к.т."),
    val subjectIsChange: Boolean = false
)