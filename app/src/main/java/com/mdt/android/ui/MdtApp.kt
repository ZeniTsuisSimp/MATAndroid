package com.mdt.android.ui

import android.app.Activity
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Assessment
import androidx.compose.material.icons.rounded.Description
import androidx.compose.material.icons.rounded.Memory
import androidx.compose.material.icons.rounded.PermDeviceInformation
import androidx.compose.material.icons.rounded.Sensors
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.mdt.android.data.CallLogSummary
import com.mdt.android.data.DashboardSnapshot
import com.mdt.android.data.DiagnosticsRepository
import com.mdt.android.data.SensorGroup

@Composable
fun MdtApp(
    activity: Activity,
    hasCallLogPermission: Boolean,
    hasPhoneStatePermission: Boolean,
    onRequestCallLogPermission: () -> Unit,
    onRequestPhoneStatePermission: () -> Unit
) {
    val repository = remember(activity) { DiagnosticsRepository(activity) }
    val snapshot = remember(hasCallLogPermission, hasPhoneStatePermission) {
        repository.loadDashboardData(hasCallLogPermission, hasPhoneStatePermission)
    }
    var selectedTab by rememberSaveable { mutableIntStateOf(0) }

    Scaffold(
        containerColor = Color(0xFFF5EFE3),
        bottomBar = {
            NavigationBar(
                modifier = Modifier.navigationBarsPadding(),
                containerColor = Color(0xFFFDF8F1)
            ) {
                screens.forEachIndexed { index, screen ->
                    NavigationBarItem(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        icon = { Icon(screen.icon, contentDescription = screen.label) },
                        label = { Text(screen.label) }
                    )
                }
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color(0xFFE8F1DD), Color(0xFFF5EFE3), Color(0xFFF7F2EC))
                    )
                )
                .padding(padding)
        ) {
            when (selectedTab) {
                0 -> OverviewScreen(snapshot)
                1 -> DeviceScreen(snapshot, hasPhoneStatePermission, onRequestPhoneStatePermission)
                2 -> SensorsScreen(snapshot.sensors)
                3 -> StorageScreen(snapshot, hasCallLogPermission, onRequestCallLogPermission)
                else -> ReportScreen(activity, snapshot, repository)
            }
        }
    }
}

private data class MdtScreen(val label: String, val icon: ImageVector)

private val screens = listOf(
    MdtScreen("Overview", Icons.Rounded.Assessment),
    MdtScreen("Device", Icons.Rounded.PermDeviceInformation),
    MdtScreen("Sensors", Icons.Rounded.Sensors),
    MdtScreen("Storage", Icons.Rounded.Memory),
    MdtScreen("Report", Icons.Rounded.Description)
)

@Composable
private fun OverviewScreen(snapshot: DashboardSnapshot) {
    LazyColumn(
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            HeroCard(
                title = "Mobile Diagnostic Tool",
                subtitle = "A quick health snapshot of your Android phone, from battery and storage to sensor readiness.",
                eyebrow = "Generated ${snapshot.generatedAt}"
            )
        }
        item {
            MetricRow(
                listOf(
                    "Battery" to snapshot.battery.percentage,
                    "Connection" to snapshot.network.connection,
                    "RAM" to snapshot.device.totalRam
                )
            )
        }
        item {
            SectionCard("Priority Checks", "Only the most useful signals are shown first.") {
                SpecRow("Battery health", snapshot.battery.health)
                SpecRow("Charging state", snapshot.battery.chargingState)
                SpecRow("Internal free", snapshot.storage.internalFree)
                SpecRow("Primary network", snapshot.network.networkType)
            }
        }
        item {
            SectionCard("Hardware Snapshot", "Core details for device verification.") {
                SpecRow("Device", snapshot.device.deviceName)
                SpecRow("Android", snapshot.device.androidVersion)
                SpecRow("CPU", snapshot.device.cpuAbi)
                SpecRow("Cameras", snapshot.device.cameraCount)
            }
        }
    }
}

