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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import org.delcom.pam_p4_ifs23002.R
import org.delcom.pam_p4_ifs23002.helper.AlertHelper
import org.delcom.pam_p4_ifs23002.helper.AlertState
import org.delcom.pam_p4_ifs23002.helper.AlertType
import org.delcom.pam_p4_ifs23002.helper.ConstHelper
import org.delcom.pam_p4_ifs23002.helper.RouteHelper
import org.delcom.pam_p4_ifs23002.helper.SuspendHelper
import org.delcom.pam_p4_ifs23002.helper.ToolsHelper.toRequestBodyText
import org.delcom.pam_p4_ifs23002.helper.ToolsHelper.uriToMultipart
import org.delcom.pam_p4_ifs23002.network.sukus.data.ResponseSukusData
import org.delcom.pam_p4_ifs23002.ui.components.BottomNavComponent
import org.delcom.pam_p4_ifs23002.ui.components.LoadingUI
import org.delcom.pam_p4_ifs23002.ui.components.TopAppBarComponent
import org.delcom.pam_p4_ifs23002.ui.viewmodels.SukuActionUIState
import org.delcom.pam_p4_ifs23002.ui.viewmodels.SukusViewModel

@Composable
fun SukusAddScreen(
    navController: NavHostController,
    snackbarHost: SnackbarHostState,
    sukusViewModel: SukusViewModel
) {
    val uiStateSuku by sukusViewModel.uiState.collectAsState()
    var isLoading by remember { mutableStateOf(false) }
    var tmpSuku by remember { mutableStateOf<ResponseSukusData?>(null) }

    LaunchedEffect(Unit) {
        uiStateSuku.sukuAction = SukuActionUIState.Idle
    }

    fun onSave(
        context: Context,
        nama: String,
        deskripsi: String,
        makanan: String,
        rumahAdat: String,
        file: Uri
    ) {
        isLoading = true
        tmpSuku = ResponseSukusData(
            nama = nama,
            deskripsi = deskripsi,
            makanan = makanan,
            rumahadat = rumahAdat,
            id = "",
            createdAt = "",
            updatedAt = ""
        )

        val namaBody = nama.toRequestBodyText()
        val deskripsiBody = deskripsi.toRequestBodyText()
        val makananBody = makanan.toRequestBodyText()
        val rumahAdatBody = rumahAdat.toRequestBodyText()
        val filePart = uriToMultipart(context, file, "file")

        sukusViewModel.postSuku(
            nama = namaBody,
            deskripsi = deskripsiBody,
            makanan = makananBody,
            rumahadat = rumahAdatBody,
            file = filePart,
        )
    }

    LaunchedEffect(uiStateSuku.sukuAction) {
        when (val state = uiStateSuku.sukuAction) {
            is SukuActionUIState.Success -> {
                SuspendHelper.showSnackBar(
                    snackbarHost = snackbarHost,
                    type = SuspendHelper.SnackBarType.SUCCESS,
                    message = state.message
                )
                RouteHelper.to(navController, ConstHelper.RouteNames.Sukus.path, true)
                isLoading = false
            }
            is SukuActionUIState.Error -> {
                SuspendHelper.showSnackBar(
                    snackbarHost = snackbarHost,
                    type = SuspendHelper.SnackBarType.ERROR,
                    message = state.message
                )
                isLoading = false
            }
            else -> {}
        }
    }

    if (isLoading) {
        LoadingUI()
        return
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
    ) {
        TopAppBarComponent(
            navController = navController,
            title = "Tambah Data Suku",
            showBackButton = true,
        )
        Box(modifier = Modifier.weight(1f)) {
            SukusAddUI(
                tmpSuku = tmpSuku,
                onSave = ::onSave
            )
        }
        BottomNavComponent(navController = navController)
    }
}

