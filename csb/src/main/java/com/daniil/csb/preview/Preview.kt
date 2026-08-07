package com.daniil.csb.preview

import android.annotation.SuppressLint
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.daniil.csb.CSB
import com.daniil.csb.SettingsScreen
import com.daniil.csb.local.LocalSettings
import com.daniil.csb.local.rememberLocalSettingsController
import com.daniil.csb.registerSettingScreens
import com.daniil.csb.settings.ContentChoice
import com.daniil.csb.settings.Info
import com.daniil.csb.settings.Select
import com.daniil.csb.settings.Switch
import com.daniil.csb.settingui.LocalSettingsStyle
import com.daniil.csb.styles.Bobble
import com.daniil.csb.styles.CSBStyle
import com.daniil.csb.styles.ClassicDark
import com.daniil.csb.styles.ClassicLight
import com.daniil.csb.styles.Material3

@SuppressLint("RememberReturnType")
@Preview(showBackground = true)
@Composable
private fun Preview() {
    remember { previewInit() }
    val style = CSB.getValue<Select.Option>("theme_select").collectAsState().value
    val isDarkTheme = CSB.getValue<Boolean>("dark_mode").collectAsState().value
    val colorScheme = if (isDarkTheme) darkColorScheme() else lightColorScheme()
    MaterialTheme(colorScheme = colorScheme) {
        Scaffold(
            modifier = Modifier.fillMaxSize()
        ) { innerPadding ->
            SettingsScreen(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                paddingValues = PaddingValues(16.dp),
                style = when (style.id) {
                    "material" -> CSBStyle.Material3()
                    "bobble" -> CSBStyle.Bobble()
                    "classic" -> if (isDarkTheme) CSBStyle.ClassicDark else CSBStyle.ClassicLight
                    else -> CSBStyle.Material3()
                }
            )
        }
    }

}


