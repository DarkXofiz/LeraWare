package ru.donyka.chunkanimator.handler;

import java.util.WeakHashMap;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_310;
import net.minecraft.class_746;
import ru.donyka.chunkanimator.AnimationOffset;
import ru.donyka.chunkanimator.compat.SodiumShaderSupport;
import ru.donyka.chunkanimator.config.AnimationMode;
import ru.donyka.chunkanimator.config.ChunkAnimatorConfig;

public final class AnimationHandler {
    private final class_310 client = class_310.method_1551();
    private final WeakHashMap<Object, AnimationHandler.AnimationData> timeStamps = new WeakHashMap();
    private final WeakHashMap<Object, class_2338> completedOrigins = new WeakHashMap();

    public AnimationOffset offsetFor(Object builtChunk) {
        AnimationHandler.AnimationData animationData = (AnimationHandler.AnimationData)this.timeStamps.get(builtChunk);
        if (animationData == null) {
            return AnimationOffset.ZERO;
        } else {
            ChunkAnimatorConfig config = ChunkAnimatorConfig.get();
            int animationDuration = config.animationDuration;
            if (animationDuration <= 0) {
                this.timeStamps.remove(builtChunk);
                return AnimationOffset.ZERO;
            } else {
                long time = animationData.timeStamp;
                if (time == -1L) {
                    time = System.currentTimeMillis();
                    animationData.timeStamp = time;
                    if ((config.mode == AnimationMode.HORIZONTAL_SLIDE || config.mode == AnimationMode.HORIZONTAL_SLIDE_ALTERNATE)
                        && this.client.field_1724 != null) {
                        animationData.chunkFacing = getChunkFacing(this.client.field_1724, animationData.origin);
                    }
                }

                long timeDifference = System.currentTimeMillis() - time;
                if (timeDifference >= animationDuration) {
                    this.timeStamps.remove(builtChunk);
                    this.completedOrigins.put(builtChunk, animationData.origin);
                    return AnimationOffset.ZERO;
                } else {
                    return this.getOffset(config, animationData, timeDifference);
                }
            }
        }
    }

    public AnimationOffset offsetFor(Object builtChunk, class_2338 position) {
        if (!this.timeStamps.containsKey(builtChunk)) {
            class_2338 completedOrigin = (class_2338)this.completedOrigins.get(builtChunk);
            if (samePosition(completedOrigin, position)) {
                return AnimationOffset.ZERO;
            }

            this.setOrigin(builtChunk, position);
        }

        return this.offsetFor(builtChunk);
    }

    public void setOrigin(Object builtChunk, class_2338 position) {
        this.completedOrigins.remove(builtChunk);
        class_746 player = this.client.field_1724;
        class_2350 facing = null;
        boolean nearPlayer = false;
        if (player != null) {
            class_2338 playerPos = zeroedPlayerPos(player);
            class_2338 centeredChunkPos = zeroedCenteredChunkPos(position);
            long distanceX = playerPos.method_10263() - centeredChunkPos.method_10263();
            long distanceZ = playerPos.method_10260() - centeredChunkPos.method_10260();
            nearPlayer = distanceX * distanceX + distanceZ * distanceZ <= 4096L;
            if (ChunkAnimatorConfig.get().mode == AnimationMode.HORIZONTAL_SLIDE) {
                facing = getChunkFacing(playerPos, centeredChunkPos);
            }
        }

        if (ChunkAnimatorConfig.get().disableAroundPlayer && nearPlayer) {
            this.timeStamps.remove(builtChunk);
            this.completedOrigins.put(builtChunk, new class_2338(position.method_10263(), position.method_10264(), position.method_10260()));
        } else {
            this.timeStamps
                .put(
                    builtChunk,
                    new AnimationHandler.AnimationData(-1L, facing, new class_2338(position.method_10263(), position.method_10264(), position.method_10260()))
                );
        }
    }

    public void clear() {
        this.timeStamps.clear();
        this.completedOrigins.clear();
        SodiumShaderSupport.clear();
    }

    public int shaderMinY() {
        return this.minY();
    }

    public int shaderMaxY() {
        return this.maxY();
    }

    public double shaderHorizonHeight() {
        return this.horizonHeight();
    }

    public int shaderPlayerBlockX() {
        class_746 player = this.client.field_1724;
        return player == null ? 0 : player.method_24515().method_10263();
    }

