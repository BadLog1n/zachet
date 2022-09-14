package com.example.universitysystem

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
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
        val linearLayoutManager = LinearLayoutManager(this.context, LinearLayoutManager.VERTICAL, true)
        linearLayoutManager.stackFromEnd = true
        feedRc.layoutManager = linearLayoutManager

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
            Toast.makeText(this.context,"Добавить действие добавление новой записи", Toast.LENGTH_SHORT).show()
            val newRecText =view.findViewById<EditText>(R.id.newMessEdittext).text.toString()
            view.findViewById<EditText>(R.id.newMessEdittext).text.clear()
            rcAdapter.addFeedRecord(FeedRecord("Аникина Елена Игоревна", "сегодня 12:10",newRecText,false))
            try {
                feedRc.scrollToPosition(
                    feedRc.adapter!!.itemCount - 1

                )}
            catch (e: NullPointerException) {

            }
            /*view.findViewById<RecyclerView>(R.id.feedRc).addOnLayoutChangeListener { _, _, _, _, bottom, _, _, _, oldBottom ->
                if (bottom < oldBottom) {
                    feedRc.post {
                        try {
                            feedRc.scrollToPosition(
                                feedRc.adapter!!.itemCount - 1

                            )}
                        catch (e: NullPointerException) {

                        }
                    }
                }
            }*/

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

}