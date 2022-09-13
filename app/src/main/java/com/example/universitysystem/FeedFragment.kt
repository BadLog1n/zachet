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


class FeedFragment : Fragment(R.layout.fragment_feed) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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

}