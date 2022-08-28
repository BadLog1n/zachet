package com.example.universitysystem

import UriPathHelper.UriPathHelper
import android.Manifest
import android.app.Activity
import android.app.DownloadManager
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment.DIRECTORY_DOWNLOADS
import android.os.Environment.isExternalStorageManager
import android.util.Log
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


class IndividualChatActivity : AppCompatActivity() {
    private var typeOfFile = ""
    private var sendName = ""
    private var getName = ""
    private lateinit var database: DatabaseReference
    private val storagePermissionCode = 0

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        val arguments = intent.extras
        getName = arguments!!["getUser"].toString()
        val sharedPref: SharedPreferences? = this.getSharedPreferences("Settings", MODE_PRIVATE)
        sendName = sharedPref?.getString("save_userid", "").toString()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_individual_chat)
        supportActionBar?.displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM
        supportActionBar?.setCustomView(R.layout.chat_action_bar)
        supportActionBar?.show()

        val rcView = findViewById<RecyclerView>(R.id.messagesRcView)
        rcView.layoutManager = LinearLayoutManager(this)
        findViewById<TextView>(R.id.receiverName_tv).text = ""



        database = FirebaseDatabase.getInstance().getReference("users/$getName")
        val requestToDatabase = database.get()
        requestToDatabase.addOnSuccessListener {
            val displayName =
                it.child("name").value.toString() + " " + it.child("surname").value.toString()
            findViewById<TextView>(R.id.receiverName_tv).text = displayName
        }
        findViewById<ImageButton>(R.id.backButton).setOnClickListener {
            onBackPressed()
        }
        findViewById<ImageButton>(R.id.clipButton).setOnClickListener {
            try {
                if ((ContextCompat.checkSelfPermission(
                        this,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    ) != PackageManager.PERMISSION_GRANTED && (ContextCompat.checkSelfPermission(
                        this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ) != PackageManager.PERMISSION_GRANTED))
                ) {
                    ActivityCompat.requestPermissions(
                        this,
                        arrayOf(
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                        ),
                        storagePermissionCode
                    )
                } else {

                    //Toast.makeText(this, "Здесь будет диалог для выбора вложения", Toast.LENGTH_SHORT).show()
                    val builder = AlertDialog.Builder(this)
                    builder.setPositiveButton("Фото") { _, _ ->

                        pickFileOrPhoto(false)

                    }
                    builder.setNeutralButton("Файл") { _, _ ->
                        try {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                                if (!isExternalStorageManager()) {
                                    Toast.makeText(
                                        this,
                                        "Вам необходимо выдать разрешение на работу с памятью в настройках," +
                                                "чтобы загружать файлы",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                } else {
                                    pickFileOrPhoto(true)
                                }
                            }
                        } catch (e: Exception) {
                            Toast.makeText(
                                this, "Нет доступа к памяти", Toast.LENGTH_SHORT
                            ).show()

                        }

                    }
                    val alertDialog = builder.create()
                    alertDialog.show()

                    val autoBtn = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE)
                    with(autoBtn) {
                        setTextColor(Color.BLACK)
                    }
                    val userBtn = alertDialog.getButton(DialogInterface.BUTTON_NEUTRAL)
                    with(userBtn) {
                        setTextColor(Color.BLACK)
                    }
                }
            } catch (e: Exception) {
                Toast.makeText(
                    this, "Нет доступа к памяти", Toast.LENGTH_SHORT
                ).show()
            }


        }
        findViewById<ImageButton>(R.id.clipButton).setOnLongClickListener {
            pickFileOrPhoto(false)
            return@setOnLongClickListener true
        }
        addPostEventListener(sendName, getName)

        findViewById<ImageButton>(R.id.sendButton).setOnClickListener {

            val text = findViewById<EditText>(R.id.messageEditText).text.toString()
            if (text == "") {
                Toast.makeText(this, "Пожалуйста, введите сообщение", Toast.LENGTH_SHORT).show()
            } else {
                sendMessage(sendName, getName, text, "text", getChatName(sendName, getName))
                findViewById<EditText>(R.id.messageEditText).text.clear()
            }
        }


    }


    /**
     * Функция, которая определяет формат загружаемого файла по параметру [isNeededFile].
     * */
    @RequiresApi(Build.VERSION_CODES.O)
    private fun pickFileOrPhoto(isNeededFile: Boolean) {
        val intent = Intent(Intent.ACTION_PICK)
        if (isNeededFile) {
            intent.type = "*/*"
            typeOfFile = "file"
        } else {
            intent.type = "image/*"
            typeOfFile = "photo"
        }
        resultLauncher.launch(intent)

    }

    /**
     * Функция, которая загружает выбранный файл(фото) на сервер и отправляет соответствуееще сообщение на сервер.
     * */
    @RequiresApi(Build.VERSION_CODES.O)
    private var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                //verifyStoragePermissions(this)
                val data: Intent? = result.data
                if (data != null && data.data != null) {
                    val uriPathHelper = UriPathHelper()
                    val filePath = uriPathHelper.getPathFromUri(this, data.data!!)!!.toString()
                    val subFile = filePath.substring(filePath.lastIndexOf("/") + 1)
                    val chatName = getChatName(sendName, getName)
                    val currentTimestamp = System.currentTimeMillis().toString()

                    putFile(filePath, chatName, currentTimestamp)
                    sendMessage(
                        sendName,
                        getName,
                        chatName = getChatName(sendName, getName),
                        type = typeOfFile,
                        text = "$currentTimestamp/$subFile"
                    )
                }
            }


        }


    /**
     * Функция, которая загружает файл на сервер. [file] - путь к файлу на телефоне, [chatName] -
     * название чата между пользователями, [currentTimestamp] - штамп о времени отправления сообщения.
     * */
    private fun putFile(file: String, chatName: String, currentTimestamp: String) {
        val refStorageRoot = FirebaseStorage.getInstance().reference
        val putPath =
            refStorageRoot.child(chatName)
        val uriFile = Uri.fromFile(File(file))
        val subFile = file.substring(file.lastIndexOf("/") + 1)

        putPath.child(currentTimestamp).child(subFile).putFile(uriFile)
    }


    private fun addPostEventListener(sendUser: String, getUser: String) {
        val chatName = getChatName(sendUser, getUser)
        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val rcView = findViewById<RecyclerView>(R.id.messagesRcView)
                rcView.layoutManager = LinearLayoutManager(this@IndividualChatActivity)
                val adapter = GroupAdapter<GroupieViewHolder>()
                val username = "username"
                val text = "text"
                val type = "type"
                val dataTime = "dataTime"
                updateChat(sendUser, getUser, false)
                // Get Post object and use the values to update the UI
                // val post = dataSnapshot.value
                //Log.w("T", "$post")

                for (i in dataSnapshot.children) {
                    when (i.child(type).value.toString()) {
                        "text" -> {
                            val dt = i.child(dataTime).value.toString()
                            val tx = i.child(text).value.toString()

                            if (i.child(username).value.toString() == sendUser) {
                                adapter.add(ChatToItem(tx, dt))
                            } else {
                                adapter.add(ChatFromItem(tx, dt))
                            }
                        }
                        "file" -> {
                            val dt = i.child(dataTime).value.toString()
                            val tx = i.child(text).value.toString()
                            if (i.child(username).value.toString() == sendUser) {
                                adapter.add(
                                    ChatToFileItem(
                                        tx,
                                        dt,
                                        chatName,
                                        this@IndividualChatActivity
                                    )
                                )
                            } else {
                                adapter.add(
                                    ChatFromFileItem(
                                        tx,
                                        dt,
                                        chatName,
                                        this@IndividualChatActivity
                                    )
                                )
                            }
                            Log.d("Message", "Новый файл")
                        }
                        "photo" -> {
                            val dt = i.child(dataTime).value.toString()
                            val tx = i.child(text).value.toString()
                            if (i.child(username).value.toString() == sendUser) {
                                adapter.add(
                                    ChatToImgItem(
                                        tx,
                                        dt,
                                        chatName,
                                        this@IndividualChatActivity,
                                        this@IndividualChatActivity
                                    )
                                )
                            } else {
                                adapter.add(
                                    ChatFromImgItem(
                                        tx,
                                        dt,
                                        chatName,
                                        this@IndividualChatActivity,
                                        this@IndividualChatActivity
                                    )
                                )
                            }
                            Log.d("Message", "Скачать фото")
                        }
                    }
                }
                rcView.adapter = adapter
                rcView.scrollToPosition(adapter.itemCount - 1)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.w("T", "loadPost:onCancelled", databaseError.toException())
            }
        }
        database = FirebaseDatabase.getInstance().getReference("chatMessages/$chatName")
        database.addValueEventListener(postListener)
    }


    private fun getChatName(sendUser: String, getUser: String): String {
        return if (sendUser > getUser) "$getUser$sendUser" else "$sendUser$getUser"
    }

    /**
     * Функция, которая отправляет новое сообщение.
     * [sendUser] пользователь приложения, [getUser] собеседник пользователя,
     * [text] - текст сообщения, [type] - тип сообщения, [chatName] - название чата между собеседниками(
     * подробнее в функции getChatName)
     * */
    @RequiresApi(Build.VERSION_CODES.O)
    private fun sendMessage(
        sendUser: String,
        getUser: String,
        text: String,
        type: String,
        chatName: String
    ) {
        val dateTime =
            LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:MM:ss"))

        updateChat(sendUser, getUser, true)
        database = FirebaseDatabase.getInstance().getReference("chatMessages")
        val message = mapOf(
            "text" to text,
            "type" to type,
            "username" to sendUser,
            "dataTime" to dateTime.toString(),
        )
        val currentTimestamp = System.currentTimeMillis().toString()
        database.child(chatName).child(currentTimestamp).updateChildren(message)
    }


    /**
     *  Функция, которая создаёт запись о существовании чата между пользователем [sendUser]
     *  и собеседником пользователя [getUser]. Параметр [isSend] определяет, было ли это
     *  отправлением сообщения или сообщение просто прочитали.
     *  @param isSend
     *  True - сообщение отправлено и у собеседника новое непрочитанное.
     *  False - чат просто открыт или сообщение прочитано.
     * */
    private fun updateChat(sendUser: String, getUser: String, isSend: Boolean) {
        database = FirebaseDatabase.getInstance().getReference("chatMembers")
        database.child(sendUser).child(getUser).setValue("true")
        if (isSend) {
            database.child(getUser).child(sendUser).setValue("false")
        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == storagePermissionCode) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Разрешение получено", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(
                    this, "Нет доступа к памяти", Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

}

