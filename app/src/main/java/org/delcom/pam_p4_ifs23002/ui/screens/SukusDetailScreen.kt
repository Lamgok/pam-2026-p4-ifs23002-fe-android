package org.delcom.pam_p4_ifs23002.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import org.delcom.pam_p4_ifs23002.R
import org.delcom.pam_p4_ifs23002.helper.ConstHelper
import org.delcom.pam_p4_ifs23002.helper.RouteHelper
import org.delcom.pam_p4_ifs23002.helper.SuspendHelper
import org.delcom.pam_p4_ifs23002.helper.SuspendHelper.SnackBarType
import org.delcom.pam_p4_ifs23002.helper.ToolsHelper
import org.delcom.pam_p4_ifs23002.network.sukus.data.ResponseSukusData
import org.delcom.pam_p4_ifs23002.ui.components.BottomDialog
import org.delcom.pam_p4_ifs23002.ui.components.BottomDialogType
import org.delcom.pam_p4_ifs23002.ui.components.BottomNavComponent
import org.delcom.pam_p4_ifs23002.ui.components.LoadingUI
import org.delcom.pam_p4_ifs23002.ui.components.TopAppBarComponent
import org.delcom.pam_p4_ifs23002.ui.components.TopAppBarMenuItem
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
    // Ambil data dari viewmodel
    val uiState by sukusViewModel.uiState.collectAsState()
    var isLoading by remember { mutableStateOf(false) }
    var isConfirmDelete by remember { mutableStateOf(false) }

    // Muat data
    var suku by remember { mutableStateOf<ResponseSukusData?>(null) }

    // Dapatkan suku berdasarkan ID
    LaunchedEffect(Unit) {
        isLoading = true
        // Reset status suku action
        sukusViewModel.getSukuById(sukuId)
    }

    // Picu ulang ketika data suku berubah
    LaunchedEffect(uiState.suku) {
        if(uiState.suku !is SukuUIState.Loading){
            if(uiState.suku is SukuUIState.Success){
                suku = (uiState.suku as SukuUIState.Success).data
                isLoading = false
            } else {
                RouteHelper.back(navController)
            }
        }
    }

    fun onDelete(){
        isLoading = true
        sukusViewModel.deleteSuku(sukuId)
    }

    LaunchedEffect(uiState.sukuAction) {
        when (val state = uiState.sukuAction) {
            is SukuActionUIState.Success -> {
                SuspendHelper.showSnackBar(
                    snackbarHost = snackbarHost,
                    type = SnackBarType.SUCCESS,
                    message = state.message
                )
                RouteHelper.to(
                    navController,
                    ConstHelper.RouteNames.Sukus.path,
                    true
                )
                isLoading = false
            }
            is SukuActionUIState.Error -> {
                SuspendHelper.showSnackBar(
                    snackbarHost = snackbarHost,
                    type = SnackBarType.ERROR,
                    message = state.message
                )
                isLoading = false
            }
            else -> {}
        }
    }

    // Tampilkan halaman loading
    if(isLoading || suku == null){
        LoadingUI()
        return
    }

    // Menu item details
    val detailMenuItems = listOf(
        TopAppBarMenuItem(
            text = "Ubah Data",
            icon = Icons.Filled.Edit,
            route = null,
            onClick = {
                RouteHelper.to(
                    navController,
                    ConstHelper.RouteNames.SukusEdit.path
                        .replace("{sukuId}", suku!!.id),
                )
            }
        ),
        TopAppBarMenuItem(
            text = "Hapus Data",
            icon = Icons.Filled.Delete,
            route = null,
            onClick = {
                isConfirmDelete = true
            }
        ),
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
    )
    {
        // Top App Bar
        TopAppBarComponent(
            navController = navController,
            title = suku!!.nama,
            showBackButton = true,
            customMenuItems = detailMenuItems
        )
        // Content
        Box(
            modifier = Modifier
                .weight(1f)
        ) {
            // Content UI
            SukusDetailUI(
                suku = suku!!
            )
            // Bottom Dialog to Confirmation Delete
            BottomDialog(
                type = BottomDialogType.ERROR,
                show = isConfirmDelete,
                onDismiss = { isConfirmDelete = false },
                title = "Konfirmasi Hapus Data",
                message = "Apakah Anda yakin ingin menghapus data ini?",
                confirmText = "Ya, Hapus",
                onConfirm = {
                    onDelete()
                },
                cancelText = "Batal",
                destructiveAction = true
            )
        }
        // Bottom Nav
        BottomNavComponent(navController = navController)
    }
}

@Composable
fun SukusDetailUI(
    suku: ResponseSukusData
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    )
    {
        // Gambar
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 16.dp)
        )
        {
            AsyncImage(
                model = ToolsHelper.getSukusImageUrl(suku.id),
                contentDescription = suku.nama,
                placeholder = painterResource(R.drawable.img_placeholder),
                error = painterResource(R.drawable.img_placeholder),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp),
                contentScale = ContentScale.Fit
            )

            Text(
                text = suku.nama,
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Deskripsi
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            shape = MaterialTheme.shapes.medium,
            elevation = CardDefaults.cardElevation(4.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        )
        {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = "Deskripsi",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold
                )
                HorizontalDivider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp)
                )
                Text(
                    text = suku.deskripsi,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }

        // Makanan Khas
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            shape = MaterialTheme.shapes.medium,
            elevation = CardDefaults.cardElevation(4.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        )
        {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = "Makanan Khas",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold
                )
                HorizontalDivider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp)
                )
                Text(
                    text = suku.makanan,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }

        // Rumah Adat
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            shape = MaterialTheme.shapes.medium,
            elevation = CardDefaults.cardElevation(4.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        )
        {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = "Rumah Adat",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold
                )
                HorizontalDivider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp)
                )
                Text(
                    text = suku.rumahadat,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}
