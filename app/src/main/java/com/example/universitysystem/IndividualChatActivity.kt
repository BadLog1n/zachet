package com.example.universitysystem

import UriPathHelper.UriPathHelper
import android.Manifest
import android.annotation.SuppressLint
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
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
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
import authCheck.AuthCheck
import chatsPackage.ChatsPackage
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Executors

class IndividualChatActivity : AppCompatActivity() {

    private var typeOfFile = ""
    private var sendName = ""
    private var getName = ""
    private lateinit var database: DatabaseReference
    private val storagePermissionCode = 0
    private val chatsPackage = ChatsPackage()

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
        findViewById<TextView>(R.id.receiver_tv).text = ""



        database = FirebaseDatabase.getInstance().getReference("users/$getName")
        val requestToDatabase = database.get()
        requestToDatabase.addOnSuccessListener {
            val name = if (it.child("name").value.toString() != "null") it.child("name").value.toString() else getName
            val surname = if (it.child("surname").value.toString() != "null") it.child("surname").value.toString() else ""
            val displayName = "$name $surname"
            findViewById<TextView>(R.id.receiver_tv).text = displayName
        }
        findViewById<ImageButton>(R.id.backFromChatBtn).setOnClickListener {
            onBackPressed()
        }


        findViewById<RecyclerView>(R.id.messagesRcView).addOnLayoutChangeListener { _, _, _, _, bottom, _, _, _, oldBottom ->
            if (bottom < oldBottom) {
                rcView.post {
                    try {
                    rcView.scrollToPosition(
                        rcView.adapter!!.itemCount - 1
                    )}
                    catch (e: NullPointerException) {
                        
                    }
                }
            }
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
            if (text.isBlank() or text.isEmpty()) {
                Toast.makeText(this, "Пожалуйста, введите сообщение", Toast.LENGTH_SHORT).show()
            } else {
                chatsPackage.sendMessage(
                    sendName,
                    getName,
                    text.replace("\\s+".toRegex(), " "),
                    "text",
                    chatsPackage.getChatName(sendName, getName)
                )
                findViewById<EditText>(R.id.messageEditText).text.clear()
            }
        }
        chatsPackage.updateChat(sendName, getName, false)

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
                    val chatName = chatsPackage.getChatName(sendName, getName)
                    val currentTimestamp = System.currentTimeMillis().toString()

                    chatsPackage.putFile(filePath, chatName, currentTimestamp)
                    chatsPackage.sendMessage(
                        sendName,
                        getName,
                        chatName = chatsPackage.getChatName(sendName, getName),
                        type = typeOfFile,
                        text = "$currentTimestamp/$subFile"
                    )
                }
            }


        }


    private fun addPostEventListener(sendUser: String, getUser: String) {
        val chatName = chatsPackage.getChatName(sendUser, getUser)
        val postListener = object : ValueEventListener {
            @SuppressLint("SimpleDateFormat")
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val rcView = findViewById<RecyclerView>(R.id.messagesRcView)
                rcView.layoutManager = LinearLayoutManager(this@IndividualChatActivity)
                val adapter = GroupAdapter<GroupieViewHolder>()
                val username = "username"
                val text = "text"
                val type = "type"
                for (i in dataSnapshot.children) {
                   val dt = SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(Date(i.key!!.toLong())).toString()


                    when (i.child(type).value.toString()) {
                        "text" -> {
                            val tx = i.child(text).value.toString()

                            if (i.child(username).value.toString() == sendUser) {
                                adapter.add(ChatToItem(tx, dt))
                            } else {
                                adapter.add(ChatFromItem(tx, dt))
                            }
                        }
                        "file" -> {
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
    private var trying = 3
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        trying = 3
        viewHolder.itemView.findViewById<ImageView>(R.id.from_img).setImageResource(0)
        displayImage(filename, chatName, viewHolder)
        if (trying >= 0) {
            viewHolder.itemView.findViewById<TextView>(R.id.from_img_time_tv).text =
                "Загрузка фотографии"
            val executor = Executors.newSingleThreadExecutor()

            CoroutineScope(Dispatchers.Main).launch {
                executor.execute {
                    while (trying > 0) {

                        Handler(Looper.getMainLooper()).post {

                            displayImage(filename, chatName, viewHolder)
                        }
                        Thread.sleep(5000)
                        trying -= 1
                    }
                    if (trying == 0) {
                        Handler(Looper.getMainLooper()).post {
                            Toast.makeText(
                                activity,
                                "Ошибка загрузки миниатюры. Пожалуйста перезайдите в чат",
                                Toast.LENGTH_SHORT
                            ).show()
                            viewHolder.itemView.findViewById<TextView>(R.id.from_img_time_tv).text =
                                "Ошибка загрузки"
                        }
                    }
                }

            }

        }

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
                    viewHolder.itemView.findViewById<TextView>(R.id.from_img_time_tv).text = time
                    trying = -1

                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
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
    private var trying = 3
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        trying = 3
        viewHolder.itemView.findViewById<ImageView>(R.id.to_img).setImageResource(0)
        displayImage(filename, chatName, viewHolder)
        if (trying >= 0) {
            viewHolder.itemView.findViewById<TextView>(R.id.to_img_time_tv).text =
                "Загрузка фотографии"
            val executor = Executors.newSingleThreadExecutor()

            CoroutineScope(Dispatchers.Main).launch {
                executor.execute {
                    while (trying > 0) {
                        Handler(Looper.getMainLooper()).post {

                            displayImage(filename, chatName, viewHolder)
                        }
                        trying -= 1
                        Thread.sleep(5000)

                    }
                    if (trying == 0) {
                        Handler(Looper.getMainLooper()).post {
                            Toast.makeText(
                                activity,
                                "Ошибка загрузки миниатюры. Пожалуйста перезайдите в чат",
                                Toast.LENGTH_SHORT
                            ).show()
                            viewHolder.itemView.findViewById<TextView>(R.id.to_img_time_tv).text =
                                "Ошибка загрузки"
                        }
                    }
                }

            }
        }



        viewHolder.itemView.findViewById<LinearLayout>(R.id.to_img_layout).setOnClickListener {
            val intent = Intent(context, ImageActivity::class.java)
            intent.putExtra("chatName", chatName)
            intent.putExtra("fileName", filename)
            context.startActivity(intent)
        }
    }

    private fun displayImage(filename: String, chatName: String, viewHolder: GroupieViewHolder) =
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val imageRef = Firebase.storage.reference
                val maxDownloadSize = 5L * 1024 * 1024 * 1024
                val bytes = imageRef.child("$chatName/$filename").getBytes(maxDownloadSize).await()
                val bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                withContext(Dispatchers.Main) {
                    viewHolder.itemView.findViewById<ImageView>(R.id.to_img)
                        .setImageBitmap(bmp)
                    //imageView?.setImageBitmap(bmp)
                    viewHolder.itemView.findViewById<TextView>(R.id.to_img_time_tv).text = time
                    trying = -1

                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                }
            }
        }
    override fun getLayout(): Int {
        return R.layout.to_img_item
    }

}
