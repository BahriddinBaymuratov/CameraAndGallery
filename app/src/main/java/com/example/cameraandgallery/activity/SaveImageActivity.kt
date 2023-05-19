package com.example.cameraandgallery.activity

import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import com.example.cameraandgallery.R
import com.example.cameraandgallery.database.MyDatabase
import com.example.cameraandgallery.databinding.ActivitySaveImageBinding
import com.example.cameraandgallery.model.ImageModel
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

class SaveImageActivity : AppCompatActivity() {

    private val binding by lazy { ActivitySaveImageBinding.inflate(layoutInflater) }
    private lateinit var myDatabase: MyDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        myDatabase = MyDatabase(this)
        val list = myDatabase.getAllImages()

        binding.pathImage.setImageURI(Uri.parse(list[1].imagePath))
        val bitmap = BitmapFactory.decodeByteArray(list[0].image, 0, list[0].image?.size!!)
        binding.bitmapImage.setImageBitmap(bitmap)
        binding.btnGet.setOnClickListener {
            pickImageFromNewGallery.launch("image/*")
        }
    }

    private val pickImageFromNewGallery =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri ?: return@registerForActivityResult
            val date = SimpleDateFormat("yyyy-MM-dd", Locale.UK).format(Date())
            val contentResolver = contentResolver?.openInputStream(uri)
            val file = File(filesDir, "$date.jpg")
            val fileOutputStream = FileOutputStream(file)
            contentResolver?.copyTo(fileOutputStream)
            contentResolver?.close()
            fileOutputStream.close()
            val fileInputStream = FileInputStream(file)
            val readBytes = fileInputStream.readBytes()
            myDatabase.saveImage(ImageModel(file.absolutePath, readBytes))
        }
}