package com.example.hypercars.view

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import coil.compose.AsyncImage
import com.cloudinary.android.MediaManager
import com.example.hypercars.repository.CartRepositoryImpl
import com.example.hypercars.repository.OrderRepositoryImpl
import com.example.hypercars.repository.ProductRepositoryImpl
import com.example.hypercars.repository.UserRepositoryImpl
import com.example.hypercars.repository.WishlistRepositoryImpl
import com.example.hypercars.viewmodel.CartViewModel
import com.example.hypercars.viewmodel.CartViewModelFactory
import com.example.hypercars.viewmodel.OrderViewModel
import com.example.hypercars.viewmodel.OrderViewModelFactory
import com.example.hypercars.viewmodel.ProductViewModel
import com.example.hypercars.viewmodel.UserViewModel
import com.example.hypercars.viewmodel.UserViewModelFactory
import com.example.hypercars.viewmodel.WishlistViewModel
import com.example.hypercars.viewmodel.WishlistViewModelFactory
import com.example.hypercars.R
import com.example.hypercars.model.CartItemModel
import com.example.hypercars.model.ProductModel
import com.example.hypercars.model.WishlistItemModel
import com.example.hypercars.repository.WishlistRepository

class UserDashboardActivity : ComponentActivity() {

    private lateinit var cartViewModel: CartViewModel
    private lateinit var wishlistViewModel: WishlistViewModel
    private lateinit var userViewModel: UserViewModel
    private lateinit var orderViewModel: OrderViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize Cloudinary with your credentials
        val cloudinaryConfig = mapOf(
            "cloud_name" to "dde2pbccs",
            "api_key" to "944295238457168",
            "api_secret" to "01BItKSvyAAGIkKEbczBcWKCU5A",
            "secure" to true
        )
        MediaManager.init(this, cloudinaryConfig)


        cartViewModel = ViewModelProvider(this, CartViewModelFactory(CartRepositoryImpl()))[CartViewModel::class.java]
        wishlistViewModel = ViewModelProvider(this,
            WishlistViewModelFactory(WishlistRepositoryImpl)
        )[WishlistViewModel::class.java]
        userViewModel = ViewModelProvider(this,
            UserViewModelFactory(UserRepositoryImpl())
        )[UserViewModel::class.java]
        orderViewModel = ViewModelProvider(this, OrderViewModelFactory(OrderRepositoryImpl()))[OrderViewModel::class.java]

