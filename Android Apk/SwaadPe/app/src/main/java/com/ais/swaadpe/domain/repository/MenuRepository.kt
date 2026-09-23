package com.ais.swaadpe.domain.repository

import com.ais.swaadpe.domain.model.MenuCategory
import com.ais.swaadpe.domain.model.VendorDetails

interface MenuRepository {
    suspend fun getVendorMenu(vendorId: Int): Result<MenuData>
}

data class MenuData(
    val vendor: VendorDetails,
    val menu: List<MenuCategory>
)
