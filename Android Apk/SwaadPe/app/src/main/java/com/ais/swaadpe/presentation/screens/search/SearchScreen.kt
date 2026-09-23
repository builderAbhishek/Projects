package com.ais.swaadpe.presentation.screens.search

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ais.swaadpe.R

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun SearchScreen(
    onBackClick: () -> Unit,
    onRestaurantClick: (Int, String) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    val focusManager = LocalFocusManager.current

    val allResults = getMockSearchResults()
    val filteredResults = if (searchQuery.isEmpty()) {
        emptyList()
    } else {
        allResults.filter { 
            it.name.contains(searchQuery, ignoreCase = true) || 
            it.cuisine.contains(searchQuery, ignoreCase = true) 
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    TextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(end = 16.dp),
                        placeholder = { Text("Search dishes or restaurants...", style = TextStyle(fontSize = 16.sp, color = Color.Gray)) },
                        leadingIcon = { Icon(Icons.Default.Search, null, tint = Color(0xFF4CAF50)) },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { searchQuery = "" }) {
                                    Icon(Icons.Default.Clear, null)
                                }
                            }
                        },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        textStyle = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Medium),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                        keyboardActions = KeyboardActions(onSearch = { focusManager.clearFocus() }),
                        singleLine = true
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                    }
                }
            )
        },
        containerColor = Color(0xFFFBFBFB)
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            if (searchQuery.isEmpty()) {
                PopularSearchesSection { searchQuery = it }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    if (filteredResults.isEmpty()) {
                        item {
                            Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                                Text("No results found for '$searchQuery'", style = TextStyle(color = Color.Gray, fontSize = 16.sp))
                            }
                        }
                    } else {
                        items(filteredResults) { result ->
                            SearchResultItem(result) {
                                onRestaurantClick(result.id, result.restaurantName)
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PopularSearchesSection(onSearchClick: (String) -> Unit) {
    val popular = listOf("Paneer", "Thali", "Pizza", "Sweets", "Burger", "Chaat")
    Column(modifier = Modifier.padding(20.dp)) {
        Text("Popular Searches", style = TextStyle(fontWeight = FontWeight.Black, fontSize = 20.sp, color = Color.Black))
        Spacer(modifier = Modifier.height(20.dp))
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            popular.forEach { term ->
                SuggestionChip(label = term) { onSearchClick(term) }
            }
        }
    }
}

@Composable
fun SuggestionChip(label: String, onClick: () -> Unit) {
    Surface(
        modifier = Modifier.clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE0E0E0)),
        color = Color.White,
        shadowElevation = 1.dp
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 18.dp, vertical = 10.dp),
            style = TextStyle(fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.DarkGray)
        )
    }
}

@Composable
fun SearchResultItem(result: SearchResult, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier.size(64.dp).background(Color(0xFFF5F5F5), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(painterResource(R.drawable.logo), null, modifier = Modifier.size(32.dp), tint = Color(0xFF4CAF50).copy(0.4f))
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(result.name, style = TextStyle(fontWeight = FontWeight.Black, fontSize = 17.sp, color = Color.Black))
                Text(result.cuisine, style = TextStyle(color = Color.Gray, fontSize = 13.sp, fontWeight = FontWeight.Medium))
                Text("from ${result.restaurantName}", style = TextStyle(color = Color(0xFF4CAF50), fontSize = 12.sp, fontWeight = FontWeight.Bold))
            }
            Surface(color = Color(0xFFE8F5E9), shape = RoundedCornerShape(8.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)) {
                    Text(result.rating.toString(), style = TextStyle(fontWeight = FontWeight.Black, fontSize = 12.sp, color = Color(0xFF2E7D32)))
                    Icon(Icons.Default.Star, null, tint = Color(0xFF2E7D32), modifier = Modifier.size(14.dp))
                }
            }
        }
    }
}

data class SearchResult(val id: Int, val name: String, val cuisine: String, val restaurantName: String, val rating: Double)
fun getMockSearchResults() = listOf(
    SearchResult(1, "Premium Thali", "North Indian", "The Grand Thali", 4.5),
    SearchResult(2, "Paneer Tikka", "Starter", "Swaad Sagar", 4.2),
    SearchResult(3, "Veg Burger", "Fast Food", "Gopal Sweets", 4.7)
)
