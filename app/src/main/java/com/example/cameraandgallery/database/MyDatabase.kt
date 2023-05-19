package com.example.cameraandgallery.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.cameraandgallery.model.ImageModel

class MyDatabase(context: Context) :
    SQLiteOpenHelper(context, "My_db", null, 1), DatabaseService {
    override fun onCreate(db: SQLiteDatabase?) {
        val query =
            "CREATE TABLE image_table (id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE, img_path TEXT NOT NULL, image BLOB NOT NULL)"
        db?.execSQL(query)
    }

    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {}

    override fun saveImage(imageModel: ImageModel) {
        val database = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put("img_path", imageModel.imagePath)
        contentValues.put("image", imageModel.image)
        database.insert("image_table", null, contentValues)
        database.close()
    }

    override fun getAllImages(): MutableList<ImageModel> {
        val database = this.readableDatabase
        val query = "SELECT * FROM image_table"
        val imageList = mutableListOf<ImageModel>()
        val cursor = database.rawQuery(query, null)
        if (cursor.moveToFirst()) {
            do {
                imageList.add(ImageModel(cursor.getInt(0), cursor.getString(1), cursor.getBlob(2)))
            } while (cursor.moveToNext())
        }
        return imageList
    }
}