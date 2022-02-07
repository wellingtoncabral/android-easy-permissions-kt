# Easy Permissions Kt
![Language](https://img.shields.io/github/languages/top/cortinico/kotlin-android-template?color=blue&logo=kotlin) [![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://github.com/wellingtoncabral/android-easy-permissions-kt/blob/main/LICENSE) ![JitPack](https://jitpack.io/v/wellingtoncabral/android-easy-permissions-kt.svg) [![ktlint](https://img.shields.io/badge/code%20style-%E2%9D%A4-FF4081.svg)](https://ktlint.github.io/)

EasyPermissionsKt is a lightweight Android library that abstracts all runtime permission boilerplate code to simplify the system permissions management.
The lib is lifecycle aware and uses the new recommended way to get results from activities (https://developer.android.com/training/basics/intents/result?hl=pt-br)

## How to add
Step 1. Add the JitPack repository to your build file

```groovy
allprojects {
    repositories {
      ...
      maven { url 'https://jitpack.io' }
    }
}
```

Step 2. Add the dependency

#### Gradle:

```groovy
dependencies {
    implementation 'com.github.wellingtoncabral:android-easy-permissions-kt:<LATEST-VERSION>'
}
```

#### Kotlin:

```kotlin
dependencies {
    implementation ("com.github.wellingtoncabral:android-easy-permissions-kt:$LATEST-VERSION"
}
```

## How to use
Register the EasyPermissionKt and implement the result callback. The result can be handled in 3 states as described below:

#### Using from Activities

```kotlin
class MyActivity : AppCompatActivity() {
    ...
    private val easyPermission = registerForPermissionsResult {
        whenPermissionGranted {
            // All permissions granted
            TODO()
        }
        whenPermissionShouldShowRationale { permissions ->
            // Permissions denied but not permanently.
            // The app should show a rationale to the user in
            // a UI element.
            // @see [https://developer.android.com/training/permissions/requesting#explain]
            TODO()
        }
        whenPermissionDeniedPermanently { permissions ->
            // Permission denied permanently
            TODO()
        }
    }   
}
```

#### Using from Fragments

```kotlin
// Using from Fragment
class MyFragment : Fragment() {
    
    private lateinit var easyPermission: EasyPermissionKt

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    
        easyPermission = registerForPermissionsResult {
            whenPermissionGranted {
                TODO()
            }
            whenPermissionShouldShowRationale { permissions ->
                TODO()
            }
            whenPermissionDeniedPermanently { permissions ->
                TODO()
            }
        }
    }
}
```

To request permissions, just call the `requestPermissions` function passing one or more permissions as shown below:

```kotlin
// Requesting permission or permissions
easyPermission.requestPermissions(
    Manifest.permission.CAMERA,
    Manifest.permission.WRITE_EXTERNAL_STORAGE
)
```

It is also possible to create an explanatory UI for the user before asking for permission.
The implementation below uses `WithDialog` which creates a native `AlertDialog`.

```kotlin
// Requesting permission with an explain why dialog
easyPermission
    .explainWhy( 
        WithDialog(
            title = "Permission Request",
            description = "To easily connect with family and friends, allow the app access to your contacts",
            positiveButtonText = "Continue",
            negativeButtonText = "Not now"
        )
    )
    .requestPermissions(Manifest.permission.READ_CONTACTS)
```

However, you can create any UI. Just create a class and implement the `ExplainWhyUI` interface.
In the `show()` method use the lambda `continuation` parameter to decide if the API continues with the request or cancels.

### Features
The EasyPermissionKt library supports the following features:

```kotlin
fun requestPermissions(vararg permissions: String)
fun hasPermissionFor(vararg permissions: String): Boolean
fun shouldShowRequestPermissionRationale(permission: String): Boolean
fun explainWhy(explainWhyUI: ExplainWhyUI): EasyPermissionKt
```
