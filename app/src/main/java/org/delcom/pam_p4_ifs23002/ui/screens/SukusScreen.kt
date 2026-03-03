package org.delcom.pam_p4_ifs23002.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
        Scaffold(
            topBar = {
                TopAppBarComponent(
                    navController = navController,
                    title = "Eksplorasi Suku",
                    showBackButton = false,
                    withSearch = true,
                    searchQuery = searchQuery,
                    onSearchQueryChange = { searchQuery = it },
                    onSearchAction = { sukusViewModel.getAllSukus(searchQuery.text) }
                )
            },
            bottomBar = { BottomNavComponent(navController = navController) },
            floatingActionButton = {
                LargeFloatingActionButton(
                    onClick = { RouteHelper.to(navController, ConstHelper.RouteNames.SukusAdd.path) },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Tambah Suku", modifier = Modifier.size(30.dp))
                }
            }
        ) { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.surface,
                                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                            )
                        )
                    )
            ) {
                if (sukus.isEmpty()) {
                    EmptyStateUI()
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(sukus) { suku ->
                            SukuItemUI(
                                suku = suku,
                                onClick = {
                                    val route = ConstHelper.RouteNames.SukusDetail.path.replace("{sukuId}", suku.id)
                                    RouteHelper.to(navController, route)
                                }
                            )
                        }
                        item { Spacer(modifier = Modifier.height(80.dp)) }
                    }
                }
            }
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
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Box Gambar dengan Shadow halus
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(MaterialTheme.colorScheme.secondaryContainer)
            ) {
                AsyncImage(
                    model = ToolsHelper.getSukusImageUrl(suku.id),
                    contentDescription = suku.nama,
                    placeholder = painterResource(R.drawable.img_placeholder),
                    error = painterResource(R.drawable.img_placeholder),
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = suku.nama,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 0.5.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                Text(
                    text = suku.deskripsi,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(vertical = 4.dp)
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    SukuBadge(icon = Icons.Default.Fastfood, text = "Kuliner")
                    SukuBadge(icon = Icons.Default.Home, text = "Arsitektur")
                }
            }

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.outline,
                modifier = Modifier.padding(start = 4.dp)
            )
        }
    }
}

@Composable
fun SukuBadge(icon: ImageVector, text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f))
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(12.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun EmptyStateUI() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            painter = painterResource(id = R.drawable.img_placeholder),
            contentDescription = null,
            modifier = Modifier.size(120.dp),
            tint = MaterialTheme.colorScheme.outlineVariant
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Belum ada data suku",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.outline
        )
    }
}
