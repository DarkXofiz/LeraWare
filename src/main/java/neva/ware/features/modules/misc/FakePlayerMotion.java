package neva.ware.features.modules.misc;

import java.lang.reflect.Method;
import java.util.Random;
import net.minecraft.class_1268;
import net.minecraft.class_1313;
import net.minecraft.class_1661;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_243;
import net.minecraft.class_310;
import net.minecraft.class_745;
import net.minecraft.class_746;
import neva.ware.events.impl.EventAttack;
import neva.ware.setting.Setting;
import neva.ware.setting.impl.SettingGroup;

public final class FakePlayerMotion {
   private static boolean flying;
   private static class_1799 savedChest;
   private static double evx;
   private static double evy;
   private static double evz;
   private static double eAngle;
   private static double wpX;
   private static double wpY;
   private static double wpZ;
   private static boolean hasWaypoint;
   private static final Random R = new Random();
   private static double homeX;
   private static double homeY;
   private static double homeZ;
   private static float baseYaw;
   private static float moveYaw;
   private static float lookYaw;
   private static float spinYaw;
   private static double vy;
   private static double kbX;
   private static double kbZ;
   private static double circleAngle;
   private static int timer;
   private static int sneakTimer;
   private static int dirSign = 1;
   private static int legDir = 1;
   private static int corner;
   private static boolean paused;
   private static boolean sneaking;
   private static boolean collided;
   private static boolean initialised;
   private static final int CHEST_SLOT = 38;
   private static float curMove;

   private FakePlayerMotion() {
   }

   public static void init(FakePlayer var0) {
      Setting var1 = new Setting<>("Movement", new SettingGroup(false, 0));
      var0.fpMovement = var1;
      var0.fpMove = new Setting<>("Move", true).addToGroup(var1);
      var0.fpMode = new Setting<>("MoveMode", FakePlayerMotion.Mode.Random).addToGroup(var1);
      var0.fpSpeed = new Setting<>("MoveSpeed", 0.22F, 0.05F, 1.0F).addToGroup(var1);
      var0.fpRadius = new Setting<>("Radius", 4.0F, 1.0F, 15.0F).addToGroup(var1);
      var0.fpChangeDelay = new Setting<>("ChangeDelay", 20, 4, 100).addToGroup(var1);
      var0.fpJump = new Setting<>("Jump", FakePlayerMotion.JumpMode.Smart).addToGroup(var1);
      var0.fpJumpChance = new Setting<>("JumpChance", 30, 1, 100, var1x -> {
         Object var2 = var0.fpJump.getValue();
         return var2 == FakePlayerMotion.JumpMode.Random || var2 == FakePlayerMotion.JumpMode.Smart;
      }).addToGroup(var1);
      var0.fpSneak = new Setting<>("Sneak", FakePlayerMotion.SneakMode.Off).addToGroup(var1);
      var0.fpSprint = new Setting<>("Sprint", true).addToGroup(var1);
      var0.fpLookAtMe = new Setting<>("LookAtMe", true).addToGroup(var1);
      var0.fpSwing = new Setting<>("RandomSwing", false).addToGroup(var1);
      var0.fpKnockback = new Setting<>("Knockback", true).addToGroup(var1);
      var0.fpStayNear = new Setting<>("StayNear", true).addToGroup(var1);
      var0.fpElytra = new Setting<>("ElytraFly", false).addToGroup(var1);
      var0.fpElytraMode = new Setting<>("ElytraMode", FakePlayerMotion.ElytraMode.Circle, var1x -> b(var0.fpElytra)).addToGroup(var1);
      var0.fpElytraSpeed = new Setting<>("ElytraSpeed", 1.0F, 0.1F, 5.0F, var1x -> b(var0.fpElytra)).addToGroup(var1);
      var0.fpElytraHeight = new Setting<>("ElytraHeight", 8.0F, 2.0F, 40.0F, var1x -> b(var0.fpElytra)).addToGroup(var1);
      var0.fpElytraRadius = new Setting<>("ElytraRadius", 12.0F, 3.0F, 50.0F, var1x -> b(var0.fpElytra)).addToGroup(var1);
   }

