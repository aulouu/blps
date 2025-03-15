INSERT INTO restaurants (name) VALUES
                                   ('Pizza Hut'),
                                   ('Burger King'),
                                   ('Sushi Master'),
                                   ('Taco Bell'),
                                   ('Pasta Express'),
                                   ('KFC'),
                                   ('McDonald''s'),
                                   ('Subway'),
                                   ('Dodo Pizza'),
                                   ('Teremok'),
                                   ('Shawarma House'),
                                   ('Kebab Street'),
                                   ('Wok to Walk'),
                                   ('Starbucks'),
                                   ('Coffee House'),
                                   ('Domino''s Pizza'),
                                   ('Burger Heroes'),
                                   ('Sushi Wok'),
                                   ('Mama Italia'),
                                   ('BBQ Chicken');

-- Ресторан 1: Pizza Hut
INSERT INTO stock (name, price, amount, restaurant_id) VALUES
                                                           ('Pepperoni Pizza', 799.00, 50, 1),
                                                           ('Margherita Pizza', 699.00, 40, 1),
                                                           ('Hawaiian Pizza', 849.00, 30, 1),
                                                           ('Cheese Pizza', 749.00, 45, 1),
                                                           ('Vegetarian Pizza', 799.00, 35, 1),
                                                           ('Meat Lovers Pizza', 899.00, 25, 1),
                                                           ('BBQ Chicken Pizza', 849.00, 30, 1),
                                                           ('Four Cheese Pizza', 799.00, 40, 1),
                                                           ('Mushroom Pizza', 749.00, 35, 1),
                                                           ('Buffalo Pizza', 899.00, 20, 1);

-- Ресторан 2: Burger King
INSERT INTO stock (name, price, amount, restaurant_id) VALUES
                                                           ('Whopper', 299.00, 60, 2),
                                                           ('Cheeseburger', 199.00, 70, 2),
                                                           ('Bacon King', 399.00, 50, 2),
                                                           ('Chicken Burger', 249.00, 55, 2),
                                                           ('Veggie Burger', 199.00, 40, 2),
                                                           ('Double Cheeseburger', 349.00, 45, 2),
                                                           ('Chicken Nuggets', 149.00, 80, 2),
                                                           ('French Fries', 99.00, 100, 2),
                                                           ('Onion Rings', 149.00, 60, 2),
                                                           ('Ice Cream', 99.00, 90, 2);

-- Ресторан 3: Sushi Master
INSERT INTO stock (name, price, amount, restaurant_id) VALUES
                                                           ('California Roll', 499.00, 30, 3),
                                                           ('Spicy Tuna Roll', 549.00, 25, 3),
                                                           ('Salmon Roll', 449.00, 35, 3),
                                                           ('Dragon Roll', 599.00, 20, 3),
                                                           ('Tempura Roll', 549.00, 25, 3),
                                                           ('Philadelphia Roll', 499.00, 30, 3),
                                                           ('Eel Roll', 649.00, 15, 3),
                                                           ('Vegetable Roll', 399.00, 40, 3),
                                                           ('Shrimp Roll', 499.00, 25, 3),
                                                           ('Miso Soup', 199.00, 50, 3);

-- Ресторан 4: Taco Bell
INSERT INTO stock (name, price, amount, restaurant_id) VALUES
                                                           ('Beef Taco', 149.00, 100, 4),
                                                           ('Chicken Taco', 149.00, 95, 4),
                                                           ('Vegetarian Taco', 129.00, 80, 4),
                                                           ('Crunchwrap Supreme', 299.00, 60, 4),
                                                           ('Nachos', 199.00, 70, 4),
                                                           ('Quesadilla', 249.00, 50, 4),
                                                           ('Burrito', 199.00, 65, 4),
                                                           ('Cinnamon Twists', 99.00, 90, 4),
                                                           ('Mexican Pizza', 349.00, 40, 4),
                                                           ('Soft Drink', 79.00, 120, 4);

