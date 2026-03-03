package org.delcom.pam_p4_ifs23002.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import org.delcom.pam_p4_ifs23002.R
import org.delcom.pam_p4_ifs23002.helper.ConstHelper
import org.delcom.pam_p4_ifs23002.helper.RouteHelper
import org.delcom.pam_p4_ifs23002.helper.SuspendHelper
import org.delcom.pam_p4_ifs23002.helper.SuspendHelper.SnackBarType
import org.delcom.pam_p4_ifs23002.helper.ToolsHelper
import org.delcom.pam_p4_ifs23002.network.sukus.data.ResponseSukusData
import org.delcom.pam_p4_ifs23002.ui.components.*
import org.delcom.pam_p4_ifs23002.ui.theme.DelcomTheme
import org.delcom.pam_p4_ifs23002.ui.viewmodels.SukuActionUIState
import org.delcom.pam_p4_ifs23002.ui.viewmodels.SukuUIState
import org.delcom.pam_p4_ifs23002.ui.viewmodels.SukusViewModel

@Composable
fun SukusDetailScreen(
    navController: NavHostController,
    snackbarHost: SnackbarHostState,
    sukusViewModel: SukusViewModel,
    sukuId: String
) {
    val uiState by sukusViewModel.uiState.collectAsState()
    var isConfirmDelete by remember { mutableStateOf(false) }

    LaunchedEffect(sukuId) {
        sukusViewModel.getSukuById(sukuId)
    }

    LaunchedEffect(uiState.sukuAction) {
        when (val state = uiState.sukuAction) {
            is SukuActionUIState.Success -> {
                SuspendHelper.showSnackBar(snackbarHost, SnackBarType.SUCCESS, state.message)
                RouteHelper.to(navController, ConstHelper.RouteNames.Sukus.path, true)
                sukusViewModel.resetSukuAction()
            }
            is SukuActionUIState.Error -> {
                SuspendHelper.showSnackBar(snackbarHost, SnackBarType.ERROR, state.message)
                sukusViewModel.resetSukuAction()
            }
            else -> {}
        }
    }

    if (uiState.suku is SukuUIState.Loading) {
        LoadingUI()
        return
    }

    if (uiState.suku is SukuUIState.Error) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Button(onClick = { RouteHelper.back(navController) }) { Text("Kembali") }
        }
        return
    }

    val suku = (uiState.suku as? SukuUIState.Success)?.data ?: return

    val detailMenuItems = listOf(
        TopAppBarMenuItem(
            text = "Ubah Data",
            icon = Icons.Filled.Edit,
            onClick = {
                val route = ConstHelper.RouteNames.SukusEdit.path.replace("{sukuId}", suku.id)
                RouteHelper.to(navController, route)
            }
        ),
        TopAppBarMenuItem(
            text = "Hapus Data",
            icon = Icons.Filled.Delete,
            onClick = { isConfirmDelete = true },
            isDestructive = true
        ),
    )

    DelcomTheme {
        Scaffold(
            topBar = {
                TopAppBarComponent(
                    navController = navController,
                    title = suku.nama,
                    showBackButton = true,
                    customMenuItems = detailMenuItems
                )
            },
            bottomBar = { BottomNavComponent(navController = navController) }
        ) { padding ->
            Box(modifier = Modifier.padding(padding).fillMaxSize()) {
                SukusDetailContent(suku = suku)

                BottomDialog(
                    type = BottomDialogType.ERROR,
                    show = isConfirmDelete,
                    onDismiss = { isConfirmDelete = false },
                    title = "Hapus Warisan Budaya?",
                    message = "Data suku '${suku.nama}' akan dihapus secara permanen. Lanjutkan?",
                    confirmText = "Ya, Hapus",
                    onConfirm = {
                        isConfirmDelete = false
                        sukusViewModel.deleteSuku(suku.id)
                    },
                    cancelText = "Batal",
                    destructiveAction = true
                )
            }
        }
    }
}

@Composable
fun SukusDetailContent(suku: ResponseSukusData) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        // Area Gambar dengan Overlay Gradient
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
        ) {
            AsyncImage(
                model = ToolsHelper.getSukusImageUrl(suku.id),
                contentDescription = suku.nama,
                placeholder = painterResource(R.drawable.img_placeholder),
                error = painterResource(R.drawable.img_placeholder),
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            listOf(Color.Transparent, Color.Black.copy(alpha = 0.6f))
                        )
                    )
            )
            Text(
                text = suku.nama,
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                ),
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(24.dp)
            )
        }

        // Konten Informasi
        Column(
            modifier = Modifier
                .padding(16.dp)
                .offset(y = (-20).dp)
                .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                .background(MaterialTheme.colorScheme.surface)
                .padding(20.dp)
        ) {
            InfoSection(
                title = "Tentang Suku",
                content = suku.deskripsi,
                icon = Icons.Default.Info
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Row(modifier = Modifier.fillMaxWidth()) {
                Box(modifier = Modifier.weight(1f)) {
                    InfoCardSmall(
                        title = "Rumah Adat",
                        content = suku.rumahadat,
                        icon = Icons.Default.Home
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Box(modifier = Modifier.weight(1f)) {
                    InfoCardSmall(
                        title = "Makanan Khas",
                        content = suku.makanan,
                        icon = Icons.Default.Fastfood
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
fun InfoSection(title: String, content: String, icon: ImageVector) {
    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = content, style = MaterialTheme.typography.bodyLarge, textAlign = TextAlign.Justify, lineHeight = 24.sp)
    }
}

@Composable
fun InfoCardSmall(title: String, content: String, icon: ImageVector) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Icon(imageVector = icon, contentDescription = null, tint = MaterialTheme.colorScheme.secondary, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.height(12.dp))
            Text(text = title, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.secondary, fontWeight = FontWeight.Bold)
            Text(text = content, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
        }
    }
}
