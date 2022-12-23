package com.oneseed.zachet

import android.annotation.SuppressLint
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
import com.oneseed.zachet.databinding.FragmentFeedBinding
import java.text.SimpleDateFormat
import java.util.*


class FeedFragment : Fragment(R.layout.fragment_feed) {
    private val authCheck = AuthCheck()

    private var rcAdapter = FeedAdapter()
    private lateinit var binding: FragmentFeedBinding
    private lateinit var database: DatabaseReference
    private var lastPost: Long = 0

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentFeedBinding.inflate(layoutInflater)
        val feedRc: RecyclerView = view.findViewById(R.id.feedRc)
        authCheck.check(view, this@FeedFragment.context)

        rcAdapter.clearRecords()
        rcAdapter.recordsList = ArrayList()
        rcAdapter.notifyDataSetChanged()
        feedRc.adapter = rcAdapter
        addPostEventListener(view)
        feedRc.adapter = rcAdapter
        val linearLayoutManager =
            LinearLayoutManager(this.context, LinearLayoutManager.VERTICAL, true)
        linearLayoutManager.stackFromEnd = true
        feedRc.layoutManager = linearLayoutManager
        /*try {
            feedRc.scrollToPosition(
                //feedRc.adapter!!.itemCount - 1
            0

            )}
        catch (e: NullPointerException) {

        }*/
        fun addRecordLayoutGone() {
            view.findViewById<LinearLayout>(R.id.addRecordLayout).visibility = View.VISIBLE
            view.findViewById<LinearLayout>(R.id.addRecordBtnLayout).visibility = View.GONE
        }
        view.findViewById<TextView>(R.id.addRecord_tv).setOnClickListener {
            addRecordLayoutGone()
        }
        view.findViewById<ImageButton>(R.id.addRecord_imgbtn).setOnClickListener {
            addRecordLayoutGone()
        }
        view.findViewById<ImageButton>(R.id.closeNewMsgImgBtn).setOnClickListener {
            view.findViewById<LinearLayout>(R.id.addRecordLayout).visibility = View.GONE
            view.findViewById<LinearLayout>(R.id.addRecordBtnLayout).visibility = View.VISIBLE
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


    var isFirstLoad = true
    private fun addPostEventListener(view: View) {
        val postListener = object : ValueEventListener {
            @SuppressLint("SimpleDateFormat")
            override fun onDataChange(dataSnapshot: DataSnapshot) {
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
                                SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(Date(postId.toLong()))
                                    .toString()
                            val text = item.child("text").value.toString()
                            val sponsored =
                                item.child("sponsored").value.toString()
                                    .toBoolean()
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


                            if (isFirstLoad && item.toString() == dataSnapshot.children.last()
                                    .toString()
                            ) {
                                val feedRc: RecyclerView = view.findViewById(R.id.feedRc)
                                feedRc.adapter = rcAdapter
                                feedRc.scrollToPosition(rcAdapter.itemCount - 1)
                                isFirstLoad = false
                                Firebase.analytics.logEvent("feed_upload") {
                                    param("feed_upload", "")
                                }
                            }


                        }
                    }

                }
            }


            override fun onCancelled(databaseError: DatabaseError) {
                Log.w("T", "loadPost:onCancelled", databaseError.toException())
            }

        }
        database = FirebaseDatabase.getInstance().getReference("feed")
        database.addValueEventListener(postListener)
    }

    private fun View.hideKeyboard() {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(windowToken, 0)
    }


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

    }

}