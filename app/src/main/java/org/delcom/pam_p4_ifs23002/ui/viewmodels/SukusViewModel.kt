package org.delcom.pam_p4_ifs23002.ui.viewmodels

import androidx.annotation.Keep
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.delcom.pam_p4_ifs23002.network.sukus.data.ResponseSukusData
import org.delcom.pam_p4_ifs23002.network.sukus.service.ISukusRepository
import javax.inject.Inject

sealed interface SukusUIState {
    data class Success(val data: List<ResponseSukusData>) : SukusUIState
    data class Error(val message: String) : SukusUIState
    object Loading : SukusUIState
}

sealed interface SukuUIState {
    data class Success(val data: ResponseSukusData) : SukuUIState
    data class Error(val message: String) : SukuUIState
    object Loading : SukuUIState
}

sealed interface SukuActionUIState {
    data class Success(val message: String) : SukuActionUIState
    data class Error(val message: String) : SukuActionUIState
    object Loading : SukuActionUIState
}

data class UIStateSukus(
    val profile: ProfileUIState = ProfileUIState.Loading,
    val sukus: SukusUIState = SukusUIState.Loading,
    var suku: SukuUIState = SukuUIState.Loading,
    var sukuAction: SukuActionUIState = SukuActionUIState.Loading
)

@HiltViewModel
@Keep
class SukusViewModel @Inject constructor(
    private val repository: ISukusRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(UIStateSukus())
    val uiState = _uiState.asStateFlow()

    fun getAllSukus(search: String? = null) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    sukus = SukusUIState.Loading
                )
            }
            _uiState.update { state ->
                val tmpState = runCatching {
                    repository.getAllNovels(search)
                }.fold(
                    onSuccess = {
                        if (it.status == "success") {
                            SukusUIState.Success(it.data?.sukus ?: emptyList())
                        } else {
                            SukusUIState.Error(it.message)
                        }
                    },
                    onFailure = {
                        SukusUIState.Error(it.message ?: "Unknown error")
                    }
                )

                state.copy(
                    sukus = tmpState
                )
            }
        }
    }

    fun postSuku(
        nama: RequestBody,
        deskripsi: RequestBody,
        makanan: RequestBody,
        rumahadat: RequestBody,
        file: MultipartBody.Part
    ) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    sukuAction = SukuActionUIState.Loading
                )
            }
            _uiState.update { state ->
                val tmpState = runCatching {
                    repository.postSukus(
                        nama = nama,
                        deskripsi = deskripsi,
                        makanan = makanan,
                        rumahadat = rumahadat,
                        image = file
                    )
                }.fold(
                    onSuccess = {
                        if (it.status == "success") {
                            SukuActionUIState.Success(it.data?.sukuId ?: "")
                        } else {
                            SukuActionUIState.Error(it.message)
                        }
                    },
                    onFailure = {
                        SukuActionUIState.Error(it.message ?: "Unknown error")
                    }
                )

                state.copy(
                    sukuAction = tmpState
                )
            }
        }
    }

    fun getSukuById(sukuId: String) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    suku = SukuUIState.Loading
                )
            }
            _uiState.update { state ->
                val tmpState = runCatching {
                    repository.getSukusById(sukuId)
                }.fold(
                    onSuccess = {
                        if (it.status == "success" && it.data != null) {
                            SukuUIState.Success(it.data.suku)
                        } else {
                            SukuUIState.Error(it.message)
                        }
                    },
                    onFailure = {
                        SukuUIState.Error(it.message ?: "Unknown error")
                    }
                )

                state.copy(
                    suku = tmpState
                )
            }
        }
    }

    fun putSuku(
        sukuId: String,
        nama: RequestBody,
        deskripsi: RequestBody,
        makanan: RequestBody,
        rumahadat: RequestBody,
        file: MultipartBody.Part
    ) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    sukuAction = SukuActionUIState.Loading
                )
            }
            _uiState.update { state ->
                val tmpState = runCatching {
                    repository.putSukusById(
                        sukuId = sukuId,
                        nama = nama,
                        deskripsi = deskripsi,
                        makanan = makanan,
                        rumahadat = rumahadat,
                        image = file
                    )
                }.fold(
                    onSuccess = {
                        if (it.status == "success") {
                            SukuActionUIState.Success(it.message)
                        } else {
                            SukuActionUIState.Error(it.message)
                        }
                    },
                    onFailure = {
                        SukuActionUIState.Error(it.message ?: "Unknown error")
                    }
                )

                state.copy(
                    sukuAction = tmpState
                )
            }
        }
    }

    fun deleteSuku(sukuId: String) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    sukuAction = SukuActionUIState.Loading
                )
            }
            _uiState.update { state ->
                val tmpState = runCatching {
                    repository.deleteSukusById(
                        sukuId = sukuId
                    )
                }.fold(
                    onSuccess = {
                        if (it.status == "success") {
                            SukuActionUIState.Success(it.message)
                        } else {
                            SukuActionUIState.Error(it.message)
                        }
                    },
                    onFailure = {
                        SukuActionUIState.Error(it.message ?: "Unknown error")
                    }
                )

                state.copy(
                    sukuAction = tmpState
                )
            }
        }
    }
}