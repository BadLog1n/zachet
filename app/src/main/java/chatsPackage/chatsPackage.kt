package chatsPackage

import android.net.Uri
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import java.io.File

class ChatsPackage {
    private lateinit var database: DatabaseReference

    /**
     * Функция, которая загружает файл на сервер. [file] - путь к файлу на телефоне, [chatName] -
     * название чата между пользователями, [currentTimestamp] - штамп о времени отправления сообщения.
     * */
    fun putFile(file: String, chatName: String, currentTimestamp: String) {
        val refStorageRoot = FirebaseStorage.getInstance().reference
        val putPath =
            refStorageRoot.child(chatName)
        val uriFile = Uri.fromFile(File(file))
        val subFile = file.substring(file.lastIndexOf("/") + 1)

        putPath.child(currentTimestamp).child(subFile).putFile(uriFile)
    }


    /**
     * Функция которая возвращает название чата между собеседниками.
     * Название определяется следующим образом: Если логин пользователя (отправителя) [sendUser] больше
     * чем логин получателя [getUser] то название чата складывается из логина отправителя плюс логин получателя,
     * иначе наоборот.
     * */
    fun getChatName(sendUser: String, getUser: String): String {
        return if (sendUser > getUser) "$getUser$sendUser" else "$sendUser$getUser"
    }


    /**
     * Функция, которая отправляет новое сообщение.
     * [sendUser] пользователь приложения, [getUser] собеседник пользователя,
     * [text] - текст сообщения, [type] - тип сообщения, [chatName] - название чата между собеседниками(
     * подробнее в функции getChatName)
     * */
    fun sendMessage(
        sendUser: String,
        getUser: String,
        text: String,
        type: String,
        chatName: String
    ) {
        database = FirebaseDatabase.getInstance().getReference("chatMessages")
        val message = mapOf(
            "text" to text,
            "type" to type,
            "username" to sendUser,
        )
        val currentTimestamp = System.currentTimeMillis().toString()
        database.child(chatName).child(currentTimestamp).updateChildren(message)
        updateChat(sendUser, getUser, true, currentTimestamp)

    }


    /**
     *  Функция, которая создаёт запись о существовании чата между пользователем [sendUser]
     *  и собеседником пользователя [getUser]. Параметр [isSend] определяет, было ли это
     *  отправлением сообщения или сообщение просто прочитали. Параматре [lastMsg] передаёт
     *  timestamp в формате string, последнего сообщения. Случай, когда lastMsg пустой,
     *  обозначает прочитанное сообщение
     *  @param isSend
     *  True - сообщение отправлено и у собеседника новое непрочитанное.
     *  False - чат просто открыт или сообщение прочитано.
     * */
    fun updateChat(sendUser: String, getUser: String, isSend: Boolean, lastMsg: String = "") {
        database = FirebaseDatabase.getInstance().getReference("chatMembers")
        database.child(sendUser).child(getUser).child("isRead").setValue("true")
        database.child(sendUser).child(getUser).child("lastMsg").setValue(lastMsg)
        if (isSend) {
            database.child(getUser).child(sendUser).child("isRead").setValue("false")
            database.child(getUser).child(sendUser).child("lastMsg").setValue(lastMsg)
        }
    }


}