# Compose Settings Builder (CSB)

Compose Settings Builder is a powerful, flexible, and type-safe library for creating settings screens in Jetpack Compose. It allows you to build complex, hierarchical settings using a clean DSL, handles data persistence automatically, and provides full programmatic control over your settings UI.

## Features

- **Declarative DSL**: Build hierarchical settings screens in minutes.
- **Auto-Persistence**: Seamlessly save and restore settings using DataStore or JSON.
- **Dynamic UI**: Use `depends` blocks for conditional visibility and interactivity.
- **Rich Component Library**: 20+ built-in setting types including Pickers, Selectors, and Inputs.
- **Fragmented Groups**: Switch between sets of settings dynamically using TabBars.
- **Customizable Styling**: Built-in Material3, Bobble, and Classic themes, plus full custom styling support.
- **Programmatic Control**: Access and modify any setting value or state from code.



## Installation

Add the library to your `build.gradle.kts`:
```kotlin
dependencies {
    implementation("io.github.daniimazh-dev:csb:1.2.0")
}
```
## Screenshots
|Material3 Theme| Bobble Theme |
|:-|:-|
| <img width="320" height="720" alt="Basic" src="https://github.com/user-attachments/assets/b4f82766-8fb9-488d-9e64-8b5ca5b24673" /> | <img width="320" height="720" alt="image" src="https://github.com/user-attachments/assets/74470580-d510-4051-afa8-e8f43598315a" /> |


## Getting Started

### 1. Initialization and Configuration

Use `registerSettingScreens` to set sreens and settings
```kotlin
fun initSettings = registerSettingScreens {
    CSB.config {
        debugMode = true
        savePatch = "my_app_settings"
        flag("useJsonSaveMethod")
    }

    createScreen("main_screen") {
        topBar = TopScreenBar.text("Settings")
            
        group("general") {
            createSwitch("notifications") {
                title = "Notifications"
                defaultValue = true
            }
        }
    }
}

```

Initialize CSB in your `Activity`. 

``` kotlin
override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initSettings() // Init
        setContetn {
             SettingsScreen()
        }
}
```
### 2. Displaying the Screen

Simply call `SettingsScreen` in your Composable hierarchy.

```kotlin
setContent {
    MaterialTheme {
        SettingsScreen()
    }
}
```

## DSL Structure

### Screens

- **`createScreen`**: Standard settings screen with automatic layout and sticky headers.
- **`createCustomScreen`**: A screen where you define the layout manually using `setContent`.
- **`createAbstractScreen`**: A background screen used for storing settings that don't need a UI.

### Groups

- **`group`**: A standard collection of settings with an optional title.
- **`fragmentedGroup`**: A group that can switch between different "fragments" of settings, usually controlled by a `TabBar`.
- **`abstractGroup`**: A background group used for storing settings that don't need a UI.

### Example: Fragmented Group

```kotlin
createScreen("advanced") {
    val controller = FragmentController()
    
    fragmentedGroup("tabs_group") {
        this.controller = controller
        createTabBar("tabs", controller)
        
        fragment("Basic") {
            createSwitch("s1") { title = "Basic Switch" }
        }
        fragment("Pro") {
            createSlider("s2") { title = "Pro Slider" }
        }
    }
}
```

## Setting Dependencies (`depends`)

Make your settings reactive by using the `depends` block.

```kotlin
createSwitch("enable_advanced") { title = "Advanced Mode" }

createSlider("power_level") {
    title = "Power Level"
    depends {
        subscribe<Switch>("enable_advanced") {
            visibleIf { it.value } // Only visible when Switch is ON
            enableIf { it.value }  // Only enabled when Switch is ON
            onChangeValue { setting ->
                if (!setting.value) CSB.setValue("power_level", 0f)
            }
        }
    }
}
```

## Text Translation

CSB supports flexible text translation. You can use the built-in `res()` helper or provide your own translation logic.

### 1. Built-in Translation (`res`)

The `res(R.string.id)` helper marks a string for translation. By default, CSB will look up the string resource using the application context.

```kotlin
createSwitch("notifications") {
    title = res(R.string.notif_title)
    description = res(R.string.notif_desc)
}
```

### 2. Custom Translator

