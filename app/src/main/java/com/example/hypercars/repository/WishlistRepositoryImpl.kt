package com.example.hypercars.repository

import kotlinx.coroutines.flow.Flowimport kotlinx.coroutines.flow.MutableStateFlowimport kotlin.collections.none
import kotlin.collections.removeAll
import kotlin.collections.toList

object WishlistRepositoryImpl : WishlistRepository {

    private val wishlist = mutableListOf<WishlistItemModel>()
    private val wishlistFlow = MutableStateFlow<List<WishlistItemModel>>(emptyList())

    override suspend fun addToWishlist(item: WishlistItemModel) {
        if (wishlist.none { it.productName == item.productName }) {
            wishlist.add(item)
            wishlistFlow.value = wishlist.toList()
        }
    }

    override fun getWishlistItems(): Flow<List<WishlistItemModel>> = wishlistFlow

    override suspend fun removeFromWishlist(item: WishlistItemModel) {
        wishlist.removeAll { it.productName == item.productName }
        wishlistFlow.value = wishlist.toList()
    }
}