-- Ресторан 5: Pasta Express
INSERT INTO stock (name, price, amount, restaurant_id) VALUES
                                                           ('Spaghetti Bolognese', 399.00, 45, 5),
                                                           ('Fettuccine Alfredo', 449.00, 40, 5),
                                                           ('Lasagna', 499.00, 35, 5),
                                                           ('Penne Arrabbiata', 349.00, 50, 5),
                                                           ('Carbonara', 449.00, 40, 5),
                                                           ('Ravioli', 399.00, 30, 5),
                                                           ('Pesto Pasta', 399.00, 35, 5),
                                                           ('Seafood Pasta', 549.00, 25, 5),
                                                           ('Vegetarian Pasta', 349.00, 40, 5),
                                                           ('Garlic Bread', 99.00, 80, 5);

-- Ресторан 6: KFC
INSERT INTO stock (name, price, amount, restaurant_id) VALUES
                                                           ('Original Recipe Chicken', 299.00, 50, 6),
                                                           ('Zinger Burger', 249.00, 60, 6),
                                                           ('Twister', 199.00, 55, 6),
                                                           ('Popcorn Chicken', 149.00, 70, 6),
                                                           ('French Fries', 99.00, 100, 6),
                                                           ('Coleslaw', 79.00, 80, 6),
                                                           ('Mashed Potatoes', 99.00, 90, 6),
                                                           ('Chicken Nuggets', 149.00, 75, 6),
                                                           ('Bucket Meal', 999.00, 20, 6),
                                                           ('Soft Drink', 79.00, 120, 6);

-- Ресторан 7: McDonald's
INSERT INTO stock (name, price, amount, restaurant_id) VALUES
                                                           ('Big Mac', 199.00, 70, 7),
                                                           ('Cheeseburger', 99.00, 100, 7),
                                                           ('Chicken McNuggets', 149.00, 80, 7),
                                                           ('French Fries', 99.00, 120, 7),
                                                           ('McChicken', 149.00, 60, 7),
                                                           ('Filet-O-Fish', 199.00, 50, 7),
                                                           ('Apple Pie', 79.00, 90, 7),
                                                           ('McFlurry', 149.00, 60, 7),
                                                           ('Soft Drink', 79.00, 150, 7),
                                                           ('Salad', 199.00, 40, 7);

-- Ресторан 8: Subway
INSERT INTO stock (name, price, amount, restaurant_id) VALUES
                                                           ('Italian B.M.T.', 299.00, 50, 8),
                                                           ('Turkey Breast', 249.00, 60, 8),
                                                           ('Veggie Delite', 199.00, 70, 8),
                                                           ('Chicken Teriyaki', 299.00, 55, 8),
                                                           ('Tuna Sandwich', 349.00, 40, 8),
                                                           ('Meatball Marinara', 299.00, 45, 8),
                                                           ('Cookies', 49.00, 100, 8),
                                                           ('Chips', 79.00, 90, 8),
                                                           ('Soft Drink', 99.00, 120, 8),
                                                           ('Salad', 199.00, 50, 8);

-- Ресторан 9: Dodo Pizza
INSERT INTO stock (name, price, amount, restaurant_id) VALUES
                                                           ('Pepperoni Pizza', 799.00, 50, 9),
                                                           ('Margherita Pizza', 699.00, 40, 9),
                                                           ('Hawaiian Pizza', 849.00, 30, 9),
                                                           ('Cheese Pizza', 749.00, 45, 9),
                                                           ('Vegetarian Pizza', 799.00, 35, 9),
                                                           ('Meat Lovers Pizza', 899.00, 25, 9),
                                                           ('BBQ Chicken Pizza', 849.00, 30, 9),
                                                           ('Four Cheese Pizza', 799.00, 40, 9),
                                                           ('Mushroom Pizza', 749.00, 35, 9),
                                                           ('Buffalo Pizza', 899.00, 20, 9);

