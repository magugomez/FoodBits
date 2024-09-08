package com.example.foodbits

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import androidx.activity.ComponentActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.util.UUID

class CreateRecipe : ComponentActivity() {
    private val PICK_IMAGE_REQUEST = 1
    private var selectedImageBitmap: Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_recipe)

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE), PICK_IMAGE_REQUEST)
        }

        findViewById<Button>(R.id.upload_image).setOnClickListener {
            openGallery()
        }

        findViewById<Button>(R.id.submit_recipe).setOnClickListener {
            if (selectedImageBitmap != null) {
                uploadImageToFirebaseStorage(selectedImageBitmap!!)
            } else {
                saveRecipe(null)
            }
        }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.data != null) {
            val imageUri = data.data
            try {
                selectedImageBitmap = MediaStore.Images.Media.getBitmap(contentResolver, imageUri)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun uploadImageToFirebaseStorage(bitmap: Bitmap) {
        val storage = FirebaseStorage.getInstance().reference
        val imageRef = storage.child("recipes/${UUID.randomUUID()}.jpg")

        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()

        val uploadTask = imageRef.putBytes(data)
        uploadTask.addOnSuccessListener {
            imageRef.downloadUrl.addOnSuccessListener { uri ->
                val imageUrl = uri.toString()
                saveRecipe(imageUrl)
            }.addOnFailureListener { exception ->
                Log.e("CreateRecipe", "Error getting download URL", exception)
            }
        }.addOnFailureListener { exception ->
            Log.e("CreateRecipe", "Error uploading image", exception)
        }
    }

    private fun saveRecipe(imageUrl: String?) {
        val db = FirebaseFirestore.getInstance()
        val recipe = Recipe(
            name = "Recipe Name",
            description = "Recipe Description",
            ingredients = listOf("Ingredient 1", "Ingredient 2"),
            steps = listOf("Step 1", "Step 2"),
            imageUrl = imageUrl ?: "", // Usa una cadena vacía si no hay imagen
            userId = "userId"
        )

        db.collection("recipes")
            .add(recipe)
            .addOnSuccessListener {
                // La receta se guardó correctamente
                Log.d("CreateRecipe", "Recipe successfully added")
            }
            .addOnFailureListener { exception ->
                // Maneja el error
                Log.e("CreateRecipe", "Error adding recipe", exception)
            }
    }
}