private fun previewInit() = registerSettingScreens {
    CSB.config {
        +"flag:disableStored"
        primaryScreenId = "README"
    }

    createScreen("README") {
        title = "Preview"
        group("Overview") {
            groupTitle = null
            createInfo("Description") {
                icon = Info.InfoIconDefault.None
                title = "Compose Settings Builder"
                description = """
                    Powerful and flexible library for creating settings screens in Jetpack Compose using a DSL.
                """.trimIndent()
            }
        }
        group("Key Features") {
            createInfo("Quick_setup") {
                title = "🚀 Quick Setup"
                description = "Build screens in minutes with a clean DSL."
            }
            createInfo("Auto-Persistence") {
                title = "💾 Auto-Persistence"
                description = "Settings are automatically saved and restored."
            }
            createInfo("Custom Styling") {
                title = "🎨 Custom Styling"
                description = "Supports Material3, Bobble, and Classic themes."
            }
        }
        group("Navigation") {
            createRedirect("Documentation_redirect") {
                title = "📖 Documentation"
                description = "Installation and Quick Start"
                redirectToId = "Documentation_main_screen"
            }
            createRedirect("Components_Reference_redirect") {
                title = "🛠️ Components"
                description = "Detailed description of all setting types"
                redirectToId = "Components_Reference"
            }
            createRedirect("Screen_Types_redirect") {
                title = "📱 Screen Types"
                description = "Screen, CustomScreen, AbstractScreen"
                redirectToId = "Screen_Types_Docs"
            }
            createRedirect("Local_Settings_redirect") {
                title = "🏠 Local Settings"
                description = "Using LocalSettingsController"
                redirectToId = "Local_Settings_Docs"
            }
            createRedirect("Custom_Component_redirect") {
                title = "🧩 Custom Components"
                description = "Creating new setting types"
                redirectToId = "Custom_Component_Guide"
            }
            createRedirect("CSB_API_redirect") {
                title = "⚙️ CSB API & Flags"
                description = "Library management and configuration flags"
                redirectToId = "CSB_API_Docs"
            }
        }
    }

    createScreen("Documentation_main_screen") {
        title = "Documentation"
        group("Installation") {
            createCodeBlock("Setup_info") {
                description = "Add the library to your build.gradle.kts"
                language = CodeBlock.Language.Kotlin
                code = """
                    dependencies {
                        implementation("io.github.daniimazh-dev:csb:1.0.1")
                    }
                """.trimIndent()
            }
        }
        group("Quick Start") {
            createCodeBlock("code_1_init") {
                description = "1. Define settings structure"
                language = CodeBlock.Language.Kotlin
                code = """
                    fun initSettings() = registerSettingScreens {
                        createScreen("Main") {
                            title = "Settings"
                            group("General") {
                                createSwitch("notifications") {
                                    title = "Notifications"
                                    defaultValue = true
                                }
                            }
                        }
                    }
                """.trimIndent()
            }
            createCodeBlock("display_code") {
                description = "2. Display the settings screen"
                language = CodeBlock.Language.Kotlin
                code = """
                   class SettingsActivity : ComponentActivity() {
                       override fun onCreate(savedInstanceState: Bundle?) {
                           super.onCreate(savedInstanceState)
                           initSettings()
                           setContent {
                               MaterialTheme {
                                   SettingsScreen()
                               }
                           }
                       }
                   }
                """.trimIndent()
            }
        }
    }

    createScreen("Components_Reference") {
        title = "Components Reference"
        group("Selection") {
            createRedirect("comp_switch") { title = "Switch"; redirectToId = "Comp_Switch_Details" }
            createRedirect("comp_select") { title = "Select"; redirectToId = "Comp_Select_Details" }
            createRedirect("comp_multiselect") { title = "Multiply Select"; redirectToId = "Comp_MultiplySelect_Details" }
            createRedirect("comp_choice") { title = "Content Choice"; redirectToId = "Comp_ContentChoice_Details" }
        }
        group("Input") {
            createRedirect("comp_slider") { title = "Slider"; redirectToId = "Comp_Slider_Details" }
            createRedirect("comp_counter") { title = "Counter"; redirectToId = "Comp_Counter_Details" }
            createRedirect("comp_textfield") { title = "TextField"; redirectToId = "Comp_TextField_Details" }
            createRedirect("comp_color") { title = "Color Picker"; redirectToId = "Comp_ColorPicker_Details" }
            createRedirect("comp_time") { title = "Time Picker"; redirectToId = "Comp_TimePicker_Details" }
        }
        group("Other") {
            createRedirect("comp_action") { title = "Action"; redirectToId = "Comp_Action_Details" }
            createRedirect("comp_info") { title = "Info"; redirectToId = "Comp_Info_Details" }
            createRedirect("comp_tabbar") { title = "TabBar"; redirectToId = "Comp_TabBar_Details" }
        }
    }

    createScreen("Comp_Switch_Details") {
        title = "Switch"
        group("Builder Parameters") {
            createInfo("sw_p1") { title = "defaultValue: Boolean"; description = "Initial value (default is false)." }
            createInfo("sw_p2") { title = "title: String?"; description = "Element title." }
            createInfo("sw_p3") { title = "description: String?"; description = "Additional description under the title." }
            createInfo("sw_p4") { title = "enabled: Boolean"; description = "Whether the element is active for interaction." }
            createInfo("sw_p5") { title = "onChangeValue: (Boolean) -> Unit"; description = "Lambda called when the value changes." }
            createInfo("sw_p6") { title = "isSaveSetting: Boolean"; description = "Whether to save the value automatically." }
            createInfo("sw_p7") { title = "uiMode: Switch.UIMode"; description = "Visual style: Switch, RadioButton, CheckBox, SquareRadioButton, OnOffState." }
        }
        group("Example") {
            createCodeBlock("sw_code") {
                language = CodeBlock.Language.Kotlin
                code = """
                    createSwitch("notifications") {
                        title = "Notifications"
                        defaultValue = true
                        uiMode = Switch.UIMode.Switch
                    }
                """.trimIndent()
            }
            createSwitch("demo_sw_details") { title = "Switch Demo"; uiMode = Switch.UIMode.Switch }
        }
    }

    createScreen("Comp_Slider_Details") {
        title = "Slider"
        group("Builder Parameters") {
            createInfo("sl_p1") { title = "defaultValue: Float"; description = "Initial value." }
            createInfo("sl_p2") { title = "range: ClosedFloatingPointRange<Float>"; description = "Value range (e.g., 0f..100f)." }
            createInfo("sl_p3") { title = "steps: Int"; description = "Number of discrete steps (0 for smooth)." }
            createInfo("sl_p4") { title = "title: String?"; description = "Element title." }
            createInfo("sl_p5") { title = "description: String?"; description = "Description under the title." }
            createInfo("sl_p6") { title = "startPointRange: String?"; description = "Text to the left of the slider." }
            createInfo("sl_p7") { title = "endPointRange: String?"; description = "Text to the right of the slider." }
            createInfo("sl_p8") { title = "onChangeValue: (Float) -> Unit"; description = "Change handler." }
            createInfo("sl_p9") { title = "enabled / isSaveSetting"; description = "Standard activity and persistence parameters." }
        }
        group("Example") {
            createCodeBlock("sl_code") {
                language = CodeBlock.Language.Kotlin
                code = """
                    createSlider("brightness") {
                        title = "Brightness"
                        range = 0f..1f
                        defaultValue = 0.5f
                    }
                """.trimIndent()
            }
            createSlider("demo_sl_details") {
                title = "Slider Demo"
                range = 0f..1f
                defaultValue = 0.5f
            }
        }
    }

    createScreen("Comp_Select_Details") {
        title = "Select"
        group("Builder Parameters") {
            createInfo("sel_p1") { title = "option(id, title)"; description = "Function to add selection options." }
            createInfo("sel_p2") { title = "defaultValueId: String?"; description = "ID of the initially selected option." }
            createInfo("sel_p3") { title = "alertTitle: String"; description = "Title of the selection dialog." }
            createInfo("sel_p4") { title = "title / description / enabled / isSaveSetting"; description = "Standard parameters." }
        }
        group("Example") {
            createCodeBlock("sel_code") {
                language = CodeBlock.Language.Kotlin
                code = """
                    createSelect("language") {
                        title = "Language"
                        option("en", "English")
                        option("ua", "Ukrainian")
                        defaultValueId = "en"
                    }
                """.trimIndent()
            }
            createSelect("demo_sel_details") {
                title = "Language Selection"
                option("en", "English")
                option("ua", "Ukrainian")
                defaultValueId = "en"
            }
        }
    }

    createScreen("Comp_ColorPicker_Details") {
        title = "Color Picker"
        group("Builder Parameters") {
            createInfo("cp_p1") { title = "defaultValue: Color?"; description = "Default color (Color object)." }
            createInfo("cp_p2") { title = "defaultValueInt: Int?"; description = "Default color in Int format (ARGB)." }
            createInfo("cp_p3") { title = "onChangeValue: (Color) -> Unit"; description = "Color change handler." }
            createInfo("cp_p4") { title = "title / description / enabled / isSaveSetting"; description = "Standard parameters." }
        }
        group("Example") {
            createCodeBlock("cp_code") {
                language = CodeBlock.Language.Kotlin
                code = """
                    createColorPicker("theme_color") {
                        title = "Theme Color"
                        defaultValue = Color.Blue
                    }
                """.trimIndent()
            }
            createColorPicker("demo_cp_details") {
                title = "Accent Color"
                defaultValue = Color.Blue
            }
        }
    }

    createScreen("Comp_MultiplySelect_Details") {
        title = "Multiply Select"
        group("Builder Parameters") {
            createInfo("ms_p1") { title = "option(id, title)"; description = "Adding selection options." }
            createInfo("ms_p2") { title = "defaultValue: List<String>"; description = "List of default selected option IDs." }
            createInfo("ms_p3") { title = "alertTitle: String"; description = "Selection window title." }
        }
        group("Example") {
            createCodeBlock("ms_code") {
                language = CodeBlock.Language.Kotlin
                code = """
                    createMultiplySelect("permissions") {
                        title = "Permissions"
                        option("cam", "Camera")
                        option("mic", "Microphone")
                        defaultValue = listOf("cam")
                    }
                """.trimIndent()
            }
            createMultiplySelect("demo_ms_details") {
                title = "Permissions"
                option("cam", "Camera")
                option("mic", "Microphone")
                defaultValue = listOf("cam")
            }
        }
    }

    createScreen("Comp_ContentChoice_Details") {
        title = "Content Choice"
        group("Builder Parameters") {
            createInfo("cc_p1") { title = "option(id, content)"; description = "Option with custom Composable content." }
            createInfo("cc_p2") { title = "uiMode: UIMode"; description = "Display: Row, Column, Grid." }
            createInfo("cc_p3") { title = "gridCells: GridCells"; description = "Grid configuration (for Grid)." }
            createInfo("cc_p4") { title = "minContentHeight: Dp"; description = "Minimum content height (default 78.dp)." }
        }
        group("Example") {
            createCodeBlock("cc_code") {
                language = CodeBlock.Language.Kotlin
                code = """
                    createContentChoice("theme_toggle") {
                        title = "Theme"
                        uiMode = ContentChoice.UIMode.Row
                        option("light") { /* content */ }
                        option("dark") { /* content */ }
                    }
                """.trimIndent()
            }
            createContentChoice("demo_cc_details") {
                title = "Theme Selection"
                uiMode = ContentChoice.UIMode.Row
                option("light") { ThemeToggleIcon(false, it, LocalSettingsStyle.current.edgeGroupCorner, size = minContentHeight) }
                option("dark") { ThemeToggleIcon(true, it, LocalSettingsStyle.current.edgeGroupCorner, size = minContentHeight) }
            }
        }
    }

    createScreen("Comp_Counter_Details") {
        title = "Counter"
        group("Builder Parameters") {
            createInfo("cnt_p1") { title = "range: IntRange"; description = "Value range (e.g., 0..10)." }
            createInfo("cnt_p2") { title = "defaultValue: Int"; description = "Initial value." }
            createInfo("cnt_p3") { title = "steps: Int"; description = "Value change step." }
        }
        group("Example") {
            createCodeBlock("cnt_code") {
                language = CodeBlock.Language.Kotlin
                code = """
                    createCounter("quantity") {
                        title = "Quantity"
                        range = 1..5
                        defaultValue = 1
                    }
                """.trimIndent()
            }
            createCounter("demo_cnt_details") {
                title = "Quantity"
                range = 1..5
                defaultValue = 1
            }
        }
    }

    createScreen("Comp_TextField_Details") {
        title = "TextField"
        group("Builder Parameters") {
            createInfo("tf_p1") { title = "defaultValue: String"; description = "Initial text." }
            createInfo("tf_p2") { title = "label: (@Composable () -> Unit)?"; description = "Label for the text field in the dialog." }
            createInfo("tf_p3") { title = "alertTitle: String"; description = "Edit dialog title." }
        }
        group("Example") {
            createCodeBlock("tf_code") {
                language = CodeBlock.Language.Kotlin
                code = """
                    createTextField("username") {
                        title = "Username"
                        defaultValue = "Guest"
                    }
                """.trimIndent()
            }
            createTextField("demo_tf_details") {
                title = "Username"
                defaultValue = "Guest"
            }
        }
    }

    createScreen("Comp_TimePicker_Details") {
        title = "Time Picker"
        group("Builder Parameters") {
            createInfo("tp_p1") { title = "defaultValue: LocalTime"; description = "Initial time (java.time.LocalTime)." }
            createInfo("tp_p2") { title = "alertTitle: String"; description = "Time selection dialog title." }
        }
        group("Example") {
            createCodeBlock("tp_code") {
                language = CodeBlock.Language.Kotlin
                code = """
                    createTimePicker("wake_up") {
                        title = "Wake up time"
                    }
                """.trimIndent()
            }
            createTimePicker("demo_tp_details") {
                title = "Wake up time"
            }
        }
    }

    createScreen("Comp_Action_Details") {
        title = "Action"
        group("Builder Parameters") {
            createInfo("act_p1") { title = "action: (Boolean) -> Unit"; description = "Action performed on click." }
            createInfo("act_p2") { title = "requestAlert: Boolean"; description = "Whether to show a confirmation dialog." }
            createInfo("act_p3") { title = "alertTitle / alertText"; description = "Texts for the confirmation dialog." }
            createInfo("act_p4") { title = "icon: @Composable () -> Unit"; description = "Icon to the right of the title." }
        }
        group("Example") {
            createCodeBlock("act_code") {
                language = CodeBlock.Language.Kotlin
                code = """
                    createAction("reset") {
                        title = "Reset settings"
                        requestAlert = true
                        alertTitle = "Are you sure?"
                        action = { if(it) { /* Reset */ } }
                    }
                """.trimIndent()
            }
            createAction("demo_act_details") {
                title = "Reset settings"
                requestAlert = true
                alertTitle = "Are you sure?"
                action = { }
            }
        }
    }

    createScreen("Comp_Info_Details") {
        title = "Info"
        group("Builder Parameters") {
            createInfo("inf_p1") { title = "icon: InfoIcon"; description = "Icon type: Massage, Warning, Error, None, custom." }
            createInfo("inf_p2") { title = "onClick: () -> Unit"; description = "Click handler." }
        }
        group("Icon Types") {
            createInfo("demo_inf_m") { title = "Massage"; icon = Info.InfoIconDefault.Message }
            createInfo("demo_inf_w") { title = "Warning"; icon = Info.InfoIconDefault.Warning }
            createInfo("demo_inf_e") { title = "Error"; icon = Info.InfoIconDefault.Error }
        }
        group("Example") {
            createCodeBlock("inf_code") {
                language = CodeBlock.Language.Kotlin
                code = """
                    createInfo("note") {
                        title = "Note"
                        description = "This is some info"
                        icon = Info.InfoIconDefault.Massage
                    }
                """.trimIndent()
            }
        }
    }

    createScreen("Comp_TabBar_Details") {
        title = "TabBar"
        group("Builder Parameters") {
            createInfo("tb_p1") { title = "tab(id, content)"; description = "Add a tab." }
            createInfo("tb_p2") { title = "controller: FragmentController?"; description = "Controller to link with FragmentedGroup." }
            createInfo("tb_p3") { title = "defaultValue: String?"; description = "ID of the default active tab." }
        }
        group("Example") {
            createCodeBlock("tb_code") {
                language = CodeBlock.Language.Kotlin
                code = """
                    createTabBar("tabs") {
                        tab("1", { Text("First") })
                        tab("2", { Text("Second") })
                    }
                """.trimIndent()
            }
            createTabBar("demo_tb_details") {
                tab("1", { Text("First") })
                tab("2", { Text("Second") })
            }
        }
    }

    createScreen("Styling_Guide") {
        title = "Styling & Themes"
        group("Built-in Themes") {
            createSelect("theme_select") {
                title = "Settings Style"
                description = "Change the visual style of this preview"
                option("material", "Material3")
                option("bobble", "Bobble")
                option("classic", "Classic")
                defaultValueId = "material"
            }
        }
        group("Customization") {
            createCodeBlock("style_code") {
                language = CodeBlock.Language.Kotlin
                code = """
                    SettingsScreen(
                        style = CSBStyle.Material3().copy(
                            activeColor = Color.Red,
                            containerCornerShape = 8.dp
                        )
                    )
                """.trimIndent()
            }
        }
    }

    createScreen("Screen_Types_Docs") {
        title = "Screen Types"
        group("Overview") {
            createRedirect("sc_std") { title = "1. Screen"; redirectToId = "Screen_Std_Details" }
            createRedirect("sc_custom") { title = "2. CustomScreen"; redirectToId = "Screen_Custom_Details" }
            createRedirect("sc_abstract") { title = "3. AbstractScreen"; redirectToId = "Screen_Abstract_Details" }
        }
    }

    createScreen("Screen_Std_Details") {
        title = "Screen"
        group("Description") {
            createInfo("sc_info") {
                description = "Standard screen with a hierarchical structure of groups and elements. The most used type."
            }
        }
        group("DSL Example") {
            createCodeBlock("sc_code") {
                language = CodeBlock.Language.Kotlin
                code = """
                    createScreen("Main") {
                        title = "Settings"
                        group("General") {
                            createSwitch("sw") { title = "Enable" }
                        }
                    }
                """.trimIndent()
            }
        }
    }

    createScreen("Screen_Custom_Details") {
        title = "CustomScreen"
        group("Description") {
            createInfo("csc_info") {
                description = "Screen with completely custom Composable content, but with the ability to register settings for automatic saving."
            }
        }
        group("DSL Example") {
            createCodeBlock("csc_code") {
                language = CodeBlock.Language.Kotlin
                code = """
                    createCustomScreen("Custom") {
                        title = "My Screen"
                        setContent {
                            Text("Custom Content")
                            useDefaultContent() // Display registered elements
                        }
                    }
                """.trimIndent()
            }
        }
    }

    createScreen("Screen_Abstract_Details") {
        title = "AbstractScreen"
        group("Description") {
            createInfo("asc_info") {
                description = "Hidden screen for global settings that don't need their own interface. Allows accessing values from anywhere via CSB.getValue()."
            }
        }
        group("Example") {
            createCodeBlock("asc_code") {
                language = CodeBlock.Language.Kotlin
                code = """
                    createAbstractScreen("Globals") {
                        createSwitch("dark_mode") { defaultValue = true }
                    }
                """.trimIndent()
            }
        }
    }

    createScreen("Local_Settings_Docs") {
        title = "Local Settings"
        group("Description") {
            createInfo("ls_desc") {
                description = "Local settings exist independently of the global navigation tree. Useful for popups or specific screens."
            }
        }
        group("Controller") {
            createCodeBlock("ls_ctrl") {
                language = CodeBlock.Language.Kotlin
                code = """
                    val controller = rememberLocalSettingsController {
                        createSwitch("local_sw") { title = "Local Switch" }
                    }
                    
                    LocalSettings(localController = controller)
                """.trimIndent()
            }
        }
        group("Variants") {
            createRedirect("ls_std_btn") { title = "Regular (setScreen)"; redirectToId = "Local_Settings_Std" }
            createRedirect("ls_custom_btn") { title = "Custom (setCustomScreen)"; redirectToId = "Local_Settings_Custom" }
        }
    }

    createScreen("Local_Settings_Std") {
        title = "Standard Local Settings"
        group("Description") {
            createInfo("ls_std_desc") {
                description = "Use setScreen to create a standard DSL-based UI. This is analogous to createScreen, but local."
            }
        }
        group("Code") {
            createCodeBlock("ls_std_code") {
                language = CodeBlock.Language.Kotlin
                code = """
                    controller.setScreen {
                        group("Local Group") {
                            createSlider("l_slider") { title = "Level" }
                        }
                    }
                """.trimIndent()
            }
        }
    }

    createScreen("Local_Settings_Custom") {
        title = "Custom Local Settings"
        group("Description") {
            createInfo("ls_c_desc") {
                description = "setCustomScreen allows defining a completely custom UI for local settings while retaining DSL logic for registering elements."
            }
        }
        group("Code") {
            createCodeBlock("ls_c_code") {
                language = CodeBlock.Language.Kotlin
                code = """
                    controller.setCustomScreen {
                        createSwitch("l_sw") { title = "Local" }
                        setContent {
                            Text("My UI")
                            useDefaultContent()
                        }
                    }
                """.trimIndent()
            }
        }
    }

    createScreen("Custom_Component_Guide") {
        title = "Custom Components"
        group("Inheritance") {
            createInfo("ccg_desc") {
                description = "To create a custom component, inherit from the ComposeSetting<T> class."
            }
            createCodeBlock("ccg_code") {
                language = CodeBlock.Language.Kotlin
                code = """
                    class MySetting(
                        override val id: String,
                        override val defaultValue: String,
                        override val title: String,
                        override val description: String? = null
                    ) : ComposeSetting<String>() {
                        private val _value = MutableStateFlow(defaultValue)
                        override val value = _value.asStateFlow()
                        
                        private val _enabled = MutableStateFlow(true)
                        override val enabled = _enabled.asStateFlow()
                        
                        override val focusState = MutableStateFlow(false)
                        override val onChangeValue: (String) -> Unit = {}
                        override var isSaveSetting = true

                        override fun enabled(state: Boolean) { _enabled.value = state }
                        override fun changeValue(newValue: String) { _value.value = newValue }

                        @Composable
                        override fun UI(modifier: Modifier, position: GroupItemClip?) {
                            // Your UI implementation
                        }
                    }
                """.trimIndent()
            }
        }
        group("Main Functions & Properties") {
            createInfo("ccg_v1") { title = "value / enabled"; description = "StateFlow with the component state." }
            createInfo("ccg_v2") { title = "changeValue(newValue)"; description = "Function to programmatically change the value." }
            createInfo("ccg_v3") { title = "isSaveSetting"; description = "Flag indicating if the state should be saved automatically." }
            createInfo("ccg_v4") { title = "UI(modifier, position)"; description = "Composable function for displaying the interface." }
        }
        group("Saving & Loading") {
            createInfo("ccg_save") { 
                title = "saveLogic / loadLogic"; 
                description = "Determine how the object is converted to SaveSettingPackage for saving in DataStore/JSON." 
            }
            createCodeBlock("ccg_logic_code") {
                language = CodeBlock.Language.Kotlin
                code = """
                    override fun saveLogic(serializer: KSerializer<T>?): SaveSettingPackage? {
                        if (!isSaveSetting) return null
                        return SaveSettingPackage.StringPackage(id, enabled.value, value.value)
                    }
                """.trimIndent()
            }
        }
    }

    createScreen("CSB_API_Docs") {
        title = "CSB API & Flags"
        group("Sections") {
            createRedirect("api_obj") { title = "CSB Object"; redirectToId = "CSB_Object_Docs" }
            createRedirect("api_flags") { title = "Configuration & Flags"; redirectToId = "CSB_Flags_Docs" }
            createRedirect("api_ctrl") { title = "Controllers"; redirectToId = "CSB_Controllers_Docs" }
        }
    }

    createScreen("CSB_Object_Docs") {
        title = "CSB Object"
        group("Functions") {
            createInfo("api_1") { title = "getValue<T>(id)"; description = "Get a StateFlow with the setting value." }
            createInfo("api_2") { title = "setValue(id, value)"; description = "Programmatically change the value." }
            createInfo("api_3") { title = "navigateToScreen(id)"; description = "Navigate to the specified screen." }
            createInfo("api_4") { title = "findSettingById(id)"; description = "Find a setting object." }
            createInfo("api_5") { title = "enable(id, state)"; description = "Enable/disable an element." }
            createInfo("api_6") { title = "resetToDefault(id)"; description = "Reset to the default value." }
        }
    }

    createScreen("CSB_Flags_Docs") {
        title = "Configuration & Flags"
        group("CSB.config") {
            createCodeBlock("api_cfg") {
                language = CodeBlock.Language.Kotlin
                code = """
                    CSB.config {
                        primaryScreenId = "Main"
                        savePatch = "my_settings"
                        +"flag:disableStored"
                    }
                """.trimIndent()
            }
        }
        group("Available Flags") {
            createInfo("f_1") { title = "disableStored"; description = "Disable automatic saving." }
            createInfo("f_2") { title = "useJsonSaveMethod"; description = "Use JSON instead of DataStore." }
            createInfo("f_3") { title = "useOneFileJsonSaveMethod"; description = "Save all screens to a single JSON file." }
            createInfo("f_4") { title = "ignoreSettingNotFoundError"; description = "Don't throw an error if an ID is not found." }
            createInfo("f_5") { title = "enableDebugMode"; description = "Enable debug mode." }
            createInfo("f_6") { title = "disableScroll"; description = "Disable automatic ScrollState in SettingsScreen." }
            createInfo("f_7") { title = "allowDisplayAbstractScreen"; description = "Allow navigation to AbstractScreen." }
            createInfo("f_8") { title = "disableContainerGroupRound"; description = "Disable rounding for elements in a group." }
        }
    }

    createScreen("CSB_Controllers_Docs") {
        title = "Controllers"
        group("GroupController") {
            createInfo("ctrl_g1") { title = "isShow(state)"; description = "Show or hide an entire group." }
            createInfo("ctrl_g2") { title = "isDisable(state)"; description = "Activate/deactivate all elements in a group." }
            createInfo("ctrl_g3") { title = "resetToDefault()"; description = "Reset all elements in a group." }
        }
        group("ScreenController") {
            createInfo("ctrl_s1") { title = "resetToDefault()"; description = "Reset all elements on a screen." }
        }
        group("FragmentController") {
            createInfo("ctrl_f1") { title = "changeFragment(key)"; description = "Switch the active fragment in a FragmentedGroup." }
            createInfo("ctrl_f2") { title = "isShow / isDisable"; description = "Management of visibility and activity of a fragmented group." }
        }
    }

    createCustomScreen("Local_setting_screen") {
        title = "Local Settings"
        setContent {
            val localController = rememberLocalSettingsController()
            localController.setCustomScreen {
                createSwitch("l_sw") { title = "Local Switch" }
                useDefaultContent()
            }
            LocalSettings(localController = localController, scrollState = null)
        }
    }

    createAbstractScreen("Abstract") {
        createSwitch("dark_mode") { defaultValue = true }
    }

    // Original preview settings and others retained for compatibility with the preview logic
    createScreen("preview_setting") {
        group("Preview Settings") {
            createSwitch("hide_preview") {
                title = "Hide preview"
                onChangeValue = { CSB.groupController("preview").isShow(!it) }
            }
        }
    }
}





