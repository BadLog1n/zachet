package com.oneseed.zachet.activities

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
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import chatsPackage.ChatsPackage
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.oneseed.zachet.R
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import uriPathHelper.UriPathHelper
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Executors



class IndividualChatActivity : AppCompatActivity() {

    private var typeOfFile = ""
    /**uid пользователя*/
    private var sendName = ""
    /**uid получателя*/
    private var getName = ""
    private lateinit var database: DatabaseReference
    /**Код запроса доступа к памяти*/
    private val storagePermissionCode = 0
    private val chatsPackage = ChatsPackage()
    /**Имя и фамилия получателя*/
    private var userName = ""
    /**Индикатор необходимости перезагружать изображения*/
    private var loadImagesAgain = true
    /**Логин получателя*/
    private var userLogin = ""
    /**UID чата*/
    private lateinit var chatName: String
    /**Индикатор нахождения в самом конце чата*/
    private var isScrolledLast = false
    private lateinit var postListener: ValueEventListener
    private lateinit var progressBar: ProgressBar


    override fun onCreate(savedInstanceState: Bundle?) {

        val arguments = intent.extras
        getName = arguments!!.getString(getString(R.string.getUser)).toString()
        val sharedPref: SharedPreferences? =
            this.getSharedPreferences(getString(R.string.settingsShared), MODE_PRIVATE)
        sendName = sharedPref?.getString(getString(R.string.uid), "").toString()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_individual_chat)

        val rootView = window.decorView as ViewGroup

        val customView = layoutInflater.inflate(R.layout.chat_action_bar, rootView, false)
        supportActionBar?.displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM
        supportActionBar?.customView = customView
        val backFromChatBtn = customView.findViewById<ImageButton>(R.id.backFromChatBtn)
        val layout = customView.findViewById<ConstraintLayout>(R.id.layout)
        val receiverTv = customView.findViewById<TextView>(R.id.receiver_tv)
        supportActionBar?.show()
        backFromChatBtn.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        layout.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        progressBar = findViewById(R.id.messagesProgressBar)
        val rcView = findViewById<RecyclerView>(R.id.messagesRcView)
        rcView.layoutManager = LinearLayoutManager(this)
        receiverTv.text = ""

        loadImagesAgain = sharedPref?.getBoolean(getString(R.string.loadImages), true) == true

        database = FirebaseDatabase.getInstance().getReference("users/$getName")
        var requestToDatabase = database.get()
        requestToDatabase.addOnSuccessListener {

            val name =
                if (it.child("name").value.toString() != "null") it.child("name").value.toString() else ""
            val surname =
                if (it.child("surname").value.toString() != "null") it.child("surname").value.toString() else ""
            val displayName: String =
                if (name.isNotEmpty() || surname.isNotEmpty()) "$name $surname" else it.child("login").value.toString()
            findViewById<TextView>(R.id.receiver_tv).text = displayName
        }

        database = FirebaseDatabase.getInstance().getReference("users/$sendName")
        requestToDatabase = database.get()
        requestToDatabase.addOnSuccessListener {
            val name =
                if (it.child("name").value.toString() != "null") it.child("name").value.toString() else ""
            val surname =
                if (it.child("surname").value.toString() != "null") it.child("surname").value.toString() else ""
            userName = "$name $surname"
            userLogin = it.child("login").value.toString()
        }

        rcView.addOnLayoutChangeListener { _, _, _, _, bottom, _, _, _, oldBottom ->
            if (bottom < oldBottom) {
                rcView.post {
                    try {
                        rcView.smoothScrollToPosition((rcView.layoutManager as LinearLayoutManager).findLastVisibleItemPosition() + 2)
                    } catch (_: NullPointerException) {
                    }
                }
            }
        }

