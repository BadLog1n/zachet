package com.oneseed.zachet.ui.grades

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.oneseed.zachet.R
import com.oneseed.zachet.databinding.FragmentGradesBinding
import com.oneseed.zachet.domain.states.BackPressedState
import com.oneseed.zachet.domain.states.StudentState
import com.oneseed.zachet.ui.grades.adapter.GradesAdapter

class GradesFragment : Fragment() {
    private var _binding: FragmentGradesBinding? = null
    private val binding get() = _binding!!
    private val viewModel: GradesFragmentViewModel by lazy {
        ViewModelProvider(this)[GradesFragmentViewModel::class.java]
    }
    private val adapter: GradesAdapter by lazy { GradesAdapter {
        Toast.makeText(
            requireContext(),
            it,
            Toast.LENGTH_SHORT
        ).show() } }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentGradesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            gradesRecyclerView.layoutManager = LinearLayoutManager(this@GradesFragment.context)
            gradesRecyclerView.adapter = adapter
            viewModel.listToObserve.observe(viewLifecycleOwner) {
                when (it) {
                    is StudentState.Success -> {
                        adapter.replaceAllGrades(it.ratingData)
                        gradesProgressBar.visibility = View.GONE
                        gradesRecyclerView.visibility = View.VISIBLE
                        swipeRefreshLayout.isEnabled = true
                        semNumSpinner.isEnabled = true
                    }

                    is StudentState.Error -> TODO()
                    StudentState.Loading -> {
                        gradesProgressBar.visibility = View.VISIBLE
                        gradesRecyclerView.visibility = View.GONE
                        swipeRefreshLayout.isEnabled = false
                    }
                }
            }
            viewModel.backPressedState.observe(viewLifecycleOwner) {
                when (it) {
                    is BackPressedState.Success -> activity?.finish()
                    is BackPressedState.Waiting -> Toast.makeText(
                        activity, getString(R.string.confirmExit), Toast.LENGTH_SHORT
                    ).show()

                    BackPressedState.Reset -> Unit
                }
            }

            val listOfSemester = viewModel.getSemesterList(requireContext())
            if (listOfSemester != null) {
                val arrayAdapter: ArrayAdapter<String> = ArrayAdapter(
                    requireContext(), android.R.layout.simple_spinner_dropdown_item, listOfSemester
                )
                arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                semNumSpinner.adapter = arrayAdapter
                semNumSpinner.visibility = View.VISIBLE
            } else textviewNeedAuth.visibility = View.VISIBLE

            requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
                viewModel.onBackPressed()
            }

            semNumSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {}
                override fun onItemSelected(
                    parent: AdapterView<*>?, view: View?, position: Int, id: Long
                ) {
                    semNumSpinner.isEnabled = false
                    val semester = semNumSpinner.selectedItem.toString().filter { it.isDigit() }
                        .toInt() + 1
                    viewModel.getGrades(requireContext(), semester)
                }
            }

            swipeRefreshLayout.setOnRefreshListener {
                val semester = semNumSpinner.selectedItem.toString().filter { it.isDigit() }
                    .toInt() + 1
                viewModel.getGrades(requireContext(), semester)
                viewModel.sendSwipeAnalytics()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}