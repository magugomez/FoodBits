package com.example.foodbits

class RecipeAdapter(private val recipeList: List<Recipe>) :
    RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder>() {

    class RecipeViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val recipeName: TextView = view.findViewById(R.id.recipe_name)
        val recipeDescription: TextView = view.findViewById(R.id.recipe_description)
        val recipeImage: ImageView = view.findViewById(R.id.recipe_image)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_recipe, parent, false)
        return RecipeViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
        val recipe = recipeList[position]
        holder.recipeName.text = recipe.name
        holder.recipeDescription.text = recipe.description
        // Cargar imagen con una librería como Glide o Picasso
        Glide.with(holder.recipeImage.context).load(recipe.imageUrl).into(holder.recipeImage)
    }

    override fun getItemCount(): Int {
        return recipeList.size
    }
}