@Composable
private fun DeviceScreen(
    snapshot: DashboardSnapshot,
    hasPhoneStatePermission: Boolean,
    onRequestPhoneStatePermission: () -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            HeroCard(
                title = "Mobile Info & Spec",
                subtitle = "Software, hardware, battery, network, camera, sound, and screen readiness in one place.",
                eyebrow = "Part 2"
            )
        }
        item {
            SectionCard("Device Identity", "High-signal info for support and inventory.") {
                SpecRow("Device name", snapshot.device.deviceName)
                SpecRow("Model number", snapshot.device.modelNumber)
                SpecRow("Brand", snapshot.device.brand)
                SpecRow("Product", snapshot.device.product)
                SpecRow("Hardware", snapshot.device.hardware)
                SpecRow("Board", snapshot.device.board)
                PermissionPrompt(
                    visible = !hasPhoneStatePermission,
                    message = "Phone identifier and advanced network fields need phone state access.",
                    action = "Allow access",
                    onAction = onRequestPhoneStatePermission
                )
                SpecRow("IMEI / MEID", snapshot.device.phoneIdentifier)
            }
        }
        item {
            SectionCard("Software & Hardware", "Real device information from Android APIs.") {
                SpecRow("Android version", snapshot.device.androidVersion)
                SpecRow("Kernel version", snapshot.device.kernelVersion)
                SpecRow("CPU ABI", snapshot.device.cpuAbi)
                SpecRow("Total RAM", snapshot.device.totalRam)
                SpecRow("Internal storage", snapshot.device.totalStorage)
            }
        }
        item {
            SectionCard("Battery & Network", "Quick checks for everyday device health.") {
                SpecRow("Battery level", snapshot.battery.percentage)
                SpecRow("Battery health", snapshot.battery.health)
                SpecRow("Battery temp", snapshot.battery.temperature)
                SpecRow("Connection", snapshot.network.connection)
                SpecRow("Network type", snapshot.network.networkType)
                SpecRow("Roaming", snapshot.network.roaming)
            }
        }
        item {
            SectionCard("Media Components", "Presence checks for camera and output-related hardware.") {
                val cameraText = if (snapshot.device.cameraCount == "0") "Not detected" else "${snapshot.device.cameraCount} camera feature(s)"
                SpecRow("Camera support", cameraText)
                SpecRow("Display test", "Ready for manual visual inspection")
                SpecRow("Sound test", "Ready for manual speaker / mic workflow")
            }
        }
    }
}

@Composable
private fun SensorsScreen(sensorGroups: List<SensorGroup>) {
    LazyColumn(
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            HeroCard(
                title = "Sensors Test",
                subtitle = "Motion, position, and environmental sensors are grouped for fast scanning.",
                eyebrow = "Part 3"
            )
        }
        items(sensorGroups) { group ->
            ExpandableSensorCard(group)
        }
    }
}

@Composable
private fun StorageScreen(
    snapshot: DashboardSnapshot,
    hasCallLogPermission: Boolean,
    onRequestCallLogPermission: () -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            HeroCard(
                title = "Data Storage & Memory",
                subtitle = "Storage usage, external memory visibility, and call-log insights with guarded permissions.",
                eyebrow = "Part 4"
            )
        }
        item {
            SectionCard("Memory Summary", "Internal and external storage health at a glance.") {
                SpecRow("Internal used", snapshot.storage.internalUsed)
                SpecRow("Internal free", snapshot.storage.internalFree)
                SpecRow("Internal total", snapshot.storage.internalTotal)
                SpecRow("External free", snapshot.storage.externalFree)
                SpecRow("External total", snapshot.storage.externalTotal)
            }
        }
        item {
            SectionCard("Call Logs", "Progressive disclosure keeps sensitive data permission-based.") {
                PermissionPrompt(
                    visible = !hasCallLogPermission,
                    message = "Grant call log access to inspect recent communication history.",
                    action = "Enable call logs",
                    onAction = onRequestCallLogPermission
                )
                CallLogPanel(snapshot.storage.callLogSummary)
            }
        }
    }
}

