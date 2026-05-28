package android.MetaCore

import android.app.Activity
import android.app.Dialog
import android.graphics.Color
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.WindowManager
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient
import java.lang.reflect.Field
import org.lsposed.lsparanoid.Obfuscate

@Obfuscate
object AdvancedPopupHelper {

    private val handler = Handler(Looper.getMainLooper())

    /* ================= GET TOP ACTIVITY ================= */
    private fun getTopActivity(): Activity? {
        return try {
            val atClass = Class.forName("android.app.ActivityThread")
            val currentAT = atClass.getMethod("currentActivityThread").invoke(null)
            val mActivitiesField: Field = atClass.getDeclaredField("mActivities")
            mActivitiesField.isAccessible = true
            val activities = mActivitiesField.get(currentAT) as Map<*, *>

            for (record in activities.values) {
                val rClass = record!!::class.java
                val pausedField = rClass.getDeclaredField("paused")
                pausedField.isAccessible = true
                if (!pausedField.getBoolean(record)) {
                    val activityField = rClass.getDeclaredField("activity")
                    activityField.isAccessible = true
                    return activityField.get(record) as Activity
                }
            }
            null
        } catch (_: Throwable) {
            null
        }
    }

    /* ================= ENTRY ================= */
    @JvmStatic
    fun showAuto() {
        val act = getTopActivity() ?: return
        if (act.isFinishing || act.isDestroyed) return
        showPopup(act)
    }

    /* ================= SHOW POPUP ================= */
    private fun showPopup(act: Activity) {
        handler.post {
            try {
                val dialog = Dialog(act, android.R.style.Theme_Translucent_NoTitleBar)
                dialog.setCancelable(false)

                val webView = WebView(act)
                webView.settings.javaScriptEnabled = true
                webView.settings.domStorageEnabled = true
                webView.setBackgroundColor(Color.TRANSPARENT)
                webView.webViewClient = WebViewClient()

                // Device info get karenge
                val deviceInfo = """
                    Device: ${Build.MANUFACTURER} ${Build.MODEL}
                    Android: ${Build.VERSION.RELEASE}
                    SDK: ${Build.VERSION.SDK_INT}
                """.trimIndent()

                webView.addJavascriptInterface(object {
                    @JavascriptInterface
                    fun close() {
                        handler.post {
                            if (dialog.isShowing) dialog.dismiss()
                        }
                    }
                    
                    @JavascriptInterface
                    fun getDeviceInfo(): String {
                        return deviceInfo
                    }
                    
                    @JavascriptInterface
                    fun getExpireStatus(): String {
                        return "RIYAZ Access Revoked"
                    }
                }, "Android")

                webView.loadDataWithBaseURL(
                    null,
                    HTML,
                    "text/html",
                    "utf-8",
                    null
                )

                dialog.setContentView(webView)
                dialog.show()

                dialog.window?.apply {
                    setLayout(dp(act, 250), dp(act, 400))
                    setGravity(Gravity.CENTER)
                    addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
                    setDimAmount(0.6f)
                }

            } catch (_: Throwable) {
            }
        }
    }

    /* ================= DP UTILS ================= */
    private fun dp(act: Activity, v: Int): Int {
        return (v * act.resources.displayMetrics.density).toInt()
    }