/**
 * Класс с конструктором для отображения данных входящего сообщения.
 */
class ChatFromItem(val text: String, private val time: String) : Item<GroupieViewHolder>() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.findViewById<TextView>(R.id.from_ms_tv).text = text
        viewHolder.itemView.findViewById<TextView>(R.id.from_ms_time_tv).text = time
    }

    override fun getLayout(): Int {
        return R.layout.from_ms_item
    }

}

/**
 * Класс с конструктором для отображения данных исходящего сообщения.
 */
class ChatToItem(val text: String, private val time: String) : Item<GroupieViewHolder>() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.findViewById<TextView>(R.id.to_ms_tv).text = text
        viewHolder.itemView.findViewById<TextView>(R.id.to_ms_time_tv).text = time
    }

    override fun getLayout(): Int {
        return R.layout.to_message_item
    }

}

/**
 * Класс с конструктором для отображения файла в исходящем сообщении.
 */
class ChatToFileItem(
    val text: String,
    private val time: String,
    private val chatName: String,
    val context: Context

) : Item<GroupieViewHolder>() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.findViewById<TextView>(R.id.to_fileName_tv).text =
            text.substringAfter("/")
        viewHolder.itemView.findViewById<TextView>(R.id.to_file_time_tv).text = time
        viewHolder.itemView.findViewById<ImageView>(R.id.to_file_img)
            .setImageResource(R.drawable.ic_file_icon)

        viewHolder.itemView.findViewById<LinearLayout>(R.id.to_file_layout).setOnClickListener {
            download(text, chatName, context)
        }


    }


    override fun getLayout(): Int {
        return R.layout.to_file_item
    }

    /**
     * Функция, позволяющая загружать файлы напрямую в телефон. [filename] - имя файла с расширением,
     * [chatName] - имя чата между пользователями.
     * */
    private fun download(filename: String, chatName: String, context: Context) {
        val storageRef = Firebase.storage.reference
        val photoRef = storageRef.child(chatName).child(filename)
        val subFileName = filename.substring(filename.lastIndexOf("/") + 1)
        photoRef.downloadUrl
            .addOnSuccessListener { uri ->
                val url = uri.toString()
                Toast.makeText(context, "Загрузка файла началась", Toast.LENGTH_SHORT).show()
                downloadFile(context, subFileName, DIRECTORY_DOWNLOADS, url)
            }.addOnFailureListener { }
    }

    /**
     * Расширение функции загрузки фото
     * */
    private fun downloadFile(
        context: Context,
        fileName: String,
        destinationDirectory: String?,
        url: String?
    ) {

        val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val uri = Uri.parse(url)
        val request = DownloadManager.Request(uri)
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        request.setDestinationInExternalPublicDir(

            destinationDirectory,
            fileName
        )
        downloadManager.enqueue(request)

    }

}

