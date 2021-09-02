package com.javright.bibliotek.ui.widget

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.DialogFragment
import com.javright.bibliotek.data.response.UserResponse
import com.javright.bibliotek.databinding.DialogCustomBinding

private const val USER = "USER"

class AlertDialogCustom : DialogFragment() {

    private lateinit var binding: DialogCustomBinding

    companion object {
        fun newInstance(user: UserResponse): AlertDialogCustom {
            val dialogFragment = AlertDialogCustom()
            dialogFragment.arguments = Bundle().apply { putParcelable(USER, user) }
            return dialogFragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DialogCustomBinding.inflate(inflater, container, false)
        dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.setCancelable(false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.getParcelable<UserResponse>(USER)?.let {
            initView(it)
        }
    }

    private fun initView(user: UserResponse) {
        if (user.email.isNullOrEmpty()) {
            binding.tvEmailDialog.visibility = GONE
            binding.tvContainerEmailDialog.visibility = GONE
        } else {
            binding.tvEmailDialog.text = user.email
        }
        if (user.phone.isNullOrEmpty()) {
            binding.tvPhoneDialog.visibility = GONE
            binding.tvContainerPhoneDialog.visibility = GONE
        } else {
            binding.tvPhoneDialog.text = user.phone
        }
        binding.containerAcceptAlertCustom.setOnClickListener {
            dismiss()
        }
    }
}