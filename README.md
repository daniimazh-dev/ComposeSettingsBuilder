# Compose Settings Builder (CSB)

Compose Settings Builder is a powerful and flexible library for creating settings screens in Jetpack Compose. It allows you to build complex, hierarchical settings with ease using a DSL, handles data persistence automatically, and supports custom styling.

## Features

- **🚀 Quick Setup**: Build complex settings screens in minutes with a clean DSL.
- **💾 Auto-Persistence**: Settings are automatically saved to local storage and restored on app launch.
- **🎨 Custom Styling**: Highly customizable UI with built-in themes (Material3, Bobble, Classic).
- **🛠️ Rich Set of Components**: Includes Switch, Slider, Color Picker, Time Picker, Counter, and more.
- **📱 Navigation Support**: Built-in hierarchical navigation between settings screens.
- **🧩 Custom Settings**: Easily create and register your own custom setting types.
- **🏠 Local Settings**: Support for settings that are not part of the global navigation tree.

## Installation

Add the library to your `build.gradle.kts`:

```kotlin
dependencies {
    implementation("io.github.daniimazh-dev:csb:1.0.0")
}
```

## Quick Start

### 1. Initialize and Register Settings

Define your settings structure using `registerSettingScreens`.

```kotlin
fun initSettings() = registerSettingScreens {
    createScreen("Main") {
        title = "Settings"

        group("General") {
            groupTitle = customGroupTitle("Preference")
            createSwitch("notifications_enabled") {
                title = "Enable Notifications"
                defaultValue = true
            }

            createSelect("theme_mode") {
                title = "Theme"
                option("system", "System")
                option("dark", "Dark")
                option("light", "Light")
                defaultValueId = "system"
            }
        }
    }
}
```

### 2. Display the Settings Screen

Use the `SettingsScreen` Composable in your activity or fragment.

```kotlin
class SettingsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Ensure settings are initialized
        initSettings()

        setContent {
            MaterialTheme {
                SettingsScreen()
            }
        }
    }
}
```

### 3. Access Setting Values

You can easily get or set values from anywhere in your code using the `CSB` object.

```kotlin
// Get a StateFlow of the value
val isEnabledFlow = CSB.getValue<Boolean>("notifications_enabled")

// Set a value programmatically
CSB.setValue("notifications_enabled", false)
```

## Available Setting Types

| Type                | Function               | Description                                         |
|:--------------------|:-----------------------|:----------------------------------------------------|
| **Switch**          | `createSwitch`         | Boolean toggle (Switch, Radio, Checkbox, etc.)      |
| **Slider**          | `createSlider`         | Range selection                                     |
| **Counter**         | `createCounter`        | Increment/Decrement values                          |
| **Select**          | `createSelect`         | Single choice from list                             |
| **Multiply Select** | `createMultiplySelect` | Multiple choices from list                          |
| **Color Picker**    | `createColorPicker`    | HSV/RGB color selection                             |
| **Time Picker**     | `createTimePicker`     | Time selection                                      |
| **TextField**       | `createTextField`      | Text input                                          |
| **Content Choice**  | `createContentChoice`  | Choice between multiple options with Icon/UI        |                                                 | Text input                                          |
| **Action**          | `createAction`         | Trigger a function with optional confirmation alert |
| **Redirect**        | `createRedirect`       | Navigate to another settings screen                 |
| **Info**            | `createInfo`           | Display informational text                          |
| **Custom**          | `createCustomSetting`  | Fully custom UI component                           |

## Local Settings

If you need to create settings that should not be part of the main hierarchical navigation tree (for example, in a drop-down menu or on a specific screen), use `LocalSettings`.
Use `rememberLocalSettingsController` to create the controller and `LocalSettings` for display:

```kotlin
  val localController = rememberLocalSettingsController {
      createSwitch("local_switch") {
          title = "Local Toggle"
          defaultValue = false 
      }
    // Customize the display of settings
    setContent {
        RegisteredSetting("local_switch") 
    }
}
// Display anywhere in your UI
LocalSettings(
    localController = localController,
    style = CSBStyle.Material3()
)

// Accessing values via the controller
val localValue = localController.getValue<Boolean>("local_switch")
```

## Custom Styling

You can customize the look and feel by passing a `SettingsStyle` to the `SettingsScreen`.

```kotlin
SettingsScreen(
    style = CSBStyle.Material3.copy(
        activeColor = Color.Blue,
        containerCornerShape = 12.dp
    )
)
```

## License

This project is licensed under the MIT License - see the LICENSE file for details.
