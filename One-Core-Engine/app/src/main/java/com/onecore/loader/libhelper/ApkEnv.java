package com.onecore.loader.libhelper;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import com.onecore.loader.BoxApplication;
import com.onecore.loader.utils.FLog;
import static com.onecore.loader.Config.GAME_LIST_PKG;
import com.Jagdish.tastytoast.TastyToast;
import com.blankj.molihuan.utilcode.util.FileUtils;
import java.io.File;
import top.niunaijun.blackbox.BlackBoxCore;
import org.lsposed.lsparanoid.Obfuscate;

@Obfuscate
public class ApkEnv {
    
    private static ApkEnv singleton;

    public static ApkEnv getInstance() {
        if (singleton == null) {
            singleton = new ApkEnv();
        }
        return singleton;
    }
    
    public static void LaunchApplication(String packageName) {
        try {
            BlackBoxCore.get().launchApk(packageName, 0);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void unInstallApp(String packageName) {
        try {
            BlackBoxCore.get().uninstallPackageAsUser(packageName, 0);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean isInstalled(String packageName) {
        try {
            return BlackBoxCore.get().isInstalled(packageName, 0);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    public boolean installByPackage(String packageName) {
        try {
            return BlackBoxCore.get().installPackageAsUser(packageName,0).success;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    public void stopRunningApp(String packageName) {
    	try {
            BlackBoxCore.get().stopPackage(packageName,0);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    public ApplicationInfo getApplicationInfo(String packageName) {
        ApplicationInfo applicationInfo = null;
        try {
        	applicationInfo = BoxApplication.get().getPackageManager().getApplicationInfo(packageName, 0);
        } catch (PackageManager.NameNotFoundException err) {
        	FLog.error(err.getMessage());
            BoxApplication.get().showToastWithImage(err.getMessage(), TastyToast.WARNING);
            return null;
        }
        return applicationInfo;
    }
    
    public ApplicationInfo getApplicationInfoContainer(String packageName) {
    	if (!isInstalled(packageName)) {
            BoxApplication.get().showToastWithImage("App not install, install first", TastyToast.WARNING);
            return null;
        }

        ApplicationInfo applicationInfo = null;
        try {
         //   applicationInfo = BlackBoxCore.get().getApplicationInfo(packageName);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        if (applicationInfo == null) {
            return null;
        }
        return applicationInfo;
    }
    
    public boolean tryAddLoader(String packageName) {
        boolean is_online = BoxApplication.STATUS_BY.equals("online");

        ApplicationInfo applicationInfo = getApplicationInfoContainer(packageName);
        if (applicationInfo == null) {
            FLog.error("Error, Application Info");
            return false;
        }

        String target = "libbgmi.so";

        if (packageName.equals(GAME_LIST_PKG[0])) {
            target = "libbgmi.so";
        } else if (packageName.equals(GAME_LIST_PKG[1])) {
            target = "libpubgm.so";
        }else if (packageName.equals(GAME_LIST_PKG[2])) {
            target = "libkorea.so";
        }else{
            target = "libbgmi.so";
        }

        File loader = new File(is_online ? new File(BoxApplication.get().getFilesDir(), "loader").toString() : BoxApplication.get().getApplicationInfo().nativeLibraryDir, target);
        File loaderDest = new File(applicationInfo.nativeLibraryDir, packageName.equals("com.miraclegames.farlight84") ? "libfarlight.so" : "libAkAudioVisiual.so");

        if (loaderDest.exists()) loaderDest.delete();
        try {
        	if (FileUtils.copy(loader.toString(), loaderDest.toString())) {
                return true;
            }
        } catch(Exception err) {
        	FLog.error(err.getMessage());
            return false;
        }
        return false;
    }
    
}


