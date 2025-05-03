/* All queries used in the web app (both data retrieval and data modification */

3 quereies must include aggregation and or join statments in a non-trivial way 


/* in bookmarksservice */
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


/* profileservice */
 
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

                 

        /* recipe service */
        
                String sql = "SELECT r.*, u.userId, u.firstName, u.lastName, c.categoryId, c.categoryName, c.categoryImageUrl, "
                + "COALESCE(AVG(rt.stars), 0) AS averageRating, "
                + "COUNT(rt.userId) AS countRatings "
                + "FROM recipe r "
                + "JOIN user u ON r.userId = u.userId "
                + "JOIN category c ON r.categoryId = c.categoryId "
                + "LEFT JOIN rating rt ON r.recipeId = rt.recipeId "
                + "WHERE r.recipeId = ? "
                + "GROUP BY r.recipeId, u.userId, c.categoryId";


                /* cusine service */
                 final String sql = "SELECT cuisineId, cuisineName, cuisineDescription FROM cuisine";

                 /* diet service */ 
                  final String sql = "SELECT dietId, dietName, dietDescription FROM diet";

                  /* frocery list service */
/*                  get grocery list */
                   final String sql = "SELECT itemId, itemName, itemQuantity, isBought FROM groceryList WHERE userId = ?";

                   /* add */
                           final String sql = "INSERT INTO groceryList (itemId, userId, itemName, itemQuantity) VALUES (?, ?, ?, ?)";

                           /* markitem as bought */ 
                            final String sql = "UPDATE groceryList SET isBought = TRUE WHERE userId = ? AND itemId = ?";