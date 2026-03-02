package org.delcom.pam_p4_ifs23002.ui.screens

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import org.delcom.pam_p4_ifs23002.helper.ConstHelper
import org.delcom.pam_p4_ifs23002.helper.RouteHelper
import org.delcom.pam_p4_ifs23002.helper.SuspendHelper
import org.delcom.pam_p4_ifs23002.helper.ToolsHelper
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
        if (uiState.suku is SukuUIState.Success) {
            suku = (uiState.suku as SukuUIState.Success).data
            isLoading = false
        } else if (uiState.suku is SukuUIState.Error) {
            RouteHelper.back(navController)
        }
    }

    fun onSave(
        context: Context,
        nama: String,
        deskripsi: String,
        makanan: String,
        rumahadat: String,
        file: Uri?
    ) {
        isLoading = true
        val filePart = file?.let { ToolsHelper.uriToMultipart(context, it, "file") }
        sukusViewModel.putSuku(sukuId, nama.toRequestBodyText(), deskripsi.toRequestBodyText(), makanan.toRequestBodyText(), rumahadat.toRequestBodyText(), filePart)
    }

    LaunchedEffect(uiState.sukuAction) {
        when (val state = uiState.sukuAction) {
            is SukuActionUIState.Success -> {
                SuspendHelper.showSnackBar(snackbarHost, SuspendHelper.SnackBarType.SUCCESS, "Data suku berhasil diperbarui")
                RouteHelper.to(navController, ConstHelper.RouteNames.Sukus.path, true)
                isLoading = false
            }
            is SukuActionUIState.Error -> {
                SuspendHelper.showSnackBar(snackbarHost, SuspendHelper.SnackBarType.ERROR, state.message)
                isLoading = false
            }
            else -> {}
        }
    }

    if (isLoading || suku == null) {
        LoadingUI()
        return
    }

    Scaffold(
        topBar = { TopAppBarComponent(navController = navController, title = "Ubah Budaya", showBackButton = true) },
        bottomBar = { BottomNavComponent(navController = navController) }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize().background(Color(0xFFFFF8F0))) {
            SukusEditUI(suku = suku!!, onSave = ::onSave)
        }
    }
}

@Composable
fun SukusEditUI(
    suku: ResponseSukusData,
    onSave: (Context, String, String, String, String, Uri?) -> Unit
) {
    var dataFile by remember { mutableStateOf<Uri?>(null) }
    var dataNama by remember { mutableStateOf(suku.nama) }
    var dataDeskripsi by remember { mutableStateOf(suku.deskripsi) }
    var dataMakanan by remember { mutableStateOf(suku.makanan) }
    var dataRumahAdat by remember { mutableStateOf(suku.rumahadat) }
    
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val deskripsiFocus = remember { FocusRequester() }
    val makananFocus = remember { FocusRequester() }
    val rumahadatFocus = remember { FocusRequester() }

    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri -> dataFile = uri }
    )

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                Box(
                    modifier = Modifier.size(180.dp).clip(RoundedCornerShape(24.dp)).background(Color.White).clickable {
                        imagePicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                    }.padding(4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    AsyncImage(
                        model = dataFile ?: ToolsHelper.getSukusImageUrl(suku.id),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(20.dp))
                    )
                    if (dataFile == null) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(imageVector = Icons.Default.AddPhotoAlternate, contentDescription = null, tint = Color(0xFFB22222), modifier = Modifier.size(48.dp))
                            Spacer(Modifier.height(8.dp))
                            Text("Ganti Gambar", color = Color(0xFFB22222), fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            CustomOutlinedTextField(value = dataNama, onValueChange = { dataNama = it }, label = "Nama Suku", imeAction = ImeAction.Next, keyboardActions = KeyboardActions(onNext = { deskripsiFocus.requestFocus() }))
            CustomOutlinedTextField(value = dataDeskripsi, onValueChange = { dataDeskripsi = it }, label = "Deskripsi Budaya", modifier = Modifier.height(150.dp).focusRequester(deskripsiFocus), singleLine = false, imeAction = ImeAction.Next, keyboardActions = KeyboardActions(onNext = { makananFocus.requestFocus() }))
            CustomOutlinedTextField(value = dataMakanan, onValueChange = { dataMakanan = it }, label = "Makanan Khas", modifier = Modifier.focusRequester(makananFocus), imeAction = ImeAction.Next, keyboardActions = KeyboardActions(onNext = { rumahadatFocus.requestFocus() }))
            CustomOutlinedTextField(value = dataRumahAdat, onValueChange = { dataRumahAdat = it }, label = "Rumah Adat", modifier = Modifier.focusRequester(rumahadatFocus), imeAction = ImeAction.Done, keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }))
            
            Spacer(modifier = Modifier.height(80.dp))
        }

        FloatingActionButton(
            onClick = { onSave(context, dataNama, dataDeskripsi, dataMakanan, dataRumahAdat, dataFile) },
            modifier = Modifier.align(Alignment.BottomEnd).padding(24.dp),
            containerColor = Color(0xFFB22222),
            contentColor = Color.White
        ) {
            Icon(Icons.Default.Save, contentDescription = "Simpan Perubahan")
        }
    }
}
