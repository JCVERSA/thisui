package com.example.repository

import com.example.model.AppItem
import com.example.model.ServerCategory
import com.example.model.TelemetryData
import com.example.model.VpnServer
import com.example.model.VpnSettings
import com.example.model.VpnStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.random.Random

class VpnRepository {

    private val scope = CoroutineScope(Dispatchers.Default)
    private var telemetryJob: Job? = null

    private val _status = MutableStateFlow(VpnStatus.DISCONNECTED)
    val status: StateFlow<VpnStatus> = _status.asStateFlow()

    private val initialServers = listOf(
        VpnServer(
            id = "de-fra",
            name = "Frankfurt 01",
            city = "Frankfurt",
            country = "Germany",
            countryCode = "DE",
            flagEmoji = "🇩🇪",
            ipAddress = "185.170.114.12",
            pingMs = 18,
            loadPercent = 38,
            category = ServerCategory.EUROPE,
            isRecommended = true
        ),
        VpnServer(
            id = "nl-ams",
            name = "Amsterdam 03",
            city = "Amsterdam",
            country = "Netherlands",
            countryCode = "NL",
            flagEmoji = "🇳🇱",
            ipAddress = "194.26.29.114",
            pingMs = 22,
            loadPercent = 45,
            category = ServerCategory.EUROPE
        ),
        VpnServer(
            id = "uk-lon",
            name = "London Central",
            city = "London",
            country = "United Kingdom",
            countryCode = "GB",
            flagEmoji = "🇬🇧",
            ipAddress = "51.89.155.84",
            pingMs = 26,
            loadPercent = 52,
            category = ServerCategory.EUROPE
        ),
        VpnServer(
            id = "ch-zur",
            name = "Zurich Ultra-Privacy",
            city = "Zurich",
            country = "Switzerland",
            countryCode = "CH",
            flagEmoji = "🇨🇭",
            ipAddress = "178.209.51.7",
            pingMs = 28,
            loadPercent = 29,
            category = ServerCategory.EUROPE
        ),
        VpnServer(
            id = "se-sto",
            name = "Stockholm Low-Ping",
            city = "Stockholm",
            country = "Sweden",
            countryCode = "SE",
            flagEmoji = "🇸🇪",
            ipAddress = "193.180.118.4",
            pingMs = 31,
            loadPercent = 34,
            category = ServerCategory.GAMING
        ),
        VpnServer(
            id = "us-nyc",
            name = "New York Metro",
            city = "New York",
            country = "United States",
            countryCode = "US",
            flagEmoji = "🇺🇸",
            ipAddress = "198.54.133.10",
            pingMs = 74,
            loadPercent = 64,
            category = ServerCategory.AMERICAS
        ),
        VpnServer(
            id = "us-mia",
            name = "Miami Streaming Fast",
            city = "Miami",
            country = "United States",
            countryCode = "US",
            flagEmoji = "🇺🇸",
            ipAddress = "104.238.167.2",
            pingMs = 82,
            loadPercent = 58,
            category = ServerCategory.STREAMING
        ),
        VpnServer(
            id = "us-sfo",
            name = "San Francisco Bay",
            city = "San Francisco",
            country = "United States",
            countryCode = "US",
            flagEmoji = "🇺🇸",
            ipAddress = "66.241.124.90",
            pingMs = 95,
            loadPercent = 49,
            category = ServerCategory.AMERICAS
        ),
        VpnServer(
            id = "ca-tor",
            name = "Toronto Shield",
            city = "Toronto",
            country = "Canada",
            countryCode = "CA",
            flagEmoji = "🇨🇦",
            ipAddress = "142.44.212.80",
            pingMs = 78,
            loadPercent = 41,
            category = ServerCategory.AMERICAS
        ),
        VpnServer(
            id = "jp-tyo",
            name = "Tokyo Gaming Node",
            city = "Tokyo",
            country = "Japan",
            countryCode = "JP",
            flagEmoji = "🇯🇵",
            ipAddress = "133.242.18.99",
            pingMs = 120,
            loadPercent = 55,
            category = ServerCategory.ASIA_PACIFIC
        ),
        VpnServer(
            id = "sg-sin",
            name = "Singapore Hub",
            city = "Singapore",
            country = "Singapore",
            countryCode = "SG",
            flagEmoji = "🇸🇬",
            ipAddress = "128.199.204.14",
            pingMs = 135,
            loadPercent = 61,
            category = ServerCategory.ASIA_PACIFIC
        ),
        VpnServer(
            id = "kr-sel",
            name = "Seoul Ultra-Fast",
            city = "Seoul",
            country = "South Korea",
            countryCode = "KR",
            flagEmoji = "🇰🇷",
            ipAddress = "211.234.120.5",
            pingMs = 128,
            loadPercent = 47,
            category = ServerCategory.GAMING
        ),
        VpnServer(
            id = "au-syd",
            name = "Sydney Ocean",
            city = "Sydney",
            country = "Australia",
            countryCode = "AU",
            flagEmoji = "🇦🇺",
            ipAddress = "139.130.4.5",
            pingMs = 168,
            loadPercent = 39,
            category = ServerCategory.ASIA_PACIFIC
        ),
        VpnServer(
            id = "br-sao",
            name = "São Paulo Express",
            city = "São Paulo",
            country = "Brazil",
            countryCode = "BR",
            flagEmoji = "🇧🇷",
            ipAddress = "177.185.10.22",
            pingMs = 142,
            loadPercent = 53,
            category = ServerCategory.AMERICAS
        )
    )

