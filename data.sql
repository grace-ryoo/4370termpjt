INSERT INTO category (categoryName, categoryImageUrl) VALUES 
('Breakfast', '/images/categories/breakfast-60x60.png'),
('Lunch', '/images/categories/lunch-60x60.png'),
('Dinner', '/images/categories/dinner-60x60.png'),
('Desserts', '/images/categories/desserts-60x60.png');

-- Insert common diet types
INSERT INTO diet (dietName, dietDescription) VALUES
('Vegetarian', 'No meat, fish, or poultry'),
('Vegan', 'No animal products'),
('Gluten-Free', 'No gluten-containing ingredients'),
('Keto', 'Low-carb, high-fat diet'),
('Paleo', 'Based on foods presumed eaten during the Paleolithic era'),
('Pescatarian', 'Vegetarian diet that includes fish'),
('Dairy-Free', 'No dairy products'),
('Low-Carb', 'Reduced carbohydrate intake'),
('None', 'No specific dietary restrictions');