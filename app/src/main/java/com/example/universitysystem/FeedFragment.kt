package com.example.universitysystem

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.universitysystem.databinding.FragmentFeedBinding
import com.example.universitysystem.databinding.FragmentGradesBinding
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class FeedFragment : Fragment(R.layout.fragment_feed) {

    private var rcAdapter = FeedAdapter()
    private lateinit var binding: FragmentFeedBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentFeedBinding.inflate(layoutInflater)
        val feedRc: RecyclerView = view.findViewById(R.id.feedRc)

        rcAdapter.clearRecords()
        rcAdapter.recordsList =  ArrayList()
        rcAdapter.notifyDataSetChanged()
        feedRc.adapter = rcAdapter
        initFeedRc()
        feedRc.adapter = rcAdapter
        val linearLayoutManager = LinearLayoutManager(this.context, LinearLayoutManager.VERTICAL, true)
        linearLayoutManager.stackFromEnd = true
        feedRc.layoutManager = linearLayoutManager
        try {
            feedRc.scrollToPosition(
                feedRc.adapter!!.itemCount - 1

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
            view.findViewById<LinearLayout>(R.id.addRecordLayout).visibility = View.GONE
            view.findViewById<LinearLayout>(R.id.addRecordBtnLayout).visibility = View.VISIBLE
            Toast.makeText(this.context,"Добавить получение имени автора", Toast.LENGTH_SHORT).show()
            val newRecText =view.findViewById<EditText>(R.id.newMessEdittext).text.toString()
            view.findViewById<EditText>(R.id.newMessEdittext).text.clear()
            val dt = SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(Date()).toString()
            rcAdapter.addFeedRecord(FeedRecord("Аникина Елена Игоревна", dt,newRecText,false))
            feedRc.adapter = rcAdapter
            val linearLayoutManager = LinearLayoutManager(this.context, LinearLayoutManager.VERTICAL, true)
            linearLayoutManager.stackFromEnd = true
            feedRc.layoutManager = linearLayoutManager
            view.hideKeyboard()
            try {
                feedRc.scrollToPosition(
                    feedRc.adapter!!.itemCount - 1

                )}
            catch (e: NullPointerException) {

            }


        }

    }

    private fun initFeedRc() {
        binding.apply {


            val sg = FeedRecord("1","12:00","smthn",true)
            rcAdapter.addFeedRecord(sg)
            val sg2 = FeedRecord("2","12:10","smthn2",false)
            rcAdapter.addFeedRecord(sg2)

        }
    }
    private fun View.hideKeyboard() {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(windowToken, 0)
    }



}