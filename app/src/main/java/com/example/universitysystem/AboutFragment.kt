package com.example.universitysystem

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Toolbar
import authCheck.AuthCheck
import chatsPackage.ChatsPackage

class HelpFragment : Fragment(R.layout.fragment_help) {
    private val authCheck = AuthCheck()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        authCheck.check(view, this@HelpFragment.context)

        activity?.findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar1)?.title = "О приложении"
    }
}