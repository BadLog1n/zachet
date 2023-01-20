package com.oneseed.zachet.dataClasses

data class ChatPreview(
    val receiverName: String,
    val latestMsgTime: Long,
    val latestMsg: String,
    val isRead: Boolean,
    val getUser: String
)
