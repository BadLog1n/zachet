package com.example.universitysystem


import android.R.attr.topOffset
import android.app.Dialog
import android.content.Context
import android.graphics.Point
import android.os.Bundle
import android.view.*
import android.widget.FrameLayout
import androidx.annotation.NonNull
import androidx.appcompat.app.AlertDialog
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


//import com.google.android.material.R.id.design_bottom_sheet

private const val COLLAPSED_HEIGHT = 228

class LogoutFragment : BottomSheetDialogFragment() {

    //override fun getTheme() = R.style.AppBottomSheetDialogTheme

    /*override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_logout, container, false)
    }

    @NonNull
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        if (context==null){
            return super.onCreateDialog(savedInstanceState)
        }
        val dialog:Dialog = BottomSheetDialog(requireContext(),R.style.AppBottomSheetDialogTheme)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        return dialog
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)
    }
    override fun onStart() {
        super.onStart()
        val dial = requireDialog()

        val dialog = dial as BottomSheetDialog?
        val bottomSheet =
            dialog!!.delegate.findViewById<FrameLayout>(com.google.android.material.R.id.design_bottom_sheet)
        if (bottomSheet != null) {
            val layoutParams = bottomSheet.layoutParams as CoordinatorLayout.LayoutParams
            layoutParams.height = getHeight()
            val behavior = BottomSheetBehavior.from(bottomSheet)
            // Изначально расширен
            behavior.setState(BottomSheetBehavior.STATE_COLLAPSED)
        }
        /*val density = requireContext().resources.displayMetrics.density
        if (dialog!=null){
            dialog?.let {
                val bottomSheet = it.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet) as FrameLayout
                //val viewBS
                val behavior = BottomSheetBehavior.from(bottomSheet)
                behavior.peekHeight = (COLLAPSED_HEIGHT * density).toInt()
                behavior.state=BottomSheetBehavior.STATE_COLLAPSED
            }
        }*/
        */

    override fun onCreateDialog(savedInstanceState: Bundle?): BottomSheetDialog {
        var dialog=
    AlertDialog.Builder(requireContext())
            .setMessage(getString(R.string.to_logout))
            .setPositiveButton(getString(R.string.ok)) { _,_ -> }

            .create()
    val dialog2 = dialog as BottomSheetDialog
        //BottomSheetDialog.Builder
        //Dialog =BottomSheetDialog(requireContext()).setTitle(getString(R.string.to_logout))

    }

    /*private fun getHeight(): Int {
        var height = 1920
        if (context != null) {
            val wm = requireContext().getSystemService(Context.WINDOW_SERVICE) as WindowManager
            val point = Point()
            if (wm != null) {
                // Используем Point, чтобы вычесть высоту строки состояния
                wm.defaultDisplay.getSize(point)
                height = point.y - getTopOffset()
            }
        }
        return height
    }
    fun getTopOffset(): Int {
        return topOffset
    }
    */

    companion object {
        const val TAG = "PurchaseConfirmationDialog"
    }
}

