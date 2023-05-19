package com.example.cameraandgallery.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.cameraandgallery.databinding.ActivityGallaryBinding
import java.io.File
import java.io.FileOutputStream

class GalleryActivity : AppCompatActivity() {
    private val binding by lazy { ActivityGallaryBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.btnOldMethod.setOnClickListener {
            pickImageFromOldGallery()
        }
        binding.btnNewMethod.setOnClickListener {
            pickImageFromNewGallery.launch("image/*")
        }
        binding.btnDelete.setOnClickListener {
            clearImages()
        }
        binding.btnAll.setOnClickListener {
            startActivity(Intent(this, MultipleImagesActivity::class.java))
        }

    }

    private fun clearImages() {
        if (filesDir.isDirectory) {
            val listFiles = filesDir.listFiles() ?: emptyArray()
            for (list in listFiles) {
                Log.d("AbsolutePath", list.absolutePath)
                binding.imageView.setImageURI(null)
                list.delete()
            }
        }
    }

    private val pickImageFromNewGallery =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri ?: return@registerForActivityResult
            binding.imageView.setImageURI(uri)
            val contentResolver = contentResolver?.openInputStream(uri)
            val file = File(filesDir, "imageNew.jpg")
            val fileOutputStream = FileOutputStream(file)
            contentResolver?.copyTo(fileOutputStream)
            contentResolver?.close()
            fileOutputStream.close()
            Log.d("AbsolutePath", file.absolutePath)
        }

    private fun pickImageFromOldGallery() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "image/*"
        startActivityForResult(intent, 100)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100 && resultCode == RESULT_OK) {
            val uri: Uri = data?.data ?: return
            binding.imageView.setImageURI(uri)
            val contentRes = contentResolver.openInputStream(uri)
            val file = File(filesDir, "image.jpg")
            val fileOutputStream = FileOutputStream(file)
            contentRes?.close()
            fileOutputStream.close()
            val absolutePath = file.absolutePath
            Log.d("AbsolutePath", absolutePath)
        }
    }
}