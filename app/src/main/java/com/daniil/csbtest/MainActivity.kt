package com.daniil.csbtest

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.daniil.csb.CSB
import com.daniil.csb.SettingsScreen
import com.daniil.csb.classes.Select
import com.daniil.csb.settingui.styles.Bobble
import com.daniil.csb.settingui.styles.CSBStyle
import com.daniil.csb.settingui.styles.ClassicDark
import com.daniil.csb.settingui.styles.ClassicLight
import com.daniil.csb.settingui.styles.Material3
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
//                        TopAppBar(
//                            title = {
//                                Text("CSB Preview")
//                            },
//                        )
                    },
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    val style by CSB.getValue<Select.Option>("theme_select").collectAsState()
                    SettingsScreen(
                        modifier = Modifier
                            .padding(innerPadding)
                            .fillMaxSize(),
                        paddingValues = PaddingValues(16.dp),
                        style = when (style.id) {
                            "material" -> CSBStyle.Material3()
                            "bobble" -> CSBStyle.Bobble()
                            "classic" -> if (isSystemInDarkTheme()) CSBStyle.ClassicDark else CSBStyle.ClassicLight
                            else -> CSBStyle.Material3()
                        }
                    )
                }
            }
        }
    }
}


