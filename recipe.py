import random

recipe_names = [
    "Spaghetti Carbonara", "Veggie Quesadilla", "Avocado Toast", "Eggplant Parmesan", "Beef Ramen", "Meatball Sub", 
    "Chili Con Carne", "Chicken Parmesan", "Shakshuka", "Pumpkin Soup", "Minestrone Soup", "Bulgur Pilaf", "Tempura", 
    "Miso Soup", "Roast Turkey", "Chicken Tikka Masala", "Steak au Poivre", "Vegetable Stir Fry", "Pad Thai", "Macaroni and Cheese", 
    "Lasagna", "Shepherd’s Pie", "Veggie Burger", "Stuffed Mushrooms", "Gazpacho", "Quiche Lorraine", "Shrimp Tacos", "Tuna Casserole", 
    "Chicken Alfredo", "Lamb Gyro", "Crab Cakes", "Pho", "Cornbread", "Sweet and Sour Pork", "Fish and Chips", "Ham and Cheese Omelette",
     "French Onion Soup", "Tofu Curry", "Cucumber Sandwiches", "Spinach Artichoke Dip", "Seafood Paella", "Ratatouille", "Shrimp Scampi", 
     "Broccoli Cheddar Soup", "Beef Tacos", "Korean Bibimbap", "Falafel Wrap", "Greek Salad", "Creme Brulee", "Mango Sticky Rice", 
     "Veggie Burger", "Fish and Chips", "Samosas", "Beef Bulgogi", "Burrito Bowl", "Chow Mein", "Caesar Salad", "Chili Con Carne",
      "Ratatouille", "Meatball Sub", "Minestrone Soup", "Sloppy Joes", "Spaghetti Carbonara", "Quiche Lorraine", "Bruschetta", 
      "Chicken Curry", "Clam Chowder", "Shakshuka", "Sloppy Joes", "Vegetable Stir Fry", "Chicken Satay", "Cobb Salad",  
       "Grilled Cheese Sandwich", "Mushroom Risotto", "BBQ Ribs", "Lemon Garlic Salmon", "Gnocchi", "Pho", "Sushi Rolls",
       "Okonomiyaki", "Egg Fried Rice", "Baked Ziti", "Margherita Pizza", "Roasted Brussels Sprouts", "Teriyaki Chicken", 
       "Pork Dumpling", "Oxtail Stew", "Lamb Gyro", "Tom Yum Soup", "French Onion Soup", "Chicken Pot Pie", "Chicken Fajitas",
       "Huevos Rancheros", "Chicken Enchiladas", "General Tso’s Chicken", "Shrimp Scampi"
]

descriptions = [
    "Mix all ingredients and simmer until tender.", "Grill until golden and serve hot.",
    "Blend spices and marinate overnight before cooking.", "Stir-fry over high heat.",
    "Bake until bubbly and golden brown.", "Serve with rice or naan.",
    "Top with herbs and a drizzle of olive oil.", "Garnish with sesame seeds and serve.",
    "Prepare all ingredients. Heat a pan over medium. Cook in stages as required. Season to taste and serve.",
    "Cook protein with garlic and herbs. Boil grains separately. Assemble with fresh vegetables. Serve with dressing.",
    "Chop and prep all vegetables. Stir-fry or roast depending on dish. Add spices. Combine and serve.",
    "Boil water. Cook pasta or rice. Sauté vegetables or meat. Mix all together with sauce. Garnish and serve.",
    "Soak grains or legumes if needed. Cook thoroughly. Prepare accompaniments. Mix and garnish with herbs.",
    "Cook protein with garlic and herbs. Boil grains separately. Assemble with fresh vegetables. Serve with dressing.",
    "Mix ingredients in a bowl. Transfer to a baking dish. Bake at specified temperature. Cool and serve.",
    "Preheat the oven. Prepare and combine ingredients. Cook as required. Let rest before serving.",
    "Season the main ingredient with salt and pepper. Heat oil in a pan. Cook until golden brown. Add sauce and simmer. Serve with sides",
    "Beat eggs or mix batter. Pour onto hot pan. Flip or bake as needed. Add fillings if required. Serve warm.",
    "Marinate meat or tofu. Stir-fry vegetables. Add sauce and protein. Cook until done. Serve hot.",
    "Prepare all ingredients. Heat a pan over medium. Cook in stages as required. Season to taste and serve.",
    "Prepare the ingredients. Preheat the oven if needed. Mix according to recipe instructions. Bake, chill, or freeze as required. Add toppings and serve."
]

cooking_levels = ["Easy", "Intermediate", "Advanced"]

def generate_recipe(id):
    name = random.choice(recipe_names)
    description = random.choice(descriptions)
    user_id = random.randint(1, 11)
    prep_time = random.randint(5, 30)
    cook_time = random.randint(0, 60)
    servings = random.randint(1, 6)
    category_id = random.randint(1, 4)
    diet_id = random.randint(1, 9)
    level = random.choice(cooking_levels)
    cuisine_id = random.randint(1, 14)

    return f"('{name}', '{description}', {user_id}, {prep_time}, {cook_time}, {servings}, {category_id}, {diet_id}, '{level}', {cuisine_id})"

def generate_sql_insert(num_rows):
    values = [generate_recipe(i) for i in range(1, num_rows + 1)]
    statement = "INSERT INTO recipe (recipeName, description, userId, prep_time, cook_time, servings, categoryId, dietId, cookingLevel, cuisineId) VALUES\n"
    statement += ",\n".join(values) + ";"
    return statement

# Generate 1000 recipes
if __name__ == "__main__":
    sql_output = generate_sql_insert(1000)
    with open("recipes.sql", "w") as file:
        file.write(sql_output)
    print("SQL file is created.")