/**
 * Класс с конструктором для отображения файла во входящем сообщении.
 */
class ChatFromFileItem(
    val text: String,
    private val time: String,
    private val chatName: String,
    val context: Context
) : Item<GroupieViewHolder>() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.findViewById<TextView>(R.id.from_fileName_tv).text =
            text.substringAfter("/")
        viewHolder.itemView.findViewById<TextView>(R.id.from_file_time_tv).text = time
        viewHolder.itemView.findViewById<ImageView>(R.id.from_file_img)
            .setImageResource(R.drawable.ic_file_icon)
        viewHolder.itemView.findViewById<LinearLayout>(R.id.from_file_layout).setOnClickListener {
            download(text, chatName, context)
        }
    }

    override fun getLayout(): Int {
        return R.layout.from_file_item
    }

    /**
     * Функция, позволяющая загружать файлы напрямую в телефон. [filename] - имя файла с расширением,
     * [chatName] - имя чата между пользователями.
     * */
    private fun download(filename: String, chatName: String, context: Context) {

        val storageRef = Firebase.storage.reference
        val photoRef = storageRef.child(chatName).child(filename)
        val subFileName = filename.substring(filename.lastIndexOf("/") + 1)

        photoRef.downloadUrl
            .addOnSuccessListener { uri ->
                val url = uri.toString()
                Toast.makeText(context, "Загрузка файла началась", Toast.LENGTH_SHORT).show()
                downloadFile(context, subFileName, DIRECTORY_DOWNLOADS, url)
            }.addOnFailureListener { }


    }

    /**
     * Расширение функции загрузки фото
     * */
    private fun downloadFile(
        context: Context,
        fileName: String,
        destinationDirectory: String?,
        url: String?
    ) {
        val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val uri = Uri.parse(url)
        val request = DownloadManager.Request(uri)
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        request.setDestinationInExternalPublicDir(
            destinationDirectory,
            fileName
        )
        downloadManager.enqueue(request)
    }
}

