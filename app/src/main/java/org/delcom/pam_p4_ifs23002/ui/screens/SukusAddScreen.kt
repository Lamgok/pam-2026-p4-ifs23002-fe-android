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
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import org.delcom.pam_p4_ifs23002.R
import org.delcom.pam_p4_ifs23002.helper.AlertHelper
import org.delcom.pam_p4_ifs23002.helper.AlertState
import org.delcom.pam_p4_ifs23002.helper.AlertType
import org.delcom.pam_p4_ifs23002.helper.ConstHelper
import org.delcom.pam_p4_ifs23002.helper.RouteHelper
import org.delcom.pam_p4_ifs23002.helper.SuspendHelper
import org.delcom.pam_p4_ifs23002.helper.SuspendHelper.SnackBarType
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
    val uiState by sukusViewModel.uiState.collectAsState()
    var isLoading by remember { mutableStateOf(false) }
    var tmpSuku by remember { mutableStateOf<ResponseSukusData?>(null) }

    LaunchedEffect(Unit) {
        // Reset action state if needed
    }

    fun onSave(
        context: Context,
        nama: String,
        deskripsi: String,
        makanan: String,
        rumahadat: String,
        file: Uri
    ) {
        isLoading = true
        val namaBody = nama.toRequestBodyText()
        val deskripsiBody = deskripsi.toRequestBodyText()
        val makananBody = makanan.toRequestBodyText()
        val rumahadatBody = rumahadat.toRequestBodyText()
        val filePart = uriToMultipart(context, file, "file")

        sukusViewModel.postSuku(namaBody, deskripsiBody, makananBody, rumahadatBody, filePart)
    }

    LaunchedEffect(uiState.sukuAction) {
        when (val state = uiState.sukuAction) {
            is SukuActionUIState.Success -> {
                SuspendHelper.showSnackBar(snackbarHost, SnackBarType.SUCCESS, "Data suku berhasil ditambahkan")
                RouteHelper.to(navController, ConstHelper.RouteNames.Sukus.path, true)
                isLoading = false
            }
            is SukuActionUIState.Error -> {
                SuspendHelper.showSnackBar(snackbarHost, SnackBarType.ERROR, state.message)
                isLoading = false
            }
            else -> {}
        }
    }

    if (isLoading) {
        LoadingUI()
        return
    }

    Scaffold(
        topBar = {
            TopAppBarComponent(navController = navController, title = "Tambah Budaya", showBackButton = true)
        },
        bottomBar = {
            BottomNavComponent(navController = navController)
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(Color(0xFFFFF8F0))
        ) {
            SukusAddUI(onSave = ::onSave)
        }
    }
}

@Composable
fun SukusAddUI(
    onSave: (Context, String, String, String, String, Uri) -> Unit
) {
    val alertState = remember { mutableStateOf(AlertState()) }
    var dataFile by remember { mutableStateOf<Uri?>(null) }
    var dataNama by remember { mutableStateOf("") }
    var dataDeskripsi by remember { mutableStateOf("") }
    var dataMakanan by remember { mutableStateOf("") }
    var dataRumahAdat by remember { mutableStateOf("") }
    
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val deskripsiFocus = remember { FocusRequester() }
    val makananFocus = remember { FocusRequester() }
    val rumahadatFocus = remember { FocusRequester() }

    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri -> dataFile = uri }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Image Picker Section
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .size(180.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .background(Color.White)
                        .clickable {
                            imagePicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                        }
                        .padding(4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    if (dataFile != null) {
                        AsyncImage(
                            model = dataFile,
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(20.dp))
                        )
                    } else {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.AddPhotoAlternate,
                                contentDescription = null,
                                tint = Color(0xFFB22222),
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(Modifier.height(8.dp))
                            Text("Pilih Gambar", color = Color(0xFFB22222), fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // Input Fields
            CustomOutlinedTextField(
                value = dataNama,
                onValueChange = { dataNama = it },
                label = "Nama Suku",
                imeAction = ImeAction.Next,
                keyboardActions = KeyboardActions(onNext = { deskripsiFocus.requestFocus() })
            )

            CustomOutlinedTextField(
                value = dataDeskripsi,
                onValueChange = { dataDeskripsi = it },
                label = "Deskripsi Budaya",
                modifier = Modifier.height(150.dp).focusRequester(deskripsiFocus),
                singleLine = false,
                imeAction = ImeAction.Next,
                keyboardActions = KeyboardActions(onNext = { makananFocus.requestFocus() })
            )

            CustomOutlinedTextField(
                value = dataMakanan,
                onValueChange = { dataMakanan = it },
                label = "Makanan Khas",
                modifier = Modifier.focusRequester(makananFocus),
                imeAction = ImeAction.Next,
                keyboardActions = KeyboardActions(onNext = { rumahadatFocus.requestFocus() })
            )

            CustomOutlinedTextField(
                value = dataRumahAdat,
                onValueChange = { dataRumahAdat = it },
                label = "Rumah Adat",
                modifier = Modifier.focusRequester(rumahadatFocus),
                imeAction = ImeAction.Done,
                keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() })
            )
            
            Spacer(modifier = Modifier.height(80.dp))
        }

        FloatingActionButton(
            onClick = {
                if (dataFile == null) {
                    AlertHelper.show(alertState, AlertType.ERROR, "Mohon pilih gambar terlebih dahulu")
                    return@FloatingActionButton
                }
                if (dataNama.isBlank() || dataDeskripsi.isBlank()) {
                    AlertHelper.show(alertState, AlertType.ERROR, "Nama dan Deskripsi wajib diisi")
                    return@FloatingActionButton
                }
                onSave(context, dataNama, dataDeskripsi, dataMakanan, dataRumahAdat, dataFile!!)
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp),
            containerColor = Color(0xFFB22222),
            contentColor = Color.White
        ) {
            Icon(Icons.Default.Save, contentDescription = "Simpan")
        }
    }

    if (alertState.value.isVisible) {
        AlertDialog(
            onDismissRequest = { AlertHelper.dismiss(alertState) },
            title = { Text(alertState.value.type.title) },
            text = { Text(alertState.value.message) },
            confirmButton = {
                TextButton(onClick = { AlertHelper.dismiss(alertState) }) {
                    Text("OK")
                }
            }
        )
    }
}

@Composable
fun CustomOutlinedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    singleLine: Boolean = true,
    imeAction: ImeAction = ImeAction.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        textStyle = TextStyle(color = Color.Black, fontSize = 16.sp), // Memastikan teks berwarna hitam agar terlihat
        modifier = modifier.fillMaxWidth(),
        singleLine = singleLine,
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color(0xFFB22222),
            focusedLabelColor = Color(0xFFB22222),
            cursorColor = Color(0xFFB22222),
            unfocusedContainerColor = Color.White,
            focusedContainerColor = Color.White,
            unfocusedTextColor = Color.Black, // Warna teks saat tidak fokus
            focusedTextColor = Color.Black // Warna teks saat fokus
        ),
        keyboardOptions = KeyboardOptions(imeAction = imeAction),
        keyboardActions = keyboardActions
    )
}