        rcView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                isScrolledLast =
                    !recyclerView.canScrollVertically(1) && newState == RecyclerView.SCROLL_STATE_IDLE
            }
        })

        val spinner = this.findViewById<Spinner>(R.id.spinner2)

        if (Build.VERSION.SDK_INT < 26) {
            spinner.visibility = View.GONE
            findViewById<ImageButton>(R.id.clipButton).visibility = View.GONE
        }

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}
            override fun onItemSelected(
                parent: AdapterView<*>?, view: View?, position: Int, id: Long
            ) {
                when (spinner.selectedItem.toString()) {
                    "Изображение" -> {
                        try {
                            if ((ContextCompat.checkSelfPermission(
                                    this@IndividualChatActivity,
                                    Manifest.permission.READ_EXTERNAL_STORAGE
                                ) != PackageManager.PERMISSION_GRANTED && (ContextCompat.checkSelfPermission(
                                    this@IndividualChatActivity,
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                                ) != PackageManager.PERMISSION_GRANTED))
                            ) {
                                ActivityCompat.requestPermissions(
                                    this@IndividualChatActivity, arrayOf(
                                        Manifest.permission.READ_EXTERNAL_STORAGE,
                                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                                    ), storagePermissionCode
                                )
                            } else {
                                pickFileOrPhoto(false)
                            }
                        } catch (e: Exception) {
                            Toast.makeText(
                                this@IndividualChatActivity,
                                "Вам необходимо выдать разрешение на работу с памятью в настройках, чтобы загружать изображения",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                    "Файл" -> {
                        try {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                                if (!isExternalStorageManager()) {
                                    Toast.makeText(
                                        this@IndividualChatActivity,
                                        "Вам необходимо выдать разрешение на работу с памятью в настройках, чтобы загружать файлы",
                                        Toast.LENGTH_LONG
                                    ).show()
                                } else {
                                    pickFileOrPhoto(true)
                                }
                            } else if ((ContextCompat.checkSelfPermission(
                                    this@IndividualChatActivity,
                                    Manifest.permission.READ_EXTERNAL_STORAGE
                                ) != PackageManager.PERMISSION_GRANTED && (ContextCompat.checkSelfPermission(
                                    this@IndividualChatActivity,
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                                ) != PackageManager.PERMISSION_GRANTED))
                            ) {
                                ActivityCompat.requestPermissions(
                                    this@IndividualChatActivity, arrayOf(
                                        Manifest.permission.READ_EXTERNAL_STORAGE,
                                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                                    ), storagePermissionCode
                                )
                            } else {
                                pickFileOrPhoto(true)
                            }

                        } catch (e: Exception) {
                            Toast.makeText(
                                this@IndividualChatActivity,
                                "Нет доступа к памяти",
                                Toast.LENGTH_SHORT
                            ).show()

                        }
                    }
                }
                spinner.setSelection(0)
            }
        }

        spinner.setOnLongClickListener {
            pickFileOrPhoto(false)
            return@setOnLongClickListener true
        }

        addPostEventListener(sendName, getName)

        findViewById<ImageButton>(R.id.sendButton).setOnClickListener {

            val text = findViewById<EditText>(R.id.messageEditText).text.toString()
            if (text.isBlank() or text.isEmpty()) {
                Toast.makeText(this, "Пожалуйста, введите сообщение", Toast.LENGTH_SHORT).show()
            } else {
                sendMessage(text)
                findViewById<EditText>(R.id.messageEditText).text.clear()
            }
        }
        chatsPackage.updateChat(sendName, getName, false)

    }

    /**Отправляет сообщение в базу данных. Об-резает сообщение, превышающее макси-мальное количество символов и отправляет частями.
     * @param text текст сообщения
     * */
    private fun sendMessage(text: String) {
        val lengthTooBig = text.length > 200
        val textSub = if (lengthTooBig) {
            text.substring(0, 200)
        } else text
        val chatName =
            if (getName.contains("group")) getName else chatsPackage.getChatName(sendName, getName)
        chatsPackage.sendMessage(
            sendName,
            getName,
            textSub.replace("\\s+".toRegex(), " "),
            "text",
            chatName,
            userName,
            userLogin
        )
        if (lengthTooBig) {
            sendMessage(text.substring(200))
        }
    }

    /**
     * Функция, которая определяет формат загружаемого файла по параметру [isNeededFile].
     * @param isNeededFile True - файл, False - изображение
     * */
    private fun pickFileOrPhoto(isNeededFile: Boolean) {
        if (Build.VERSION.SDK_INT >= 26) {
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
    }

    /**
     * Функция, которая загружает выбранный файл(фото) на сервер и отправляет соответствуееще сообщение на сервер.
     * */
    private var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                //verifyStoragePermissions(this)
                val data: Intent? = result.data
                if (data != null && data.data != null) {
                    val uriPathHelper = UriPathHelper()
                    val filePath = uriPathHelper.getPathFromUri(this, data.data!!)!!.toString()
                    val subFile = filePath.substring(filePath.lastIndexOf("/") + 1)
                    val chatName =
                        if (getName.contains("group")) getName else chatsPackage.getChatName(
                            sendName, getName
                        )
                    val currentTimestamp = System.currentTimeMillis().toString()

                    val builder = AlertDialog.Builder(this)
                    builder.setTitle("Отправить файл?")
                    builder.setPositiveButton("Да") { _, _ ->
                        chatsPackage.putFile(filePath, chatName, currentTimestamp)
                        chatsPackage.sendMessage(
                            sendName,
                            getName,
                            "$currentTimestamp/$subFile",
                            typeOfFile,
                            chatName,
                            userName,
                            userLogin
                        )
                    }
                    builder.setNeutralButton("Отмена") { _, _ -> }
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
            }
        }

    var lastTimeMessage: Long = 0
    var isFirstLoad = true


    /**Слушатель изменений в базе данных с дальнейшим отображением их.
     * @param sendUser UID пользователя
     * @param getName UID получателя
     * */
    private fun addPostEventListener(sendUser: String, getName: String) {
        chatName =
            if (getName.contains("group")) getName else chatsPackage.getChatName(sendName, getName)
        val rcView = findViewById<RecyclerView>(R.id.messagesRcView)
        rcView.layoutManager = LinearLayoutManager(this@IndividualChatActivity)
        val adapter = GroupAdapter<GroupieViewHolder>()
        rcView.adapter = adapter
        postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.childrenCount == 0L) {
                    progressBar.visibility = View.GONE
                }
                val userUid = "userUid"
                val text = "text"
                val type = "type"
                for (i in dataSnapshot.children) {

                    if (lastTimeMessage < i.key!!.toLong()) {
                        lastTimeMessage = i.key!!.toLong()
                        //Log.d("timestamp", i.key!!.toLong().toString())

                        val dt = SimpleDateFormat(
                            "dd/MM/yyyy HH:mm:ss", Locale.getDefault()
                        ).format(Date(i.key!!.toLong())).toString()
                        val usernameId = i.child("userLogin").value.toString()
                        val sendNameUser = i.child("sendName").value.toString()
                        val displaySendName =
                            if (getName.contains("group")) "$sendNameUser ($usernameId)" else ""

                        when (i.child(type).value.toString()) {
                            "text" -> {
                                val tx = i.child(text).value.toString()

                                if (i.child(userUid).value.toString() == sendUser) {
                                    adapter.add(ChatToItem(tx, dt, displaySendName))
                                } else {
                                    adapter.add(ChatFromItem(tx, dt, displaySendName))
                                }
                            }
                            "file" -> {
                                val tx = i.child(text).value.toString()
                                if (i.child(userUid).value.toString() == sendUser) {
                                    adapter.add(
                                        ChatToFileItem(
                                            tx,
                                            dt,
                                            chatName,
                                            this@IndividualChatActivity,
                                            displaySendName
                                        )
                                    )
                                } else {
                                    adapter.add(
                                        ChatFromFileItem(
                                            tx,
                                            dt,
                                            chatName,
                                            this@IndividualChatActivity,
                                            displaySendName
                                        )
                                    )
                                }
                                Log.d("Message", "Новый файл")
                            }
                            "photo" -> {
                                val tx = i.child(text).value.toString()
                                if (i.child(userUid).value.toString() == sendUser) {
                                    adapter.add(
                                        ChatToImgItem(
                                            tx,
                                            dt,
                                            chatName,
                                            this@IndividualChatActivity,
                                            displaySendName,
                                            loadImagesAgain
                                        )
                                    )
                                } else {
                                    adapter.add(
                                        ChatFromImgItem(
                                            tx,
                                            dt,
                                            chatName,
                                            this@IndividualChatActivity,
                                            displaySendName,
                                            loadImagesAgain
                                        )
                                    )
                                }
                                // Log.d("Message", "Скачать фото")
                            }
                        }
                    }
                    if (isFirstLoad && i.toString() == dataSnapshot.children.last().toString()) {
                        rcView.scrollToPosition(adapter.itemCount - 1)
                        isFirstLoad = false
                        Firebase.analytics.logEvent("chats_upload") {
                            param("chats_upload", "")
                        }
                        progressBar.visibility = View.GONE
                    } else if (isScrolledLast) {
                        rcView.smoothScrollToPosition(adapter.itemCount - 1)
                    }
                    rcView.adapter?.notifyItemChanged(adapter.itemCount)

                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.w("T", "loadPost:onCancelled", databaseError.toException())
            }
        }

    }

    // Убирает слушатель изменений в базе данных
    override fun onStop() {
        database = FirebaseDatabase.getInstance().getReference("chatMessages/$chatName")
        database.addValueEventListener(postListener)
        //Log.w("Base", "Database closed")

        super.onStop()
    }

    // Устанавливает слушатель изменений в базе данных
    override fun onResume() {
        database = FirebaseDatabase.getInstance().getReference("chatMessages/$chatName")
        database.addValueEventListener(postListener)
        super.onResume()
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
 * Класс с конструктором для отображения данных исходящего сообщения.
 * @param text текст сообщения
 * @param time дата и время отправления сообщения
 * @param displayUser имя отправителя
 */
class ChatFromItem(val text: String, private val time: String, private val displayUser: String) :
    Item<GroupieViewHolder>() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.findViewById<TextView>(R.id.from_ms_tv).text = text
        viewHolder.itemView.findViewById<TextView>(R.id.from_ms_time_tv).text = time
        if (displayUser != "") {
            val viewName = viewHolder.itemView.findViewById<TextView>(R.id.from_ms_name)
            viewName.text = displayUser
            viewName.visibility = View.VISIBLE
        }
    }

    override fun getLayout(): Int {
        return R.layout.from_ms_item
    }

}

