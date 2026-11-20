package ru.donyka.chunkanimator.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import net.fabricmc.loader.api.FabricLoader;

public final class ChunkAnimatorConfig {
    public static final AnimationMode DEFAULT_MODE = AnimationMode.BELOW;
    public static final EasingFunction DEFAULT_EASING_FUNCTION = EasingFunction.SINE;
    public static final int DEFAULT_ANIMATION_DURATION = 1000;
    public static final boolean DEFAULT_DISABLE_AROUND_PLAYER = false;
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final String FILE_NAME = "chunkanimator.json";
    private static ChunkAnimatorConfig instance;
    public AnimationMode mode = DEFAULT_MODE;
    public EasingFunction easingFunction = DEFAULT_EASING_FUNCTION;
    public int animationDuration = 1000;
    public boolean disableAroundPlayer = false;

    public static ChunkAnimatorConfig get() {
        if (instance == null) {
            load();
        }

        return instance;
    }

    public static void load() {
        Path path = path();
        if (Files.exists(path, new LinkOption[0])) {
            try {
                Reader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8);

                try {
                    instance = (ChunkAnimatorConfig)GSON.fromJson(reader, ChunkAnimatorConfig.class);
                } catch (Throwable var5) {
                    if (reader != null) {
                        try {
                            reader.close();
                        } catch (Throwable var4) {
                            var5.addSuppressed(var4);
                        }
                    }

                    throw var5;
                }

                if (reader != null) {
                    reader.close();
                }
            } catch (Exception var6) {
                instance = new ChunkAnimatorConfig();
            }
        } else {
            instance = new ChunkAnimatorConfig();
            save();
        }

        if (instance == null) {
            instance = new ChunkAnimatorConfig();
        }

        instance.sanitize();
    }

    public static void save() {
        ChunkAnimatorConfig config = get();
        config.sanitize();

        try {
            Path path = path();
            Files.createDirectories(path.getParent());
            Writer writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8);

            try {
                GSON.toJson(config, writer);
            } catch (Throwable var6) {
                if (writer != null) {
                    try {
                        writer.close();
                    } catch (Throwable var5) {
                        var6.addSuppressed(var5);
                    }
                }

                throw var6;
            }

            if (writer != null) {
                writer.close();
            }
        } catch (Exception var7) {
        }
    }

    public void setMode(AnimationMode value) {
        this.mode = value == null ? DEFAULT_MODE : value;
    }

    public void setEasingFunction(EasingFunction value) {
        this.easingFunction = value == null ? DEFAULT_EASING_FUNCTION : value;
    }

    public void setAnimationDuration(int value) {
        this.animationDuration = Math.max(0, value);
    }

    private void sanitize() {
        if (this.mode == null) {
            this.mode = DEFAULT_MODE;
        }

        if (this.easingFunction == null) {
            this.easingFunction = DEFAULT_EASING_FUNCTION;
        }

        this.animationDuration = Math.max(0, this.animationDuration);
    }

    private static Path path() {
        return FabricLoader.getInstance().getConfigDir().resolve("chunkanimator.json");
    }
}
