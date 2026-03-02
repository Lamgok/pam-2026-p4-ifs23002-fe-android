package org.delcom.pam_p4_ifs23002.ui.screens

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import org.delcom.pam_p4_ifs23002.R
import org.delcom.pam_p4_ifs23002.helper.*
import org.delcom.pam_p4_ifs23002.helper.SuspendHelper.SnackBarType
import org.delcom.pam_p4_ifs23002.helper.ToolsHelper.toRequestBodyText
import org.delcom.pam_p4_ifs23002.network.sukus.data.ResponseSukusData
import org.delcom.pam_p4_ifs23002.ui.components.BottomNavComponent
import org.delcom.pam_p4_ifs23002.ui.components.LoadingUI
import org.delcom.pam_p4_ifs23002.ui.components.TopAppBarComponent
import org.delcom.pam_p4_ifs23002.ui.viewmodels.SukuActionUIState
import org.delcom.pam_p4_ifs23002.ui.viewmodels.SukuUIState
import org.delcom.pam_p4_ifs23002.ui.viewmodels.SukusViewModel

@Composable
fun SukusEditScreen(
    navController: NavHostController,
    snackbarHost: SnackbarHostState,
    sukusViewModel: SukusViewModel,
    sukuId: String
) {
    val uiState by sukusViewModel.uiState.collectAsState()
    var isLoading by remember { mutableStateOf(false) }
    var suku by remember { mutableStateOf<ResponseSukusData?>(null) }

    LaunchedEffect(Unit) {
        isLoading = true
        sukusViewModel.getSukuById(sukuId)
    }

    LaunchedEffect(uiState.suku) {
        if (uiState.suku !is SukuUIState.Loading) {
            if (uiState.suku is SukuUIState.Success) {
                suku = (uiState.suku as SukuUIState.Success).data
                isLoading = false
            } else {
                RouteHelper.back(navController)
                isLoading = false
            }
        }
    }

    fun onSave(context: Context, nama: String, deskripsi: String, makanan: String, rumah: String, file: Uri?) {
        isLoading = true
        val filePart = file?.let { ToolsHelper.uriToMultipart(context, it, "file") }
        sukusViewModel.putSuku(sukuId, nama.toRequestBodyText(), deskripsi.toRequestBodyText(), makanan.toRequestBodyText(), rumah.toRequestBodyText(), filePart)
    }

    LaunchedEffect(uiState.sukuAction) {
        when (val state = uiState.sukuAction) {
            is SukuActionUIState.Success -> {
                SuspendHelper.showSnackBar(snackbarHost, SnackBarType.SUCCESS, state.message)
                RouteHelper.to(navController, ConstHelper.RouteNames.SukusDetail.path.replace("{sukuId}", sukuId), true)
                isLoading = false
            }
            is SukuActionUIState.Error -> {
                SuspendHelper.showSnackBar(snackbarHost, SnackBarType.ERROR, state.message)
                isLoading = false
            }
            else -> {}
        }
    }

    if (isLoading || suku == null) {
        LoadingUI()
        return
    }

    Column(modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.background)) {
        TopAppBarComponent(navController = navController, title = "Ubah Data", showBackButton = true)
        Box(modifier = Modifier.weight(1f)) {
            SukusEditUI(suku = suku!!, onSave = ::onSave)
        }
        BottomNavComponent(navController = navController)
    }
}

@Composable
fun SukusEditUI(suku: ResponseSukusData, onSave: (Context, String, String, String, String, Uri?) -> Unit) {
    val alertState = remember { mutableStateOf(AlertState()) }
    var dataFile by remember { mutableStateOf<Uri?>(null) }
    var dataNama by remember { mutableStateOf(suku.nama) }
    var dataDeskripsi by remember { mutableStateOf(suku.deskripsi) }
    var dataMakanan by remember { mutableStateOf(suku.makanan) }
    var dataRumah by remember { mutableStateOf(suku.rumahadat) }

    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val imagePicker = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { dataFile = it }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                Box(modifier = Modifier.size(150.dp).clip(RoundedCornerShape(12.dp)).background(MaterialTheme.colorScheme.primaryContainer).clickable {
                    imagePicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                }, contentAlignment = Alignment.Center) {
                    AsyncImage(model = dataFile ?: ToolsHelper.getSukusImageUrl(suku.id), contentDescription = null, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
                }
                Text(text = "Tap untuk mengganti gambar", style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(top = 8.dp))
            }

            val fieldColors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = MaterialTheme.colorScheme.onPrimaryContainer,
                unfocusedTextColor = MaterialTheme.colorScheme.onPrimaryContainer,
                focusedBorderColor = MaterialTheme.colorScheme.primaryContainer,
                unfocusedBorderColor = MaterialTheme.colorScheme.onPrimaryContainer,
            )

            OutlinedTextField(value = dataNama, onValueChange = { dataNama = it }, label = { Text("Nama Suku") }, colors = fieldColors, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = dataDeskripsi, onValueChange = { dataDeskripsi = it }, label = { Text("Deskripsi") }, colors = fieldColors, modifier = Modifier.fillMaxWidth().height(120.dp), maxLines = 5)
            OutlinedTextField(value = dataMakanan, onValueChange = { dataMakanan = it }, label = { Text("Makanan Khas") }, colors = fieldColors, modifier = Modifier.fillMaxWidth().height(120.dp), maxLines = 5)
            OutlinedTextField(value = dataRumah, onValueChange = { dataRumah = it }, label = { Text("Rumah Adat") }, colors = fieldColors, modifier = Modifier.fillMaxWidth().height(120.dp), maxLines = 5)
            Spacer(modifier = Modifier.height(64.dp))
        }

        FloatingActionButton(onClick = {
            if (dataNama.isEmpty()) AlertHelper.show(alertState, AlertType.ERROR, "Nama tidak boleh kosong!")
            else onSave(context, dataNama, dataDeskripsi, dataMakanan, dataRumah, dataFile)
        }, modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp), containerColor = MaterialTheme.colorScheme.primary) {
            Icon(Icons.Default.Save, contentDescription = null)
        }
    }

    if (alertState.value.isVisible) {
        AlertDialog(onDismissRequest = { AlertHelper.dismiss(alertState) }, title = { Text(alertState.value.type.title) }, text = { Text(alertState.value.message) }, confirmButton = { TextButton(onClick = { AlertHelper.dismiss(alertState) }) { Text("OK") } })
    }
}