/**
 * Класс с конструктором для отображения данных исходящего сообщения.
 * @param text текст сообщения
 * @param time дата и время отправления сообщения
 * @param displayUser имя отправителя
 */
class ChatToItem(val text: String, private val time: String, private val displayUser: String) :
    Item<GroupieViewHolder>() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.findViewById<TextView>(R.id.to_ms_tv).text = text
        viewHolder.itemView.findViewById<TextView>(R.id.to_ms_time_tv).text = time
        if (displayUser != "") {
            val viewName = viewHolder.itemView.findViewById<TextView>(R.id.to_msg_name_tv)
            viewName.text = displayUser
            viewName.visibility = View.VISIBLE
        }
    }

    override fun getLayout(): Int {
        return R.layout.to_message_item
    }

}

/**
 * Класс с конструктором для отображения файла в исходящем сообщении.
 * @param text название файла
 * @param time дата и время отправления сообщения
 * @param chatName ID чата
 * @param displayUser имя отправителя
 */
class ChatToFileItem(
    val text: String,
    private val time: String,
    private val chatName: String,
    val context: Context,
    private val displayUser: String

) : Item<GroupieViewHolder>() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.findViewById<TextView>(R.id.to_fileName_tv).text =
            text.substringAfter("/")
        viewHolder.itemView.findViewById<TextView>(R.id.to_file_time_tv).text = time
        if (displayUser != "") {
            val viewName = viewHolder.itemView.findViewById<TextView>(R.id.to_fl_uname_tv)
            viewName.text = displayUser
            viewName.visibility = View.VISIBLE
        }


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
        photoRef.downloadUrl.addOnSuccessListener { uri ->
            val url = uri.toString()
            Toast.makeText(context, "Загрузка файла началась", Toast.LENGTH_SHORT).show()
            downloadFile(context, subFileName, DIRECTORY_DOWNLOADS, url)
        }.addOnFailureListener { }
    }

    /**
     * Расширение функции загрузки файла, отвечает за загрузку файла
     * @param fileName имя файла без расширения
     * @param destinationDirectory путь загрузки
     * @param url ссылка на файл
     * */
    private fun downloadFile(
        context: Context, fileName: String, destinationDirectory: String?, url: String?
    ) {

        val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val uri = Uri.parse(url)
        val request = DownloadManager.Request(uri)
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        request.setDestinationInExternalPublicDir(

            destinationDirectory, fileName
        )
        downloadManager.enqueue(request)

    }

}

