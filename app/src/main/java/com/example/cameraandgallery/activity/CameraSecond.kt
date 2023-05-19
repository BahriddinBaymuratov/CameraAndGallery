package com.example.cameraandgallery.activity

import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import com.example.cameraandgallery.R
import com.example.cameraandgallery.databinding.ActivityCameraSecondBinding
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class CameraSecond : AppCompatActivity() {
    private val binding by lazy { ActivityCameraSecondBinding.inflate(layoutInflater) }
    private val PERMISSION_CODE = 1000
    private val IMAGE_CAPTURE_CODE = 1001
    var vFilename: String = ""
    private lateinit var currentImagePath: String
    private lateinit var image_uri: Uri

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.btnOldMethod.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                openCamera()
            } else {
                toast("Sorry you're version android is not support, Min Android 6.0 (Marsmallow)")
            }
        }
    }

    private fun openCamera() {
        val values = ContentValues()
        values.put(MediaStore.Images.Media.TITLE, "New Picture")
        values.put(MediaStore.Images.Media.DESCRIPTION, "From the Camera")

        //camera intent
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraIntent.resolveActivity(packageManager)

        // set filename
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        vFilename = "FOTO_$timeStamp.jpg"

        // set direcory folder
        val file = File("/sdcard/niabsen/", vFilename)

        val photoFile = createImageFile()
        photoFile.also {
            val image_uri = FileProvider.getUriForFile(
                this,
                this.applicationContext.packageName + ".provider",
                file
            )
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri)
            startActivityForResult(cameraIntent, IMAGE_CAPTURE_CODE)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        //called when user presses ALLOW or DENY from Permission Request Popup
        when (requestCode) {
            PERMISSION_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] ==
                    PackageManager.PERMISSION_GRANTED
                ) {
                    //permission from popup was granted
                    openCamera()
                } else {
                    //permission from popup was denied
                    toast("Permission denied")
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (::currentImagePath.isInitialized) {
//
//            //File object of camera image
//            val file = File("/sdcard/niabsen/", vFilename)
//            toast(file.toString())
//
//            //Uri of camera image
//            val uri = FileProvider.getUriForFile(
//                this,
//                this.applicationContext.packageName + ".provider",
//                file
//            )
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

    @Throws(IOException::class)
    private fun createImageFile(): File {
        val format = SimpleDateFormat("yyyy MMdd HHmm ss", Locale.getDefault()).format(Date())
        val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val file = File.createTempFile("JPEG_$format", ".jpg", storageDir)
        currentImagePath = file.absolutePath
        return file
    }

    private fun toast(text: String) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
    }

}