package ru.donyka.chunkanimator.compat;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;
import org.lwjgl.opengl.GL20C;
import ru.donyka.chunkanimator.ChunkAnimator;
import ru.donyka.chunkanimator.config.ChunkAnimatorConfig;
import ru.donyka.chunkanimator.handler.AnimationHandler;

public final class SodiumShaderSupport {
   private static final int REGION_WIDTH = 8;
   private static final int REGION_HEIGHT = 4;
   private static final int REGION_LENGTH = 8;
   private static final int REGION_SIZE = 256;
   private static final int DONE = -1;
   private static final long START_TIME = System.currentTimeMillis();
   private static final WeakHashMap<Object, Integer> SECTION_STARTS = new WeakHashMap<>();
   private static final Map<Class<?>, Method> GET_SECTION_METHODS = new HashMap<>();
   private static final Map<Class<?>, Method> IS_BUILT_METHODS = new HashMap<>();
   private static final Map<Integer, SodiumShaderSupport.UniformLocations> UNIFORMS = new HashMap<>();
   private static final int[] DISABLED_TIMES = new int[256];
   private static final int[] SECTION_TIMES = new int[256];

   private SodiumShaderSupport() {
   }

   public static synchronized void markSectionBuilt(Object section, boolean built) {
      if (section != null) {
         if (built) {
            SECTION_STARTS.putIfAbsent(section, Integer.valueOf(now()));
         } else {
            SECTION_STARTS.remove(section);
         }
      }
   }

   public static void apply(Object region, int regionOriginX, int regionOriginY, int regionOriginZ) {
      int program = GL20C.glGetInteger(35725);
      if (program != 0) {
         SodiumShaderSupport.UniformLocations uniforms = UNIFORMS.get(program);
         if (uniforms == null) {
            uniforms = new SodiumShaderSupport.UniformLocations(program);
            UNIFORMS.put(program, uniforms);
         }

         ChunkAnimatorConfig config = ChunkAnimatorConfig.get();
         if (config.animationDuration <= 0) {
            uniforms.setInt("u_ChunkAnimatorEnabled", 0);
            uniforms.setIntArray("u_ChunkAnimatorStartTimes", DISABLED_TIMES);
         } else {
            int now = now();
            fillSectionTimes(region, now, regionOriginX, regionOriginZ, config);
            AnimationHandler handler = ChunkAnimator.animationHandler();
            uniforms.setInt("u_ChunkAnimatorEnabled", 1);
            uniforms.setInt("u_ChunkAnimatorMode", config.mode.ordinal());
            uniforms.setInt("u_ChunkAnimatorEasing", config.easingFunction.ordinal());
            uniforms.setInt("u_ChunkAnimatorCurrentTime", now);
            uniforms.setInt("u_ChunkAnimatorDuration", config.animationDuration);
            uniforms.setInt("u_ChunkAnimatorDisableAroundPlayer", config.disableAroundPlayer ? 1 : 0);
            uniforms.setIntArray("u_ChunkAnimatorStartTimes", SECTION_TIMES);
            uniforms.setFloat("u_ChunkAnimatorMinY", (float)handler.shaderMinY());
            uniforms.setFloat("u_ChunkAnimatorMaxY", (float)handler.shaderMaxY());
            uniforms.setFloat("u_ChunkAnimatorHorizon", (float)handler.shaderHorizonHeight());
            uniforms.setFloat("u_ChunkAnimatorRegionOriginX", (float)regionOriginX);
            uniforms.setFloat("u_ChunkAnimatorRegionOriginY", (float)regionOriginY);
            uniforms.setFloat("u_ChunkAnimatorRegionOriginZ", (float)regionOriginZ);
            uniforms.setFloat("u_ChunkAnimatorPlayerX", (float)handler.shaderPlayerBlockX());
            uniforms.setFloat("u_ChunkAnimatorPlayerZ", (float)handler.shaderPlayerBlockZ());
         }
      }
   }

   public static void clear() {
      SECTION_STARTS.clear();
   }

