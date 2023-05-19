package com.example.cameraandgallery.database

import com.example.cameraandgallery.model.ImageModel

interface DatabaseService {

    fun saveImage(imageModel: ImageModel)
    fun getAllImages(): MutableList<ImageModel>

}