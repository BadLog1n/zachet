package com.oneseed.zachet.data.feed

import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.oneseed.zachet.R
import com.oneseed.zachet.domain.models.FeedRecord
import com.oneseed.zachet.domain.repository.Repository
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class GetFeedListImpl:Repository.GetFeedList {
    override fun getFeedsList(callback: (result: ArrayList<FeedRecord>) -> Unit) {
        TODO("Not yet implemented")
    }

    private fun addPostEventListener(view: View) {
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
}