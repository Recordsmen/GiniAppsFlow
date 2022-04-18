package com.example.giniappsflow.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.GridLayoutManager
import com.example.giniappsflow.GalleryAdapter
import com.example.giniappsflow.LinksActivity
import com.example.giniappsflow.R
import com.example.giniappsflow.databinding.GalleryFragmentBinding
import com.example.giniappsflow.viewModel.MainViewModel
import kotlinx.coroutines.launch

class GalleryFragment : Fragment() {

    private val viewModel: MainViewModel by activityViewModels()

    lateinit var binding:GalleryFragmentBinding
    var startConfiguration = 3

    private val galleryAdapter by lazy {
        GalleryAdapter { view,path->
            val image = BitmapFactory.decodeFile(path)
            viewModel.uploadImageToImgur(image,path)
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
        binding = GalleryFragmentBinding.inflate(layoutInflater,container,false)

        startConfiguration = when (resources.configuration.orientation){
            Configuration.ORIENTATION_PORTRAIT -> PORTRAIT_SPANCOUNT
            else   -> LANDSCAPE_SPANCOUNT
        }
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED -> {

            }
            shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE) -> {
        }
            else -> {
                requestPermissions(
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    2)
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            2 -> {
                if ((grantResults.isNotEmpty() &&
                            grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    init(startConfiguration)
                } else {
                }
                return
            }
            else -> {
                requestPermissions(
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    2)
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
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
        } else {
            init(startConfiguration)
        }
    }

    private fun init(spancount:Int) {
        val gridLayoutManager = GridLayoutManager(this.activity, spancount)
        binding.recyclerView.apply {
            layoutManager = gridLayoutManager
            adapter = galleryAdapter
        }
        loadPictures()
        getErrors()
    }

    private fun loadPictures() {
        viewModel.getImagesFromGallery(requireContext())
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.images.collect {
                    galleryAdapter.update(it)
                }
            }
        }
    }

    private fun getErrors() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.error.collect {
                    Toast.makeText(context,it,Toast.LENGTH_SHORT).show()
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_links -> {
                val intent = Intent(context, LinksActivity::class.java)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    //Deprecated
//fun blur(path: String,bitmap:Bitmap):Bitmap{
//        val tmp = imread(path)
//        Utils.bitmapToMat(bitmap, tmp)
//        Imgproc.GaussianBlur(tmp, tmp, Size(3.0, 3.0), 0.0)
//        Utils.matToBitmap(tmp, bitmap)
//        return bitmap
//    }
}

