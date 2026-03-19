package com.ch000se.profileapp.data.local

import androidx.room.TypeConverter
import com.ch000se.profileapp.domain.model.ContactCategory

internal class Converters {

    @TypeConverter
    fun fromCategoryList(categories: List<ContactCategory>): String {
        return categories.joinToString(",") { it.name }
    }

    @TypeConverter
    fun toCategoryList(categoriesString: String): List<ContactCategory> {
        if (categoriesString.isBlank()) return emptyList()
        return categoriesString.split(",")
            .mapNotNull { categoryName ->
                try {
                    ContactCategory.valueOf(categoryName.trim())
                } catch (e: IllegalArgumentException) {
                    null
                }
            }
    }
}