/**
 * Класс с конструктором для отображения картинки в входящем сообщении.
 */
class ChatFromImgItem(
    private val filename: String,
    private val time: String,
    private val chatName: String,
    val context: Context,
    private val activity: Activity?
) : Item<GroupieViewHolder>() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {

        viewHolder.itemView.findViewById<TextView>(R.id.from_img_time_tv).text = time
/*
        viewHolder.itemView.findViewById<ImageView>(R.id.from_img)
            .setImageResource(R.drawable.aesthetic_desert_2560_x_1440)
*/
        viewHolder.itemView.findViewById<ImageView>(R.id.from_img).setImageResource(0)
        displayImage(filename, chatName, viewHolder)
        viewHolder.itemView.findViewById<LinearLayout>(R.id.from_img_layout).setOnClickListener {
            val intent = Intent(context, ImageActivity::class.java)
            intent.putExtra("chatName", chatName)
            intent.putExtra("fileName", filename)
            context.startActivity(intent)
        }

    }

    private fun displayImage(filename: String, chatName: String, viewHolder: GroupieViewHolder) =
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val imageRef = Firebase.storage.reference
                val maxDownloadSize = 5L * 1024 * 1024 * 1024
                val bytes = imageRef.child("$chatName/$filename").getBytes(maxDownloadSize).await()
                val bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                withContext(Dispatchers.Main) {
                    viewHolder.itemView.findViewById<ImageView>(R.id.from_img)
                        .setImageBitmap(bmp)
                    //imageView?.setImageBitmap(bmp)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(activity, e.message, Toast.LENGTH_LONG).show()
                }
            }
        }

    override fun getLayout(): Int {
        return R.layout.from_img_item
    }

}


/**
 * Класс с конструктором для отображения картинки в исходящем сообщении.
 */
class ChatToImgItem(
    private val filename: String,
    private val time: String,
    private val chatName: String,
    val context: Context,
    private val activity: Activity?
) :
    Item<GroupieViewHolder>() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {


        viewHolder.itemView.findViewById<TextView>(R.id.to_img_time_tv).text = time
/*
        viewHolder.itemView.findViewById<ImageView>(R.id.from_img)
            .setImageResource(R.drawable.aesthetic_desert_2560_x_1440)
*/
        viewHolder.itemView.findViewById<ImageView>(R.id.to_img).setImageResource(0)
        displayImage(filename, chatName, viewHolder)
        viewHolder.itemView.findViewById<LinearLayout>(R.id.to_img_layout).setOnClickListener {
            val intent = Intent(context, ImageActivity::class.java)
            intent.putExtra("chatName", chatName)
            intent.putExtra("fileName", filename)
            context.startActivity(intent)
        }
    }

    private fun displayImage(filename: String, chatName: String, viewHolder: GroupieViewHolder) =
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val imageRef = Firebase.storage.reference
                val maxDownloadSize = 5L * 1024 * 1024 * 1024
                val bytes = imageRef.child("$chatName/$filename").getBytes(maxDownloadSize).await()
                val bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                withContext(Dispatchers.Main) {
                    viewHolder.itemView.findViewById<ImageView>(R.id.to_img)
                        .setImageBitmap(bmp)
                    //imageView?.setImageBitmap(bmp)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(activity, e.message, Toast.LENGTH_LONG).show()
                }
            }
        }

    override fun getLayout(): Int {
        return R.layout.to_img_item
    }

}
