package org.delcom.pam_p4_ifs23002.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import org.delcom.pam_p4_ifs23002.R
import org.delcom.pam_p4_ifs23002.helper.ConstHelper
import org.delcom.pam_p4_ifs23002.helper.RouteHelper
import org.delcom.pam_p4_ifs23002.helper.ToolsHelper
import org.delcom.pam_p4_ifs23002.network.sukus.data.ResponseSukusData
import org.delcom.pam_p4_ifs23002.ui.components.BottomNavComponent
import org.delcom.pam_p4_ifs23002.ui.components.LoadingUI
import org.delcom.pam_p4_ifs23002.ui.components.TopAppBarComponent
import org.delcom.pam_p4_ifs23002.ui.theme.DelcomTheme
import org.delcom.pam_p4_ifs23002.ui.viewmodels.SukusUIState
import org.delcom.pam_p4_ifs23002.ui.viewmodels.SukusViewModel

@Composable
fun SukusScreen(
    navController: NavHostController,
    sukusViewModel: SukusViewModel
) {
    val uiStateSuku by sukusViewModel.uiState.collectAsState()
    var searchQuery by remember { mutableStateOf(TextFieldValue("")) }

    LaunchedEffect(Unit) {
        sukusViewModel.getAllSukus(searchQuery.text)
    }

    if (uiStateSuku.sukus is SukusUIState.Loading) {
        LoadingUI()
        return
    }

    val sukus = (uiStateSuku.sukus as? SukusUIState.Success)?.data ?: emptyList()

    DelcomTheme {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.background)
        ) {
            TopAppBarComponent(
                navController = navController,
                title = "Suku",
                showBackButton = false,
                withSearch = true,
                searchQuery = searchQuery,
                onSearchQueryChange = { searchQuery = it },
                onSearchAction = { sukusViewModel.getAllSukus(searchQuery.text) }
            )

            Box(modifier = Modifier.weight(1f)) {
                if (sukus.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(text = "Tidak ada data!", style = MaterialTheme.typography.bodyMedium)
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        item { Spacer(modifier = Modifier.height(8.dp)) }
                        items(sukus) { suku ->
                            SukuItemUI(
                                suku = suku,
                                onClick = {
                                    // PERBAIKAN: Gunakan format rute yang benar sesuai ConstHelper
                                    val route = ConstHelper.RouteNames.SukusDetail.path.replace("{sukuId}", suku.id)
                                    RouteHelper.to(navController, route)
                                }
                            )
                        }
                        item { Spacer(modifier = Modifier.height(8.dp)) }
                    }
                }

                FloatingActionButton(
                    onClick = { RouteHelper.to(navController, ConstHelper.RouteNames.SukusAdd.path) },
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(16.dp),
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Tambah Suku")
                }
            }
            BottomNavComponent(navController = navController)
        }
    }
}

@Composable
fun SukuItemUI(
    suku: ResponseSukusData,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable(onClick = onClick),
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = ToolsHelper.getSukusImageUrl(suku.id),
                contentDescription = suku.nama,
                placeholder = painterResource(R.drawable.img_placeholder),
                error = painterResource(R.drawable.img_placeholder),
                modifier = Modifier
                    .size(80.dp)
                    .clip(MaterialTheme.shapes.medium),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = suku.nama,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = suku.deskripsi,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}