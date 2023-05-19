package com.example.cameraandgallery.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.example.cameraandgallery.databinding.ActivityCameraBinding
import com.karumi.dexter.BuildConfig
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class CameraActivity : AppCompatActivity() {
    private val binding by lazy { ActivityCameraBinding.inflate(layoutInflater) }
    private lateinit var currentImagePath: String
    private lateinit var image_uri: Uri

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.btnOldMethod.setOnClickListener {
            cameraOld()
        }
        binding.btnNewMethod.setOnClickListener {
            image_uri = FileProvider.getUriForFile(
                this,
                BuildConfig.APPLICATION_ID,
                createImageFile()
            )
            pickImageFromCamera.launch(image_uri)
        }
        binding.btnDelete.setOnClickListener {
            clearCamera()
        }
    }

    private fun cameraOld() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.resolveActivity(packageManager)

        val photoFile = createImageFile()
        photoFile.also {
            val image_uri = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID, it)

            intent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri)
            startActivityForResult(intent, 10)
        }
    }

    private fun clearCamera() {

    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        val format = SimpleDateFormat("yyyy MMdd HHmm ss", Locale.getDefault()).format(Date())
        val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val file = File.createTempFile("JPEG_$format", ".jpg", storageDir)
        currentImagePath = file.absolutePath
        return file
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (::currentImagePath.isInitialized) {
            binding.imageView.setImageURI(Uri.fromFile(File(currentImagePath)))
        }
    }

    private val pickImageFromCamera = registerForActivityResult(ActivityResultContracts.TakePicture()) {
        if (it) {
            binding.imageView.setImageURI(image_uri)
            val contentResolver = contentResolver?.openInputStream(image_uri)
            val file = File(filesDir, "imageNew.jpg")
            val fileOutputStream = FileOutputStream(file)
            contentResolver?.copyTo(fileOutputStream)
            contentResolver?.close()
            fileOutputStream.close()
            Log.d("AbsolutePath", file.absolutePath)
        }
    }
}