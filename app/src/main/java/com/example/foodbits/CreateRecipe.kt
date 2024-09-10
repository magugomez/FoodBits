package com.example.foodbits

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.util.UUID

class CreateRecipe : ComponentActivity() {
    private val PICK_IMAGE_REQUEST = 1
    private var selectedImageBitmap: Bitmap? = null

    private lateinit var ingredientsRecyclerView: RecyclerView
    private lateinit var stepsRecyclerView: RecyclerView
    private lateinit var ingredientsAdapter: IngredientsAdapter
    private lateinit var stepsAdapter: StepsAdapter

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_recipe)

        ingredientsRecyclerView = findViewById(R.id.ingredients_list)
        stepsRecyclerView = findViewById(R.id.steps_list)

        ingredientsAdapter = IngredientsAdapter()
        stepsAdapter = StepsAdapter()

        ingredientsRecyclerView.layoutManager = LinearLayoutManager(this)
        stepsRecyclerView.layoutManager = LinearLayoutManager(this)

        ingredientsRecyclerView.adapter = ingredientsAdapter
        stepsRecyclerView.adapter = stepsAdapter

        findViewById<Button>(R.id.add_ingredient).setOnClickListener {
            addIngredient()
        }

        findViewById<Button>(R.id.add_step).setOnClickListener {
            addStep()
        }

        findViewById<Button>(R.id.upload_image).setOnClickListener {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    PICK_IMAGE_REQUEST
                )
            } else {
                openGallery()
            }
        }

        findViewById<Button>(R.id.submit_recipe).setOnClickListener {
            if (selectedImageBitmap != null) {
                uploadImageToFirebaseStorage(selectedImageBitmap!!)
            } else {
                saveRecipe(null)
            }
        }

        findViewById<ImageButton>(R.id.back_button).setOnClickListener {
            finish()
        }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        galleryLauncher.launch(intent)
    }

    private val galleryLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK && result.data != null) {
            val imageUri = result.data?.data
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
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: "unknown_user"

        val recipe = Recipe(
            name = findViewById<EditText>(R.id.recipe_name).text.toString(),
            description = findViewById<EditText>(R.id.recipe_description).text.toString(),
            ingredients = ingredientsAdapter.getIngredients(),
            steps = stepsAdapter.getSteps(),
            imageUrl = imageUrl ?: "",
            userId = userId
        )

        db.collection("recipes")
            .add(recipe)
            .addOnSuccessListener {
                Log.d("CreateRecipe", "Recipe successfully added")
            }
            .addOnFailureListener { exception ->
                Log.e("CreateRecipe", "Error adding recipe", exception)
            }
    }

    private fun addIngredient() {
        val ingredientInput = EditText(this)
        AlertDialog.Builder(this)
            .setTitle("Añadir Ingrediente")
            .setView(ingredientInput)
            .setPositiveButton("Añadir") { _, _ ->
                val ingredient = ingredientInput.text.toString()
                if (ingredient.isNotEmpty()) {
                    ingredientsAdapter.addIngredient(ingredient)
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun addStep() {
        val stepInput = EditText(this)
        AlertDialog.Builder(this)
            .setTitle("Añadir Paso")
            .setView(stepInput)
            .setPositiveButton("Añadir") { _, _ ->
                val step = stepInput.text.toString()
                if (step.isNotEmpty()) {
                    stepsAdapter.addStep(step)
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }
}
