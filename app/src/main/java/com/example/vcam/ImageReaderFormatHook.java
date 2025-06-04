
package com.example.vcam;

import java.lang.reflect.Constructor;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class ImageReaderFormatHook implements IXposedHookLoadPackage {

    @Override
    public void handleLoadPackage(final XC_LoadPackage.LoadPackageParam lpparam) {
        // Chỉ hook app cần fake camera, ví dụ: ae.zealnova.snpit
        if (!lpparam.packageName.equals("ae.zealnova.snpit")) return;

        XposedBridge.log("【VCAM】【hook】Injecting ImageReader format hook into: " + lpparam.packageName);

        try {
            XposedHelpers.findAndHookConstructor(
                "android.media.ImageReader",
                lpparam.classLoader,
                int.class, int.class, int.class, int.class,
                new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        int originalFormat = (int) param.args[2];
                        if (originalFormat == 0x21) {
                            param.args[2] = 0x7fa30c04; // ép format về vendor-private format mà fake camera đang dùng
                            XposedBridge.log("【VCAM】【hook】ép ImageReader format từ 0x21 thành 0x7fa30c04");
                        }
                    }
                }
            );
        } catch (Throwable t) {
            XposedBridge.log("【VCAM】【hook】Lỗi khi hook ImageReader: " + t.getMessage());
        }
    }
}