   private static synchronized void fillSectionTimes(Object region, int now, int regionOriginX, int regionOriginZ, ChunkAnimatorConfig config) {
      Method getSection = getSectionMethod(region);
      if (getSection == null) {
         System.arraycopy(DISABLED_TIMES, 0, SECTION_TIMES, 0, 256);
      } else {
         AnimationHandler handler = ChunkAnimator.animationHandler();

         for (int sectionId = 0; sectionId < 256; sectionId++) {
            Object section = section(region, getSection, sectionId);
            Integer startTime = sectionStart(section, now);
            if (startTime != null
               && now - startTime < config.animationDuration
               && (!config.disableAroundPlayer || !handler.shaderIsNearPlayer(regionOriginX + localX(sectionId) * 16, regionOriginZ + localZ(sectionId) * 16))) {
               SECTION_TIMES[sectionId] = startTime;
            } else {
               SECTION_TIMES[sectionId] = -1;
            }
         }
      }
   }

   private static Integer sectionStart(Object section, int now) {
      if (section == null) {
         return null;
      } else {
         Integer startTime = SECTION_STARTS.get(section);
         if (startTime == null && isBuilt(section)) {
            SECTION_STARTS.put(section, now);
            return now;
         } else {
            return startTime;
         }
      }
   }

   private static int now() {
      return (int)Math.min(2147483647L, System.currentTimeMillis() - START_TIME);
   }

   private static int localX(int sectionId) {
      return sectionId >> 5 & 7;
   }

   private static int localY(int sectionId) {
      return sectionId & 3;
   }

   private static int localZ(int sectionId) {
      return sectionId >> 2 & 7;
   }

   private static Method getSectionMethod(Object region) {
      Class<?> type = region.getClass();
      if (GET_SECTION_METHODS.containsKey(type)) {
         return GET_SECTION_METHODS.get(type);
      } else {
         try {
            Method method = type.getMethod("getSection", int.class);
            GET_SECTION_METHODS.put(type, method);
            return method;
         } catch (ReflectiveOperationException var3) {
            GET_SECTION_METHODS.put(type, null);
            return null;
         }
      }
   }

   private static Object section(Object region, Method getSection, int sectionId) {
      try {
         return getSection.invoke(region, sectionId);
      } catch (ReflectiveOperationException var4) {
         return null;
      }
   }

   private static boolean isBuilt(Object section) {
      Method isBuilt = isBuiltMethod(section);
      if (isBuilt == null) {
         return false;
      } else {
         try {
            return Boolean.TRUE.equals(isBuilt.invoke(section));
         } catch (ReflectiveOperationException var3) {
            return false;
         }
      }
   }

   private static Method isBuiltMethod(Object section) {
      Class<?> type = section.getClass();
      if (IS_BUILT_METHODS.containsKey(type)) {
         return IS_BUILT_METHODS.get(type);
      } else {
         try {
            Method method = type.getMethod("isBuilt");
            IS_BUILT_METHODS.put(type, method);
            return method;
         } catch (ReflectiveOperationException var3) {
            IS_BUILT_METHODS.put(type, null);
            return null;
         }
      }
   }

   static {
      Arrays.fill(DISABLED_TIMES, -1);
   }

   private static final class UniformLocations {
      private final int program;
      private final Map<String, Integer> locations = new HashMap<>();

      private UniformLocations(int program) {
         this.program = program;
      }

      private void setInt(String name, int value) {
         int location = this.location(name);
         if (location >= 0) {
            GL20C.glUniform1i(location, value);
         }
      }

      private void setIntArray(String name, int[] value) {
         int location = this.location(name + "[0]");
         if (location >= 0) {
            GL20C.glUniform1iv(location, value);
         }
      }

      private void setFloat(String name, float value) {
         int location = this.location(name);
         if (location >= 0) {
            GL20C.glUniform1f(location, value);
         }
      }

      private int location(String name) {
         Integer cached = this.locations.get(name);
         if (cached != null) {
            return cached;
         } else {
            int location = GL20C.glGetUniformLocation(this.program, name);
            this.locations.put(name, location);
            return location;
         }
      }
   }
}
