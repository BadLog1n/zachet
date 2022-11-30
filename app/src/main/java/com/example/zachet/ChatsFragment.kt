package com.example.zachet

import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.addCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import authCheck.AuthCheck
import chatsPackage.ChatsPackage
import com.google.firebase.database.*
import java.util.concurrent.Executors
import kotlin.system.exitProcess

class ChatsFragment : Fragment(R.layout.fragment_chats) {
    private val authCheck = AuthCheck()

    private val chatsPackage = ChatsPackage()


    private lateinit var database: DatabaseReference
    private var clickBack = false


    private var rcAdapter = ChatsAdapter()
    private var list = ArrayList<ChatPreview>()

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)
        authCheck.check(view, this@ChatsFragment.context)

        rcAdapter.clearRecords()
        val sharedPref: SharedPreferences? = activity?.getSharedPreferences(
            "Settings",
            AppCompatActivity.MODE_PRIVATE
        )
        val linearLayoutManager =
            LinearLayoutManager(this@ChatsFragment.context, LinearLayoutManager.VERTICAL, true)
        linearLayoutManager.stackFromEnd = true
        val userName = sharedPref?.getString(getString(R.string.saveUserId), "").toString()
        val recyclerView: RecyclerView = view.findViewById(R.id.chatsRcView)
        recyclerView.layoutManager = linearLayoutManager

        rcAdapter.clearRecords()
        rcAdapter.chatsList = ArrayList()
        rcAdapter.notifyDataSetChanged()
        recyclerView.adapter = rcAdapter
        recyclerView.layoutManager = linearLayoutManager


        view.findViewById<EditText>(R.id.searchTxtInput)
            .setOnEditorActionListener { _, actionId, _ ->
                userSearch()
                actionId == EditorInfo.IME_ACTION_GO

            }
        view.findViewById<ImageButton>(R.id.findChatButton).setOnClickListener {
            userSearch()
        }
        addPostEventListener(userName)
        val isTeacher = sharedPref?.getBoolean(getString(R.string.isTeacher), false)

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {

            if (isTeacher == true) {
                if (!clickBack) {
                    Toast.makeText(activity, "Нажмите ещё раз, чтобы выйти", Toast.LENGTH_SHORT)
                        .show()
                    clickBack = true
                    val executor = Executors.newSingleThreadExecutor()
                    executor.execute {
                        Thread.sleep(2000)
                        clickBack = false
                    }
                } else {
                    Thread.sleep(150)
                    exitProcess(0)
                }
            } else {
                findNavController().navigate(R.id.gradesFragment)
            }
        }

        val versionName = getAppVersion(requireContext())

        database = FirebaseDatabase.getInstance().getReference("version")
        database.get().addOnSuccessListener {
            if (versionName < it.value.toString() && isTeacher == true) {
                val builder = AlertDialog.Builder(requireContext())
                builder.setMessage(R.string.updateText)
                builder.setTitle(R.string.updateTitle)
                builder.setPositiveButton("Ок") { _, _ ->

                }
                builder.setNeutralButton("Перейти") { _, _ ->
                    findNavController().navigate(R.id.helpFragment)
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
        }
    }

    @Suppress("DEPRECATION")
    private fun getAppVersion(context: Context?): String {
        var version = ""
        try {
            val pInfo = if (Build.VERSION.SDK_INT >= 33) {
                context?.packageManager?.getPackageInfo(requireContext().packageName, 0)
                //TODO "Добавить следующий код когда будет переведено на новую версию проекта")
                //context?.packageManager?.getPackageInfo(requireContext().packageName, PackageManager.PackageInfoFlags.of(0))
            } else {
                context?.packageManager?.getPackageInfo(requireContext().packageName, 0)
            }
            version = pInfo!!.versionName
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }

        return version
    }


    private fun userSearch() {

        val user = view?.findViewById<EditText>(R.id.searchTxtInput)?.text.toString()
        database = FirebaseDatabase.getInstance().getReference("users")
        database.child(user).get().addOnSuccessListener {
            if (it.exists() && user != "") {

                val memberslist: Array<String> = if (it.child("members").exists())
                    it.child("members").value.toString().split(";").toTypedArray() else (arrayOf(""))
                val sharedPref: SharedPreferences? = activity?.getSharedPreferences(
                    "Settings",
                    AppCompatActivity.MODE_PRIVATE
                )
                val userName = sharedPref?.getString(getString(R.string.saveUserId), "").toString()
                if (memberslist[0] != "" && userName !in memberslist) {
                    Toast.makeText(requireContext(), "У вас нет доступа к чату", Toast.LENGTH_SHORT).show()
                    return@addOnSuccessListener
                }
                val intent =
                    Intent(this@ChatsFragment.context, IndividualChatActivity::class.java)
                intent.putExtra(getString(R.string.getUser), user)
                startActivity(intent)
                view?.findViewById<EditText>(R.id.searchTxtInput)?.setText("")
                val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as
                        InputMethodManager
                imm.hideSoftInputFromWindow(view?.windowToken, 0)
            } else {
                Toast.makeText(
                    activity,
                    "Пользователя или беседы не существует",
                    Toast.LENGTH_SHORT
                )
                    .show()
            }
        }
    }

    var isFirstTry = true
    private fun addPostEventListener(userName: String) {
        val postListener = object : ValueEventListener {
            var count = 0

            @SuppressLint("SimpleDateFormat", "NotifyDataSetChanged")
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (member in dataSnapshot.children) {
                    val getUser = member.key.toString()

                    val isRead = member.child("isRead").value.toString().toBooleanStrict()
                    val chatName =
                        if (getUser.contains("group")) getUser else chatsPackage.getChatName(
                            userName,
                            getUser
                        )
                    val text = "text"
                    val type = "type"
                    database = FirebaseDatabase.getInstance().getReference("users/$getUser")
                    var requestToDatabase = database.get()
                    requestToDatabase.addOnSuccessListener { itData ->
                        val name =
                            if (itData.child("name").value.toString() != "null") itData.child("name").value.toString() else getUser
                        val surname =
                            if (itData.child("surname").value.toString() != "null") itData.child("surname").value.toString() else ""
                        database =
                            FirebaseDatabase.getInstance().getReference("chatMessages/$chatName")
                        requestToDatabase = database.limitToLast(1).get()

                        requestToDatabase.addOnSuccessListener { itNew ->
                            for (i in itNew.children) {
/*
                                val dt =
                                    SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(Date(i.key!!.toLong()))
                                        .toString()*/
                                //val dt = i.key!!.toString()
                                val dtLong = i.key!!.toString().toLong()
                                val tx = i.child(text).value.toString()
                                when (i.child(type).value.toString()) {

                                    "text" -> {
                                        val chat =
                                            ChatPreview(
                                                "$name $surname",
                                                dtLong,
                                                tx,
                                                isRead,
                                                getUser
                                            )
                                        if (!isFirstTry) {
                                            try {
                                                val item: ChatPreview =
                                                    list.single { (it.getUser == getUser && it.latestMsgTime < dtLong) }
                                                rcAdapter.removeObject(item)
                                                list.remove(item)
                                                list.add(chat)
                                                rcAdapter.addChatPreview(chat)
                                            } catch (e: Exception) {
                                                try {
                                                    val item: ChatPreview =
                                                        list.single { (it.getUser == getUser && it.newMsg != isRead) }
                                                    rcAdapter.chatChange(item, chat)
                                                    list.remove(item)
                                                    list.add(chat)
                                                } catch (e: Exception) {
                                                    try {
                                                        list.single { (it.getUser == getUser) }

                                                    } catch (e: Exception) {
                                                        list.add(chat)
                                                        rcAdapter.addChatPreview(chat)
                                                    }
                                                }
                                            }

                                        } else {
                                            list.add(chat)
                                        }


                                    }
                                    "file" -> {
                                        val chat =
                                            ChatPreview(
                                                "$name $surname",
                                                dtLong,
                                                "Файл",
                                                isRead,
                                                getUser
                                            )
                                        if (!isFirstTry) {
                                            try {
                                                val item: ChatPreview =
                                                    list.single { (it.getUser == getUser && it.latestMsgTime < dtLong) }
                                                rcAdapter.removeObject(item)
                                                list.remove(item)
                                                list.add(chat)
                                                rcAdapter.addChatPreview(chat)
                                            } catch (e: Exception) {
                                                try {
                                                    val item: ChatPreview =
                                                        list.single { (it.getUser == getUser && it.newMsg != isRead) }
                                                    rcAdapter.chatChange(item, chat)
                                                    list.remove(item)
                                                    list.add(chat)
                                                } catch (e: Exception) {
                                                    try {
                                                        list.single { (it.getUser == getUser) }

                                                    } catch (e: Exception) {
                                                        list.add(chat)
                                                        rcAdapter.addChatPreview(chat)
                                                    }
                                                }
                                            }
                                        } else {
                                            list.add(chat)
                                        }

                                    }
                                    "photo" -> {
                                        val chat = ChatPreview(
                                            "$name $surname",
                                            dtLong,
                                            "Изображение",
                                            isRead,
                                            getUser
                                        )
                                        if (!isFirstTry) {
                                            try {
                                                val item: ChatPreview =
                                                    list.single { (it.getUser == getUser && it.latestMsgTime < dtLong) }
                                                rcAdapter.chatChange(item, chat)
                                                list.remove(item)
                                                list.add(chat)
                                            } catch (e: Exception) {
                                                try {
                                                    val item: ChatPreview =
                                                        list.single { (it.getUser == getUser && it.newMsg != isRead) }
                                                    rcAdapter.removeObject(item)
                                                    list.remove(item)
                                                    list.add(chat)
                                                    rcAdapter.addChatPreview(chat)
                                                } catch (e: Exception) {
                                                    try {
                                                        list.single { (it.getUser == getUser) }

                                                    } catch (e: Exception) {
                                                        list.add(chat)
                                                        rcAdapter.addChatPreview(chat)
                                                    }
                                                }
                                            }
                                        } else {
                                            list.add(chat)
                                        }
                                    }

                                }
                                count += 1
                            }
                            /* Log.d("member", member.toString())
                             Log.d("snapshot", dataSnapshot.children.last().toString())
                             Log.d("check", (dataSnapshot.children.last().toString() == member.toString()).toString())
                             */
                            if (member.toString() == dataSnapshot.children.last()
                                    .toString() && isFirstTry
                            ) {
                                list = ArrayList(list.sortedWith(compareBy { it.latestMsgTime }))
                                //Log.d("list", list.toString())

                                // list.reverse()
                                rcAdapter.clearRecords()
                                for (item in list) {
                                    rcAdapter.addChatPreview(item)
                                }
                                isFirstTry = false
                            }

                        }
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                //  Log.w("T", "loadPost:onCancelled", databaseError.toException())
            }
        }
        database = FirebaseDatabase.getInstance().getReference("chatMembers/$userName")
        database.addValueEventListener(postListener)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_chats, container, false)
    }


}