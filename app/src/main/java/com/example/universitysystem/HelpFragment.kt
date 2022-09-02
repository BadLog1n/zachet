package com.example.universitysystem

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Toolbar

class HelpFragment : Fragment(R.layout.fragment_help) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar1)?.title = "Помощь"
    }
}