   private static boolean b(Setting<?> var0) {
      return var0 != null && Boolean.TRUE.equals(var0.getValue());
   }

   private static double f(Setting<?> var0) {
      return ((Number)var0.getValue()).doubleValue();
   }

   private static int i(Setting<?> var0) {
      return ((Number)var0.getValue()).intValue();
   }

   public static void reset(FakePlayer var0) {
      class_745 var1 = FakePlayer.fakePlayer;
      initialised = false;
      if (var1 != null) {
         homeX = var1.method_23317();
         homeY = var1.method_23318();
         homeZ = var1.method_23321();
         baseYaw = var1.method_36454();
         moveYaw = baseYaw;
         lookYaw = baseYaw;
         spinYaw = baseYaw;
         curMove = baseYaw;
         vy = 0.0;
         kbX = 0.0;
         kbZ = 0.0;
         timer = 0;
         sneakTimer = 0;
         dirSign = 1;
         legDir = 1;
         corner = 0;
         paused = false;
         sneaking = false;
         collided = false;
         circleAngle = 0.0;
         flying = false;
         savedChest = null;
         hasWaypoint = false;
         evx = 0.0;
         evy = 0.0;
         evz = 0.0;
         eAngle = 0.0;
         initialised = true;
      }
   }

   public static void onAttack(FakePlayer var0, EventAttack var1) {
      class_745 var2 = FakePlayer.fakePlayer;
      class_746 var3 = class_310.method_1551().field_1724;
      if (var2 != null && var3 != null && var1 != null && var1.isPre() && var1.getEntity() == var2) {
         if (b(var0.fpMove) && b(var0.fpKnockback)) {
            double var4 = var2.method_23317() - var3.method_23317();
            double var6 = var2.method_23321() - var3.method_23321();
            double var8 = Math.sqrt(var4 * var4 + var6 * var6);
            if (var8 < 1.0E-4) {
               var4 = R.nextDouble() - 0.5;
               var6 = R.nextDouble() - 0.5;
               var8 = Math.sqrt(var4 * var4 + var6 * var6);
            }

            double var10 = var3.method_5624() ? 0.75 : 0.42;
            kbX = var4 / var8 * var10;
            kbZ = var6 / var8 * var10;
            if (var2.method_24828()) {
               vy = 0.36;
            }
         }
      }
   }

