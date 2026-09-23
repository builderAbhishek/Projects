package com.ais.swaadpe.data.repository

import com.ais.swaadpe.data.api.SwaadPeApi
import com.ais.swaadpe.domain.model.*
import com.ais.swaadpe.domain.repository.HomeData
import com.ais.swaadpe.domain.repository.HomeRepository
import javax.inject.Inject

class HomeRepositoryImpl @Inject constructor(
    private val api: SwaadPeApi
) : HomeRepository {

    override suspend fun getHomeData(
        userId: Int,
        lat: Double,
        lng: Double,
        address: String,
        query: String?
    ): Result<HomeData> {
        return try {
            val response = api.getHomeData(userId, lat, lng, address, query)
            if (response.isSuccessful && response.body()?.status == true) {
                val data = response.body()!!.data
                Result.success(
                    HomeData(
                        locationText = data.locationText,
                        activeOrder = data.activeOrder?.let {
                            ActiveOrder(it.orderNumber, it.orderStatus, it.grandTotal)
                        },
                        banners = data.banners.map { Banner(it.id, it.title, it.imageUrl) },
                        categories = data.categories.map { Category(it.id, it.name, it.iconUrl) },
                        topPicks = data.topPicks.map {
                            TopPick(
                                id = it.itemId,
                                name = it.itemName,
                                price = it.price,
                                imageUrl = it.imageUrl,
                                foodType = it.foodType,
                                vendorId = it.vendorId,
                                vendorName = it.vendorName
                            )
                        },
                        restaurants = data.restaurants.map { 
                            Restaurant(
                                id = it.id,
                                name = it.name,
                                rating = it.rating,
                                deliveryTime = it.deliveryTime,
                                imageUrl = if (it.images.isNotEmpty()) it.images[0] else null,
                                cuisineTags = if (it.isPureVeg) "Pure Veg" else "Multi-Cuisine",
                                isOpen = it.isOpen,
                                isPureVeg = it.isPureVeg,
                                distanceDisplay = it.distanceDisplay,
                                images = it.images
                            )
                        },
                        matchedItems = data.matchedItems?.map { 
                            MatchedItem(
                                id = it.id,
                                name = it.name,
                                description = it.description,
                                price = it.price,
                                imageUrl = it.imageUrl,
                                isAvailable = it.isAvailable,
                                vendorId = it.vendorId,
                                vendorName = it.vendorName
                            )
                        } ?: emptyList()
                    )
                )
            } else {
                Result.failure(Exception(response.body()?.message ?: "Unknown error"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
