package org.delcom.pam_p4_ifs23002.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
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

    // Memastikan pengambilan data dilakukan saat layar dimuat atau sukuId berubah
    LaunchedEffect(sukuId) {
        sukusViewModel.getSukuById(sukuId)
    }

    // Penanganan Aksi (Edit/Hapus)
    LaunchedEffect(uiState.sukuAction) {
        when (val state = uiState.sukuAction) {
            is SukuActionUIState.Success -> {
                SuspendHelper.showSnackBar(snackbarHost, SnackBarType.SUCCESS, state.message)
                // Kembali ke daftar suku setelah aksi berhasil
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
        val errorMsg = (uiState.suku as SukuUIState.Error).message
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Gagal memuat data: $errorMsg", color = MaterialTheme.colorScheme.error)
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = { RouteHelper.back(navController) }) {
                    Text("Kembali")
                }
            }
        }
        return
    }

    // Pastikan data sukses dimuat
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
                SukusDetailUI(suku = suku)

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
                .padding(vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
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