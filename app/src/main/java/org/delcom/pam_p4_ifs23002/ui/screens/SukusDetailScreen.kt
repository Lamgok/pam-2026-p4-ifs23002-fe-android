package org.delcom.pam_p4_ifs23002.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
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

    // Memicu pengambilan data suku berdasarkan ID
    LaunchedEffect(sukuId) {
        sukusViewModel.getSukuById(sukuId)
    }

    // Menangani status aksi (seperti setelah menghapus data)
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

    // State Loading
    if (uiState.suku is SukuUIState.Loading) {
        LoadingUI()
        return
    }

    // State Error
    if (uiState.suku is SukuUIState.Error) {
        LaunchedEffect(Unit) { RouteHelper.back(navController) }
        return
    }

    // Pastikan data sukses dimuat
    val suku = (uiState.suku as? SukuUIState.Success)?.data ?: return

    // Menu item untuk detail (Ubah dan Hapus)
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
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.background)
        ) {
            TopAppBarComponent(
                navController = navController,
                title = suku.nama,
                showBackButton = true,
                customMenuItems = detailMenuItems
            )
            
            Box(modifier = Modifier.weight(1f)) {
                SukusDetailUI(suku = suku)
                
                // Dialog Konfirmasi Hapus
                BottomDialog(
                    type = BottomDialogType.ERROR,
                    show = isConfirmDelete,
                    onDismiss = { isConfirmDelete = false },
                    title = "Konfirmasi Hapus Data",
                    message = "Apakah Anda yakin ingin menghapus data suku '${suku.nama}' ini?",
                    confirmText = "Ya, Hapus",
                    onConfirm = {
                        isConfirmDelete = false
                        sukusViewModel.deleteSuku(suku.id)
                    },
                    cancelText = "Batal",
                    destructiveAction = true
                )
            }
            BottomNavComponent(navController = navController)
        }
    }
}

@Composable
fun SukusDetailUI(suku: ResponseSukusData) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
        ) {
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
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
            )
        }

        DetailContentCard("Deskripsi", suku.deskripsi)
        DetailContentCard("Makanan Khas", suku.makanan)
        DetailContentCard("Rumah Adat", suku.rumahadat)
    }
}
