/* All queries used in the web app (both data retrieval and data modification */

/* 
* SQL query in BookmarkService.
* Retrieves information about user's bookmarked recipes.
* Includes recipe data, user info, category details, and aggregated rating data. 
* Uses 4 joins (3 inner joins and 1 left join)
* Uses aggregation. (average and count of ratings)
*/

final String sql =  "SELECT r.*, u.username, u.firstName, u.lastName, c.categoryId, c.categoryName, c.categoryImageUrl, " +
    "COALESCE(AVG(rt.stars), 0) AS averageRating, " +
    "COUNT(rt.userId) AS countRatings " +
    "FROM bookmark b " +
    "JOIN recipe r ON b.recipeId = r.recipeId " +
    "JOIN user u ON r.userId = u.userId " +
    "JOIN category c ON r.categoryId = c.categoryId " +
    "LEFT JOIN rating rt ON r.recipeId = rt.recipeId " +
    "WHERE b.userId = ? AND b.bookmark_type = ? " +
    "GROUP BY r.recipeId, u.userId, u.username, u.firstName, u.lastName, c.categoryId, c.categoryName, c.categoryImageUrl";


/* 
* SQL query in ProfileService.
* Retrieves all recipes created by a specific user. 
* Includes recipe category information and rating stats. 
* Uses 3 joins. 
* Uses aggregation (average and count of ratings)
*/
 
String sql = "SELECT r.*, u.userId, u.firstName, u.lastName, " +
        "c.categoryId, c.categoryName, c.categoryImageUrl, " +
        "COALESCE(AVG(rt.stars), 0) AS averageRating, " +
        "COUNT(rt.userId) AS countRatings " +
        "FROM user u " +
        "JOIN recipe r ON u.userId = r.userId " +
        "JOIN category c ON r.categoryId = c.categoryId " +
        "LEFT JOIN rating rt ON r.recipeId = rt.recipeId " +
        "WHERE u.userId = ? " +
        "GROUP BY r.recipeId, u.userId, c.categoryId "; 

/*
* SQL query in RecipeService.
* Retrives information for a recipe.
* Includes recipe data, user info, category information, and aggregating rating data.
* Uses joins.
* Uses aggregation (average and count of ratings)
*/
        
String sql = "SELECT r.*, u.userId, u.firstName, u.lastName, c.categoryId, c.categoryName, c.categoryImageUrl, "
        "COALESCE(AVG(rt.stars), 0) AS averageRating, "
        "COUNT(rt.userId) AS countRatings "
        "FROM recipe r "
        "JOIN user u ON r.userId = u.userId "
        "JOIN category c ON r.categoryId = c.categoryId "
        "LEFT JOIN rating rt ON r.recipeId = rt.recipeId "
        "WHERE r.recipeId = ? "
        "GROUP BY r.recipeId, u.userId, c.categoryId";

/*
* SQL query in CusineService
* Retrives all cusines from the cusine table, including ID, name, and description.
*/
    final String sql = "SELECT cuisineId, cuisineName, cuisineDescription FROM cuisine";

/*
* SQL query in DietService
* Retrives all diet types from diet table, including ID, name, and description. 
*/
    final String sql = "SELECT dietId, dietName, dietDescription FROM diet";

/* 
* SQL query in GroceryListService
* Retrieves grocery list items for a specific user, showing whether each item has been bought. 
*/
    final String sql = "SELECT itemId, itemName, itemQuantity, isBought FROM groceryList WHERE userId = ?";

/*
* SQL query in GroceryListService
* Inserts a new item into the grocery list for a specific user. 
*/
    final String sql = "INSERT INTO groceryList (itemId, userId, itemName, itemQuantity) VALUES (?, ?, ?, ?)";

/* 
* SQL query in GroceryListService
* Marks a specific grocery item as bought for a specific user. 
*/
    final String sql = "UPDATE groceryList SET isBought = TRUE WHERE userId = ? AND itemId = ?";