        setContent {
            UserDashboardBody(cartViewModel, wishlistViewModel, userViewModel, orderViewModel)
        }
    }

    override fun onResume() {
        super.onResume()
        val currentUserId = userViewModel.getCurrentUser()?.uid
        currentUserId?.let {
            userViewModel.getUserByID(it)
            orderViewModel.loadOrdersByUser(it)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserDashboardBody(
    cartViewModel: CartViewModel,
    wishlistViewModel: WishlistViewModel,
    userViewModel: UserViewModel,
    orderViewModel: OrderViewModel
) {
    val context = LocalContext.current
    val productRepo = remember { ProductRepositoryImpl() }
    val productViewModel = remember { ProductViewModel(productRepo) }

    val currentUser = userViewModel.getCurrentUser()
    val currentUserId = currentUser?.uid

    val user by userViewModel.users.observeAsState()
    val filteredProducts by productViewModel.filteredProducts.observeAsState(emptyList())
    val orders by orderViewModel.userOrders.observeAsState(emptyList())
    val loading by productViewModel.loading.observeAsState(true)

    var menuExpanded by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All") }

    LaunchedEffect(currentUserId) {
        currentUserId?.let {
            userViewModel.getUserByID(it)
            orderViewModel.loadOrdersByUser(it)
        }
        productViewModel.getAllProducts()
    }

    LaunchedEffect(searchQuery, selectedCategory) {
        productViewModel.filterByCategoryAndSearch(selectedCategory, searchQuery)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(id = R.drawable.retrocruglogo),
                            contentDescription = "RetroCrug Logo",
                            modifier = Modifier
                                .size(55.dp)
                                .clip(CircleShape)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("VIP Motors")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Black,
                    titleContentColor = Color.White
                ),
                actions = {
                    IconButton(onClick = {
                        context.startActivity(Intent(context, EditProfileActivity::class.java))
                    }) {
                        Icon(Icons.Default.Person, contentDescription = "Edit Profile", tint = Color.White)
                    }
                    Box {
                        IconButton(onClick = { menuExpanded = true }) {
                            Icon(Icons.Default.MoreVert, contentDescription = "Menu", tint = Color.White)
                        }
                        DropdownMenu(
                            expanded = menuExpanded,
                            onDismissRequest = { menuExpanded = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Address Book") },
                                onClick = {
                                    menuExpanded = false
                                    context.startActivity(Intent(context, AddressActivity::class.java))
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Logout") },
                                onClick = {
                                    menuExpanded = false
                                    Toast.makeText(context, "Logged out", Toast.LENGTH_SHORT).show()
                                    val intent = Intent(context, LoginActivity::class.java)
                                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                    context.startActivity(intent)
                                }
                            )
                        }
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar(containerColor = Color.Red) {
                NavigationBarItem(
                    selected = true,
                    onClick = {},
                    icon = { Icon(Icons.Default.Home, contentDescription = "Home", tint = Color.White) },
                    label = { Text("Home", color = Color.White) }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { context.startActivity(Intent(context, CartActivity::class.java)) },
                    icon = { Icon(Icons.Default.ShoppingCart, contentDescription = "Cart", tint = Color.White) },
                    label = { Text("Cart", color = Color.White) }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { context.startActivity(Intent(context, WishlistActivity::class.java)) },
                    icon = { Icon(Icons.Default.FavoriteBorder, contentDescription = "Wishlist", tint = Color.White) },
                    label = { Text("Wishlist", color = Color.White) }
                )
            }
        }
    ) { padding ->
        Column(
            Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            // VISIBLE DASHBOARD HEADER
            Text(
                text = "DASHBOARD",
                color = Color(0xFF4CAF50),
                style = MaterialTheme.typography.headlineLarge,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(top = 24.dp, bottom = 16.dp)
            )
            UserHeader(
                user = user
            )

            Column {
                TextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search products...") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                )

                val categories = listOf("All", "Bugatti", "Konigsegg", "Lamborghini", "Others")

                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 12.dp, bottom = 8.dp)
                ) {
                    items(categories) { category ->
                        FilterChip(
                            selected = selectedCategory == category,
                            onClick = {
                                selectedCategory = category
                                productViewModel.filterByCategoryAndSearch(category, searchQuery)
                            },
                            label = { Text(category) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Color(0xFF4CAF50),
                                selectedLabelColor = Color.White,
                                containerColor = Color.LightGray,
                                labelColor = Color.Black
                            ),
                            modifier = Modifier.padding(end = 8.dp)
                        )
                    }
                }
            }

            if (loading || filteredProducts.isEmpty()) {
                LazyColumn(modifier = Modifier.padding(8.dp)) {
                    items(3) { index ->
                        val cars = listOf(
                            Triple("Bugatti Chiron", "Top Speed: 420 km/h", "$5,500,000"),
                            Triple("Konigsegg Jesko", "Top Speed: 483 km/h", "$5,000,000"),
                            Triple("Lamborghini Sian", "Hybrid V12, 819 HP", "$4,000,000")
                        )
                        val images = listOf(
                            R.drawable.chiron,
                            R.drawable.jesko,
                            R.drawable.sian
                        )
                        val (name, specs, price) = cars[index]
                        HardcodedProductCard(name, specs, price, images[index], cartViewModel, wishlistViewModel, context)
                    }
                }
            } else {
                LazyColumn(modifier = Modifier.padding(8.dp)) {
                    items(filteredProducts) { product ->
                        product?.let {
                            ProductCard(it, cartViewModel, wishlistViewModel, context)
                        }
                    }
                }
            }

            if (orders.isNotEmpty()) {
                Text(
                    text = "Your Orders",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(start = 12.dp, top = 8.dp)
                )
                LazyColumn(
                    modifier = Modifier.fillMaxHeight(0.4f)
                ) {
                    items(orders) { order ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                        ) {
                            Column(Modifier.padding(12.dp)) {
                                Text("Order ID: ${order.orderId}")
                                Text("Total: Rs. ${order.totalAmount}")
                                Text("Status: ${order.orderStatus}")
                            }
                        }
                    }
                }
            } else {
                Text(
                    text = "You have no orders yet.",
                    modifier = Modifier.padding(12.dp),
                    color = Color.Gray
                )
            }
        }
    }
}