@Composable
private fun ReportScreen(
    activity: Activity,
    snapshot: DashboardSnapshot,
    repository: DiagnosticsRepository
) {
    val report = remember(snapshot) { repository.buildReport(snapshot) }

    LazyColumn(
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            HeroCard(
                title = "Diagnostic Report",
                subtitle = "Generate a ready-to-share phone status summary from the current session.",
                eyebrow = "Shareable output"
            )
        }
        item {
            SectionCard("Current Report", "This can be exported to mail, notes, or support channels.") {
                Text(text = report, style = MaterialTheme.typography.bodyMedium, color = Color(0xFF2C3026))
            }
        }
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF20352B)),
                shape = RoundedCornerShape(24.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Export report", style = MaterialTheme.typography.titleMedium, color = Color(0xFFF7F2EC))
                        Text(
                            "Share diagnostics with support or save them for maintenance records.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFFD5DFC8)
                        )
                    }
                    TextButton(onClick = {
                        activity.startActivity(
                            Intent.createChooser(
                                Intent(Intent.ACTION_SEND).apply {
                                    type = "text/plain"
                                    putExtra(Intent.EXTRA_SUBJECT, "MDT Android Report")
                                    putExtra(Intent.EXTRA_TEXT, report)
                                },
                                "Share diagnostic report"
                            )
                        )
                    }) {
                        Text("Share")
                    }
                }
            }
        }
    }
}

@Composable
private fun HeroCard(title: String, subtitle: String, eyebrow: String) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF20352B)),
        shape = RoundedCornerShape(28.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(eyebrow, color = Color(0xFFD5DFC8), style = MaterialTheme.typography.labelLarge)
            Text(title, color = Color(0xFFF7F2EC), style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            Text(subtitle, color = Color(0xFFE6E3DA), style = MaterialTheme.typography.bodyLarge)
        }
    }
}

@Composable
private fun MetricRow(items: List<Pair<String, String>>) {
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        items.forEach { (title, value) ->
            Card(
                modifier = Modifier.weight(1f),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFDF8F1)),
                shape = RoundedCornerShape(22.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(title, style = MaterialTheme.typography.labelLarge, color = Color(0xFF5F6559))
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(value, style = MaterialTheme.typography.titleLarge, color = Color(0xFF20352B), fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

@Composable
private fun SectionCard(title: String, description: String, content: @Composable () -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFDF8F1)),
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(title, style = MaterialTheme.typography.titleLarge, color = Color(0xFF20352B))
                Text(description, style = MaterialTheme.typography.bodyMedium, color = Color(0xFF5F6559))
            }
            Divider(color = Color(0xFFE7DEC8))
            content()
        }
    }
}

@Composable
private fun SpecRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = label,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.bodyMedium,
            color = Color(0xFF5F6559)
        )
        Spacer(modifier = Modifier.size(16.dp))
        Text(
            text = value,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.bodyMedium,
            color = Color(0xFF20352B),
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun PermissionPrompt(
    visible: Boolean,
    message: String,
    action: String,
    onAction: () -> Unit
) {
    if (!visible) return

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color(0xFFE8F1DD),
        shape = RoundedCornerShape(18.dp)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(message, style = MaterialTheme.typography.bodyMedium, color = Color(0xFF20352B))
            Text(
                text = action,
                modifier = Modifier.clickable { onAction() },
                style = MaterialTheme.typography.labelLarge,
                color = Color(0xFF2E5A3E),
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun CallLogPanel(summary: CallLogSummary) {
    SpecRow("Total calls", summary.totalCalls)
    SpecRow("Latest activity", summary.lastCall)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ExpandableSensorCard(group: SensorGroup) {
    var expanded by rememberSaveable(group.title) { mutableIntStateOf(0) }

    Card(
        onClick = { expanded = if (expanded == 0) 1 else 0 },
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFDF8F1)),
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(group.title, style = MaterialTheme.typography.titleLarge, color = Color(0xFF20352B))
                    Text(
                        "${group.sensors.count { it.availability == "Available" }} of ${group.sensors.size} detected",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF5F6559)
                    )
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(999.dp))
                        .background(Color(0xFFE8F1DD))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(if (expanded == 0) "Show" else "Hide", color = Color(0xFF2E5A3E))
                }
            }

            if (expanded == 1) {
                group.sensors.forEach { sensor ->
                    Divider(color = Color(0xFFE7DEC8))
                    SpecRow(sensor.label, "${sensor.availability} | ${sensor.vendor}")
                }
            }
        }
    }
}
