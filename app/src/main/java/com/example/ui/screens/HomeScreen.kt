package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Security
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.TelemetryData
import com.example.model.VpnServer
import com.example.model.VpnSettings
import com.example.model.VpnStatus
import com.example.ui.components.ServerSelectorRow
import com.example.ui.components.SquircleConnectButton
import com.example.ui.components.StatusBadgeCard
import com.example.ui.components.TelemetryCards
import com.example.ui.components.TopHeaderBar
import com.example.ui.theme.CyanNeon
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkCardBorder
import com.example.ui.theme.DarkSurfaceCard
import com.example.ui.theme.MintGreen
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun HomeScreen(
    status: VpnStatus,
    selectedServer: VpnServer,
    telemetry: TelemetryData,
    settings: VpnSettings,
    onToggleConnect: () -> Unit,
    onOpenServerPicker: () -> Unit,
    onDiagnose: () -> Unit,
    onResetStats: () -> Unit,
    onAbout: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
            .statusBarsPadding()
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp)
            .testTag("home_screen_content"),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top session header bar
        TopHeaderBar(
            onDiagnose = onDiagnose,
            onResetStats = onResetStats,
            onAbout = onAbout
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Top Status Badge Card ("Not protected" / "Protected • WireGuard")
        StatusBadgeCard(
            status = status,
            protocol = settings.selectedProtocol
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Server selector row ("Server Name light Server ⌄")
        ServerSelectorRow(
            server = selectedServer,
            onClick = onOpenServerPicker
        )

        Spacer(modifier = Modifier.height(22.dp))

        // Center Signature Squircle Connect Button
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            SquircleConnectButton(
                status = status,
                onToggle = onToggleConnect
            )
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Subtitle status indicator
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            val indicatorColor = when (status) {
                VpnStatus.DISCONNECTED -> Color(0xFFFF5252)
                VpnStatus.CONNECTING -> CyanNeon
                VpnStatus.CONNECTED -> MintGreen
                VpnStatus.DISCONNECTING -> Color(0xFFFBBF24)
            }
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .background(indicatorColor, CircleShape)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = when (status) {
                    VpnStatus.DISCONNECTED -> "Traffic route is unprotected"
                    VpnStatus.CONNECTING -> "Synchronizing encryption keys..."
                    VpnStatus.CONNECTED -> "Tunnel active • All traffic encrypted"
                    VpnStatus.DISCONNECTING -> "Closing secure socket..."
                },
                style = MaterialTheme.typography.bodySmall.copy(
                    color = TextSecondary,
                    fontSize = 13.sp
                )
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Live Telemetry Cards
        TelemetryCards(
            status = status,
            telemetry = telemetry
        )

        if (status == VpnStatus.CONNECTED) {
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Security Features Quick Badge
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(DarkSurfaceCard.copy(alpha = 0.6f))
                .border(1.dp, DarkCardBorder.copy(alpha = 0.7f), RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Outlined.Security,
                        contentDescription = "Shield Guard",
                        tint = if (settings.killSwitchEnabled) MintGreen else TextMuted,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Kill Switch Safeguard",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = TextPrimary
                            )
                        )
                        Text(
                            text = if (settings.killSwitchEnabled) "Active • Leak prevention on" else "Disabled in settings",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = if (settings.killSwitchEnabled) MintGreen else TextSecondary,
                                fontSize = 12.sp
                            )
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Protocol",
                        tint = CyanNeon,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = settings.selectedProtocol,
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = CyanNeon,
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}