@Composable
fun UserHeader(
    user: com.example.hypercars.model.UserModel?
) {
    Box(modifier = Modifier.padding(12.dp)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = "Welcome, ${user?.firstName ?: "User"}!",
                style = MaterialTheme.typography.titleLarge
            )
        }
    }
}

@Composable
fun HardcodedProductCard(
    title: String,
    subtitle: String,
    price: String,
    imageRes: Int,
    cartViewModel: CartViewModel,
    wishlistViewModel: WishlistViewModel,
    context: android.content.Context
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFC8E6C9)
        ),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {

            Image(
                painter = painterResource(id = imageRes),
                contentDescription = title,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(text = title, style = MaterialTheme.typography.titleMedium)
            Text(text = subtitle, style = MaterialTheme.typography.bodyMedium, color = Color.DarkGray)
            Text(text = price, style = MaterialTheme.typography.bodyLarge, color = Color(0xFF1B5E20))

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = {
                        // For hardcoded products, we'll create a simple cart item
                        val cartItem = CartItemModel(
                            productId = title.replace(" ", "_").lowercase(),
                            productName = title,
                            productPrice = price.replace("$", "").replace(",", "").toDoubleOrNull() ?: 0.0,
                            image = "",
                            quantity = 1
                        )
                        cartViewModel.addToCart(cartItem)
                        Toast.makeText(context, "Added to cart", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF4CAF50),
                        contentColor = Color.White
                    )
                ) {
                    Text("Add to Cart")
                }

                IconButton(onClick = {
                    val wishlistItem = WishlistItemModel(
                        productName = title,
                        productPrice = price.replace("$", "").replace(",", "").toDoubleOrNull() ?: 0.0,
                        image = ""
                    )
                    wishlistViewModel.addToWishlist(wishlistItem)
                    Toast.makeText(context, "Added to wishlist", Toast.LENGTH_SHORT).show()
                }) {
                    Icon(Icons.Default.FavoriteBorder, contentDescription = "Wishlist", tint = Color.Red)
                }
            }
        }
    }
}

@Composable
fun ProductCard(
    product: ProductModel,
    cartViewModel: CartViewModel,
    wishlistViewModel: WishlistViewModel,
    context: android.content.Context
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFC8E6C9)
        ),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {

            AsyncImage(
                model = product.image,
                contentDescription = product.productName,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp),
                contentScale = ContentScale.Crop,
                error = painterResource(id = R.drawable.placeholder)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(text = product.productName ?: "Unnamed", style = MaterialTheme.typography.titleMedium)
            Text(text = "Rs. ${product.productPrice ?: 0}", style = MaterialTheme.typography.bodyLarge)

            product.category?.let {
                Box(
                    modifier = Modifier
                        .padding(top = 6.dp)
                        .background(Color(0xFF4CAF50), shape = RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = {
                        val cartItem = CartItemModel(
                            productId = product.productId ?: "",
                            productName = product.productName ?: "",
                            productPrice = product.productPrice ?: 0.0,
                            image = product.image ?: "",
                            quantity = 1
                        )
                        cartViewModel.addToCart(cartItem)
                        Toast.makeText(context, "Added to cart", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF4CAF50),
                        contentColor = Color.White
                    )
                ) {
                    Text("Add to Cart")
                }

                IconButton(onClick = {
                    val wishlistItem = WishlistItemModel(
                        productName = product.productName ?: "",
                        productPrice = product.productPrice ?: 0.0,
                        image = product.image ?: ""
                    )
                    wishlistViewModel.addToWishlist(wishlistItem)
                    Toast.makeText(context, "Added to wishlist", Toast.LENGTH_SHORT).show()
                }) {
                    Icon(Icons.Default.FavoriteBorder, contentDescription = "Wishlist", tint = Color.Red)
                }
            }
        }
    }
}