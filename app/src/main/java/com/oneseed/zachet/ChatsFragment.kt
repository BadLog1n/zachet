package com.oneseed.zachet

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ProgressBar
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
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import java.util.concurrent.Executors

class ChatsFragment : Fragment(R.layout.fragment_chats) {
    private val authCheck = AuthCheck()
    private val chatsPackage = ChatsPackage()
    private lateinit var database: DatabaseReference
    private var clickBack = false
    private var notFirstLoad = false
    private lateinit var userName: String
    private lateinit var postListener: ValueEventListener
    var list = ArrayList<ChatPreview>()
    private var rcAdapter = ChatsAdapter()
    private lateinit var progressBar: ProgressBar


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        authCheck.check(view, this@ChatsFragment.context)
        rcAdapter.clearRecords()
        val sharedPref: SharedPreferences? = activity?.getSharedPreferences(
            "Settings", AppCompatActivity.MODE_PRIVATE
        )
        val linearLayoutManager =
            LinearLayoutManager(this@ChatsFragment.context, LinearLayoutManager.VERTICAL, true)
        linearLayoutManager.stackFromEnd = true
        userName = sharedPref?.getString(getString(R.string.uid), "").toString()
        progressBar = view.findViewById(R.id.chatProgressBar)
        val recyclerView: RecyclerView = view.findViewById(R.id.chatsRcView)
        recyclerView.layoutManager = linearLayoutManager
        rcAdapter.clearRecords()
        rcAdapter.chatsList = ArrayList()
        rcAdapter.notifyItemRangeChanged(0, rcAdapter.itemCount)
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
        addPostEventListener()
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
                    activity?.finish()
                }
            } else {
                findNavController().navigate(R.id.gradesFragment)
            }
        }
        val versionName = getAppVersion(requireContext())


        if (isTeacher == true) {
            database = FirebaseDatabase.getInstance().getReference("versionInfo")
            database.get().addOnSuccessListener {
                val actualVersion = it.child("actualVersion").value.toString()
                if (versionName < it.child("minVersion").value.toString()) {
                    val builder = AlertDialog.Builder(requireContext())
                    builder.setMessage(R.string.updateTextForced)
                    builder.setTitle(R.string.updateTitleForced)
                    builder.setNegativeButton("Выход") { _, _ ->
                        activity?.finish()
                    }
                    builder.setPositiveButton("Обновить") { _, _ ->
                        val openDownloadFile = Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("https://github.com/BadLog1n/zachet/releases/download/$actualVersion/$actualVersion.apk")
                        )
                        startActivity(openDownloadFile)
                        activity?.finish()
                    }
                    builder.setNeutralButton("RuStore") { _, _ ->
                        val openDownloadFile = Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("https://apps.rustore.ru/app/com.oneseed.zachet")
                        )
                        startActivity(openDownloadFile)
                        activity?.finish()
                    }
                    val alertDialog = builder.create()
                    alertDialog.setCancelable(false)
                    alertDialog.show()
                    alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK)
                    alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK)
                    alertDialog.getButton(AlertDialog.BUTTON_NEUTRAL).setTextColor(Color.BLACK)
                } else if (versionName < actualVersion) {
                    val builder = AlertDialog.Builder(requireContext())
                    builder.setMessage(R.string.updateText)
                    builder.setTitle(R.string.updateTitle)
                    builder.setNegativeButton("Ок") { _, _ ->
                    }
                    builder.setPositiveButton("Обновить") { _, _ ->
                        val openDownloadFile = Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("https://github.com/BadLog1n/zachet/releases/download/$actualVersion/$actualVersion.apk")
                        )
                        startActivity(openDownloadFile)
                    }
                    builder.setNeutralButton("RuStore") { _, _ ->
                        val openDownloadFile = Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("https://apps.rustore.ru/app/com.oneseed.zachet")
                        )
                        startActivity(openDownloadFile)
                    }
                    val alertDialog = builder.create()
                    alertDialog.show()
                    alertDialog.getButton(AlertDialog.BUTTON_NEUTRAL).setTextColor(Color.BLACK)

                    alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK)
                    alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK)

                }

            }
        }
    }

    private fun getAppVersion(context: Context?): String {
        var version = ""
        try {
            val pInfo = if (Build.VERSION.SDK_INT >= 33) {
                context?.packageManager?.getPackageInfo(
                    requireContext().packageName, PackageManager.PackageInfoFlags.of(0)
                )
            } else {
                @Suppress("DEPRECATION") context?.packageManager?.getPackageInfo(
                    requireContext().packageName, 0
                )
            }
            version = pInfo!!.versionName
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        return version
    }

    private fun userSearch() {
        Firebase.analytics.logEvent("user_search") {
            param("userSearch", "")
        }
        val user = view?.findViewById<EditText>(R.id.searchTxtInput)?.text.toString()
        database = FirebaseDatabase.getInstance().getReference("login")
        database.child(user).get().addOnSuccessListener { itLogin ->
            val getUser = if (itLogin.exists() && user != "") {
                itLogin.value.toString()
            } else {
                Toast.makeText(
                    activity, "Пользователя или беседы не существует", Toast.LENGTH_SHORT
                ).show()
                return@addOnSuccessListener
            }
            database = FirebaseDatabase.getInstance().getReference("users")
            database.child(getUser).get().addOnSuccessListener {
                val membersList: Array<String> =
                    if (it.child("members").exists()) it.child("members").value.toString()
                        .split(";").toTypedArray() else (arrayOf(""))
                val sharedPref: SharedPreferences? = activity?.getSharedPreferences(
                    "Settings", AppCompatActivity.MODE_PRIVATE
                )
                val userName = sharedPref?.getString(getString(R.string.uid), "").toString()
                if (membersList[0] != "" && userName !in membersList) {
                    Toast.makeText(
                        requireContext(), "У вас нет доступа к чату", Toast.LENGTH_SHORT
                    ).show()
                } else {
                    val intent =
                        Intent(this@ChatsFragment.context, IndividualChatActivity::class.java)
                    intent.putExtra(getString(R.string.getUser), getUser)
                    startActivity(intent)
                    view?.findViewById<EditText>(R.id.searchTxtInput)?.setText("")
                    val imm =
                        context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(view?.windowToken, 0)
                }
            }
        }
    }

    private fun addPostEventListener() {
        postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (member in dataSnapshot.children) {
                    val getUser = member.key.toString()
                    val isRead = member.child("isRead").value.toString().toBoolean()
                    database = FirebaseDatabase.getInstance().getReference("users/$getUser")
                    database.get().addOnSuccessListener { itData ->
                        val name =
                            if (itData.child("name").value.toString() != "null") itData.child("name").value.toString() else ""
                        val chatName =
                            if (itData.child("group").value.toString() == "true") getUser else chatsPackage.getChatName(
                                userName, getUser
                            )
                        val surname =
                            if (itData.child("surname").value.toString() != "null") itData.child("surname").value.toString() else ""
                        val displayName: String =
                            if (name.isNotEmpty() || surname.isNotEmpty()) "$name $surname" else itData.child(
                                "login"
                            ).value.toString()

                        database =
                            FirebaseDatabase.getInstance().getReference("chatMessages/$chatName")
                        val requestToDatabase = database.limitToLast(1).get()
                        requestToDatabase.addOnSuccessListener { itNew ->
                            for (i in itNew.children) {
                                val dtLong = i.key!!.toString().toLong()
                                val displayText: String = when (i.child("type").value.toString()) {
                                    "text" -> i.child("text").value.toString()
                                    "file" -> "Файл"
                                    "photo" -> "Фотография"
                                    else -> "Сообщение"
                                }

                                val chat = ChatPreview(
                                    displayName, dtLong, displayText, isRead, getUser
                                )
                                Log.d("notFirstLoad", notFirstLoad.toString())

                                if (notFirstLoad && itNew.children.count() == 1 && list.singleOrNull { (it.getUser == getUser) } != null) {
                                    list.remove(list.single { (it.getUser == getUser) })
                                }
                                list.add(chat)
                                if (member.toString() == dataSnapshot.children.last().toString()) {

                                    list =
                                        ArrayList(list.sortedWith(compareBy { it.latestMsgTime }))
                                    rcAdapter.clearRecords()
                                    list.forEach { element ->
                                        rcAdapter.addChatPreview(element)
                                    }
                                    rcAdapter.notifyItemRangeChanged(0, rcAdapter.itemCount)

                                    notFirstLoad = true
                                    progressBar.visibility = View.GONE

                                }
                            }
                        }
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_chats, container, false)
    }


    override fun onStop() {
        database = FirebaseDatabase.getInstance().getReference("chatMembers/$userName")
        database.removeEventListener(postListener)
        super.onStop()
    }

    override fun onResume() {
        database = FirebaseDatabase.getInstance().getReference("chatMembers/$userName")
        database.addValueEventListener(postListener)
        super.onResume()
    }
}