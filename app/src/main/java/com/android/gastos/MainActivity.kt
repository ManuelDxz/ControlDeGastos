package com.android.gastos

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.android.gastos.data.Expense
import com.android.gastos.data.PeriodType
import com.android.gastos.ui.GastosViewModel
import com.android.gastos.ui.GastosViewModelFactory
import com.android.gastos.ui.screens.AddExpenseSheet
import com.android.gastos.ui.screens.AddIncomeSheet
import com.android.gastos.ui.screens.CalendarScreen
import com.android.gastos.ui.screens.CreditsSheet
import com.android.gastos.ui.screens.HomeScreen
import com.android.gastos.ui.screens.NewPeriodSheet
import com.android.gastos.ui.screens.PeriodsScreen
import com.android.gastos.ui.screens.ProfileSheet
import com.android.gastos.ui.screens.StatsScreen
import com.android.gastos.ui.theme.ControlDeGastosTheme
import java.time.LocalDate

private sealed class Destination(val route: String, val label: String) {
    data object Home : Destination("home", "Inicio")
    data object Calendar : Destination("calendar", "Calendario")
    data object Stats : Destination("stats", "Estadísticas")
    data object Periods : Destination("periods", "Periodos")
}

private val bottomDestinations = listOf(Destination.Home, Destination.Calendar, Destination.Stats, Destination.Periods)

private sealed class ExpenseSheetRequest {
    data class New(val date: LocalDate) : ExpenseSheetRequest()
    data class Edit(val expense: Expense) : ExpenseSheetRequest()
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val repository = (application as GastosApplication).repository
        setContent {
            ControlDeGastosTheme {
                val viewModel: GastosViewModel = viewModel(factory = GastosViewModelFactory(repository))
                GastosApp(viewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun GastosApp(viewModel: GastosViewModel) {
    val navController = rememberNavController()
    var newPeriodRequest by remember { mutableStateOf<Pair<LocalDate, PeriodType>?>(null) }
    var expenseSheetRequest by remember { mutableStateOf<ExpenseSheetRequest?>(null) }
    var showAddIncome by remember { mutableStateOf(false) }
    var showCredits by remember { mutableStateOf(false) }
    var showProfile by remember { mutableStateOf(false) }
    val categories by viewModel.categories.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Control de Gastos") },
                actions = {
                    IconButton(onClick = { showCredits = true }) {
                        Icon(Icons.Filled.Info, contentDescription = "Créditos")
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar {
                val backStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = backStackEntry?.destination
                bottomDestinations.forEach { destination ->
                    val selected = currentDestination?.hierarchy?.any { it.route == destination.route } == true
                    NavigationBarItem(
                        selected = selected,
                        onClick = {
                            navController.navigate(destination.route) {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = {
                            val icon = when (destination) {
                                Destination.Home -> Icons.Filled.Home
                                Destination.Calendar -> Icons.Filled.CalendarToday
                                Destination.Stats -> Icons.Filled.BarChart
                                Destination.Periods -> Icons.Filled.CalendarMonth
                            }
                            Icon(icon, contentDescription = destination.label)
                        },
                        label = { Text(destination.label) }
                    )
                }
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = Destination.Home.route,
            modifier = Modifier.padding(padding),
            enterTransition = { fadeIn(tween(220)) },
            exitTransition = { fadeOut(tween(220)) },
            popEnterTransition = { fadeIn(tween(220)) },
            popExitTransition = { fadeOut(tween(220)) }
        ) {
            composable(Destination.Home.route) {
                HomeScreen(
                    viewModel = viewModel,
                    onAddExpense = { expenseSheetRequest = ExpenseSheetRequest.New(LocalDate.now()) },
                    onAddIncome = { showAddIncome = true },
                    onNewPeriod = { newPeriodRequest = LocalDate.now() to PeriodType.QUINCENAL },
                    onEditExpense = { expense -> expenseSheetRequest = ExpenseSheetRequest.Edit(expense) }
                )
            }
            composable(Destination.Calendar.route) {
                CalendarScreen(
                    viewModel = viewModel,
                    onAddExpenseForDate = { date -> expenseSheetRequest = ExpenseSheetRequest.New(date) },
                    onCreatePeriodForDate = { date, type -> newPeriodRequest = date to type },
                    onEditExpense = { expense -> expenseSheetRequest = ExpenseSheetRequest.Edit(expense) }
                )
            }
            composable(Destination.Stats.route) {
                StatsScreen(viewModel = viewModel)
            }
            composable(Destination.Periods.route) {
                PeriodsScreen(viewModel = viewModel, onNewPeriod = { newPeriodRequest = LocalDate.now() to PeriodType.QUINCENAL })
            }
        }
    }

    newPeriodRequest?.let { (referenceDate, initialType) ->
        NewPeriodSheet(
            referenceDate = referenceDate,
            initialType = initialType,
            onDismiss = { newPeriodRequest = null },
            onCreate = { type, label, income, start, end ->
                viewModel.createPeriod(type, label, income, start, end)
            }
        )
    }

    expenseSheetRequest?.let { request ->
        val expenseToEdit = (request as? ExpenseSheetRequest.Edit)?.expense
        val initialDate = when (request) {
            is ExpenseSheetRequest.New -> request.date
            is ExpenseSheetRequest.Edit -> request.expense.date
        }
        AddExpenseSheet(
            categories = categories,
            initialDate = initialDate,
            expenseToEdit = expenseToEdit,
            onDismiss = { expenseSheetRequest = null },
            onAdd = { categoryId, description, amount, date ->
                viewModel.addExpense(categoryId, description, amount, date)
            },
            onUpdate = { expense, categoryId, description, amount, date ->
                viewModel.updateExpense(expense, categoryId, description, amount, date)
            },
            onCreateCategory = { name, iconKey, colorArgb, onCreated ->
                viewModel.createCategory(name, iconKey, colorArgb, onCreated)
            },
            onDeleteCategory = { category -> viewModel.deleteCategory(category) }
        )
    }

    if (showAddIncome) {
        AddIncomeSheet(
            onDismiss = { showAddIncome = false },
            onAdd = { description, amount, date ->
                viewModel.addIncome(description, amount, date)
            }
        )
    }

    if (showCredits) {
        CreditsSheet(
            onDismiss = { showCredits = false },
            onOpenProfile = {
                showCredits = false
                showProfile = true
            }
        )
    }

    if (showProfile) {
        ProfileSheet(onDismiss = { showProfile = false })
    }
}
