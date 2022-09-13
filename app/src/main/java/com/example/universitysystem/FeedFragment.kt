package com.example.universitysystem

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.universitysystem.databinding.FragmentFeedBinding
import com.example.universitysystem.databinding.FragmentGradesBinding
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder


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
        feedRc.layoutManager = LinearLayoutManager(this.context)

        view.findViewById<TextView>(R.id.addRecord_tv).setOnClickListener {
            view.findViewById<LinearLayout>(R.id.addRecordLayout).visibility = View.VISIBLE
            view.findViewById<LinearLayout>(R.id.addRecordBtnLayout).visibility = View.GONE
        }
        view.findViewById<ImageButton>(R.id.addRecord_imgbtn).setOnClickListener {
            view.findViewById<LinearLayout>(R.id.addRecordLayout).visibility = View.VISIBLE
            view.findViewById<LinearLayout>(R.id.addRecordBtnLayout).visibility = View.GONE
        }
        view.findViewById<Button>(R.id.publishNewMessButton).setOnClickListener {
            view.findViewById<LinearLayout>(R.id.addRecordLayout).visibility = View.GONE
            view.findViewById<LinearLayout>(R.id.addRecordBtnLayout).visibility = View.VISIBLE
        }


    }

    private fun initFeedRc() {
        binding.apply {


            val sg = FeedRecord("1","12:00","smthn",true,false)
            rcAdapter.addFeedRecord(sg)
            val sg2 = FeedRecord("2","12:10","smthn2",false,true)
            rcAdapter.addFeedRecord(sg2)

        }
    }

}