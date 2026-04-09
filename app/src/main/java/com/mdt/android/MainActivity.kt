package com.mdt.android

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.mdt.android.ui.MdtApp
import com.mdt.android.ui.theme.MDTTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MDTTheme {
                var hasCallLogPermission by rememberSaveable {
                    mutableStateOf(hasPermission(Manifest.permission.READ_CALL_LOG))
                }
                var hasPhoneStatePermission by rememberSaveable {
                    mutableStateOf(hasPermission(Manifest.permission.READ_PHONE_STATE))
                }
                var hasNotificationPermission by rememberSaveable {
                    mutableStateOf(
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            hasPermission(Manifest.permission.POST_NOTIFICATIONS)
                        } else true
                    )
                }

                val callLogLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.RequestPermission()
                ) { granted ->
                    hasCallLogPermission = granted
                }

                val phoneStateLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.RequestPermission()
                ) { granted ->
                    hasPhoneStatePermission = granted
                }

                val notificationLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.RequestPermission()
                ) { granted ->
                    hasNotificationPermission = granted
                }

                LaunchedEffect(Unit) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && !hasNotificationPermission) {
                        notificationLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    }
                }

                MdtApp(
                    activity = this,
                    hasCallLogPermission = hasCallLogPermission,
                    hasPhoneStatePermission = hasPhoneStatePermission,
                    onRequestCallLogPermission = {
                        callLogLauncher.launch(Manifest.permission.READ_CALL_LOG)
                    },
                    onRequestPhoneStatePermission = {
                        phoneStateLauncher.launch(Manifest.permission.READ_PHONE_STATE)
                    }
                )
            }
        }
    }
}
