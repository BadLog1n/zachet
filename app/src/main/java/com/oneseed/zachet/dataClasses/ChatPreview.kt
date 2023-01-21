package com.oneseed.zachet.dataClasses

/**
 * Data-класс элемента чата.
 *@param receiverName имя и фамилия отправителя
 *@param latestMsgTime время отправленного сообщения
 *@param latestMsg текст сообщения
 *@param isRead индикатор «прочте-ния» сообщения
 *@param getUser UID получателя
 *  */
data class ChatPreview(
    val receiverName: String,
    val latestMsgTime: Long,
    val latestMsg: String,
    val isRead: Boolean,
    val getUser: String
)
