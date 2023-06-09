package com.oneseed.zachet.ui

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import authCheck.AuthCheck
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.oneseed.zachet.R
import com.oneseed.zachet.adapters.FeedAdapter
import com.oneseed.zachet.dataClasses.FeedRecord
import java.text.SimpleDateFormat
import java.util.*


class FeedFragment : Fragment(R.layout.fragment_feed) {
    private val authCheck = AuthCheck()
    private var rcAdapter = FeedAdapter()
    private lateinit var database: DatabaseReference

    /**Значение последней отображенной записи*/
    private var lastPost: Long = 0
    private lateinit var progressBar: ProgressBar
    private lateinit var addRecordLayout: LinearLayout
    private lateinit var addRecordBtnLayout: LinearLayout
    private lateinit var postListener: ValueEventListener


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val feedRc: RecyclerView = view.findViewById(R.id.feedRc)
        authCheck.check(view, this@FeedFragment.context)
        progressBar = view.findViewById(R.id.feedProgressBar)
        rcAdapter.clearRecords()
        rcAdapter.recordsList = ArrayList()
        rcAdapter.notifyItemRangeChanged(0, rcAdapter.itemCount)
        feedRc.adapter = rcAdapter
        addPostEventListener(view)
        feedRc.adapter = rcAdapter
        val linearLayoutManager =
            LinearLayoutManager(this.context, LinearLayoutManager.VERTICAL, true)
        linearLayoutManager.stackFromEnd = true
        feedRc.layoutManager = linearLayoutManager
        addRecordLayout = view.findViewById(R.id.addRecordLayout)
        addRecordBtnLayout = view.findViewById(R.id.addRecordBtnLayout)
        addRecordBtnLayout.visibility = View.GONE

        fun addRecordLayoutGone() {
            addRecordLayout.visibility = View.GONE
            addRecordBtnLayout.visibility = View.VISIBLE
        }

        fun addRecordLayoutShow() {
            addRecordLayout.visibility = View.VISIBLE
            addRecordBtnLayout.visibility = View.GONE
        }

        view.findViewById<TextView>(R.id.addRecord_tv).setOnClickListener {
            addRecordLayoutShow()
        }
        view.findViewById<ImageButton>(R.id.addRecord_imgbtn).setOnClickListener {
            addRecordLayoutShow()
        }
        view.findViewById<ImageButton>(R.id.closeNewMsgImgBtn).setOnClickListener {
            addRecordLayout.visibility = View.GONE
            addRecordBtnLayout.visibility = View.VISIBLE
            view.hideKeyboard()
        }

        view.findViewById<LinearLayout>(R.id.layout).setOnClickListener {
            addRecordLayout.visibility = View.GONE
            addRecordBtnLayout.visibility = View.VISIBLE
            view.hideKeyboard()
        }

        view.findViewById<Button>(R.id.publishNewMessButton).setOnClickListener {

            val text = view.findViewById<EditText>(R.id.newMessEdittext).text.toString()
            if (text.isNotBlank()) {
                sendPost(text)
                addRecordLayoutGone()
                view.findViewById<EditText>(R.id.newMessEdittext).text.clear()
                view.hideKeyboard()
            } else {
                Toast.makeText(this.context, "Пожалуйста, введите текст", Toast.LENGTH_SHORT).show()
            }


        }


        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            findNavController().navigate(R.id.gradesFragment)
        }


    }

    /**Проверка на первую загрузку данных с базы данных.*/
    var isFirstLoad = true

    /**Слушатель изменений в базе данных с дальнейшим отображением их*/
    private fun addPostEventListener(view: View) {
        postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (!dataSnapshot.exists()) {
                    progressBar.visibility = View.GONE
                    addRecordLayout.visibility = View.VISIBLE
                    addRecordBtnLayout.visibility = View.GONE
                    return
                }
                for (item in dataSnapshot.children) {
                    val postId = item.key.toString()
                    if (lastPost < postId.toLong()) {
                        val postAuthor = item.child("author").value.toString()

                        database = FirebaseDatabase.getInstance().getReference("users/$postAuthor")
                        val requestToDatabase = database.get()
                        requestToDatabase.addOnSuccessListener { itName ->
                            val name =
                                if (itName.child("name").value.toString() != "null") itName.child("name").value.toString() else ""
                            val surname =
                                if (itName.child("surname").value.toString() != "null") itName.child(
                                    "surname"
                                ).value.toString() else ""
                            val displayName = "$name $surname"
                            val displayLogin = itName.child("login").value.toString()

                            val dateTime =
                                SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault()).format(
                                    Date(postId.toLong())
                                ).toString()
                            val text = item.child("text").value.toString()
                            val sponsored = item.child("sponsored").value.toString().toBoolean()
                            val user = Firebase.auth.currentUser

                            val uid = user?.uid
                            rcAdapter.addFeedRecord(
                                FeedRecord(
                                    displayName,
                                    postAuthor,
                                    dateTime,
                                    text,
                                    sponsored,
                                    item.key.toString(),
                                    uid.toString(),
                                    displayLogin
                                )
                            )
                            lastPost = postId.toLong()
                            val feedRc: RecyclerView = view.findViewById(R.id.feedRc)
                            if (item.toString() == dataSnapshot.children.last().toString()) {
                                if (isFirstLoad) {
                                    rcAdapter.notifyItemRangeChanged(0, rcAdapter.itemCount)
                                    isFirstLoad = false
                                } else {
                                    rcAdapter.notifyItemChanged(rcAdapter.itemCount)
                                    if ((feedRc.layoutManager as LinearLayoutManager).findLastVisibleItemPosition() + 2 == rcAdapter.itemCount && rcAdapter.itemCount > 0) {
                                        feedRc.smoothScrollToPosition(rcAdapter.itemCount)
                                    }
                                }
                                Firebase.analytics.logEvent("feed_upload") {
                                    param("feed_upload", "")
                                }
                                addRecordLayout.visibility = View.GONE
                                addRecordBtnLayout.visibility = View.VISIBLE
                                progressBar.visibility = View.GONE
                            }
/*                            if ((feedRc.layoutManager as LinearLayoutManager).findLastVisibleItemPosition() > 0){


                            }*/
                        }
                    }

                }
            }


            override fun onCancelled(databaseError: DatabaseError) {
                Log.w("T", "loadPost:onCancelled") //databaseError.toException())
            }

        }
    }

    /**Скрывает клавиатуру.*/
    private fun View.hideKeyboard() {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(windowToken, 0)
    }


    /**Сохраняет новую запись в базе данных.
     *@param text текст записи.
     * */
    private fun sendPost(
        text: String,
    ) {
        val user = Firebase.auth.currentUser

        val uid = user?.uid
        database = FirebaseDatabase.getInstance().getReference("feed")
        val message = mapOf(
            "text" to text,
            "sponsored" to false,
            "author" to uid.toString(),
        )
        val currentTimestamp = System.currentTimeMillis().toString()
        database.child(currentTimestamp).updateChildren(message)
        rcAdapter.notifyItemRangeChanged(0, rcAdapter.itemCount)

    }

    // Убирает слушатель изменений в базе данных

    override fun onStop() {
        database = FirebaseDatabase.getInstance().getReference("feed")
        database.removeEventListener(postListener)
        super.onStop()
    }

    // Устанавливает слушатель изменений в базе данных
    override fun onResume() {
        database = FirebaseDatabase.getInstance().getReference("feed")
        database.addValueEventListener(postListener)
        super.onResume()
    }

}