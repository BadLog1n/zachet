package com.oneseed.zachet

data class FeedRecord(
    val author: String,
    val authorIdChat: String,
    val time: String,
    val record_text: String,
    val isSponsored: Boolean,
    val record: String,
    val userId: String,
    val displayLogin: String
)