/**
 * Класс с конструктором для отображения файла во входящем сообщении.
 * @param text название файла
 * @param time дата и время отправления сообщения
 * @param chatName ID чата
 * @param displayUser имя отправителя
 */
class ChatFromFileItem(
    val text: String,
    private val time: String,
    private val chatName: String,
    val context: Context,
    private val displayUser: String
) : Item<GroupieViewHolder>() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.findViewById<TextView>(R.id.from_fileName_tv).text =
            text.substringAfter("/")
        viewHolder.itemView.findViewById<TextView>(R.id.from_file_time_tv).text = time

        if (displayUser != "") {
            val viewName = viewHolder.itemView.findViewById<TextView>(R.id.from_fl_uname)
            viewName.text = displayUser
            viewName.visibility = View.VISIBLE
        }

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

        photoRef.downloadUrl.addOnSuccessListener { uri ->
            val url = uri.toString()
            Toast.makeText(context, "Загрузка файла началась", Toast.LENGTH_SHORT).show()
            downloadFile(context, subFileName, DIRECTORY_DOWNLOADS, url)
        }.addOnFailureListener { }


    }

    /**
     * Расширение функции загрузки файла, отвечает за загрузку файла
     * @param fileName имя файла без расширения
     * @param destinationDirectory путь загрузки
     * @param url ссылка на файл
     * */
    private fun downloadFile(
        context: Context, fileName: String, destinationDirectory: String?, url: String?
    ) {
        val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val uri = Uri.parse(url)
        val request = DownloadManager.Request(uri)
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        request.setDestinationInExternalPublicDir(
            destinationDirectory, fileName
        )
        downloadManager.enqueue(request)
    }
}