   public static void tick(FakePlayer var0) {
      class_745 var1 = FakePlayer.fakePlayer;
      class_310 var2 = class_310.method_1551();
      if (var1 != null && var2 != null && var2.field_1724 != null && var2.field_1687 != null) {
         if (var0.fpMode != null) {
            if (b(var0.play) && !var0.positions.isEmpty()) {
               if (flying) {
                  stopFlying(var1);
               }
            } else if (!var1.method_29504()) {
               if (!initialised) {
                  reset(var0);
               }

               if (b(var0.fpElytra)) {
                  elytraTick(var0, var1, var2.field_1724);
               } else {
                  if (flying) {
                     stopFlying(var1);
                  }

                  if (!b(var0.fpMove)) {
                     if (sneaking) {
                        var1.method_5660(false);
                        sneaking = false;
                     }
                  } else {
                     class_746 var3 = var2.field_1724;
                     FakePlayerMotion.Mode var4 = (FakePlayerMotion.Mode)var0.fpMode.getValue();
                     double var5 = f(var0.fpSpeed);
                     double var7 = f(var0.fpRadius);
                     int var9 = Math.max(1, i(var0.fpChangeDelay));
                     boolean var10 = b(var0.fpStayNear);
                     double var11 = var1.method_23317();
                     double var13 = var1.method_23318();
                     double var15 = var1.method_23321();
                     double var17 = var3.method_23317();
                     double var19 = var3.method_23318();
                     double var21 = var3.method_23321();
                     double var23 = var17 - var11;
                     double var25 = var21 - var15;
                     double var27 = Math.sqrt(var23 * var23 + var25 * var25);
                     float var29 = yawTo(var23, var25);
                     double var30 = Math.hypot(homeX - var11, homeZ - var15);
                     float var32 = yawTo(homeX - var11, homeZ - var15);
                     boolean var33 = true;
                     boolean var34 = false;
                     boolean var35 = false;
                     double var36 = var5;
                     switch (var4) {
                        case Random:
                           if (--timer <= 0 || collided) {
                              timer = var9 / 2 + R.nextInt(var9);
                              paused = !collided && R.nextInt(4) == 0;
                              moveYaw = R.nextFloat() * 360.0F;
                           }

                           if (var10 && var30 > var7) {
                              moveYaw = var32 + (R.nextFloat() - 0.5F) * 40.0F;
                              paused = false;
                           }

                           var33 = !paused;
                           break;
                        case Chaos:
                           if (--timer <= 0 || collided) {
                              timer = 2 + R.nextInt(Math.max(2, var9 / 3));
                              moveYaw = R.nextFloat() * 360.0F;
                              paused = R.nextInt(7) == 0;
                           }

                           if (var10 && var30 > var7) {
                              moveYaw = var32 + (R.nextFloat() - 0.5F) * 90.0F;
                              paused = false;
                           }

                           var33 = !paused;
                           var35 = true;
                           if (R.nextInt(8) == 0) {
                              var34 = true;
                           }

                           if (R.nextInt(25) == 0) {
                              sneakTimer = 0;
                           }

                           var36 = var5 * (0.7 + R.nextDouble() * 0.6);
                           break;
                        case Strafe:
                           if (--timer <= 0 || collided) {
                              timer = var9 / 2 + R.nextInt(var9);
                              dirSign = -dirSign;
                           }

                           float var76 = var29 + 90.0F * (float)dirSign;
                           if (var30 > var7 && dot(var76, var11 - homeX, var15 - homeZ) > 0.0) {
                              dirSign = -dirSign;
                              var76 = var29 + 90.0F * (float)dirSign;
                           }

                           moveYaw = var76;
                           var35 = true;
                           break;
                        case Dodge:
                           float var75 = Math.abs(wrap(var3.method_36454() - yawTo(-var23, -var25)));
                           if (var75 < 25.0F && var27 < 10.0) {
                              if (timer <= 0) {
                                 dirSign = R.nextBoolean() ? 1 : -1;
                                 timer = 5 + R.nextInt(8);
                              }

                              timer--;
                              moveYaw = var29 + 90.0F * (float)dirSign;
                              var36 = var5 * 1.4;
                              var35 = true;
                              if (R.nextInt(6) == 0) {
                                 var34 = true;
                              }
                           } else {
                              timer = 0;
                              if (var30 > 0.6) {
                                 moveYaw = var32;
                                 var36 = Math.min(var5, var30);
                              } else {
                                 var33 = false;
                              }
                           }
                           break;
                        case Orbit:
                           if (collided) {
                              dirSign = -dirSign;
                           }

                           double var74 = Math.atan2(var15 - var21, var11 - var17) + (double)dirSign * (var5 / Math.max(var7, 0.5));
                           double var79 = var17 + Math.cos(var74) * var7;
                           double var81 = var21 + Math.sin(var74) * var7;
                           moveYaw = yawTo(var79 - var11, var81 - var15);
                           var36 = Math.min(var5 * 1.5, Math.hypot(var79 - var11, var81 - var15));
                           var35 = true;
                           break;
                        case Circle:
                           if (collided) {
                              dirSign = -dirSign;
                           }

                           circleAngle = circleAngle + (double)dirSign * (var5 / Math.max(var7, 0.5));
                           double var73 = homeX + Math.cos(circleAngle) * var7;
                           double var78 = homeZ + Math.sin(circleAngle) * var7;
                           moveYaw = yawTo(var73 - var11, var78 - var15);
                           var36 = Math.min(var5 * 1.5, Math.hypot(var73 - var11, var78 - var15));
                           break;
                        case Square:
                           double var72 = corner != 0 && corner != 3 ? homeX - var7 : homeX + var7;
                           double var40 = corner < 2 ? homeZ + var7 : homeZ - var7;
                           double var42 = Math.hypot(var72 - var11, var40 - var15);
                           if (var42 < 0.4 || collided && R.nextInt(10) == 0) {
                              corner = (corner + 1) % 4;
                           }

                           moveYaw = yawTo(var72 - var11, var40 - var15);
                           var36 = Math.min(var5, Math.max(var42, 0.05));
                           break;
                        case ZigZag:
                           if (--timer <= 0) {
                              timer = Math.max(4, var9 / 2);
                              dirSign = -dirSign;
                           }

                           double var71 = along(baseYaw, var11 - homeX, var15 - homeZ);
                           if (var71 * (double)legDir > var7 || collided) {
                              legDir = -legDir;
                           }

                           moveYaw = baseYaw + (legDir < 0 ? 180.0F : 0.0F) + 40.0F * (float)dirSign;
                           break;
                        case BackForth:
                           double var38 = along(baseYaw, var11 - homeX, var15 - homeZ);
                           if (var38 * (double)legDir > var7 || collided) {
                              legDir = -legDir;
                           }

                           moveYaw = baseYaw + (legDir < 0 ? 180.0F : 0.0F);
                           break;
                        case Follow:
                           if (var27 > var7) {
                              moveYaw = var29;
                              var36 = Math.min(var5 * 1.15, var27 - var7 + 0.05);
                              if (collided) {
                                 var34 = true;
                              }
                           } else {
                              var33 = false;
                           }
                           break;
                        case Flee:
                           if (var27 < var7 * 2.0) {
                              moveYaw = var29 + 180.0F + (float)Math.sin((double)var1.hashCode() + (double)System.nanoTime() / 2.0E8) * 30.0F;
                              var36 = var5 * 1.2;
                              if (collided) {
                                 var34 = true;
                                 moveYaw = moveYaw + (R.nextBoolean() ? 90.0F : -90.0F);
                              }
                           } else {
                              var33 = false;
                           }
                           break;
                        case BunnyHop:
                           if (--timer <= 0 || collided) {
                              timer = var9 / 2 + R.nextInt(var9);
                              moveYaw = R.nextFloat() * 360.0F;
                           }

                           if (var10 && var30 > var7) {
                              moveYaw = var32 + (R.nextFloat() - 0.5F) * 40.0F;
                           }

                           var34 = true;
                           var36 = var5 * (var1.method_24828() ? 1.0 : 1.6);
                           break;
                        case Spin:
                           var33 = false;
                           spinYaw = wrap(spinYaw + 20.0F + (float)var5 * 80.0F);
                     }

                     Object var77 = var0.fpSneak.getValue();
                     if (var77 == FakePlayerMotion.SneakMode.Always) {
                        sneaking = true;
                     } else if (var77 != FakePlayerMotion.SneakMode.Random && var4 != FakePlayerMotion.Mode.Chaos) {
                        sneaking = false;
                     } else if (--sneakTimer <= 0) {
                        sneaking = R.nextInt(3) == 0;
                        sneakTimer = 10 + R.nextInt(40);
                     }

                     var1.method_5660(sneaking);
                     boolean var39 = b(var0.fpSprint) && var33 && !sneaking;
                     var1.method_5728(var39);
                     if (var39) {
                        var36 *= 1.3;
                     }

                     if (sneaking) {
                        var36 *= 0.3;
                     }

                     Object var80 = var0.fpJump.getValue();
                     double var41 = (double)i(var0.fpJumpChance) / 100.0 / 20.0;

                     boolean var43 = switch ((FakePlayerMotion.JumpMode)var80) {
                        case Off -> var4 == FakePlayerMotion.Mode.BunnyHop && var34;
                        case Always -> true;
                        case Random -> var34 || R.nextDouble() < var41;
                        case OnCollide -> var34 || collided && var33;
                        case Smart -> var34 || collided && var33 || R.nextDouble() < var41;
                     };
                     if (var35) {
                        lookYawSmoothMove(moveYaw, 360.0F);
                     } else {
                        lookYawSmoothMove(moveYaw, 30.0F);
                     }

                     double var44 = Math.toRadians((double)curMove);
                     double var46 = var33 ? -Math.sin(var44) * var36 : 0.0;
                     double var48 = var33 ? Math.cos(var44) * var36 : 0.0;
                     boolean var50 = var1.method_5799() || var1.method_5771();
                     if (var50) {
                        var46 *= 0.5;
                        var48 *= 0.5;
                        vy = 0.04;
                     } else if (var1.method_24828() && var43 && vy <= 0.0) {
                        vy = 0.42;
                     }

                     double var51 = var46 + kbX;
                     double var53 = var48 + kbZ;
                     var1.method_5784(class_1313.field_6308, new class_243(var51, vy, var53));
                     double var55 = var1.method_23317();
                     double var57 = var1.method_23318();
                     double var59 = var1.method_23321();
                     collided = Math.abs(var55 - var11 - var51) > 0.001 || Math.abs(var59 - var15 - var53) > 0.001;
                     if (Math.abs(var57 - var13 - vy) > 0.001) {
                        vy = 0.0;
                     }

                     if (!var50) {
                        vy = (vy - 0.08) * 0.98;
                     }

                     double var61 = var1.method_24828() ? 0.55 : 0.91;
                     kbX *= var61;
                     kbZ *= var61;
                     if (Math.abs(kbX) < 0.003) {
                        kbX = 0.0;
                     }

                     if (Math.abs(kbZ) < 0.003) {
                        kbZ = 0.0;
                     }

                     float var64 = 0.0F;
                     if (var4 == FakePlayerMotion.Mode.Spin) {
                        float var63 = spinYaw;
                        lookYaw = spinYaw;
                     } else if (b(var0.fpLookAtMe)) {
                        double var65 = var17 - var55;
                        double var67 = var21 - var59;
                        float var82 = yawTo(var65, var67);
                        double var69 = Math.sqrt(var65 * var65 + var67 * var67);
                        var64 = (float)(-Math.toDegrees(Math.atan2(var19 - var57, Math.max(var69, 0.01))));
                        var64 = Math.max(-90.0F, Math.min(90.0F, var64));
                        lookYaw = approach(lookYaw, var82, 45.0F);
                     } else {
                        float var83 = var33 ? curMove : lookYaw;
                        lookYaw = approach(lookYaw, var83, 35.0F);
                     }

                     var1.method_5814(var11, var13, var15);
                     var1.method_5759(var55, var57, var59, lookYaw, var64, 1);
                     var1.method_5847(lookYaw);
                     if (b(var0.fpSwing) && R.nextInt(8) == 0) {
                        var1.method_6104(class_1268.field_5808);
                     }
                  }
               }
            }
         }
      }
   }