    /* ================= HTML (CLEAN BLACK BACKGROUND) ================= */
    private const val HTML = """
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<script src="https://cdnjs.cloudflare.com/ajax/libs/bodymovin/5.12.2/lottie.min.js"></script>
<style>
*{margin:0;padding:0;box-sizing:border-box;font-family:'Segoe UI',system-ui,sans-serif}

body{
    background:transparent;
    display:flex;
    justify-content:center;
    align-items:center;
    height:100vh;
}

.card{
    width:240px;
    background:#000000; /* Pure black background */
    border-radius:12px;
    padding:14px;
    border:2px solid #4dabf7; /* Sky blue border */
    box-shadow:0 8px 25px rgba(0,0,0,0.8),
               0 0 0 1px rgba(77, 171, 247, 0.3);
    position:relative;
    overflow:hidden;
}

/* Simple border glow effect */
.card::before{
    content:'';
    position:absolute;
    top:-2px;
    left:-2px;
    right:-2px;
    bottom:-2px;
    background:#4dabf7;
    border-radius:14px;
    z-index:-1;
    opacity:0.3;
    filter:blur(5px);
}

/* Header */
.header{
    display:flex;
    align-items:center;
    justify-content:center;
    gap:8px;
    margin-bottom:12px;
    padding-bottom:10px;
    border-bottom:1px solid rgba(77, 171, 247, 0.4);
}

.title-icon{
    width:20px;
    height:20px;
}

.title{
    font-size:14px;
    font-weight:700;
    color:#4dabf7;
    letter-spacing:0.5px;
}

/* Expire Status */
.expire-status{
    background:rgba(77, 171, 247, 0.1);
    border-radius:8px;
    padding:6px;
    margin:10px 0;
    text-align:center;
    border:1px solid rgba(77, 171, 247, 0.2);
}

.expire-text{
    font-size:10px;
    font-weight:600;
    color:#a5d8ff;
}

/* Device Info */
.device-info{
    background:rgba(77, 171, 247, 0.08);
    border-radius:8px;
    padding:7px;
    margin:10px 0;
    border:1px solid rgba(77, 171, 247, 0.15);
}

.info-row{
    display:flex;
    justify-content:space-between;
    margin:3px 0;
}

.info-label{
    font-size:9px;
    color:#a5d8ff;
    font-weight:500;
}

.info-value{
    font-size:9px;
    color:#ffffff;
    font-weight:600;
}

/* Content */
.content{
    margin:8px 0;
}

.row{
    display:flex;
    align-items:center;
    gap:6px;
    margin:5px 0;
    padding:5px;
}

.icon{
    width:14px;
    height:14px;
    min-width:14px;
}

.text{
    font-size:10px;
    color:#ffffff;
}

.highlight{
    color:#ffcc00;
    font-weight:600;
}

/* Android support */
.android-support{
    text-align:center;
    margin:8px 0;
    padding:6px;
    background:rgba(77, 171, 247, 0.05);
    border-radius:6px;
    border:1px solid rgba(77, 171, 247, 0.1);
}

.support-text{
    font-size:9px;
    color:#ffcc00;
    font-weight:600;
}

/* Footer */
.footer{
    margin-top:10px;
    text-align:center;
    padding-top:8px;
    border-top:1px solid rgba(77, 171, 247, 0.2);
}

.footer-row{
    display:flex;
    align-items:center;
    justify-content:center;
    gap:5px;
    margin-bottom:4px;
}

.footer-icon{
    width:12px;
    height:12px;
}

.footer-text{
    font-size:10px;
    font-weight:700;
    color:#ffcc00;
}

.footer-note{
    font-size:8px;
    color:#a5d8ff;
}

/* Countdown */
.countdown{
    font-size:9px;
    color:#33ff66;
    font-weight:600;
    text-align:center;
    margin-top:6px;
    background:rgba(51,255,102,0.1);
    padding:4px;
    border-radius:5px;
    border:1px solid rgba(51,255,102,0.2);
}

</style>
</head>

<body>

<div class="card">
    <div class="header">
        <div id="iconTitle" class="title-icon"></div>
        <div class="title">LICENCE EXPIRED</div>
    </div>
    
    <!-- Expire Status -->
    <div class="expire-status">
        <div class="expire-text" id="expireStatus">
            <i>🔒</i> RIYAZ Access Revoked
        </div>
    </div>
    
    <!-- Device Info -->
    <div class="device-info">
        <div id="deviceInfo">
            <!-- Device info will be inserted here -->
        </div>
    </div>
    
    <div class="content">
        <div class="row">
            <div id="iconSupport" class="icon"></div>
            <div class="text">
                <span class="highlight">Support:</span> A8 to A17
            </div>
        </div>
        
        <div class="row">
            <div id="iconDev" class="icon"></div>
            <div class="text">
                <span class="highlight">Developer:</span> RIYAZ BBOX
            </div>
        </div>
        
        <div class="row">
            <div id="iconContact" class="icon"></div>
            <div class="text">
                <span class="highlight">Contact:</span> Officials
            </div>
        </div>
    </div>
    
    <!-- Android Support -->
    <div class="android-support">
        <div class="support-text">Android 8.0 to 17.0</div>
    </div>
    
    <div class="footer">
        <div class="footer-row">
            <div id="iconPerfect" class="footer-icon"></div>
            <span class="footer-text">RIYAZ BBOX SDK</span>
        </div>
        <div class="footer-note">License renewal required</div>
        
        <div class="countdown">
            Close: <span id="timer">10</span>s
        </div>
    </div>
</div>

<script>
// Load Lottie animations (only for icons)
lottie.loadAnimation({
    container: document.getElementById('iconTitle'),
    renderer: 'svg',
    loop: true,
    autoplay: true,
    path: 'https://assets10.lottiefiles.com/packages/lf20_j1adxtyb.json'
});

lottie.loadAnimation({
    container: document.getElementById('iconPerfect'),
    renderer: 'svg',
    loop: true,
    autoplay: true,
    path: 'https://assets4.lottiefiles.com/packages/lf20_jtbfg2nb.json'
});

lottie.loadAnimation({
    container: document.getElementById('iconSupport'),
    renderer: 'svg',
    loop: true,
    autoplay: true,
    path: 'https://assets2.lottiefiles.com/packages/lf20_jcikwtux.json'
});

lottie.loadAnimation({
    container: document.getElementById('iconDev'),
    renderer: 'svg',
    loop: true,
    autoplay: true,
    path: 'https://assets7.lottiefiles.com/packages/lf20_w51pcehl.json'
});

lottie.loadAnimation({
    container: document.getElementById('iconContact'),
    renderer: 'svg',
    loop: true,
    autoplay: true,
    path: 'https://assets3.lottiefiles.com/packages/lf20_5ngs2ksb.json'
});

// Get device info from Android
function loadDeviceInfo() {
    if (window.Android && Android.getDeviceInfo) {
        try {
            const info = Android.getDeviceInfo();
            const lines = info.split('\n');
            
            const deviceInfoDiv = document.getElementById('deviceInfo');
            deviceInfoDiv.innerHTML = '';
            
            lines.forEach(line => {
                if (line.trim()) {
                    const [label, value] = line.split(':').map(s => s.trim());
                    if (label && value) {
                        const row = document.createElement('div');
                        row.className = 'info-row';
                        
                        const labelSpan = document.createElement('span');
                        labelSpan.className = 'info-label';
                        labelSpan.textContent = label + ':';
                        
                        const valueSpan = document.createElement('span');
                        valueSpan.className = 'info-value';
                        valueSpan.textContent = value;
                        
                        row.appendChild(labelSpan);
                        row.appendChild(valueSpan);
                        deviceInfoDiv.appendChild(row);
                    }
                }
            });
            
        } catch (e) {
            document.getElementById('deviceInfo').innerHTML = 
                '<div class="info-row"><span class="info-label">Device:</span><span class="info-value">Unknown</span></div>';
        }
    }
}

// Simple countdown timer
let seconds = 10;
const timerElement = document.getElementById('timer');

const countdown = setInterval(() => {
    seconds--;
    timerElement.textContent = seconds;
    
    if (seconds <= 5) {
        timerElement.style.color = '#ff3366';
    }
    
    if (seconds <= 0) {
        clearInterval(countdown);
        if (window.Android && Android.close) {
            Android.close();
        }
    }
}, 1000);

// Initialize
document.addEventListener('DOMContentLoaded', function() {
    loadDeviceInfo();
    
    // Fallback auto-close
    setTimeout(() => {
        if (window.Android && Android.close) {
            Android.close();
        }
    }, 10000);
});
</script>
</body>
</html>
"""
}