/**
 * Класс с конструктором для отображения картинки в входящем сообщении.
 * @param filename название файла
 * @param time дата и время отправления сообщения
 * @param chatName  ID чата
 * @param displayUser имя отправителя
 * @param loadImagesAgain нужно ли очищать ImageView при каждом новом обращении
 */
class ChatFromImgItem(
    private val filename: String,
    private val time: String,
    private val chatName: String,
    val context: Context,
    private val displayUser: String,
    private val loadImagesAgain: Boolean,
) : Item<GroupieViewHolder>() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        val imageView = viewHolder.itemView.findViewById<ImageView>(R.id.from_img)
        val viewName = viewHolder.itemView.findViewById<TextView>(R.id.from_img_name)
        if (!loadImagesAgain) imageView.setImageDrawable(null)
        displayImage(filename, chatName, viewHolder)
        viewHolder.itemView.findViewById<TextView>(R.id.from_img_time_tv).text =
            "Загрузка фотографии"
        val executor = Executors.newSingleThreadExecutor()
        if (displayUser != "") {

            viewName.text = displayUser
            viewName.visibility = View.VISIBLE
        }

        executor.execute {
            for (i in 1..3) {
                if (imageView.drawable != null) {
                    break
                }
                Handler(Looper.getMainLooper()).post {
                    displayImage(filename, chatName, viewHolder)
                }
                Thread.sleep(5000)
            }
            if (imageView.drawable == null) {
                Handler(Looper.getMainLooper()).post {
                    viewHolder.itemView.findViewById<TextView>(R.id.from_img_time_tv).text =
                        "Ошибка загрузки"
                    viewHolder.itemView.findViewById<LinearLayout>(R.id.from_img_layout).visibility =
                        View.GONE
                    viewName.visibility = View.GONE
                    viewHolder.itemView.findViewById<ProgressBar>(R.id.from_img_progress).visibility =
                        View.GONE
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

    /**
     * Отображение изображения в чате.
     * @param filename Название файла
     * @param chatName ID чата
     */
    private fun displayImage(filename: String, chatName: String, viewHolder: GroupieViewHolder) =
        CoroutineScope(Dispatchers.IO).launch {

            try {
                viewHolder.itemView.findViewById<LinearLayout>(R.id.from_img_layout).visibility =
                    View.VISIBLE
                viewHolder.itemView.findViewById<ProgressBar>(R.id.from_img_progress).visibility =
                    View.VISIBLE
                val imageRef = Firebase.storage.reference
                val maxDownloadSize = 5L * 1024 * 1024 * 1024
                val bytes = imageRef.child("$chatName/$filename").getBytes(maxDownloadSize).await()
                val bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                withContext(Dispatchers.Main) {
                    viewHolder.itemView.findViewById<ImageView>(R.id.from_img).setImageBitmap(bmp)
                    viewHolder.itemView.findViewById<TextView>(R.id.from_img_time_tv).text = time
                    viewHolder.itemView.findViewById<ProgressBar>(R.id.from_img_progress).visibility =
                        View.GONE


                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {}
            }

        }

    override fun getLayout(): Int {
        return R.layout.from_img_item
    }

}


/**
 * Класс с конструктором для отображения картинки в исходящем сообщении.
 * @param filename название файла
 * @param time дата и время отправления сообщения
 * @param chatName  ID чата
 * @param displayUser имя отправителя
 * @param loadImagesAgain нужно ли очищать ImageView при каждом новом обращении
 */
class ChatToImgItem(
    private val filename: String,
    private val time: String,
    private val chatName: String,
    val context: Context,
    private val displayUser: String,
    private val loadImagesAgain: Boolean
) : Item<GroupieViewHolder>() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        val imageView = viewHolder.itemView.findViewById<ImageView>(R.id.to_img)
        val viewName = viewHolder.itemView.findViewById<TextView>(R.id.to_img_name_tv)
        if (!loadImagesAgain) imageView.setImageDrawable(null)
        displayImage(filename, chatName, viewHolder)
        viewHolder.itemView.findViewById<TextView>(R.id.to_img_time_tv).text = "Загрузка фотографии"
        val executor = Executors.newSingleThreadExecutor()
        if (displayUser != "") {

            viewName.text = displayUser
            viewName.visibility = View.VISIBLE
        }
        executor.execute {
            for (i in 1..3) {
                if (imageView.drawable != null) {
                    break
                }
                Handler(Looper.getMainLooper()).post {
                    displayImage(filename, chatName, viewHolder)
                }
                Thread.sleep(5000)
            }

            if (imageView.drawable == null) {
                Handler(Looper.getMainLooper()).post {
                    viewHolder.itemView.findViewById<TextView>(R.id.to_img_time_tv).text =
                        "Ошибка загрузки"
                    viewHolder.itemView.findViewById<LinearLayout>(R.id.to_img_layout).visibility =
                        View.GONE
                    viewName.visibility = View.GONE
                    viewHolder.itemView.findViewById<ProgressBar>(R.id.to_img_progress).visibility =
                        View.GONE

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

    /**
     * Отображение изображения в чате.
     * @param filename Название файла
     * @param chatName ID чата
     */
    private fun displayImage(filename: String, chatName: String, viewHolder: GroupieViewHolder) =
        CoroutineScope(Dispatchers.Main).launch {
            try {
                viewHolder.itemView.findViewById<LinearLayout>(R.id.to_img_layout).visibility =
                    View.VISIBLE
                viewHolder.itemView.findViewById<ProgressBar>(R.id.to_img_progress).visibility =
                    View.VISIBLE
                val imageRef = Firebase.storage.reference
                val maxDownloadSize = 5L * 1024 * 1024 * 1024
                val bytes = imageRef.child("$chatName/$filename").getBytes(maxDownloadSize).await()
                val bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                withContext(Dispatchers.Main) {
                    viewHolder.itemView.findViewById<ImageView>(R.id.to_img).setImageBitmap(bmp)
                    //imageView?.setImageBitmap(bmp)
                    viewHolder.itemView.findViewById<TextView>(R.id.to_img_time_tv).text = time
                    viewHolder.itemView.findViewById<ProgressBar>(R.id.to_img_progress).visibility =
                        View.GONE


                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {}
            }
        }

    override fun getLayout(): Int {
        return R.layout.to_img_item
    }

}
