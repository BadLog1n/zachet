package com.example.zachet



import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialogFragment



class LogoutFragment : BottomSheetDialogFragment() {

    private val checkSettings = "check_settings"
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_logout, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<Button>(R.id.bs_logoutButton).setOnClickListener {
            val sharedPref: SharedPreferences? = activity?.getSharedPreferences("Settings",
                Context.MODE_PRIVATE
            )
            sharedPref?.edit()?.putBoolean(checkSettings, false)?.apply()
            findNavController().navigate(R.id.loginFragment)
            dismiss()
        }
        view.findViewById<Button>(R.id.bs_cancelButton).setOnClickListener {
            view.findViewById<FrameLayout>(R.id.standard_bottom_sheet).apply {
                dismiss()
            }
        }
    }

    companion object {
        const val TAG = "ModalBottomSheet"

    }


}

