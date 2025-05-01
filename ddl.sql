-- Create the database.
create database if not exists cs4370_miso_hungry;

-- Use the created database.
use cs4370_miso_hungry;

-- Create the user table.
CREATE TABLE IF NOT EXISTS user (
    userId INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    firstName VARCHAR(255) NOT NULL,
    lastName VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    CONSTRAINT firstName_min_length CHECK (CHAR_LENGTH(TRIM(firstName)) >= 2),
    CONSTRAINT lastName_min_length CHECK (CHAR_LENGTH(TRIM(lastName)) >= 2)
);

create table if not exists category (
    categoryId int auto_increment,
    categoryName VARCHAR(255) not null,
    categoryImageUrl VARCHAR(255) not null,
    primary key (categoryId)
);

create table if not exists recipe (
    recipeId int auto_increment,
    recipeName VARCHAR(255) not null,
    description VARCHAR(1000) not null,
    recipeCreateDate DATETIME DEFAULT CURRENT_TIMESTAMP,
    userId INT not null,
    categoryId INT not null,
    prep_time INT not null,
    cook_time INT not null,
    servings INT not null,
    cuisineId VARCHAR(255) not null,
    dietId VARCHAR(255) not null,
    cookingLevel VARCHAR(255) not null,
    primary key (recipeId),
    FOREIGN KEY (userId) REFERENCES user(userId),
    FOREIGN KEY (categoryId) REFERENCES category(categoryId)
);

create table if not exists bookmark (
    recipeId INT NOT NULL,  -- Foreign key referencing recipe(id)
    userId INT NOT NULL,  -- Foreign key referencing user(userId)
    bookmark_type ENUM('PAST', 'FUTURE'),
    PRIMARY KEY (recipeId, userId),  -- Composite primary key (ensures unique bookmarks)
    CONSTRAINT fk_bookmark_recipe FOREIGN KEY (recipeId) REFERENCES recipe(recipeId) ON DELETE CASCADE,
    CONSTRAINT fk_bookmark_user FOREIGN KEY (userId) REFERENCES user(userId) ON DELETE CASCADE
);

create table if not exists rating (
    recipeId INT NOT NULL,  -- Foreign key referencing recipe(id)
    userId INT NOT NULL,  -- Foreign key referencing user(userId)
    PRIMARY KEY (recipeId, userId),  -- Composite primary key (ensures unique bookmarks)
    CONSTRAINT fk_rating_recipe FOREIGN KEY (recipeId) REFERENCES recipe(recipeId) ON DELETE CASCADE,
    CONSTRAINT fk_rating_user FOREIGN KEY (userId) REFERENCES user(userId) ON DELETE CASCADE
);

