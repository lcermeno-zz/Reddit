package com.qiubo.deviget


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.qiubo.deviget.databinding.FragmentDetailBinding

/**
 * A simple [Fragment] subclass.
 */
class DetailFragment : Fragment() {

    private lateinit var _binding: FragmentDetailBinding

    
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = DataBindingUtil.inflate(layoutInflater, R.layout.fragment_detail, container, false)
        _binding.lifecycleOwner = this
        _binding.post = arguments?.let { DetailFragmentArgs.fromBundle(it).item }
        // Inflate the layout for this fragment
        return _binding.root
    }

}
