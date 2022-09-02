package com.example.universitysystem

//Необходимо добавить параметр с названием чата, чтобы удалять не через конкретный объект, а через поиск с именем чата

data class ChatPreview(val receiverName:String, val latestMsgTime:Long, val latestMsg:String, val newMsg: Boolean, val getUser: String)
