package top.niunaijun.blackbox.core.env;

import android.content.ComponentName;
import android.MetaCore.RemoteManager;
import android.os.Build;

import java.util.ArrayList;
import java.util.List;

import top.niunaijun.blackbox.BlackBoxCore;
import top.niunaijun.blackbox.utils.compat.BuildCompat;
import org.lsposed.lsparanoid.Obfuscate;

@Obfuscate
public class AppSystemEnv {
    private static final List<String> sSystemPackages = new ArrayList<>();
    private static final List<String> sSuPackages = new ArrayList<>();
    private static final List<String> sXposedPackages = new ArrayList<>();
    private static final List<String> sPreInstallPackages = new ArrayList<>();

    static {
        // Core / AOSP
        sSystemPackages.add("android");
        sSystemPackages.add("com.google.android.webview");
        sSystemPackages.add("com.google.android.webview.dev");
        sSystemPackages.add("com.google.android.webview.beta");
        sSystemPackages.add("com.google.android.webview.canary");
        sSystemPackages.add("com.android.webview");
        // Extra WebView variants (from Code #2)
        sSystemPackages.add("com.le.android.webview");
        sSystemPackages.add("com.android.camera");
        sSystemPackages.add("com.android.talkback");
        sSystemPackages.add("com.miui.gallery");
        // MIUI / Xiaomi
        sSystemPackages.add("com.lbe.security.miui");
        sSystemPackages.add("com.miui.contentcatcher");
        sSystemPackages.add("com.miui.catcherpatch");
        // Permission Controllers (added)
        sSystemPackages.add("com.android.permissioncontroller");
        sSystemPackages.add("com.google.android.permissioncontroller");
        // Google Gboard
        sSystemPackages.add("com.google.android.inputmethod.latin");
        // Huawei
        sSystemPackages.add("com.huawei.webview");
        // Oppo / ColorOS & OEM IDs (added)
        sSystemPackages.add("com.heytap.openid");
        sSystemPackages.add("com.coloros.safecenter");
        // Samsung / Asus / Lenovo / ZUI / MSA (added)
        sSystemPackages.add("com.samsung.android.deviceidservice");
        sSystemPackages.add("com.asus.msa.SupplementaryDID");
        sSystemPackages.add("com.zui.deviceidservice");
        sSystemPackages.add("com.mdid.msa");
        // ---- SU / Root apps ----
        sSuPackages.add("com.noshufou.android.su");
        sSuPackages.add("com.noshufou.android.su.elite");
        sSuPackages.add("eu.chainfire.supersu");
        sSuPackages.add("com.koushikdutta.superuser");
        sSuPackages.add("com.thirdparty.superuser");
        sSuPackages.add("com.yellowes.su");
        //sSystemPackages.add(BlackBoxCore.getHostPkg());
        // Magisk (added)
        sSuPackages.add("com.topjohnwu.magisk");
        // ---- Xposed ----
        sXposedPackages.add("de.robv.android.xposed.installer");
        // Twitter / X
        sSystemPackages.add("com.twitter.android");
        sSystemPackages.add("com.twitter.android.lite");
        // Facebook
        sSystemPackages.add("com.facebook.katana");
        sSystemPackages.add("com.facebook.orca");
        sSystemPackages.add("com.facebook.lite");
        sSystemPackages.add("com.facebook.mlite");
        sSystemPackages.add("com.facebook.services");
    }

    public static boolean isOpenPackage(String packageName) {
        return sSystemPackages.contains(packageName);
    }

    public static boolean isOpenPackage(ComponentName componentName) {
        return componentName != null && isOpenPackage(componentName.getPackageName());
    }

    public static boolean isBlackPackage(String packageName) {
        if (BlackBoxCore.get().setHideRoot() && sSuPackages.contains(packageName)) {
            return true;
        }
        return RemoteManager.sHideXposed && sXposedPackages.contains(packageName);
    }

    public static List<String> getPreInstallPackages() {
        return sPreInstallPackages;
    }
}