If you use a custom translation system (e.g., a server-side CMS or a custom library), implement the `CSBTranslator` interface.

```kotlin
class MyCustomTranslator : CSBTranslator {
    @Composable
    override fun translate(key: String): String {
        return key // Translation logic 
    }
}

// Register in config
CSB.config {
    translator = MyCustomTranslator()
}
```

## Programmatic API

### The `CSB` Object

Access your settings from anywhere in your app:

- `CSB.getValue<T>(id)`: Returns a `StateFlow<T>` of the setting value.
- `CSB.setValue(id, value)`: Updates the setting value programmatically.
- `CSB.navigateToScreen(id)`: Changes the current visible screen.
- `CSB.resetAllSettingsToDefault()`: Resets all registered settings.

### Controllers

Gain fine-grained control over UI components:

- `GroupController`: Show/hide or enable/disable entire groups.
- `FragmentController`: Switch active fragments programmatically.
- `ScreenController`: Control screen-specific behavior.

```kotlin
CSB.groupController("general").isShow(false)
CSB.fragmentController("tabs_group").setFragment("Pro")
```

## Styling

CSB uses the `SettingStyle` interface to define the look and feel of the settings UI.

### 1. Built-in Styles

You can choose from several pre-defined styles provided by `CSBStyle`:

- `CSBStyle.Material3()`: The default theme following Material Design 3 guidelines.
- `CSBStyle.Bobble()`: A more rounded, "bubbly" theme.
- `CSBStyle.ClassicLight` / `ClassicDark`: A traditional settings look.

Pass the style to the `SettingsScreen` composable:

```kotlin
SettingsScreen(
    style = CSBStyle.Bobble()
)
```

### 2. Custom Styling

To create your own style, extend `DefaultSettingStyle` and override the properties you want to change.

```kotlin
val myStyle = object : DefaultSettingStyle() {
    override var activeColor = Color.Red
    override var containerCorner = 0.dp
    // Override colors, shapes, typography, or even layout slots
}

SettingsScreen(style = myStyle)
```

| CSB library with custom style | Original pixel settings |
|:-|:-|
| <img width="320" height="720" alt="CSB" src="https://github.com/user-attachments/assets/537863f1-a870-44fe-b2e3-a60569367885" /> | <img width="320" height="720" alt="Pixel" src="https://github.com/user-attachments/assets/f7bdd8af-11f7-4569-841c-6c4cc9374bbb" />

## Setting Types Reference

| Function | Type | Description |
| :--- | :--- | :--- |
| `createSwitch` | `Boolean` | Toggle, Radio, or Checkbox. |
| `createSlider` | `Float` | Single range selection. |
| `createRangeSlider`| `Range` | Multi-point range selection. |
| `createCounter` | `Int` | Increment/Decrement counter. |
| `createSelect` | `Option` | Single choice from a list. |
| `createMultiplySelect`| `List<Option>`| Multiple choices from a list. |
| `createColorPicker` | `Color` | HSV/RGB color selector. |
| `createTimePicker` | `LocalTime` | Time selection dialog. |
| `createDatePicker` | `LocalDate` | Date selection dialog. |
| `createTextField` | `String` | Standard text input. |
| `createPasswordField`| `String` | Secure text input. |
| `createSearchField` | `String` | Expandable search bar. |
| `createRatingBar` | `Int` | Star-based rating. |
| `crateProgressBar` | `Float` | Visual progress indicator. |
| `createContentChoice`| `String` | Choice with custom Icon/UI. |
| `createTabBar` | `String` | Horizontal tab navigation. |
| `createAction` | `Unit` | Button to trigger a function. |
| `createRedirect` | `String` | Navigation link to another screen. |
| `createInfo` | `Unit` | Display-only info text. |
| `createUI` | `Unit` | Custom Composable block. |

## Configuration Flags

Use `CSB.config { flag("...") }` to tweak library behavior:

- `disableStored`: Disables automatic persistence.
- `useJsonSaveMethod`: Saves settings as JSON files instead of DataStore.
- `useOneFileJsonSaveMethod`: Combines all settings into a single JSON file.
- `ignoreSettingNotFoundError`: Prevents crashes if a setting ID is missing.
- `allowDisplayAbstractScreen`: Renders abstract screens for debugging.

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