-- Ресторан 10: Teremok
INSERT INTO stock (name, price, amount, restaurant_id) VALUES
                                                           ('Blini with Salmon', 299.00, 50, 10),
                                                           ('Blini with Caviar', 499.00, 30, 10),
                                                           ('Blini with Jam', 99.00, 80, 10),
                                                           ('Blini with Sour Cream', 99.00, 70, 10),
                                                           ('Borscht', 199.00, 60, 10),
                                                           ('Pelmeni', 249.00, 50, 10),
                                                           ('Beef Stroganoff', 349.00, 40, 10),
                                                           ('Buckwheat with Mushrooms', 199.00, 55, 10),
                                                           ('Kvas', 79.00, 100, 10),
                                                           ('Tea', 49.00, 120, 10);

-- Ресторан 11: Shawarma House
INSERT INTO stock (name, price, amount, restaurant_id) VALUES
                                                           ('Chicken Shawarma', 199.00, 60, 11),
                                                           ('Beef Shawarma', 249.00, 50, 11),
                                                           ('Vegetarian Shawarma', 179.00, 40, 11),
                                                           ('Falafel Wrap', 149.00, 55, 11),
                                                           ('Hummus', 99.00, 70, 11),
                                                           ('Tabouleh', 129.00, 45, 11),
                                                           ('Garlic Sauce', 49.00, 100, 11),
                                                           ('French Fries', 99.00, 80, 11),
                                                           ('Soft Drink', 79.00, 120, 11),
                                                           ('Baklava', 149.00, 30, 11);

-- Ресторан 12: Kebab Street
INSERT INTO stock (name, price, amount, restaurant_id) VALUES
                                                           ('Chicken Kebab', 299.00, 50, 12),
                                                           ('Lamb Kebab', 349.00, 40, 12),
                                                           ('Beef Kebab', 329.00, 45, 12),
                                                           ('Vegetable Kebab', 249.00, 55, 12),
                                                           ('Rice Pilaf', 149.00, 70, 12),
                                                           ('Grilled Vegetables', 199.00, 60, 12),
                                                           ('Flatbread', 49.00, 100, 12),
                                                           ('Yogurt Sauce', 49.00, 90, 12),
                                                           ('Soft Drink', 79.00, 120, 12),
                                                           ('Kunefe', 199.00, 25, 12);

-- Ресторан 13: Wok to Walk
INSERT INTO stock (name, price, amount, restaurant_id) VALUES
                                                           ('Chicken Wok', 349.00, 50, 13),
                                                           ('Beef Wok', 399.00, 45, 13),
                                                           ('Shrimp Wok', 449.00, 40, 13),
                                                           ('Vegetable Wok', 299.00, 55, 13),
                                                           ('Noodles', 149.00, 70, 13),
                                                           ('Fried Rice', 199.00, 60, 13),
                                                           ('Soy Sauce', 29.00, 100, 13),
                                                           ('Chili Sauce', 29.00, 90, 13),
                                                           ('Soft Drink', 79.00, 120, 13),
                                                           ('Spring Rolls', 149.00, 30, 13);

-- Ресторан 14: Starbucks
INSERT INTO stock (name, price, amount, restaurant_id) VALUES
                                                           ('Latte', 199.00, 80, 14),
                                                           ('Cappuccino', 179.00, 70, 14),
                                                           ('Americano', 149.00, 90, 14),
                                                           ('Mocha', 249.00, 60, 14),
                                                           ('Caramel Macchiato', 279.00, 50, 14),
                                                           ('Croissant', 99.00, 100, 14),
                                                           ('Blueberry Muffin', 129.00, 70, 14),
                                                           ('Chocolate Cake', 199.00, 40, 14),
                                                           ('Iced Tea', 149.00, 80, 14),
                                                           ('Bottled Water', 79.00, 120, 14);

-- Ресторан 15: Coffee House
INSERT INTO stock (name, price, amount, restaurant_id) VALUES
                                                           ('Espresso', 99.00, 90, 15),
                                                           ('Flat White', 199.00, 70, 15),
                                                           ('Hot Chocolate', 179.00, 80, 15),
                                                           ('Green Tea', 99.00, 100, 15),
                                                           ('Cheesecake', 249.00, 40, 15),
                                                           ('Sandwich', 199.00, 60, 15),
                                                           ('Bagel', 149.00, 70, 15),
                                                           ('Fruit Salad', 199.00, 50, 15),
                                                           ('Smoothie', 249.00, 60, 15),
                                                           ('Bottled Juice', 129.00, 80, 15);

