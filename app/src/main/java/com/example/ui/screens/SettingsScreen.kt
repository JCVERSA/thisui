package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AltRoute
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.VpnSettings
import com.example.ui.components.AnimateEntrance
import com.example.ui.components.SlateMark
import com.example.ui.components.SlateMorphIcon
import com.example.ui.components.SlateMorphIconType
import com.example.ui.components.SwiftSlateToggle
import com.example.ui.components.bounceClick
import com.example.ui.theme.CyanNeon
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkCardBorder
import com.example.ui.theme.DarkSurfaceCard
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.MintGreen
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun SettingsScreen(
    settings: VpnSettings,
    onUpdateKillSwitch: (Boolean) -> Unit,
    onUpdateSplitTunneling: (Boolean) -> Unit,
    onUpdateProtocol: (String) -> Unit,
    onUpdateAutoConnect: (Boolean) -> Unit,
    onUpdateDnsProtection: (Boolean) -> Unit,
    onUpdateSelectedDns: (String) -> Unit,
    onToggleAppBypass: (String) -> Unit,
    onRunDiagnostics: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    var showDnsOptions by remember { mutableStateOf(false) }
    var showSplitTunnelApps by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
            .statusBarsPadding()
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp)
            .testTag("settings_screen_content")
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "VPN Settings",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp,
                        color = TextPrimary
                    )
                )
                Text(
                    text = "Customize connection security, protocol, and tunneling",
                    style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary),
                    modifier = Modifier.padding(top = 2.dp)
                )
            }

            SlateMark(
                size = 32.dp,
                tint = CyanNeon
            )
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Section: Core Security
        SettingsSectionHeader(title = "SECURITY SAFEGUARDS")

        // Kill Switch Toggle
        SettingsToggleItem(
            icon = Icons.Default.Security,
            title = "Kill Switch",
            subtitle = "Block all internet traffic if VPN disconnects unexpectedly",
            isChecked = settings.killSwitchEnabled,
            onCheckedChange = onUpdateKillSwitch,
            iconTint = if (settings.killSwitchEnabled) MintGreen else TextMuted,
            testTag = "kill_switch_toggle"
        )

        Spacer(modifier = Modifier.height(10.dp))

        // DNS Leak Protection
        SettingsToggleItem(
            icon = Icons.Default.Dns,
            title = "DNS Leak Protection",
            subtitle = "Routes DNS queries exclusively through encrypted private servers",
            isChecked = settings.dnsProtectionEnabled,
            onCheckedChange = onUpdateDnsProtection,
            iconTint = CyanNeon,
            testTag = "dns_protection_toggle"
        )

        // Custom DNS Selector with SwiftSlate Morph Chevron
        if (settings.dnsProtectionEnabled) {
            Spacer(modifier = Modifier.height(6.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(DarkSurfaceVariant.copy(alpha = 0.5f))
                    .border(1.dp, DarkCardBorder, RoundedCornerShape(14.dp))
                    .bounceClick { showDnsOptions = !showDnsOptions }
                    .padding(14.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Selected DNS Provider",
                            style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = settings.selectedDns,
                                style = MaterialTheme.typography.labelMedium.copy(
                                    color = CyanNeon,
                                    fontWeight = FontWeight.SemiBold
                                )
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            SlateMorphIcon(
                                type = SlateMorphIconType.ExpandCollapse,
                                toggled = showDnsOptions,
                                modifier = Modifier.size(16.dp),
                                tint = CyanNeon,
                                contentDescription = "Toggle DNS Options"
                            )
                        }
                    }

                    AnimatedVisibility(
                        visible = showDnsOptions,
                        enter = expandVertically(),
                        exit = shrinkVertically()
                    ) {
                        Column(modifier = Modifier.padding(top = 10.dp)) {
                            listOf(
                                "Cloudflare (1.1.1.1)",
                                "Google (8.8.8.8)",
                                "Quad9 (9.9.9.9)"
                            ).forEach { dnsName ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .bounceClick { onUpdateSelectedDns(dnsName) }
                                        .padding(vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    RadioButton(
                                        selected = settings.selectedDns == dnsName,
                                        onClick = { onUpdateSelectedDns(dnsName) },
                                        colors = RadioButtonDefaults.colors(
                                            selectedColor = CyanNeon,
                                            unselectedColor = TextMuted
                                        )
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = dnsName,
                                        style = MaterialTheme.typography.bodyMedium.copy(color = TextPrimary)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Auto-Connect on Wi-Fi
        SettingsToggleItem(
            icon = Icons.Default.Wifi,
            title = "Auto-Connect on Wi-Fi",
            subtitle = "Automatically establish encrypted tunnel on public networks",
            isChecked = settings.autoConnectOnWifi,
            onCheckedChange = onUpdateAutoConnect,
            iconTint = Color(0xFF38BDF8),
            testTag = "auto_connect_toggle"
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Section: Routing & Tunneling
        SettingsSectionHeader(title = "TUNNELING & PROTOCOL")

        // Split Tunneling Item
        SettingsToggleItem(
            icon = Icons.Default.AltRoute,
            title = "Split Tunneling",
            subtitle = "Choose specific apps that bypass VPN encryption",
            isChecked = settings.splitTunnelingEnabled,
            onCheckedChange = {
                onUpdateSplitTunneling(it)
                if (it) showSplitTunnelApps = true
            },
            iconTint = Color(0xFFFBBF24),
            testTag = "split_tunneling_toggle"
        )

        // Split Tunneling App List with SwiftSlate Morph Chevron
        if (settings.splitTunnelingEnabled) {
            Spacer(modifier = Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(DarkSurfaceVariant.copy(alpha = 0.5f))
                    .border(1.dp, DarkCardBorder, RoundedCornerShape(14.dp))
                    .padding(14.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Apps Bypassing VPN (${settings.splitTunnelApps.count { it.isBypassing }})",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = TextPrimary
                            )
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.bounceClick { showSplitTunnelApps = !showSplitTunnelApps }
                        ) {
                            Text(
                                text = if (showSplitTunnelApps) "Collapse" else "Configure",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = CyanNeon,
                                    fontWeight = FontWeight.SemiBold
                                )
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            SlateMorphIcon(
                                type = SlateMorphIconType.ExpandCollapse,
                                toggled = showSplitTunnelApps,
                                modifier = Modifier.size(16.dp),
                                tint = CyanNeon,
                                contentDescription = "Toggle App List"
                            )
                        }
                    }

                    AnimatedVisibility(
                        visible = showSplitTunnelApps,
                        enter = expandVertically(),
                        exit = shrinkVertically()
                    ) {
                        Column(
                            modifier = Modifier.padding(top = 10.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            settings.splitTunnelApps.forEachIndexed { idx, app ->
                                AnimateEntrance(index = idx) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(DarkSurfaceCard)
                                            .padding(horizontal = 12.dp, vertical = 8.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = app.appName,
                                                style = MaterialTheme.typography.bodyMedium.copy(
                                                    fontWeight = FontWeight.Medium,
                                                    color = TextPrimary
                                                )
                                            )
                                            Text(
                                                text = if (app.isBypassing) "Bypasses VPN tunnel" else "Protected by VPN",
                                                style = MaterialTheme.typography.bodySmall.copy(
                                                    color = if (app.isBypassing) Color(0xFFFBBF24) else MintGreen,
                                                    fontSize = 11.sp
                                                )
                                            )
                                        }

                                        SwiftSlateToggle(
                                            checked = app.isBypassing,
                                            onCheckedChange = { onToggleAppBypass(app.packageName) },
                                            activeColor = Color(0xFFFBBF24),
                                            testTag = "app_bypass_${app.packageName}"
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Protocol Selector Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(DarkSurfaceCard)
                .border(1.dp, DarkCardBorder, RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(CyanNeon.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.VpnKey,
                            contentDescription = "Protocol",
                            tint = CyanNeon,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Tunneling Protocol",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = TextPrimary
                            )
                        )
                        Text(
                            text = "Active: ${settings.selectedProtocol}",
                            style = MaterialTheme.typography.bodySmall.copy(color = CyanNeon)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                listOf(
                    Pair("WireGuard", "Next-gen cryptography, lightweight, fastest throughput"),
                    Pair("OpenVPN (UDP)", "Optimized for gaming, low latency video streaming"),
                    Pair("OpenVPN (TCP)", "Resilient firewall traversal, guaranteed packet delivery")
                ).forEach { (protocolName, desc) ->
                    val isSelected = settings.selectedProtocol.startsWith(protocolName.split(" ")[0])
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isSelected) DarkSurfaceVariant.copy(alpha = 0.6f) else Color.Transparent)
                            .bounceClick { onUpdateProtocol(protocolName) }
                            .padding(horizontal = 10.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = isSelected,
                            onClick = { onUpdateProtocol(protocolName) },
                            colors = RadioButtonDefaults.colors(
                                selectedColor = CyanNeon,
                                unselectedColor = TextMuted
                            )
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = protocolName,
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    color = TextPrimary
                                )
                            )
                            Text(
                                text = desc,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = TextSecondary,
                                    fontSize = 11.sp
                                )
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Section: Tools & Diagnostics
        SettingsSectionHeader(title = "DIAGNOSTICS & SYSTEM")

        // Network Benchmark Button
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(DarkSurfaceCard)
                .border(1.dp, DarkCardBorder, RoundedCornerShape(16.dp))
                .bounceClick(onClick = onRunDiagnostics)
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(MintGreen.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Speed,
                            contentDescription = "Test",
                            tint = MintGreen,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Test Network Latency",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = TextPrimary
                            )
                        )
                        Text(
                            text = "Pings all regional nodes for optimal routing",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = TextSecondary,
                                fontSize = 12.sp
                            )
                        )
                    }
                }

                Text(
                    text = "RUN",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = MintGreen,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // About VPNSXB
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(DarkSurfaceCard)
                .border(1.dp, DarkCardBorder, RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(DarkSurfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "About",
                        tint = TextSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "VPNSXB • Deck Edition",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = TextPrimary
                        )
                    )
                    Text(
                        text = "Version 1.0.0 • Strict Zero-Log Policy",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = TextSecondary,
                            fontSize = 12.sp
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
private fun SettingsSectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelSmall.copy(
            color = CyanNeon,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        ),
        modifier = Modifier.padding(vertical = 8.dp)
    )
}

@Composable
private fun SettingsToggleItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    iconTint: Color,
    testTag: String
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(DarkSurfaceCard)
            .border(1.dp, DarkCardBorder, RoundedCornerShape(16.dp))
            .padding(horizontal = 16.dp, vertical = 14.dp)
            .testTag(testTag)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(iconTint.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = iconTint,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = TextPrimary
                        )
                    )
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = TextSecondary,
                            fontSize = 12.sp
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.width(10.dp))

            SwiftSlateToggle(
                checked = isChecked,
                onCheckedChange = onCheckedChange,
                activeColor = iconTint,
                testTag = testTag
            )
        }
    }
}
