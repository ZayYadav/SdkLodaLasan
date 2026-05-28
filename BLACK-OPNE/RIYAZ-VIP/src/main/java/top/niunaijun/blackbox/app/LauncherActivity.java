package top.niunaijun.blackbox.app;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.webkit.WebView;
import androidx.annotation.Nullable;
import top.niunaijun.blackbox.BlackBoxCore;
import top.niunaijun.blackbox.R;
import top.niunaijun.blackbox.utils.Slog;
import android.widget.ImageView;
import android.widget.TextView;
import android.view.animation.OvershootInterpolator;
import org.lsposed.lsparanoid.Obfuscate;

@Obfuscate
public class LauncherActivity extends Activity {
    
    public static final String TAG = "SplashScreen";
    public static final String KEY_INTENT = "launch_intent";
    public static final String KEY_PKG = "launch_pkg";
    public static final String KEY_USER_ID = "launch_user_id";
    private boolean isRunning = false;

    public static void launch(Intent intent, int userId) {
        Intent splash = new Intent();
        splash.setClass(BlackBoxCore.getContext(), LauncherActivity.class);
        splash.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        splash.putExtra(KEY_INTENT, intent);
        splash.putExtra(KEY_PKG, intent.getPackage());
        splash.putExtra(KEY_USER_ID, userId);
        BlackBoxCore.getContext().startActivity(splash);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        if (intent == null) {
            finish();
            return;
        }

        Intent launchIntent = intent.getParcelableExtra(KEY_INTENT);
        String packageName = intent.getStringExtra(KEY_PKG);
        int userId = intent.getIntExtra(KEY_USER_ID, 0);

        if (launchIntent == null) {
            Slog.e(TAG, "launchIntent is null! Cannot launch app.");
            finish();
            return;
        }

        if (packageName == null) {
            packageName = launchIntent.getPackage();
            if (packageName == null) {
                Slog.e(TAG, "Package name is null! Cannot launch app.");
                finish();
                return;
            }
        }

        PackageInfo packageInfo = BlackBoxCore.getBPackageManager().getPackageInfo(packageName, 0, userId);
        if (packageInfo == null) {
            Slog.e(TAG, packageName + " not installed!");
            finish();
            return;
        }

        setContentView(R.layout.activity_launcher);

        // ===== Premium Loading WebView =====
        WebView web = findViewById(R.id.web_loading);
        web.getSettings().setJavaScriptEnabled(true);
        web.setBackgroundColor(Color.TRANSPARENT);
        String html = getPremiumLoadingHtml();
        web.loadDataWithBaseURL(null, html, "text/html", "utf-8", null);

        // ===== App Icon =====
        Drawable icon = packageInfo.applicationInfo.loadIcon(BlackBoxCore.getPackageManager());
        ImageView iconView = findViewById(R.id.iv_icon);
        iconView.setImageDrawable(icon);

        // ===== App Name =====
        TextView nameView = findViewById(R.id.tv_app_name);
        if (nameView != null) {
            CharSequence label = packageInfo.applicationInfo.loadLabel(BlackBoxCore.getPackageManager());
            nameView.setText(label);
            nameView.setAlpha(0f);
            nameView.animate().alpha(1f).setDuration(400).start();
        }

        // ===== Icon Animation =====
        iconView.setScaleX(0.7f);
        iconView.setScaleY(0.7f);
        iconView.setAlpha(0f);
        iconView.animate()
                .scaleX(1f).scaleY(1f).alpha(1f)
                .setDuration(450)
                .setInterpolator(new OvershootInterpolator())
                .start();

        // ===== Launch App =====
        new Thread(() -> BlackBoxCore.getBActivityManager().startActivity(launchIntent, userId)).start();
    }

    private String getPremiumLoadingHtml() {
        return "<!DOCTYPE html>" +
               "<html>" +
               "<head>" +
               "<meta charset='utf-8'>" +
               "<meta name='viewport' content='width=device-width, initial-scale=1.0'>" +
               "<style>" +
               "body {" +
               "    margin: 0;" +
               "    padding: 0;" +
               "    background: transparent;" +
               "    display: flex;" +
               "    justify-content: center;" +
               "    align-items: center;" +
               "    height: 100vh;" +
               "    font-family: 'Poppins', 'Segoe UI', sans-serif;" +
               "}" +
               ".container {" +
               "    width: 80%;" +
               "    max-width: 300px;" +
               "}" +
               ".title {" +
               "    color: rgba(255, 215, 0, 0.9);" +
               "    font-size: 14px;" +
               "    font-weight: 600;" +
               "    text-transform: uppercase;" +
               "    letter-spacing: 2px;" +
               "    text-align: center;" +
               "    margin-bottom: 12px;" +
               "    text-shadow: 0 0 10px rgba(255, 215, 0, 0.5);" +
               "}" +
               ".progress-bar {" +
               "    width: 100%;" +
               "    height: 4px;" +
               "    background: rgba(255, 255, 255, 0.1);" +
               "    border-radius: 20px;" +
               "    overflow: hidden;" +
               "    position: relative;" +
               "    box-shadow: 0 0 20px rgba(255, 215, 0, 0.3);" +
               "}" +
               ".progress-fill {" +
               "    width: 40%;" +
               "    height: 100%;" +
               "    background: linear-gradient(90deg, #FFD700, #FFB347, #FFD700);" +
               "    background-size: 200% 100%;" +
               "    animation: shimmer 1.2s infinite ease-in-out;" +
               "    border-radius: 20px;" +
               "    box-shadow: 0 0 15px #FFD700;" +
               "}" +
               "@keyframes shimmer {" +
               "    0% { transform: translateX(-100%); }" +
               "    100% { transform: translateX(300%); }" +
               "}" +
               ".subtitle {" +
               "    color: rgba(255, 255, 255, 0.7);" +
               "    font-size: 12px;" +
               "    text-align: center;" +
               "    margin-top: 16px;" +
               "    letter-spacing: 1px;" +
               "}" +
               "</style>" +
               "</head>" +
               "<body>" +
               "<div class='container'>" +
               "    <div class='title'>PREMIUM LAUNCHER</div>" +
               "    <div class='progress-bar'><div class='progress-fill'></div></div>" +
               "    <div class='subtitle'>preparing secure environment...</div>" +
               "</div>" +
               "</body>" +
               "</html>";
    }

    @Override
    protected void onPause() {
        super.onPause();
        isRunning = true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isRunning) {
            finish();
        }
    }
}