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
- **🌍 Multi-language**: Built-in support for translations using Android resources.

## Installation

Add the library to your `build.gradle.kts`:

```kotlin
dependencies {
    implementation("io.github.daniimazh-dev:csb:1.1.0")
}
```

## Quick Start

### 1. Initialize and Register Settings

Define your settings structure using `registerSettingScreens`. You can use the `res()` helper for automatic translation.

```kotlin
fun initSettings() = registerSettingScreens {
    CSB.config {
        debugMode = true
        translator = object : CSBTranslator {
            @Composable
            override fun translate(key: String): String = {
                // Implementation of the transfer
                return key
            }
        }
    }

    createScreen("Main") {
        title = ScreenTitle.setText(res(R.string.settings_title))

        group("General") {
            
            createSwitch("notifications_enabled") {
                title = res(R.string.enable_notifications)
                defaultValue = true
            }

            createSelect("theme_mode") {
                title = "Theme"
                option("system", "System")
                option("dark", "Dark")
                option("light", "Light")
            }
        }
    }
}
```

### 2. Multi-language Support

CSB comes with a built-in translator. You can use the `res(R.string.key)` helper in your DSL, and the library will automatically fetch the translated string from your Android resources.

```kotlin
createSwitch("my_switch") {
    title = res(R.string.switch_label)
    description = res(R.string.switch_desc)
}
```

### 3. Display the Settings Screen

Use the `SettingsScreen` Composable in your UI.

```kotlin
setContent {
    MaterialTheme {
        SettingsScreen()
    }
}
```

### 4. Fragmented Groups

Use `fragmentedGroup` when you need to switch between different sets of settings dynamically (e.g., using a TabBar).

```kotlin
createScreen("Advanced") {
    val fragController = FragmentController()
    
    fragmentedGroup("switcher") {
        this.controller = fragController
        createTabBar("tabs", fragController)
        
        fragment("General") {
            createSwitch("s1")
        }
        fragment("Extra") {
            createSlider("s2")
        }
    }
}
```

## Available Setting Types

| Type                | Function               | Description                                         |
|:--------------------|:-----------------------|:----------------------------------------------------|
| **Switch**          | `createSwitch`         | Boolean toggle (Switch, Radio, Checkbox, etc.)      |
| **Slider**          | `createSlider`         | Range selection                                     |
| **Range Slider**    | `createRangeSlider`    | Multi-point range selection                         |
| **Counter**         | `createCounter`        | Increment/Decrement values                          |
| **Select**          | `createSelect`         | Single choice from list                             |
| **Multiply Select** | `createMultiplySelect` | Multiple choices from list                          |
| **Color Picker**    | `createColorPicker`    | HSV/RGB color selection                             |
| **Time Picker**     | `createTimePicker`     | Time selection                                      |
| **Date Picker**     | `createDatePicker`     | Date selection                                      |
| **TextField**       | `createTextField`      | Text input                                          |
| **PasswordField**   | `createPasswordField`  | Secure text input                                   |
| **SearchField**     | `createSearchField`    | Expandable search input                             |
| **RatingBar**       | `createRatingBar`      | Star-based rating input                             |
| **ProgressBar**     | `crateProgressBar`     | Visual progress indicator                           |
| **Content Choice**  | `createContentChoice`  | Choice between multiple options with Icon/UI        |
| **Code Preview**    | `createCodePreview`    | Display code with syntax highlighting               |
| **File Picker**     | `createFilePicker`     | Generic picker for files, folders, etc.             |
| **TabBar**          | `createTabBar`         | Horizontal tab navigation                           |
| **Action**          | `createAction`         | Trigger a function with optional confirmation alert |
| **Redirect**        | `createRedirect`       | Navigate to another settings screen                 |
| **Info**            | `createInfo`           | Display informational text                          |
| **Custom**          | `createCustomSetting`  | Fully custom UI component                           |

## Access Setting Values

You can easily get or set values from anywhere in your code using the `CSB` object.

```kotlin
// Get a StateFlow of the value
val isEnabledFlow = CSB.getValue<Boolean>("notifications_enabled")

// Set a value programmatically
CSB.setValue("notifications_enabled", false)
```

## Local Settings

If you need to create settings that should not be part of the main hierarchical navigation tree, use `LocalSettings`.

```kotlin
val localController = rememberLocalSettingsController {
      createSwitch("local_switch") {
          title = "Local Toggle"
          defaultValue = false 
      }
}
// Display anywhere in your UI
LocalSettings(localController = localController)
```

## License

This project is licensed under the MIT License - see the LICENSE file for details.
