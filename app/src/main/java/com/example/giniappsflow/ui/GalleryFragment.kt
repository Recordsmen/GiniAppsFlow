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
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.giniappsflow.GalleryAdapter
import com.example.giniappsflow.viewModel.MainViewModel
import com.example.giniappsflow.R
import com.example.giniappsflow.databinding.PortraitModeFragmentBinding
import jp.wasabeef.blurry.Blurry
import kotlinx.android.synthetic.main.grid_view_item.*
import kotlinx.android.synthetic.main.portrait_mode_fragment.*

class GalleryFragment : Fragment() {

    private val viewModel: MainViewModel by lazy {
        ViewModelProvider(this)[MainViewModel::class.java]
    }

    lateinit var binding:PortraitModeFragmentBinding

    private val pictures by lazy {
        ArrayList<String>(viewModel.getGallerySize(this.requireContext()))
    }

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
        val pageSize = 30
        val gridLayoutManager = GridLayoutManager(this.activity, spancount)
        recyclerView.apply {
            layoutManager = gridLayoutManager
            adapter = galleryAdapter
        }

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (gridLayoutManager.findLastVisibleItemPosition() == galleryAdapter.itemCount - 1) {
                    loadPictures(pageSize)
                }
            }
        })
        loadPictures(pageSize)
    }
    private fun loadPictures(pageSize: Int) {
        viewModel.getImagesFromGallery(requireContext(), pageSize) {
            if (it.isNotEmpty()) {
                pictures.addAll(it)
                galleryAdapter.update(it)
            }
        }
    }
    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)

        // Checks the orientation of the screen
        if (newConfig.orientation === Configuration.ORIENTATION_LANDSCAPE) {
            init(LANDSCAPE_SPANCOUNT)
        } else if (newConfig.orientation === Configuration.ORIENTATION_PORTRAIT) {
            init(PORTRAIT_SPANCOUNT)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }
}