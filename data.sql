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

-- Insert cuisine types
INSERT INTO cuisine (cuisineName, cuisineDescription) VALUES 
('Italian', 'Traditional Italian dishes including pasta, pizza, and Mediterranean flavors'),
('Chinese', 'Various regional Chinese cuisines featuring stir-fries, noodles, and rice dishes'),
('Mexican', 'Traditional Mexican dishes with tortillas, beans, and diverse spices'),
('Indian', 'Rich and diverse Indian cuisine with curries, tandoor dishes, and flatbreads'),
('Japanese', 'Japanese cuisine featuring sushi, noodles, and traditional dishes'),
('Thai', 'Thai dishes with sweet, sour, and spicy flavors'),
('Mediterranean', 'Healthy Mediterranean diet with olive oil, fresh vegetables, and seafood'),
('American', 'Classic American comfort food and regional specialties'),
('French', 'Elegant French cuisine with rich sauces and sophisticated techniques'),
('Korean', 'Korean dishes featuring kimchi, barbecue, and rice-based meals'),
('Vietnamese', 'Fresh Vietnamese cuisine with herbs and light preparations'),
('Middle Eastern', 'Middle Eastern fare with kebabs, hummus, and aromatic spices'),
('Greek', 'Traditional Greek dishes with olives, feta, and Mediterranean herbs'),
('Spanish', 'Spanish cuisine featuring paella, tapas, and seafood dishes');