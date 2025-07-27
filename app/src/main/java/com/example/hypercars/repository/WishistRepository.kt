package com.example.sportsequipmentstore.repository

import com.example.sportsequipmentstore.model.WishlistItemModel
import kotlinx.coroutines.flow.Flow

interface WishlistRepository {
    suspend fun addToWishlist(item: WishlistItemModel)
    fun getWishlistItems(): Flow<List<WishlistItemModel>>
    suspend fun removeFromWishlist(item: WishlistItemModel)
}
