package com.example.model

enum class VpnStatus {
    DISCONNECTED,
    CONNECTING,
    CONNECTED,
    DISCONNECTING
}

enum class ServerCategory(val label: String) {
    ALL("All"),
    EUROPE("Europe"),
    AMERICAS("Americas"),
    ASIA_PACIFIC("Asia-Pac"),
    GAMING("Low Ping"),
    STREAMING("Streaming")
}

data class VpnServer(
    val id: String,
    val name: String,
    val city: String,
    val country: String,
    val countryCode: String,
    val flagEmoji: String,
    val ipAddress: String,
    val pingMs: Int,
    val loadPercent: Int,
    val category: ServerCategory,
    val isFavorite: Boolean = false,
    val isRecommended: Boolean = false
)

data class TelemetryData(
    val durationSeconds: Long = 0,
    val downloadSpeedMbps: Float = 0f,
    val uploadSpeedMbps: Float = 0f,
    val totalDownloadMb: Float = 0f,
    val totalUploadMb: Float = 0f,
    val currentPingMs: Int = 22,
    val virtualIp: String = "185.220.101.5",
    val originalIp: String = "79.132.241.98"
) {
    fun formattedDuration(): String {
        val hours = durationSeconds / 3600
        val minutes = (durationSeconds % 3600) / 60
        val seconds = durationSeconds % 60
        return if (hours > 0) {
            String.format("%02d:%02d:%02d", hours, minutes, seconds)
        } else {
            String.format("%02d:%02d", minutes, seconds)
        }
    }
}

data class AppItem(
    val packageName: String,
    val appName: String,
    val isBypassing: Boolean = false
)

data class VpnSettings(
    val killSwitchEnabled: Boolean = true,
    val splitTunnelingEnabled: Boolean = false,
    val selectedProtocol: String = "WireGuard",
    val autoConnectOnWifi: Boolean = false,
    val dnsProtectionEnabled: Boolean = true,
    val selectedDns: String = "Cloudflare (1.1.1.1)",
    val mtuSize: Int = 1420,
    val splitTunnelApps: List<AppItem> = listOf(
        AppItem("com.android.chrome", "Google Chrome", false),
        AppItem("com.spotify.music", "Spotify", true),
        AppItem("com.netflix.mediaclient", "Netflix", false),
        AppItem("com.google.android.youtube", "YouTube", false),
        AppItem("com.whatsapp", "WhatsApp", false),
        AppItem("com.telegram.messenger", "Telegram", false),
        AppItem("com.paypal.android.p2pmobile", "PayPal", true),
        AppItem("com.binance.dev", "Binance", true)
    )
)

enum class NavTab(val title: String) {
    HOME("Home"),
    SERVERS("Servers"),
    SETTINGS("Settings")
}
