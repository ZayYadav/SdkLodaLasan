package top.niunaijun.blackbox.fake.service;

import android.content.pm.ProviderInfo;
import android.util.Log;
import black.android.content.BRIClipboardStub;
import black.android.os.BRServiceManager;
import java.lang.reflect.Method;
import top.niunaijun.blackbox.BlackBoxCore;
import top.niunaijun.blackbox.app.BActivityThread;
import top.niunaijun.blackbox.fake.hook.BinderInvocationStub;
import top.niunaijun.blackbox.fake.hook.MethodHook;
import top.niunaijun.blackbox.fake.hook.ProxyMethod;
import top.niunaijun.blackbox.utils.Slog;

public class IClipboardManagerProxy extends BinderInvocationStub {
    private static final String TAG = "IClipboardManagerProxy";

    public IClipboardManagerProxy() {
        super(BRServiceManager.get().getService("clipboard"));
    }

    public Object getWho() {
        return BRIClipboardStub.get().asInterface(BRServiceManager.get().getService("clipboard"));
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        int argIndex = getPackNameIndex(args);
        if (argIndex != -1) {
            args[argIndex] = BlackBoxCore.getHostPkg();
        }
        return super.invoke(proxy, method, args);
    }

    private int getPackNameIndex(Object[] args) {
        if (args == null) {
            return -1;
        }
        for (int i = 0; i < args.length; i++) {
            if (args[i] instanceof String) {
                Log.d(TAG, "args[" + i + "] " + args[i]);
                return i;
            }
        }
        return -1;
    }

    public void inject(Object baseInvocation, Object proxyInvocation) {
        replaceSystemService("clipboard");
    }

    public boolean isBadEnv() {
        return false;
    }
}
