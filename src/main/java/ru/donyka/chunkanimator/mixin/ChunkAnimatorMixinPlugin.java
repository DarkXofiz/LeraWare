package ru.donyka.chunkanimator.mixin;

import java.util.List;
import java.util.Set;
import net.fabricmc.loader.api.FabricLoader;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

public final class ChunkAnimatorMixinPlugin implements IMixinConfigPlugin {
   private boolean sodiumLoaded;
   private boolean caffeineSodium;
   private boolean legacyJellysquidSodium;

   public void onLoad(String mixinPackage) {
      this.sodiumLoaded = FabricLoader.getInstance().isModLoaded("sodium");
      if (this.sodiumLoaded) {
         String sodiumVersion = FabricLoader.getInstance()
            .getModContainer("sodium")
            .map(container -> container.getMetadata().getVersion().getFriendlyString())
            .orElse("");
         this.caffeineSodium = isAtLeast(sodiumVersion, 0, 6);
         this.legacyJellysquidSodium = isAtLeast(sodiumVersion, 0, 5) && !this.caffeineSodium;
      }
   }

   public String getRefMapperConfig() {
      return null;
   }

   public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
      if (mixinClassName.endsWith(".BuiltChunkMixin") || mixinClassName.endsWith(".WorldRendererMixin")) {
         return !this.sodiumLoaded;
      } else if (mixinClassName.contains(".sodium.legacy.")) {
         return this.legacyJellysquidSodium;
      } else {
         return mixinClassName.contains(".sodium.") ? this.caffeineSodium : true;
      }
   }

   public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {
   }

   public List<String> getMixins() {
      return null;
   }

   public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
   }

   public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
   }

   private static boolean isAtLeast(String version, int minimumMajor, int minimumMinor) {
      int[] parts = parseVersion(version);
      return parts[0] != minimumMajor ? parts[0] > minimumMajor : parts[1] >= minimumMinor;
   }

   private static int[] parseVersion(String version) {
      String[] parts = version.split("[.+-]", 3);
      int major = parsePart(parts, 0);
      int minor = parsePart(parts, 1);
      return new int[]{major, minor};
   }

   private static int parsePart(String[] parts, int index) {
      if (index >= parts.length) {
         return 0;
      } else {
         try {
            return Integer.parseInt(parts[index]);
         } catch (NumberFormatException var3) {
            return 0;
         }
      }
   }
}