    private val _servers = MutableStateFlow(initialServers)
    val servers: StateFlow<List<VpnServer>> = _servers.asStateFlow()

    private val _selectedServer = MutableStateFlow(initialServers[0])
    val selectedServer: StateFlow<VpnServer> = _selectedServer.asStateFlow()

    private val _telemetry = MutableStateFlow(TelemetryData())
    val telemetry: StateFlow<TelemetryData> = _telemetry.asStateFlow()

    private val _settings = MutableStateFlow(VpnSettings())
    val settings: StateFlow<VpnSettings> = _settings.asStateFlow()

    fun selectServer(server: VpnServer) {
        _selectedServer.value = server
        if (_status.value == VpnStatus.CONNECTED) {
            // Reconnect to new server
            scope.launch {
                _status.value = VpnStatus.CONNECTING
                delay(1200)
                _status.value = VpnStatus.CONNECTED
                _telemetry.update {
                    it.copy(
                        virtualIp = server.ipAddress,
                        currentPingMs = server.pingMs
                    )
                }
            }
        }
    }

    fun toggleFavorite(serverId: String) {
        _servers.update { currentList ->
            currentList.map {
                if (it.id == serverId) it.copy(isFavorite = !it.isFavorite) else it
            }
        }
        if (_selectedServer.value.id == serverId) {
            _selectedServer.update { it.copy(isFavorite = !it.isFavorite) }
        }
    }

    fun connect() {
        if (_status.value != VpnStatus.DISCONNECTED) return
        scope.launch {
            _status.value = VpnStatus.CONNECTING
            // Realistic handshake delay (1.5 seconds)
            delay(1500)
            _status.value = VpnStatus.CONNECTED
            startTelemetry()
        }
    }

    fun disconnect() {
        if (_status.value != VpnStatus.CONNECTED) return
        scope.launch {
            _status.value = VpnStatus.DISCONNECTING
            delay(800)
            _status.value = VpnStatus.DISCONNECTED
            stopTelemetry()
        }
    }

    private fun startTelemetry() {
        telemetryJob?.cancel()
        val server = _selectedServer.value
        _telemetry.value = TelemetryData(
            durationSeconds = 0,
            virtualIp = server.ipAddress,
            currentPingMs = server.pingMs
        )

        telemetryJob = scope.launch {
            var duration = 0L
            var totalDown = 0f
            var totalUp = 0f

            while (isActive) {
                delay(1000)
                duration++

                // Simulate realistic fluctuating bandwidth
                val downSpeed = (24f + Random.nextFloat() * 48f) // 24 - 72 MB/s
                val upSpeed = (5f + Random.nextFloat() * 18f)    // 5 - 23 MB/s
                totalDown += downSpeed * 0.125f // megabytes
                totalUp += upSpeed * 0.125f

                // Small jitter in ping
                val pingJitter = server.pingMs + Random.nextInt(-3, 4).coerceAtLeast(0)

                _telemetry.update {
                    it.copy(
                        durationSeconds = duration,
                        downloadSpeedMbps = downSpeed,
                        uploadSpeedMbps = upSpeed,
                        totalDownloadMb = totalDown,
                        totalUploadMb = totalUp,
                        currentPingMs = pingJitter
                    )
                }
            }
        }
    }

    private fun stopTelemetry() {
        telemetryJob?.cancel()
        telemetryJob = null
        _telemetry.update {
            it.copy(
                downloadSpeedMbps = 0f,
                uploadSpeedMbps = 0f
            )
        }
    }

    fun updateKillSwitch(enabled: Boolean) {
        _settings.update { it.copy(killSwitchEnabled = enabled) }
    }

    fun updateSplitTunneling(enabled: Boolean) {
        _settings.update { it.copy(splitTunnelingEnabled = enabled) }
    }

    fun updateProtocol(protocol: String) {
        _settings.update { it.copy(selectedProtocol = protocol) }
    }

    fun updateAutoConnect(enabled: Boolean) {
        _settings.update { it.copy(autoConnectOnWifi = enabled) }
    }

    fun updateDnsProtection(enabled: Boolean) {
        _settings.update { it.copy(dnsProtectionEnabled = enabled) }
    }

    fun updateSelectedDns(dns: String) {
        _settings.update { it.copy(selectedDns = dns) }
    }

    fun toggleAppBypass(packageName: String) {
        _settings.update { current ->
            val updatedApps = current.splitTunnelApps.map { app ->
                if (app.packageName == packageName) {
                    app.copy(isBypassing = !app.isBypassing)
                } else app
            }
            current.copy(splitTunnelApps = updatedApps)
        }
    }
}
