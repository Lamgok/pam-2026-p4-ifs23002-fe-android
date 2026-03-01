package org.delcom.pam_p4_ifs23002.ui

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import org.delcom.pam_p4_ifs23002.helper.ConstHelper
import org.delcom.pam_p4_ifs23002.ui.components.CustomSnackbar
import org.delcom.pam_p4_ifs23002.ui.screens.HomeScreen
import org.delcom.pam_p4_ifs23002.ui.screens.PlantsAddScreen
import org.delcom.pam_p4_ifs23002.ui.screens.PlantsDetailScreen
import org.delcom.pam_p4_ifs23002.ui.screens.PlantsEditScreen
import org.delcom.pam_p4_ifs23002.ui.screens.PlantsScreen
import org.delcom.pam_p4_ifs23002.ui.screens.ProfileScreen
import org.delcom.pam_p4_ifs23002.ui.screens.SukusScreen
import org.delcom.pam_p4_ifs23002.ui.screens.SukusAddScreen
import org.delcom.pam_p4_ifs23002.ui.screens.SukusDetailScreen
import org.delcom.pam_p4_ifs23002.ui.screens.SukusEditScreen
import org.delcom.pam_p4_ifs23002.ui.viewmodels.PlantViewModel
import org.delcom.pam_p4_ifs23002.ui.viewmodels.SukusViewModel

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun UIApp(
    navController: NavHostController = rememberNavController(),
    plantViewModel: PlantViewModel,
    sukusViewModel: SukusViewModel
) {
    // Inisialisasi SnackbarHostState
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState){ snackbarData ->
            CustomSnackbar(snackbarData, onDismiss = { snackbarHostState.currentSnackbarData?.dismiss() })
        } },
    ) { _ ->
        NavHost(
            navController = navController,
            startDestination = ConstHelper.RouteNames.Home.path,
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF7F8FA))

        ) {
            // Home
            composable(
                route = ConstHelper.RouteNames.Home.path,
            ) { _ ->
                HomeScreen(
                    navController = navController,
                )
            }

            // Profile
            composable(
                route = ConstHelper.RouteNames.Profile.path,
            ) { _ ->
                ProfileScreen(
                    navController = navController,
                    plantViewModel = plantViewModel
                )
            }

            // Plants
            composable(
                route = ConstHelper.RouteNames.Plants.path,
            ) { _ ->
                PlantsScreen(
                    navController = navController,
                    plantViewModel = plantViewModel
                )
            }

            // Plants Add
            composable(
                route = ConstHelper.RouteNames.PlantsAdd.path,
            ) { _ ->
                PlantsAddScreen(
                    navController = navController,
                    snackbarHost = snackbarHostState,
                    plantViewModel = plantViewModel
                )
            }

            // Plants Detail
            composable(
                route = ConstHelper.RouteNames.PlantsDetail.path,
                arguments = listOf(
                    navArgument("plantId") { type = NavType.StringType },
                )
            ) { backStackEntry ->
                val plantId = backStackEntry.arguments?.getString("plantId") ?: ""

                PlantsDetailScreen(
                    navController = navController,
                    snackbarHost = snackbarHostState,
                    plantViewModel = plantViewModel,
                    plantId = plantId
                )
            }

            // Plants Edit
            composable(
                route = ConstHelper.RouteNames.PlantsEdit.path,
                arguments = listOf(
                    navArgument("plantId") { type = NavType.StringType },
                )
            ) { backStackEntry ->
                val plantId = backStackEntry.arguments?.getString("plantId") ?: ""

                PlantsEditScreen(
                    navController = navController,
                    snackbarHost = snackbarHostState,
                    plantViewModel = plantViewModel,
                    plantId = plantId
                )
            }

            // Sukus
            composable(
                route = ConstHelper.RouteNames.Sukus.path,
            ) { _ ->
                SukusScreen(
                    navController = navController,
                    sukusViewModel = sukusViewModel
                )
            }

            // Sukus Add
            composable(
                route = ConstHelper.RouteNames.SukusAdd.path,
            ) { _ ->
                SukusAddScreen(
                    navController = navController,
                    snackbarHost = snackbarHostState,
                    sukusViewModel = sukusViewModel
                )
            }

            // Sukus Detail
            composable(
                route = ConstHelper.RouteNames.SukusDetail.path,
                arguments = listOf(
                    navArgument("sukuId") { type = NavType.StringType },
                )
            ) { backStackEntry ->
                val sukuId = backStackEntry.arguments?.getString("sukuId") ?: ""

                SukusDetailScreen(
                    navController = navController,
                    snackbarHost = snackbarHostState,
                    sukusViewModel = sukusViewModel,
                    sukuId = sukuId
                )
            }

            // Sukus Edit
            composable(
                route = ConstHelper.RouteNames.SukusEdit.path,
                arguments = listOf(
                    navArgument("sukuId") { type = NavType.StringType },
                )
            ) { backStackEntry ->
                val sukuId = backStackEntry.arguments?.getString("sukuId") ?: ""

                SukusEditScreen(
                    navController = navController,
                    snackbarHost = snackbarHostState,
                    sukusViewModel = sukusViewModel,
                    sukuId = sukuId
                )
            }
        }
    }

}
