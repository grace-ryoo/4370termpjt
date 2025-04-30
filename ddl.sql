-- Create the database.
create database if not exists cs4370_miso_hungry;

-- Use the created database.
use cs4370_miso_hungry;

-- Create the user table.
create table if not exists user (
    userId int auto_increment,
    password varchar(255) not null,
    firstName varchar(255) not null,
    lastName varchar(255) not null,
    primary key (userId),
    constraint firstName_min_length check (char_length(trim(firstName)) >= 2),
    constraint lastName_min_length check (char_length(trim(lastName)) >= 2)
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

CREATE TABLE if not exists recipe_ingredients (
    recipeId INT NOT NULL,
    ingredientName VARCHAR(255) NOT NULL,
    ingredientAmount VARCHAR(255) NOT NULL,
    ingredientUnit VARCHAR(255) NOT NULL,
    PRIMARY KEY (recipeId, ingredientName),
    CONSTRAINT fk_recipe_ingredients_recipe FOREIGN KEY (recipeId) REFERENCES recipe(recipeId) ON DELETE CASCADE
)

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

