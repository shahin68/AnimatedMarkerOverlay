package com.marker.overlay.ui.fragments

import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding

abstract class BaseFragment<Binding : ViewBinding>(@LayoutRes contentLayoutId: Int) : Fragment(contentLayoutId) {
    protected var _binding : Binding? = null
    protected  val binding : Binding get() = _binding!!

    override fun onDetach() {
        super.onDetach()
        _binding = null
    }

}