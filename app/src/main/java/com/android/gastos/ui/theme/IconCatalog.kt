package com.android.gastos.ui.theme

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Cake
import androidx.compose.material.icons.filled.CalendarViewWeek
import androidx.compose.material.icons.filled.Celebration
import androidx.compose.material.icons.filled.Checkroom
import androidx.compose.material.icons.filled.ChildCare
import androidx.compose.material.icons.filled.Coffee
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Flight
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalGasStation
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.LocalMovies
import androidx.compose.material.icons.filled.LocalPharmacy
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * All category icons are referenced by a stable string key (persisted in Room),
 * never by enum ordinal, so the icon set can grow without breaking saved data.
 */
val IconCatalog: LinkedHashMap<String, ImageVector> = linkedMapOf(
    "restaurant" to Icons.Filled.Restaurant,
    "fastfood" to Icons.Filled.Fastfood,
    "coffee" to Icons.Filled.Coffee,
    "shopping_cart" to Icons.Filled.ShoppingCart,
    "credit_card" to Icons.Filled.CreditCard,
    "savings" to Icons.Filled.Savings,
    "directions_bus" to Icons.Filled.DirectionsBus,
    "directions_car" to Icons.Filled.DirectionsCar,
    "flight" to Icons.Filled.Flight,
    "local_gas_station" to Icons.Filled.LocalGasStation,
    "local_hospital" to Icons.Filled.LocalHospital,
    "local_pharmacy" to Icons.Filled.LocalPharmacy,
    "fitness_center" to Icons.Filled.FitnessCenter,
    "spa" to Icons.Filled.Spa,
    "water_drop" to Icons.Filled.WaterDrop,
    "wifi" to Icons.Filled.Wifi,
    "home" to Icons.Filled.Home,
    "build" to Icons.Filled.Build,
    "pets" to Icons.Filled.Pets,
    "child_care" to Icons.Filled.ChildCare,
    "school" to Icons.Filled.School,
    "checkroom" to Icons.Filled.Checkroom,
    "local_movies" to Icons.Filled.LocalMovies,
    "sports_esports" to Icons.Filled.SportsEsports,
    "music_note" to Icons.Filled.MusicNote,
    "book" to Icons.Filled.Book,
    "cake" to Icons.Filled.Cake,
    "celebration" to Icons.Filled.Celebration,
    "date_range" to Icons.Filled.DateRange,
    "calendar_view_week" to Icons.Filled.CalendarViewWeek,
    "more_horiz" to Icons.Filled.MoreHoriz
)

fun iconFor(key: String): ImageVector = IconCatalog[key] ?: Icons.Filled.MoreHoriz

/** Curated swatches at a tone that reads reasonably on both light and dark surfaces. */
val CategoryColorSwatches: List<Long> = listOf(
    0xFF2A78D6, // blue
    0xFF0277BD, // light blue
    0xFF00796B, // teal
    0xFF1BAF7A, // aqua/green
    0xFF2E7D32, // green
    0xFFC98500, // amber
    0xFFEB6834, // orange
    0xFFD84315, // deep orange
    0xFFD32F2F, // red
    0xFFD55181, // magenta
    0xFFC2185B, // pink
    0xFF7B1FA2, // purple
    0xFF512DA8, // deep purple
    0xFF3949AB, // indigo
    0xFF6750A4, // violet
    0xFF8D6E63, // brown
    0xFF546E7A, // blue grey
    0xFF757575, // grey
    0xFF0097A7, // cyan
    0xFF00695C  // dark teal
)
