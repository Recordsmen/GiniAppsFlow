package com.example.giniappsflow.ui

import android.Manifest
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.*
import androidx.recyclerview.widget.GridLayoutManager
import com.example.giniappsflow.GalleryAdapter
import com.example.giniappsflow.viewModel.MainViewModel
import com.example.giniappsflow.R
import com.example.giniappsflow.database.local.Image
import com.example.giniappsflow.databinding.PortraitModeFragmentBinding
import jp.wasabeef.blurry.Blurry
import kotlinx.coroutines.launch

const val TAG = "GALLERY_FRAGMENT"

class GalleryFragment : Fragment() {

    private val viewModel: MainViewModel by lazy {
        ViewModelProvider(this)[MainViewModel::class.java]
    }

    lateinit var binding:PortraitModeFragmentBinding

    private val galleryAdapter by lazy {
        GalleryAdapter { view,path->
            viewModel.loadImage(path)
            Blurry.with(context).capture(view).into(view as ImageView)
            Log.i("Path",path)
        }
    }
    companion object{
        const val LANDSCAPE_SPANCOUNT = 5
        const val PORTRAIT_SPANCOUNT = 3
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setHasOptionsMenu(true)
        binding = PortraitModeFragmentBinding.inflate(layoutInflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requestReadStoragePermission()
    }

    private fun requestReadStoragePermission() {
        val readStorage = Manifest.permission.READ_EXTERNAL_STORAGE
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && ContextCompat.checkSelfPermission(
                this.requireContext(),
                readStorage
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(arrayOf(readStorage), 2)
        } else
            init(PORTRAIT_SPANCOUNT)
    }

    private fun init(spancount:Int) {
        val gridLayoutManager = GridLayoutManager(this.activity, spancount)
        binding.recyclerView.apply {
            layoutManager = gridLayoutManager
            adapter = galleryAdapter
        }
        loadPictures()
    }
    private fun loadPictures() {
        viewModel.getImagesFromGallery(requireContext())
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.imageUriList.collect { data ->
                    val listOfImages = mutableListOf<String>()
                    listOfImages.add(data)
                    galleryAdapter.update(listOfImages)
                }
            }
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            init(LANDSCAPE_SPANCOUNT)
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            init(PORTRAIT_SPANCOUNT)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }
}