@Composable
fun SukusAddUI(
    tmpSuku: ResponseSukusData?,
    onSave: (Context, String, String, String, String, Uri) -> Unit
) {
    val alertState = remember { mutableStateOf(AlertState()) }
    var dataFile by remember { mutableStateOf<Uri?>(null) }
    var dataNama by remember { mutableStateOf(tmpSuku?.nama ?: "") }
    var dataDeskripsi by remember { mutableStateOf(tmpSuku?.deskripsi ?: "") }
    var dataMakanan by remember { mutableStateOf(tmpSuku?.makanan ?: "") }
    var dataRumahAdat by remember { mutableStateOf(tmpSuku?.rumahadat ?: "") }
    val context = LocalContext.current

    val focusManager = LocalFocusManager.current
    val deskripsiFocus = remember { FocusRequester() }
    val makananFocus = remember { FocusRequester() }
    val rumahAdatFocus = remember { FocusRequester() }

    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri: Uri? -> dataFile = uri }
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .size(150.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .clickable { imagePicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) },
                contentAlignment = Alignment.Center
            ) {
                if (dataFile != null) {
                    AsyncImage(
                        model = dataFile,
                        contentDescription = "Pratinjau Gambar",
                        placeholder = painterResource(R.drawable.img_placeholder),
                        error = painterResource(R.drawable.img_placeholder),
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Text("Pilih Gambar", color = MaterialTheme.colorScheme.onPrimaryContainer)
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text("Tap untuk memilih gambar", style = MaterialTheme.typography.bodySmall)
        }

        OutlinedTextField(
            value = dataNama,
            onValueChange = { dataNama = it },
            label = { Text("Nama Suku") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            keyboardActions = KeyboardActions(onNext = { deskripsiFocus.requestFocus() })
        )

        OutlinedTextField(
            value = dataDeskripsi,
            onValueChange = { dataDeskripsi = it },
            label = { Text("Deskripsi") },
            modifier = Modifier.fillMaxWidth().height(120.dp).focusRequester(deskripsiFocus),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            keyboardActions = KeyboardActions(onNext = { makananFocus.requestFocus() }),
            maxLines = 5, minLines = 3
        )

        OutlinedTextField(
            value = dataMakanan,
            onValueChange = { dataMakanan = it },
            label = { Text("Makanan Khas") },
            modifier = Modifier.fillMaxWidth().height(120.dp).focusRequester(makananFocus),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            keyboardActions = KeyboardActions(onNext = { rumahAdatFocus.requestFocus() }),
            maxLines = 5, minLines = 3
        )

        OutlinedTextField(
            value = dataRumahAdat,
            onValueChange = { dataRumahAdat = it },
            label = { Text("Rumah Adat") },
            modifier = Modifier.fillMaxWidth().height(120.dp).focusRequester(rumahAdatFocus),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
            maxLines = 5, minLines = 3
        )

        Spacer(modifier = Modifier.height(64.dp))
    }

    Box(modifier = Modifier.fillMaxSize()) {
        FloatingActionButton(
            onClick = {
                if (dataFile == null) {
                    AlertHelper.show(alertState, AlertType.ERROR, "Gambar tidak boleh kosong!")
                    return@FloatingActionButton
                }
                if (dataNama.isEmpty()) {
                    AlertHelper.show(alertState, AlertType.ERROR, "Nama tidak boleh kosong!")
                    return@FloatingActionButton
                }
                if (dataDeskripsi.isEmpty()) {
                    AlertHelper.show(alertState, AlertType.ERROR, "Deskripsi tidak boleh kosong!")
                    return@FloatingActionButton
                }
                if (dataMakanan.isEmpty()) {
                    AlertHelper.show(alertState, AlertType.ERROR, "Makanan khas tidak boleh kosong!")
                    return@FloatingActionButton
                }
                if (dataRumahAdat.isEmpty()) {
                    AlertHelper.show(alertState, AlertType.ERROR, "Rumah adat tidak boleh kosong!")
                    return@FloatingActionButton
                }
                onSave(context, dataNama, dataDeskripsi, dataMakanan, dataRumahAdat, dataFile!!)
            },
            modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp),
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        ) {
            Icon(Icons.Default.Save, contentDescription = "Simpan Data")
        }
    }

    if (alertState.value.isVisible) {
        AlertDialog(
            onDismissRequest = { AlertHelper.dismiss(alertState) },
            title = { Text(alertState.value.type.title) },
            text = { Text(alertState.value.message) },
            confirmButton = {
                TextButton(onClick = { AlertHelper.dismiss(alertState) }) { Text("OK") }
            }
        )
    }
}
