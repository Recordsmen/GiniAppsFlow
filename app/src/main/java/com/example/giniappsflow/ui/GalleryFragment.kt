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
import android.widget.ImageView
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
import jp.wasabeef.blurry.Blurry
import kotlinx.coroutines.launch
import org.opencv.android.Utils
import org.opencv.core.Size
import org.opencv.imgcodecs.Imgcodecs.imread
import org.opencv.imgproc.Imgproc


const val TAG = "GALLERY_FRAGMENT"

class GalleryFragment : Fragment() {

    private val viewModel: MainViewModel by activityViewModels()

    lateinit var binding:GalleryFragmentBinding

    private val galleryAdapter by lazy {
        GalleryAdapter { view,path->
            Blurry.with(context).capture(view).into(view as ImageView)
            val image = BitmapFactory.decodeFile(path)
//            val blurred = blur(path,image)
            viewModel.uploadImageToImgur(image,path)
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
        binding = GalleryFragmentBinding.inflate(layoutInflater,container,false)

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
                viewModel.images.collect {
                    galleryAdapter.update(it)
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
fun blur(path: String,bitmap:Bitmap):Bitmap{
        val tmp = imread(path)
        Utils.bitmapToMat(bitmap, tmp)
        Imgproc.cvtColor(tmp, tmp, Imgproc.COLOR_RGB2HSV_FULL)
        Imgproc.GaussianBlur(tmp, tmp, Size(3.0, 3.0), 0.0, 0.0)
        Utils.matToBitmap(tmp, bitmap)
        return bitmap
    }
}