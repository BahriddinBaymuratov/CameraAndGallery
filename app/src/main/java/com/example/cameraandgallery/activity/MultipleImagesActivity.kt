package com.example.cameraandgallery.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.GridLayoutManager
import com.example.cameraandgallery.R
import com.example.cameraandgallery.adapter.ImagesAdapter
import com.example.cameraandgallery.databinding.ActivityMultipleImagesBinding

class MultipleImagesActivity : AppCompatActivity() {
    private val binding by lazy { ActivityMultipleImagesBinding.inflate(layoutInflater) }
    private lateinit var imagesAdapter: ImagesAdapter
    private var imageUris: MutableList<Uri> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        imagesAdapter = ImagesAdapter()
        binding.recyclerView.apply {
            adapter = imagesAdapter
            layoutManager = GridLayoutManager(this@MultipleImagesActivity, 2)
            setHasFixedSize(true)
        }
        binding.btnPick.setOnClickListener {
            getMultipleImagesNewWay()
            // getMultipleImagesOldWay()
        }

    }

    private fun getMultipleImagesNewWay() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        startActivityForResult(intent, 10)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 10 && resultCode == RESULT_OK) {
            if (data?.clipData != null) {
                for (i in 0 until data.clipData?.itemCount!!) {
                    imageUris.add(data.clipData?.getItemAt(i)?.uri!!)
                }
                binding.textCount.text = imageUris.size.toString()
                imagesAdapter.imageUris = imageUris
                imagesAdapter.notifyDataSetChanged()
            } else if (data?.data != null) {
                imageUris.add(Uri.parse(data.data?.path!!))
            }
        }
    }

    private val getManyImages =
        registerForActivityResult(ActivityResultContracts.GetMultipleContents()) { results ->
            if (results != null) {
                for (uri in results) {
                    binding.textCount.text = results.size.toString()
                    imageUris.add(uri)
                }
                imagesAdapter.imageUris = imageUris
                imagesAdapter.notifyDataSetChanged()
            }
        }
}