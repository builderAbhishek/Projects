package com.ais.swaadpe.domain.repository

import com.ais.swaadpe.domain.model.Banner
import com.ais.swaadpe.domain.model.Category
import com.ais.swaadpe.domain.model.Restaurant
import com.ais.swaadpe.domain.model.MatchedItem
import com.ais.swaadpe.domain.model.TopPick
import com.ais.swaadpe.domain.model.ActiveOrder

interface HomeRepository {
    suspend fun getHomeData(
        userId: Int,
        lat: Double,
        lng: Double,
        address: String,
        query: String? = null
    ): Result<HomeData>
}

data class HomeData(
    val locationText: String,
    val activeOrder: ActiveOrder?,
    val banners: List<Banner>,
    val categories: List<Category>,
    val topPicks: List<TopPick>,
    val restaurants: List<Restaurant>,
    val matchedItems: List<MatchedItem>
)
