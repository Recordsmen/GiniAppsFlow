package com.example.giniappsflow.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.giniappsflow.SendedAdapter
import com.example.giniappsflow.databinding.LinksFragmentBinding
import com.example.giniappsflow.viewModel.MainViewModel
import kotlinx.coroutines.launch


class LinksFragment : Fragment() {

    lateinit var binding: LinksFragmentBinding

    private val viewModel: MainViewModel by activityViewModels()

    private val sendedAdapter by lazy {
        SendedAdapter { view, path->
            view.setOnClickListener{
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(path)))
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = LinksFragmentBinding.inflate(layoutInflater,container,false)

        binding.recyclerViewLinks.adapter = sendedAdapter

        viewModel.getAllLinks()
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.links.collect {
                    sendedAdapter.update(it)
                }
            }
        }

        return binding.root
    }
}