   private static void startFlying(class_745 var0) {
      try {
         class_1661 var1 = var0.method_31548();
         savedChest = var1.method_5438(38);
         var1.method_5447(38, new class_1799(class_1802.field_8833));
      } catch (Throwable var2) {
      }

      if (sneaking) {
         var0.method_5660(false);
         sneaking = false;
      }

      var0.method_5728(false);
      var0.method_23669();
      evx = 0.0;
      evy = 0.6;
      evz = 0.0;
      hasWaypoint = false;
      flying = true;
   }

   private static void stopFlying(class_745 var0) {
      flying = false;

      try {
         var0.method_23670();
      } catch (Throwable var3) {
         setFlagReflect(var0, 7, false);
      }

      try {
         if (savedChest != null) {
            var0.method_31548().method_5447(38, savedChest);
         }
      } catch (Throwable var2) {
      }

      savedChest = null;
      vy = evy;
      kbX = evx * 0.5;
      kbZ = evz * 0.5;
      var0.method_18800(0.0, 0.0, 0.0);
   }

   private static void setFlagReflect(Object var0, int var1, boolean var2) {
      for (Class var3 = var0.getClass(); var3 != null; var3 = var3.getSuperclass()) {
         for (Method var7 : var3.getDeclaredMethods()) {
            if (var7.getName().equals("method_5729") && var7.getParameterCount() == 2) {
               try {
                  var7.setAccessible(true);
                  var7.invoke(var0, var1, var2);
               } catch (Throwable var9) {
               }

               return;
            }
         }
      }
   }

