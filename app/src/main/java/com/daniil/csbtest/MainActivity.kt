package com.daniil.csbtest

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.daniil.csb.SettingsScreen
import com.daniil.csb.settingui.styles.Bobble
import com.daniil.csb.settingui.styles.CSBStyle
import com.daniil.csbtest.ui.theme.ComposeSettingsBuilderTheme

class MainActivity : ComponentActivity() {

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        initSettings()

        setContent {
            ComposeSettingsBuilderTheme {
                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = {
                                Text("CSB Preview")
                            },
                        )
                    },
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    SettingsScreen(
                        modifier = Modifier
                            .padding(innerPadding)
                            .fillMaxSize(),
                        paddingValues = PaddingValues(16.dp),
                        style = CSBStyle.Bobble()
                    )
                }
            }
        }
    }
}


