package com.ais.swaadpe.data.repository

import com.ais.swaadpe.data.api.SwaadPeApi
import com.ais.swaadpe.domain.model.MenuCategory
import com.ais.swaadpe.domain.model.MenuItem
import com.ais.swaadpe.domain.model.VendorDetails
import com.ais.swaadpe.domain.repository.MenuData
import com.ais.swaadpe.domain.repository.MenuRepository
import javax.inject.Inject

class MenuRepositoryImpl @Inject constructor(
    private val api: SwaadPeApi
) : MenuRepository {

    override suspend fun getVendorMenu(vendorId: Int): Result<MenuData> {
        return try {
            val response = api.getVendorMenu(vendorId)
            val body = response.body()
            if (response.isSuccessful && body?.status == true) {
                val data = body.data
                val vendor = VendorDetails(
                    id = data.vendorDetails.id,
                    name = data.vendorDetails.name,
                    image = data.vendorDetails.image,
                    address = data.vendorDetails.address,
                    rating = data.vendorDetails.rating,
                    deliveryTime = data.vendorDetails.deliveryTime,
                    minOrder = data.vendorDetails.minOrder,
                    isOpen = data.vendorDetails.isOpen
                )
                
                val menu = data.menu.map { catDto ->
                    MenuCategory(
                        name = catDto.categoryName,
                        items = catDto.items.map { itemDto ->
                            MenuItem(
                                id = itemDto.id,
                                name = itemDto.name,
                                description = itemDto.description,
                                price = itemDto.price,
                                imageUrl = itemDto.imageUrl,
                                isAvailable = itemDto.isAvailable
                            )
                        }
                    )
                }
                
                Result.success(MenuData(vendor, menu))
            } else {
                Result.failure(Exception(body?.message ?: "Failed to fetch menu"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
