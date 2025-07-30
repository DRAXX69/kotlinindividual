package com.example.hypercars.repository


import com.example.hypercars.model.WishlistItemModel
import kotlinx.coroutines.flow.Flow

interface WishlistRepository {

    suspend fun addToWishlist(item: WishlistItemModel)
    fun getWishlistItems(): Flow<List<WishlistItemModel>>
    suspend fun removeFromWishlist(item: WishlistItemModel)
}