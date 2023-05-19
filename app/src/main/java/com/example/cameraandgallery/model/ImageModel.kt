package com.example.cameraandgallery.model

class ImageModel {
    var id: Int? = null
    var imagePath: String? = null
    var image: ByteArray? = null

    constructor()

    constructor(imagePath: String?, image: ByteArray?) {
        this.imagePath = imagePath
        this.image = image
    }

    constructor(id: Int?, imagePath: String?, image: ByteArray?) {
        this.id = id
        this.imagePath = imagePath
        this.image = image
    }

}