   private static void elytraTick(FakePlayer var0, class_745 var1, class_746 var2) {
      if (!flying) {
         startFlying(var1);
      }

      if (!var1.method_6128()) {
         var1.method_23669();
      }

      double var3 = f(var0.fpElytraSpeed);
      double var5 = f(var0.fpElytraHeight);
      double var7 = Math.max(1.0, f(var0.fpElytraRadius));
      FakePlayerMotion.ElytraMode var9 = (FakePlayerMotion.ElytraMode)var0.fpElytraMode.getValue();
      double var10 = var1.method_23317();
      double var12 = var1.method_23318();
      double var14 = var1.method_23321();
      double var16 = var2.method_23317();
      double var18 = var2.method_23318();
      double var20 = var2.method_23321();
      double var22 = Math.min(0.6, var3 * 3.0 / var7);
      double var24;
      double var26;
      double var28;
      switch (var9) {
         case Orbit:
            double var63 = Math.atan2(var14 - var20, var10 - var16) + (double)dirSign * var22;
            var24 = var16 + Math.cos(var63) * var7;
            var28 = var20 + Math.sin(var63) * var7;
            var26 = var18 + var5;
            break;
         case Follow:
            double var62 = Math.sqrt((var16 - var10) * (var16 - var10) + (var20 - var14) * (var20 - var14));
            if (var62 > var7) {
               var24 = var16;
               var28 = var20;
               var26 = var18 + Math.min(var5, 4.0);
            } else {
               double var65 = Math.atan2(var14 - var20, var10 - var16) + (double)dirSign * var22;
               var24 = var16 + Math.cos(var65) * var7 * 0.8;
               var28 = var20 + Math.sin(var65) * var7 * 0.8;
               var26 = var18 + Math.min(var5, 4.0);
            }
            break;
         case Random:
            if (!hasWaypoint || dist3(var10, var12, var14, wpX, wpY, wpZ) < Math.max(2.0, var3 * 2.0) || collided && R.nextInt(5) == 0) {
               double var61 = R.nextDouble() * Math.PI * 2.0;
               double var32 = var7 * (0.3 + R.nextDouble() * 0.7);
               wpX = homeX + Math.cos(var61) * var32;
               wpZ = homeZ + Math.sin(var61) * var32;
               wpY = homeY + var5 * (0.5 + R.nextDouble());
               hasWaypoint = true;
            }

            var24 = wpX;
            var26 = wpY;
            var28 = wpZ;
            break;
         case Figure8:
            eAngle = eAngle + (double)dirSign * var3 / var7;
            double var60 = eAngle + (double)dirSign * var22;
            var24 = homeX + Math.sin(var60) * var7;
            var28 = homeZ + Math.sin(var60) * Math.cos(var60) * var7;
            var26 = homeY + var5 + Math.sin(var60 * 2.0) * 1.5;
            break;
         case Wave:
            double var59 = Math.atan2(var14 - homeZ, var10 - homeX) + (double)dirSign * var22;
            var24 = homeX + Math.cos(var59) * var7;
            var28 = homeZ + Math.sin(var59) * var7;
            var26 = homeY + Math.max(2.0, var5 + Math.sin(var59 * 3.0) * var5 * 0.5);
            break;
         default:
            double var30 = Math.atan2(var14 - homeZ, var10 - homeX) + (double)dirSign * var22;
            var24 = homeX + Math.cos(var30) * var7;
            var28 = homeZ + Math.sin(var30) * var7;
            var26 = homeY + var5;
      }

      double var64 = var24 - var10;
      double var66 = var26 - var12;
      double var34 = var28 - var14;
      double var36 = Math.sqrt(var64 * var64 + var66 * var66 + var34 * var34);
      boolean var38 = var9 == FakePlayerMotion.ElytraMode.Random || var9 == FakePlayerMotion.ElytraMode.Follow;
      double var39 = var38 ? Math.min(var3, Math.max(var36, 0.05)) : var3;
      double var41 = 0.0;
      double var43 = 0.0;
      double var45 = 0.0;
      if (var36 > 1.0E-4) {
         var41 = var64 / var36 * var39;
         var43 = var66 / var36 * var39;
         var45 = var34 / var36 * var39;
      }

      double var47 = Math.min(0.45, 0.2 + var3 * 0.04);
      evx = evx + (var41 - evx) * var47;
      evy = evy + (var43 - evy) * var47;
      evz = evz + (var45 - evz) * var47;
      if (var1.method_24828() && evy < 0.3) {
         evy = 0.5;
      }

      var1.method_5784(class_1313.field_6308, new class_243(evx, evy, evz));
      double var49 = var1.method_23317();
      double var51 = var1.method_23318();
      double var53 = var1.method_23321();
      collided = Math.abs(var49 - var10 - evx) > 0.001 || Math.abs(var53 - var14 - evz) > 0.001;
      if (collided) {
         evy = Math.max(evy, 0.4);
         if (R.nextInt(20) == 0) {
            dirSign = -dirSign;
         }
      }

      if (Math.abs(var51 - var12 - evy) > 0.001 && evy < 0.0) {
         evy = 0.2;
      }

      double var55 = Math.sqrt(evx * evx + evz * evz);
      float var57 = var55 > 0.001 ? yawTo(evx, evz) : lookYaw;
      float var58 = (float)(-Math.toDegrees(Math.atan2(evy, Math.max(var55, 0.001))));
      var58 = Math.max(-90.0F, Math.min(90.0F, var58));
      lookYaw = approach(lookYaw, var57, 30.0F);
      curMove = lookYaw;
      var1.method_5814(var10, var12, var14);
      var1.method_5759(var49, var51, var53, lookYaw, var58, 1);
      var1.method_5847(lookYaw);
      var1.method_18800(evx, evy, evz);
      if (b(var0.fpSwing) && R.nextInt(8) == 0) {
         var1.method_6104(class_1268.field_5808);
      }
   }