-- Ресторан 16: Domino's Pizza
INSERT INTO stock (name, price, amount, restaurant_id) VALUES
                                                           ('Pepperoni Pizza', 799.00, 50, 16),
                                                           ('Cheese Pizza', 699.00, 60, 16),
                                                           ('Veggie Pizza', 749.00, 45, 16),
                                                           ('Meat Feast Pizza', 899.00, 40, 16),
                                                           ('Hawaiian Pizza', 799.00, 50, 16),
                                                           ('Garlic Bread', 149.00, 80, 16),
                                                           ('Chicken Wings', 299.00, 60, 16),
                                                           ('Chocolate Lava Cake', 199.00, 70, 16),
                                                           ('Soft Drink', 99.00, 120, 16),
                                                           ('Salad', 199.00, 50, 16);

-- Ресторан 17: Burger Heroes
INSERT INTO stock (name, price, amount, restaurant_id) VALUES
                                                           ('Classic Burger', 249.00, 60, 17),
                                                           ('Cheeseburger', 299.00, 55, 17),
                                                           ('Bacon Burger', 349.00, 50, 17),
                                                           ('Veggie Burger', 199.00, 70, 17),
                                                           ('Chicken Burger', 279.00, 60, 17),
                                                           ('French Fries', 99.00, 100, 17),
                                                           ('Onion Rings', 149.00, 80, 17),
                                                           ('Milkshake', 199.00, 50, 17),
                                                           ('Soft Drink', 79.00, 120, 17),
                                                           ('Ice Cream', 99.00, 90, 17);

-- Ресторан 18: Sushi Wok
INSERT INTO stock (name, price, amount, restaurant_id) VALUES
                                                           ('California Roll', 499.00, 40, 18),
                                                           ('Spicy Tuna Roll', 549.00, 35, 18),
                                                           ('Dragon Roll', 599.00, 30, 18),
                                                           ('Tempura Roll', 549.00, 35, 18),
                                                           ('Miso Soup', 99.00, 80, 18),
                                                           ('Edamame', 149.00, 70, 18),
                                                           ('Sashimi', 699.00, 25, 18),
                                                           ('Green Tea', 79.00, 100, 18),
                                                           ('Soft Drink', 99.00, 120, 18),
                                                           ('Mochi Ice Cream', 199.00, 40, 18);

-- Ресторан 19: Mama Italia
INSERT INTO stock (name, price, amount, restaurant_id) VALUES
                                                           ('Spaghetti Carbonara', 399.00, 50, 19),
                                                           ('Lasagna', 499.00, 40, 19),
                                                           ('Pizza Margherita', 599.00, 45, 19),
                                                           ('Ravioli', 349.00, 55, 19),
                                                           ('Tiramisu', 199.00, 60, 19),
                                                           ('Bruschetta', 149.00, 70, 19),
                                                           ('Caesar Salad', 249.00, 50, 19),
                                                           ('Minestrone Soup', 199.00, 60, 19),
                                                           ('Soft Drink', 99.00, 120, 19),
                                                           ('Garlic Bread', 99.00, 80, 19);

-- Ресторан 20: BBQ Chicken
INSERT INTO stock (name, price, amount, restaurant_id) VALUES
                                                           ('BBQ Chicken Wings', 299.00, 60, 20),
                                                           ('Honey Garlic Chicken', 349.00, 50, 20),
                                                           ('Spicy Chicken', 329.00, 55, 20),
                                                           ('Grilled Chicken', 299.00, 60, 20),
                                                           ('French Fries', 99.00, 100, 20),
                                                           ('Coleslaw', 79.00, 80, 20),
                                                           ('Corn on the Cob', 149.00, 70, 20),
                                                           ('Soft Drink', 79.00, 120, 20),
                                                           ('Garlic Bread', 99.00, 90, 20),
                                                           ('Chocolate Cake', 199.00, 40, 20);
