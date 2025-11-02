import firebase_admin
from firebase_admin import credentials, firestore

# Initialize Firebase
cred = credentials.Certificate({
    "type": "service_account",
    "project_id": "abcd-11c4c",
    "private_key_id": "YOUR_PRIVATE_KEY_ID",
    "private_key": "-----BEGIN PRIVATE KEY-----\nYOUR_PRIVATE_KEY\n-----END PRIVATE KEY-----\n",
    "client_email": "firebase-adminsdk@abcd-11c4c.iam.gserviceaccount.com",
    "client_id": "YOUR_CLIENT_ID",
    "auth_uri": "https://accounts.google.com/o/oauth2/auth",
    "token_uri": "https://oauth2.googleapis.com/token",
    "auth_provider_x509_cert_url": "https://www.googleapis.com/oauth2/v1/certs"
})

firebase_admin.initialize_app(cred)
db = firestore.client()

# Canteen menu items
menu_items = [
    # Breakfast
    {"name": "Poha", "description": "Flattened rice with spices", "category": "Breakfast", "price": 30, "image": "", "available": True},
    {"name": "Upma", "description": "Semolina porridge", "category": "Breakfast", "price": 35, "image": "", "available": True},
    {"name": "Idli (2 pcs)", "description": "Steamed rice cakes with chutney", "category": "Breakfast", "price": 40, "image": "", "available": True},
    {"name": "Dosa", "description": "Crispy rice pancake", "category": "Breakfast", "price": 50, "image": "", "available": True},
    {"name": "Vada (2 pcs)", "description": "Deep fried lentil fritters", "category": "Breakfast", "price": 35, "image": "", "available": True},
    {"name": "Bread Omelette", "description": "2 eggs with bread", "category": "Breakfast", "price": 45, "image": "", "available": True},
    
    # Lunch
    {"name": "Veg Thali", "description": "Complete meal with rice, roti, dal, sabzi", "category": "Lunch", "price": 80, "image": "", "available": True},
    {"name": "Dal Rice", "description": "Yellow dal with steamed rice", "category": "Lunch", "price": 60, "image": "", "available": True},
    {"name": "Chole Bhature", "description": "Chickpeas curry with fried bread", "category": "Lunch", "price": 70, "image": "", "available": True},
    {"name": "Paneer Butter Masala", "description": "Cottage cheese in creamy tomato gravy", "category": "Lunch", "price": 90, "image": "", "available": True},
    {"name": "Biryani", "description": "Fragrant rice with vegetables", "category": "Lunch", "price": 85, "image": "", "available": True},
    {"name": "Rajma Rice", "description": "Kidney beans curry with rice", "category": "Lunch", "price": 65, "image": "", "available": True},
    
    # Snacks
    {"name": "Samosa (2 pcs)", "description": "Crispy pastry with potato filling", "category": "Snacks", "price": 20, "image": "", "available": True},
    {"name": "Veg Pakora", "description": "Mixed vegetable fritters", "category": "Snacks", "price": 30, "image": "", "available": True},
    {"name": "Veg Sandwich", "description": "Grilled sandwich with veggies", "category": "Snacks", "price": 40, "image": "", "available": True},
    {"name": "Cheese Sandwich", "description": "Grilled sandwich with cheese", "category": "Snacks", "price": 50, "image": "", "available": True},
    {"name": "Veg Pizza", "description": "7 inch personal pizza", "category": "Snacks", "price": 100, "image": "", "available": True},
    {"name": "Veg Burger", "description": "Veggie patty with fries", "category": "Snacks", "price": 70, "image": "", "available": True},
    {"name": "French Fries", "description": "Crispy fried potatoes", "category": "Snacks", "price": 40, "image": "", "available": True},
    {"name": "Spring Roll (2 pcs)", "description": "Crispy vegetable rolls", "category": "Snacks", "price": 50, "image": "", "available": True},
    
    # Beverages
    {"name": "Masala Tea", "description": "Indian spiced tea", "category": "Beverages", "price": 15, "image": "", "available": True},
    {"name": "Coffee", "description": "Hot coffee", "category": "Beverages", "price": 20, "image": "", "available": True},
    {"name": "Cold Coffee", "description": "Iced coffee with milk", "category": "Beverages", "price": 40, "image": "", "available": True},
    {"name": "Mango Juice", "description": "Fresh mango juice", "category": "Beverages", "price": 35, "image": "", "available": True},
    {"name": "Orange Juice", "description": "Fresh orange juice", "category": "Beverages", "price": 35, "image": "", "available": True},
    {"name": "Lassi", "description": "Traditional yogurt drink", "category": "Beverages", "price": 30, "image": "", "available": True},
    {"name": "Smoothie", "description": "Mixed fruit smoothie", "category": "Beverages", "price": 50, "image": "", "available": True},
    {"name": "Buttermilk", "description": "Spiced yogurt drink", "category": "Beverages", "price": 20, "image": "", "available": True},
    
    # Desserts
    {"name": "Gulab Jamun (2 pcs)", "description": "Sweet milk dumplings", "category": "Desserts", "price": 30, "image": "", "available": True},
    {"name": "Jalebi", "description": "Crispy sweet spirals", "category": "Desserts", "price": 35, "image": "", "available": True},
    {"name": "Ice Cream", "description": "Vanilla/Chocolate scoop", "category": "Desserts", "price": 40, "image": "", "available": True},
    {"name": "Kulfi", "description": "Traditional Indian ice cream", "category": "Desserts", "price": 35, "image": "", "available": True},
    {"name": "Rasgulla (2 pcs)", "description": "Spongy sweet cheese balls", "category": "Desserts", "price": 30, "image": "", "available": True},
]

# Add items to Firestore
for item in menu_items:
    db.collection('canteen_items').add(item)
    print(f"Added: {item['name']}")

print(f"\nSuccessfully added {len(menu_items)} items to canteen menu!")
