package com.oneseed.zachet.data.feed

import android.util.Log
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.oneseed.zachet.domain.models.FeedRecord
import com.oneseed.zachet.domain.repository.Repository
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class GetFeedListImpl : Repository.GetFeedList {
    private lateinit var database: DatabaseReference

    override fun getFeedsList(count: Long): ArrayList<FeedRecord> {
        val resultList: ArrayList<FeedRecord> = ArrayList()

        database = FirebaseDatabase.getInstance().getReference("posts")
        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val feedRecords = getFeedRecords(dataSnapshot).take(count.toInt())
                resultList.addAll(feedRecords)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.w("T", "loadPost:onCancelled") //databaseError.toException()
            }
        })

        return resultList
    }

    private fun getFeedRecords(dataSnapshot: DataSnapshot): List<FeedRecord> {
        val feedRecords: MutableList<FeedRecord> = mutableListOf()

        for (item in dataSnapshot.children) {
            val postId = item.key.toString()
            val postAuthor = item.child("author").value.toString()
            // Получение остальных необходимых данных из item и создание объекта FeedRecord
            val name = item.child("name").value?.toString() ?: ""
            val surname = item.child("surname").value?.toString() ?: ""
            val displayName = "$name $surname"
            val displayLogin = item.child("login").value?.toString() ?: ""
            val dateTime = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
                .format(Date(postId.toLong())).toString()
            val text = item.child("text").value?.toString() ?: ""
            val sponsored = item.child("sponsored").value?.toString()?.toBoolean() ?: false
            val user = Firebase.auth.currentUser
            val uid = user?.uid

            val feedRecord = FeedRecord(
                displayName,
                postAuthor,
                dateTime,
                text,
                sponsored,
                item.key.toString(),
                uid.toString(),
                displayLogin
            )
            feedRecords.add(feedRecord)
        }

        return feedRecords
    }

   /* private fun addPostEventListener(view: View) {
        postListener = object : ValueEventListener {
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
                            *//*if ((feedRc.layoutManager as LinearLayoutManager).findLastVisibleItemPosition() > 0){}*//*
                        }
                    }
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
                Log.w("T", "loadPost:onCancelled") //databaseError.toException())
            }
        }
    }*/
}










