# Migration Guide: Kotlin & Jetpack Compose to Java & XML

This document outlines the step-by-step process required to migrate the **MDT - Mobile Diagnostic Tool** from its current modern Android stack (Kotlin + Jetpack Compose) to the traditional Android stack (Java + XML).

## Phase 1: Build Configuration Changes

You will need to update your `build.gradle.kts` (or convert it to `build.gradle` Groovy) to strip out Compose and Kotlin-specific configurations, while adding traditional Java UI dependencies.

### 1. Root `build.gradle.kts`
Remove the Kotlin plugins:
```diff
plugins {
    id("com.android.application") version "8.5.2" apply false
-   id("org.jetbrains.kotlin.android") version "2.0.20" apply false
-   id("org.jetbrains.kotlin.plugin.compose") version "2.0.20" apply false
}
```

### 2. App-Level `app/build.gradle.kts`
Remove Compose configurations and Kotlin dependencies:
```diff
plugins {
    id("com.android.application")
-   id("org.jetbrains.kotlin.android")
-   id("org.jetbrains.kotlin.plugin.compose")
}

android {
-   buildFeatures {
-       compose = true
-   }
+   buildFeatures {
+       viewBinding = true
+   }
}

dependencies {
    // Remove all Compose dependencies
-   implementation(platform("androidx.compose:compose-bom:2024.09.00"))
-   implementation("androidx.compose.ui:ui")
-   implementation("androidx.compose.material3:material3")
-   implementation("androidx.activity:activity-compose:1.9.2")

    // Add traditional View dependencies
+   implementation("androidx.appcompat:appcompat:1.6.1")
+   implementation("com.google.android.material:material:1.12.0")
+   implementation("androidx.constraintlayout:constraintlayout:2.1.4")
}
```

---

## Phase 2: Restructuring the Source Code 

Instead of declarative Kotlin UI functions, you will use XML layouts managed by Java Activity classes.

### 1. Remove Kotlin Files
Delete all `.kt` files from `app/src/main/java/com/mdt/android/`.

### 2. Create Java Source Files
Create a new `MainActivity.java` inside `app/src/main/java/com/mdt/android/`:

```java
package com.mdt.android;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // We will inflate the new XML layout here
        setContentView(R.layout.activity_main);
    }
}
```

---

## Phase 3: Building the XML UI

Jetpack Compose encapsulates the UI in Kotlin functions. In Java, UI elements go into `res/layout/*.xml`.

### 1. Base Layout
Create `app/src/main/res/layout/activity_main.xml`. Setup a `ConstraintLayout` or `LinearLayout` instead of your unified Compose dashboard:

```xml
<?xml version="1.0" encoding="utf-8"?>
<androidx.constraintlayout.widget.ConstraintLayout 
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="match_parent">

    <TextView
        android:id="@+id/tv_device_info"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="Hardware and Software details loading..."
        app:layout_constraintTop_toTopOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        android:layout_margin="16dp"/>

</androidx.constraintlayout.widget.ConstraintLayout>
```

---

## Phase 4: Translating the Logic to Java

**Permissions & Callbacks:**
Instead of Compose side-effects (`LaunchedEffect`) and Accompanist permissions, use traditional Activity Result APIs in Java:
```java
private final ActivityResultLauncher<String> requestPermissionLauncher =
    registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
        if (isGranted) {
            // Permission granted, access Call Logs, Telephony, etc.
        } else {
            // Permission denied
        }
    });

// To request:
requestPermissionLauncher.launch(Manifest.permission.READ_PHONE_STATE);
```

**Sensor Managers & Hardware:**
The APIs (e.g., `SensorManager`, `BatteryManager`, `TelephonyManager`) remain exactly the same, but the syntax will use classic Java getter/setter conventions.
```java
SensorManager sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
Sensor accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
```

## Phase 5: Replacing Coroutines

Any asynchronous work you previously had running using Kotlin Coroutines (`suspend` functions, `viewModelScope.launch`) will need to be transitioned into Java equivalents. You can utilize:
- `java.util.concurrent.Executors`
- RxJava
- Java `CompletableFuture` (for newer API targets)
