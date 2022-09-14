package com.example.universitysystem

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.universitysystem.databinding.FragmentFeedBinding
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import com.google.firebase.database.*


class FeedFragment : Fragment(R.layout.fragment_feed) {

    private var rcAdapter = FeedAdapter()
    private lateinit var binding: FragmentFeedBinding
    private lateinit var database: DatabaseReference
    private lateinit var author: String
    private var lastPost: Long = 0
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val sharedPref: SharedPreferences? = activity?.getSharedPreferences(
            "Settings",
            Context.MODE_PRIVATE
        )
        author = sharedPref?.getString("save_userid", "").toString()
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentFeedBinding.inflate(layoutInflater)
        val feedRc: RecyclerView = view.findViewById(R.id.feedRc)

        rcAdapter.clearRecords()
        rcAdapter.recordsList = ArrayList()
        rcAdapter.notifyDataSetChanged()
        feedRc.adapter = rcAdapter
        addPostEventListener()
        feedRc.adapter = rcAdapter
        val linearLayoutManager =
            LinearLayoutManager(this.context, LinearLayoutManager.VERTICAL, true)
        linearLayoutManager.stackFromEnd = true
        feedRc.layoutManager = linearLayoutManager
        try {
            feedRc.scrollToPosition(
                //feedRc.adapter!!.itemCount - 1
            0

            )}
        catch (e: NullPointerException) {

        }

        view.findViewById<TextView>(R.id.addRecord_tv).setOnClickListener {
            view.findViewById<LinearLayout>(R.id.addRecordLayout).visibility = View.VISIBLE
            view.findViewById<LinearLayout>(R.id.addRecordBtnLayout).visibility = View.GONE
        }
        view.findViewById<ImageButton>(R.id.addRecord_imgbtn).setOnClickListener {
            view.findViewById<LinearLayout>(R.id.addRecordLayout).visibility = View.VISIBLE
            view.findViewById<LinearLayout>(R.id.addRecordBtnLayout).visibility = View.GONE

        }
        view.findViewById<ImageButton>(R.id.closeNewMsgImgBtn).setOnClickListener {
            view.findViewById<LinearLayout>(R.id.addRecordLayout).visibility = View.GONE
            view.findViewById<LinearLayout>(R.id.addRecordBtnLayout).visibility = View.VISIBLE
            view.hideKeyboard()
        }
        view.findViewById<Button>(R.id.publishNewMessButton).setOnClickListener {
            val text = view.findViewById<EditText>(R.id.newMessEdittext).text.toString()
            sendPost(text, false)
            view.findViewById<LinearLayout>(R.id.addRecordLayout).visibility = View.GONE
            view.findViewById<LinearLayout>(R.id.addRecordBtnLayout).visibility = View.VISIBLE
            //Toast.makeText(this.context,"Добавить получение имени автора", Toast.LENGTH_SHORT).show()
            //val newRecText =view.findViewById<EditText>(R.id.newMessEdittext).text.toString()
            view.findViewById<EditText>(R.id.newMessEdittext).text.clear()
            /*val dt = SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(Date()).toString()
            rcAdapter.addFeedRecord(FeedRecord("Аникина Елена Игоревна", dt,newRecText,false))
            feedRc.adapter = rcAdapter
            val linearLayoutManager = LinearLayoutManager(this.context, LinearLayoutManager.VERTICAL, true)
            linearLayoutManager.stackFromEnd = true
            feedRc.layoutManager = linearLayoutManager
            */
            view.hideKeyboard()
            try {
                feedRc.scrollToPosition(
                    feedRc.adapter!!.itemCount

                )
            } catch (e: NullPointerException) {

            }

        }

    }


    private fun addPostEventListener() {
        val postListener = object : ValueEventListener {
            @SuppressLint("SimpleDateFormat")
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (item in dataSnapshot.children) {
                    val postId = item.key.toString()
                    if (lastPost < postId.toLong()) {
                        val postAuthor = dataSnapshot.child(postId).child("author").value.toString()

                        database = FirebaseDatabase.getInstance().getReference("users/$postAuthor")
                        val requestToDatabase = database.get()
                        requestToDatabase.addOnSuccessListener {
                            val name =
                                if (it.child("name").value.toString() != "null") it.child("name").value.toString() else ""
                            val surname =
                                if (it.child("surname").value.toString() != "null") it.child("surname").value.toString() else ""
                            val displayName = "$name $surname"

                            val dateTime =
                                SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(Date(postId.toLong()))
                                    .toString()
                            val text = dataSnapshot.child(postId).child("text").value.toString()
                            val sponsored =
                                dataSnapshot.child(postId).child("sponsored").value.toString()
                                    .toBoolean()
                            rcAdapter.addFeedRecord(
                                FeedRecord(
                                    "$displayName ($postAuthor)",
                                    dateTime,
                                    text,
                                    sponsored
                                )
                            )
                            lastPost = postId.toLong()
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
        sponsored: Boolean
    ) {
        database = FirebaseDatabase.getInstance().getReference("feed")
        val message = mapOf(
            "text" to text,
            "sponsored" to sponsored,
            "author" to author,
        )
        val currentTimestamp = System.currentTimeMillis().toString()
        database.child(currentTimestamp).updateChildren(message)

    }

}