@Composable
fun ThemeToggleIcon(
    isDarkTheme: Boolean,
    isActive: Boolean,
    shape: Shape,
    modifier: Modifier = Modifier,
    size: Dp = 60.dp
) {
    // Color animation depending on theme and activity
    val skyColor by animateColorAsState(
        targetValue = when {
            !isDarkTheme && isActive -> Color(0xFFE0F7FA) // Active day sky
            isDarkTheme && isActive -> Color(0xFF1A1B2F)  // Active night sky
            else -> Color(0xFFE0E0E0)                      // Inactive background (gray)
        },
        animationSpec = tween(500), label = "Sky"
    )

    val celestialColor by animateColorAsState(
        targetValue = when {
            !isDarkTheme && isActive -> Color(0xFFFFB300) // Active Sun
            isDarkTheme && isActive -> Color(0xFFFFF59D)  // Active Moon
            else -> Color(0xFF9E9E9E)                      // Inactive luminary
        },
        animationSpec = tween(500), label = "Celestial"
    )

    val mountainColor1 by animateColorAsState(
        targetValue = when {
            !isDarkTheme && isActive -> Color(0xFF90A4AE) // Active mountains day
            isDarkTheme && isActive -> Color(0xFF37474F)  // Active mountains night
            else -> Color(0xFFBDBDBD)                      // Inactive mountains
        },
        animationSpec = tween(500), label = "Mountain1"
    )

    val mountainColor2 by animateColorAsState(
        targetValue = when {
            !isDarkTheme && isActive -> Color(0xFF78909C)
            isDarkTheme && isActive -> Color(0xFF263238)
            else -> Color(0xFF757575)
        },
        animationSpec = tween(500), label = "Mountain2"
    )

    Canvas(
        modifier = modifier
            .size(size)
            .clip(shape)
            .background(skyColor)
    ) {
        val width = size.toPx()
        val height = size.toPx()

        // 2. Luminary (Sun or Moon)
        if (!isDarkTheme) {
            // Sun
            drawCircle(
                color = celestialColor,
                radius = width * 0.15f,
                center = Offset(width * 0.35f, height * 0.35f)
            )
        } else {
            // Moon (by overlaying sky color circle)
            drawCircle(
                color = celestialColor,
                radius = width * 0.15f,
                center = Offset(width * 0.35f, height * 0.35f)
            )
            drawCircle(
                color = skyColor,
                radius = width * 0.15f,
                center = Offset(width * 0.43f, height * 0.30f)
            )
        }

        // 3. Back mountain
        val path1 = Path().apply {
            moveTo(width * 0.12f, height * 0.80f)
            lineTo(width * 0.45f, height * 0.45f)
            lineTo(width * 0.80f, height * 0.80f)
            close()
        }
        drawPath(path = path1, color = mountainColor1)

        // 4. Front mountain
        val path2 = Path().apply {
            moveTo(width * 0.30f, height * 0.80f)
            lineTo(width * 0.65f, height * 0.52f)
            lineTo(width * 0.90f, height * 0.80f)
            close()
        }
        drawPath(path = path2, color = mountainColor2)
    }
}
 
