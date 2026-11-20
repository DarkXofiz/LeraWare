package neva.ware.features.modules.render;

import net.minecraft.class_290;
import net.minecraft.class_2960;
import net.minecraft.class_310;
import net.minecraft.class_5944;
import neva.ware.utility.render.shaders.satin.api.managed.ManagedCoreShader;
import neva.ware.utility.render.shaders.satin.api.managed.ShaderEffectManager;
import neva.ware.utility.render.shaders.satin.api.managed.uniform.Uniform1f;
import neva.ware.utility.render.shaders.satin.api.managed.uniform.Uniform3f;
import neva.ware.utility.render.shaders.satin.api.managed.uniform.Uniform4f;
import neva.ware.utility.render.shaders.satin.api.managed.uniform.UniformMat4;
import neva.ware.utility.render.shaders.satin.impl.ResettableManagedShaderBase;

final class CustomSkyGpu {
    static final ManagedCoreShader SHADER;
    static UniformMat4 uView;
    static Uniform1f uRadius;
    static Uniform1f uTime;
    static Uniform1f uStyle;
    static Uniform1f uQuality;
    static Uniform4f uCloud;
    static Uniform4f uBright;
    static Uniform4f[] uFlash = new Uniform4f[4];
    static Uniform3f uSun;
    static Uniform3f uTop;
    static Uniform3f uHor;
    static Uniform3f uBot;
    static Uniform3f uDark;
    static Uniform3f uLight;
    static Uniform3f uFlashCol;
    private static boolean looked;
    private static long lastTry;

    private CustomSkyGpu() {
    }

    static boolean ready() {
        if (SHADER == null) {
            return false;
        } else {
            class_5944 var0 = null;

            try {
                var0 = SHADER.getProgram();
            } catch (Throwable var5) {
            }

            if (var0 == null) {
                long var1 = System.currentTimeMillis();
                if (var1 - lastTry > 2000L && SHADER instanceof ResettableManagedShaderBase) {
                    lastTry = var1;

                    try {
                        ResettableManagedShaderBase var3 = (ResettableManagedShaderBase)SHADER;
                        if (!var3.isErrored()) {
                            var3.initializeOrLog(class_310.method_1551().method_1478());
                            var0 = SHADER.getProgram();
                        }
                    } catch (Throwable var4) {
                    }
                }
            }

            if (var0 == null) {
                return false;
            } else {
                if (!looked) {
                    try {
                        uView = SHADER.findUniformMat4("uView");
                        uRadius = SHADER.findUniform1f("Radius");
                        uTime = SHADER.findUniform1f("Time");
                        uStyle = SHADER.findUniform1f("Style");
                        uQuality = SHADER.findUniform1f("Quality");
                        uCloud = SHADER.findUniform4f("Cloud");
                        uBright = SHADER.findUniform4f("Bright");

                        for (int var7 = 0; var7 < 4; var7++) {
                            uFlash[var7] = SHADER.findUniform4f("Flash" + var7);
                        }

                        uSun = SHADER.findUniform3f("Sun");
                        uTop = SHADER.findUniform3f("Top");
                        uHor = SHADER.findUniform3f("Hor");
                        uBot = SHADER.findUniform3f("Bot");
                        uDark = SHADER.findUniform3f("Dark");
                        uLight = SHADER.findUniform3f("Light");
                        uFlashCol = SHADER.findUniform3f("FlashCol");
                        looked = true;
                    } catch (Throwable var6) {
                        return false;
                    }
                }

                return true;
            }
        }
    }

    static {
        ManagedCoreShader var0 = null;

        try {
            var0 = ShaderEffectManager.getInstance().manageCoreShader(class_2960.method_60655("nevaware", "sky"), class_290.field_1592);
        } catch (Throwable var2) {
        }

        SHADER = var0;
    }
}
