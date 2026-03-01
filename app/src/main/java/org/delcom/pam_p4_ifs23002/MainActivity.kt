package org.delcom.pam_p4_ifs23002

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import dagger.hilt.android.AndroidEntryPoint
import org.delcom.pam_p4_ifs23002.ui.UIApp
import org.delcom.pam_p4_ifs23002.ui.theme.DelcomTheme
import org.delcom.pam_p4_ifs23002.ui.viewmodels.PlantViewModel
import org.delcom.pam_p4_ifs23002.ui.viewmodels.SukusViewModel


@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val plantViewModel: PlantViewModel by viewModels()
    private val sukusViewModel: SukusViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DelcomTheme {
                UIApp(
                    plantViewModel = plantViewModel,
                    sukusViewModel = sukusViewModel
                )
            }
        }
    }
}