   private static double dist3(double var0, double var2, double var4, double var6, double var8, double var10) {
      return Math.sqrt((var0 - var6) * (var0 - var6) + (var2 - var8) * (var2 - var8) + (var4 - var10) * (var4 - var10));
   }

   private static void lookYawSmoothMove(float var0, float var1) {
      curMove = approach(curMove, var0, var1);
   }

   private static float yawTo(double var0, double var2) {
      return (float)Math.toDegrees(Math.atan2(var2, var0)) - 90.0F;
   }

   private static float wrap(float var0) {
      var0 %= 360.0F;
      if (var0 >= 180.0F) {
         var0 -= 360.0F;
      }

      if (var0 < -180.0F) {
         var0 += 360.0F;
      }

      return var0;
   }

   private static float approach(float var0, float var1, float var2) {
      float var3 = wrap(var1 - var0);
      if (var3 > var2) {
         var3 = var2;
      }

      if (var3 < -var2) {
         var3 = -var2;
      }

      return var0 + var3;
   }

   private static double dot(float var0, double var1, double var3) {
      double var5 = Math.toRadians((double)var0);
      return -Math.sin(var5) * var1 + Math.cos(var5) * var3;
   }

   private static double along(float var0, double var1, double var3) {
      return dot(var0, var1, var3);
   }

   public static enum ElytraMode {
      Circle,
      Orbit,
      Follow,
      Random,
      Figure8,
      Wave;
   }

   public static enum JumpMode {
      Off,
      Always,
      Random,
      OnCollide,
      Smart;
   }

   public static enum Mode {
      Random,
      Chaos,
      Strafe,
      Dodge,
      Orbit,
      Circle,
      Square,
      ZigZag,
      BackForth,
      Follow,
      Flee,
      BunnyHop,
      Spin;
   }

   public static enum SneakMode {
      Off,
      Always,
      Random;
   }
}
