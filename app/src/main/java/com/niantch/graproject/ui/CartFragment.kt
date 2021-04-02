package com.niantch.graproject.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.niantch.graproject.R
import com.niantch.graproject.databinding.CartFragmentBinding

class CartFragment: Fragment(R.layout.cart_fragment) {
    lateinit var binding: CartFragmentBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = CartFragmentBinding.bind(view)
    }
}