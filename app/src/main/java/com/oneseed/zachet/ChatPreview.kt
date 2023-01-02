package com.oneseed.zachet

//Необходимо добавить параметр с названием чата, чтобы удалять не через конкретный объект, а через поиск с именем чата

data class ChatPreview(val receiverName:String, val latestMsgTime:Long, val latestMsg:String, val isRead: Boolean, val getUser: String)
