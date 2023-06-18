package com.oneseed.zachet.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.DatabaseReference
import com.oneseed.zachet.R
import com.oneseed.zachet.adapters.FeedAdapter
import com.oneseed.zachet.databinding.FragmentFeedBinding


class FeedFragment : Fragment(R.layout.fragment_feed) {
    private var rcAdapter = FeedAdapter()
    private var _binding: FragmentFeedBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentFeedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        with(binding) {
            super.onViewCreated(view, savedInstanceState)
            feedRc.adapter = rcAdapter
            val linearLayoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, true)
            linearLayoutManager.stackFromEnd = true
            feedRc.layoutManager = linearLayoutManager
            addRecordTv.setOnClickListener {
                addRecordLayoutShow()
            }
            addRecordImgbtn.setOnClickListener {
                addRecordLayoutShow()
            }
            closeNewMsgImgBtn.setOnClickListener {
                addRecordLayout.visibility = View.GONE
                addRecordBtnLayout.visibility = View.VISIBLE
                view.hideKeyboard()
            }
            layout.setOnClickListener {
                addRecordLayout.visibility = View.GONE
                addRecordBtnLayout.visibility = View.VISIBLE
                view.hideKeyboard()
            }
            publishNewMessButton.setOnClickListener {
                val text = newMessEdittext.text.toString()
                if (text.isNotBlank()) {
                    //viewmodel.sendFeed(message)
                    addRecordLayoutGone()
                    newMessEdittext.text.clear()
                    view.hideKeyboard()
                } else {
                    Toast.makeText(
                        requireContext(), "Пожалуйста, введите текст", Toast.LENGTH_SHORT
                    ).show()
                }
            }
            requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
                findNavController().navigate(R.id.gradesFragment)
            }
        }

    }

    private fun addRecordLayoutGone() {
        binding.addRecordLayout.visibility = View.GONE
        binding.addRecordBtnLayout.visibility = View.VISIBLE
    }

    private fun addRecordLayoutShow() {
        binding.addRecordLayout.visibility = View.VISIBLE
        binding.addRecordBtnLayout.visibility = View.GONE
    }

    /**Скрывает клавиатуру.*/
    private fun View.hideKeyboard() {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(windowToken, 0)
    }

    // Убирает слушатель изменений в базе данных
    override fun onStop() {
        super.onStop()
        //viewmodel.stoplistener
    }

    // Устанавливает слушатель изменений в базе данных
    override fun onResume() {
        super.onResume()
        //viewmodel.startlistener
    }

}