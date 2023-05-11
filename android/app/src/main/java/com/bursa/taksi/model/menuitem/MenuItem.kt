package com.bursa.taksi.model.menuitem

data class MenuItem (
    val type: MenuItemTypes,
) {
    enum class MenuItemTypes {
        Account,
        Options
    }
}

