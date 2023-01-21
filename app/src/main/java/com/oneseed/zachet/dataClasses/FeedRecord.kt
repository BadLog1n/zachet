package com.oneseed.zachet.dataClasses




/**
 * data-классом элемента записи.
 *@param author имя и фамилия отправителя
 *@param authorIdChat UID автора
 *@param time время публикации
 *@param record_text текст записи
 *@param isSponsored индикатор спонсиро-вания записи
 *@param record уникальный иденти-фикатор записи
 *@param userId UID пользователя приложения
 *@param displayLogin логин автора записи

 *  */
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