    public int shaderPlayerBlockZ() {
        class_746 player = this.client.field_1724;
        return player == null ? 0 : player.method_24515().method_10260();
    }

    public boolean shaderIsNearPlayer(int originX, int originZ) {
        class_746 player = this.client.field_1724;
        if (player == null) {
            return false;
        } else {
            class_2338 playerPos = zeroedPlayerPos(player);
            long distanceX = playerPos.method_10263() - (originX + 8L);
            long distanceZ = playerPos.method_10260() - (originZ + 8L);
            return distanceX * distanceX + distanceZ * distanceZ <= 4096L;
        }
    }

    private AnimationOffset getOffset(ChunkAnimatorConfig config, AnimationHandler.AnimationData animationData, long timeDifference) {
        class_2338 origin = animationData.origin;
        AnimationMode mode = config.mode;
        if (mode == AnimationMode.HYBRID) {
            mode = origin.method_10264() < this.horizonHeight() ? AnimationMode.BELOW : AnimationMode.ABOVE;
        }

        if (mode == AnimationMode.BELOW) {
            float distance = Math.abs(origin.method_10264() - this.minY());
            return new AnimationOffset(0.0F, -distance + this.ease(config, timeDifference, distance), 0.0F);
        } else if (mode == AnimationMode.ABOVE) {
            float distance = Math.max(0, this.maxY() - origin.method_10264());
            return new AnimationOffset(0.0F, distance - this.ease(config, timeDifference, distance), 0.0F);
        } else {
            if (mode == AnimationMode.HORIZONTAL_SLIDE || mode == AnimationMode.HORIZONTAL_SLIDE_ALTERNATE) {
                class_2350 facing = animationData.chunkFacing;
                if (facing != null) {
                    float distance = -(200.0F - this.ease(config, timeDifference, 200.0F));
                    return new AnimationOffset(facing.method_10148() * distance, 0.0F, facing.method_10165() * distance);
                }
            }

            return AnimationOffset.ZERO;
        }
    }

    private float ease(ChunkAnimatorConfig config, long timeDifference, float distance) {
        return config.easingFunction.easeOut((float)timeDifference, 0.0F, distance, config.animationDuration);
    }

    private int minY() {
        return this.client.field_1687 == null ? 0 : this.client.field_1687.method_8597().comp_651();
    }

    private int maxY() {
        return this.client.field_1687 == null ? 256 : this.client.field_1687.method_8597().comp_651() + this.client.field_1687.method_8597().comp_652();
    }

    private double horizonHeight() {
        return 63.0;
    }

    private static class_2338 zeroedPlayerPos(class_746 player) {
        class_2338 playerPos = player.method_24515();
        return new class_2338(playerPos.method_10263(), 0, playerPos.method_10260());
    }

    private static class_2338 zeroedCenteredChunkPos(class_2338 position) {
        return new class_2338(position.method_10263() + 8, 0, position.method_10260() + 8);
    }

    private static class_2350 getChunkFacing(class_746 player, class_2338 chunkOrigin) {
        return getChunkFacing(zeroedPlayerPos(player), zeroedCenteredChunkPos(chunkOrigin));
    }

    private static class_2350 getChunkFacing(class_2338 from, class_2338 to) {
        int differenceX = from.method_10263() - to.method_10263();
        int differenceZ = from.method_10260() - to.method_10260();
        int absoluteX = Math.abs(differenceX);
        int absoluteZ = Math.abs(differenceZ);
        if (absoluteX > absoluteZ) {
            return differenceX > 0 ? class_2350.field_11034 : class_2350.field_11039;
        } else {
            return differenceZ > 0 ? class_2350.field_11035 : class_2350.field_11043;
        }
    }

    private static boolean samePosition(class_2338 first, class_2338 second) {
        return first != null
            && second != null
            && first.method_10263() == second.method_10263()
            && first.method_10264() == second.method_10264()
            && first.method_10260() == second.method_10260();
    }

    private static final class AnimationData {
        private long timeStamp;
        private class_2350 chunkFacing;
        private final class_2338 origin;

        private AnimationData(long timeStamp, class_2350 chunkFacing, class_2338 origin) {
            this.timeStamp = timeStamp;
            this.chunkFacing = chunkFacing;
            this.origin = origin;
        }
    }
}
