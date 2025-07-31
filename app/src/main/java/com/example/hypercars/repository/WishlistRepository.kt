package com.example.hypercars.repository


import com.example.hypercars.model.WishlistItemModel
import kotlinx.coroutines.flow.Flow

interface WishlistRepository {

     fun addToWishlist(item: WishlistItemModel)
    fun getWishlistItems(): Flow<List<WishlistItemModel>>
    fun removeFromWishlist(item: WishlistItemModel)
}