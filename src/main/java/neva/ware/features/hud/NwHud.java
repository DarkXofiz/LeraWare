package neva.ware.features.hud;

import java.awt.Color;
import java.util.List;
import net.minecraft.class_408;
import net.minecraft.class_437;
import net.minecraft.class_4587;
import neva.ware.features.modules.Module;
import neva.ware.features.modules.client.HudEditor;
import neva.ware.gui.clickui.NwDraw;
import neva.ware.gui.font.FontRenderer;
import neva.ware.gui.font.FontRenderers;
import neva.ware.gui.hud.HudEditorGui;
import neva.ware.setting.Setting;
import neva.ware.setting.impl.ColorSetting;
import neva.ware.utility.render.Render2DEngine;
import neva.ware.utility.render.animation.EaseOutBack;

public final class NwHud {
   public static float blurAlpha = 1.0F;
   private static FontRenderer font;
   private static FontRenderer bold;
   private static FontRenderer small;
   private static boolean fontFail;
   public static final float FONT_PX = 7.0F;
   public static final float SMALL_PX = 6.0F;
   public static final float BAR_H = 15.0F;
   public static final float HEAD_H = 16.0F;
   public static final float ROW_H = 12.0F;
   private static final float[] N_PTS = new float[]{
      0.245F,
      0.5F,
      0.2066F,
      0.4965F,
      0.1661F,
      0.4819F,
      0.1344F,
      0.4605F,
      0.1041F,
      0.4291F,
      0.0816F,
      0.3969F,
      0.0595F,
      0.3587F,
      0.0409F,
      0.3247F,
      0.0195F,
      0.2873F,
      -3.0E-4F,
      0.2545F,
      -0.0229F,
      0.2171F,
      -0.0429F,
      0.1831F,
      -0.0655F,
      0.145F,
      -0.0873F,
      0.1128F,
      -0.1161F,
      0.0816F,
      -0.1518F,
      0.0587F,
      -0.189F,
      0.0472F,
      -0.2342F,
      0.0451F,
      -0.2732F,
      0.0532F,
      -0.3115F,
      0.0729F,
      -0.3387F,
      0.0985F,
      -0.3623F,
      0.1341F,
      -0.3793F,
      0.1696F,
      -0.3965F,
      0.2109F,
      -0.4113F,
      0.2472F,
      -0.4269F,
      0.2873F,
      -0.4394F,
      0.3229F,
      -0.4532F,
      0.364F,
      -0.4679F,
      0.4008F,
      -0.491F,
      0.4392F,
      -0.5236F,
      0.4687F,
      -0.5594F,
      0.4821F,
      -0.6014F,
      0.4792F,
      -0.6334F,
      0.4609F,
      -0.6563F,
      0.4264F,
      -0.6627F,
      0.3889F,
      -0.6559F,
      0.3448F,
      -0.6423F,
      0.3069F,
      -0.6245F,
      0.2663F,
      -0.6099F,
      0.231F,
      -0.596F,
      0.1907F,
      -0.5845F,
      0.1539F,
      -0.5709F,
      0.1118F,
      -0.5575F,
      0.0747F,
      -0.5415F,
      0.0338F,
      -0.5259F,
      -0.0066F,
      -0.5131F,
      -0.0427F,
      -0.4996F,
      -0.084F,
      -0.4875F,
      -0.1211F,
      -0.4728F,
      -0.1628F,
      -0.4589F,
      -0.1993F,
      -0.4431F,
      -0.2397F,
      -0.4298F,
      -0.2757F,
      -0.416F,
      -0.317F,
      -0.4037F,
      -0.3541F,
      -0.3873F,
      -0.3951F,
      -0.3683F,
      -0.4288F,
      -0.3398F,
      -0.4611F,
      -0.308F,
      -0.4827F,
      -0.2667F,
      -0.4971F,
      -0.2228F,
      -0.5F,
      -0.1847F,
      -0.4927F,
      -0.1461F,
      -0.474F,
      -0.1169F,
      -0.4492F,
      -0.0901F,
      -0.4147F,
      -0.0705F,
      -0.3806F,
      -0.051F,
      -0.341F,
      -0.0345F,
      -0.3062F,
      -0.0155F,
      -0.2676F,
      0.0018F,
      -0.2333F,
      0.0216F,
      -0.194F,
      0.0396F,
      -0.1588F,
      0.0615F,
      -0.1203F,
      0.0843F,
      -0.0895F,
      0.1158F,
      -0.0617F,
      0.1544F,
      -0.0434F,
      0.1933F,
      -0.0365F,
      0.2385F,
      -0.0396F,
      0.276F,
      -0.0518F,
      0.3115F,
      -0.0753F,
      0.3361F,
      -0.1036F,
      0.3572F,
      -0.1413F,
      0.3726F,
      -0.1776F,
      0.3885F,
      -0.2192F,
      0.4024F,
      -0.2556F,
      0.4172F,
      -0.2958F,
      0.4294F,
      -0.3319F,
      0.4428F,
      -0.3736F,
      0.457F,
      -0.4107F,
      0.4792F,
      -0.4491F,
      0.5109F,
      -0.4782F,
      0.5462F,
      -0.4915F,
      0.5891F,
      -0.4893F,
      0.6236F,
      -0.4722F,
      0.6512F,
      -0.4397F,
      0.6627F,
      -0.4036F,
      0.6614F,
      -0.3601F,
      0.6514F,
      -0.3218F,
      0.6359F,
      -0.2801F,
      0.622F,
      -0.244F,
      0.6081F,
      -0.2033F,
      0.5967F,
      -0.1666F,
      0.5834F,
      -0.1249F,
      0.5704F,
      -0.0879F,
      0.5548F,
      -0.0467F,
      0.5394F,
      -0.0059F,
      0.5266F,
      0.0304F,
      0.5132F,
      0.0716F,
      0.5013F,
      0.1086F,
      0.4872F,
      0.1502F,
      0.4738F,
      0.1869F,
      0.4586F,
      0.2278F,
      0.4457F,
      0.2642F,
      0.4322F,
      0.3054F,
      0.4201F,
      0.3424F,
      0.4043F,
      0.3833F,
      0.386F,
      0.4175F,
      0.3587F,
      0.4513F,
      0.3282F,
      0.4752F,
      0.2883F,
      0.493F
   };
   private static final float[] N_NRM = new float[]{
      0.0426F,
      0.9991F,
      -0.2231F,
      0.9748F,
      -0.446F,
      0.895F,
      -0.6493F,
      0.7606F,
      -0.7703F,
      0.6377F,
      -0.8444F,
      0.5357F,
      -0.8705F,
      0.4921F,
      -0.8722F,
      0.4891F,
      -0.8629F,
      0.5054F,
      -0.8559F,
      0.5171F,
      -0.8583F,
      0.5131F,
      -0.861F,
      0.5085F,
      -0.8459F,
      0.5334F,
      -0.7812F,
      0.6242F,
      -0.6426F,
      0.7662F,
      -0.4278F,
      0.9039F,
      -0.1627F,
      0.9867F,
      0.0721F,
      0.9974F,
      0.3377F,
      0.9413F,
      0.5682F,
      0.8229F,
      0.7699F,
      0.6382F,
      0.8686F,
      0.4955F,
      0.9132F,
      0.4076F,
      0.9245F,
      0.3811F,
      0.9295F,
      0.3689F,
      0.9372F,
      0.3488F,
      0.9456F,
      0.3252F,
      0.9394F,
      0.3428F,
      0.8939F,
      0.4483F,
      0.7726F,
      0.6349F,
      0.5311F,
      0.8473F,
      0.1349F,
      0.9909F,
      -0.276F,
      0.9612F,
      -0.693F,
      0.721F,
      -0.9262F,
      0.377F,
      -1.0F,
      -0.0053F,
      -0.9705F,
      -0.2409F,
      -0.9285F,
      -0.3715F,
      -0.9199F,
      -0.3922F,
      -0.9356F,
      -0.3531F,
      -0.9498F,
      -0.3129F,
      -0.9531F,
      -0.3026F,
      -0.9463F,
      -0.3232F,
      -0.9356F,
      -0.3531F,
      -0.932F,
      -0.3624F,
      -0.9374F,
      -0.3482F,
      -0.9468F,
      -0.3219F,
      -0.9506F,
      -0.3103F,
      -0.947F,
      -0.3212F,
      -0.9392F,
      -0.3433F,
      -0.9327F,
      -0.3606F,
      -0.9346F,
      -0.3556F,
      -0.9436F,
      -0.3312F,
      -0.9485F,
      -0.3167F,
      -0.9388F,
      -0.3444F,
      -0.9038F,
      -0.4279F,
      -0.8116F,
      -0.5842F,
      -0.6662F,
      -0.7457F,
      -0.4413F,
      -0.8974F,
      -0.1992F,
      -0.98F,
      0.0528F,
      -0.9986F,
      0.3212F,
      -0.947F,
      0.5401F,
      -0.8416F,
      0.7272F,
      -0.6864F,
      0.8281F,
      -0.5606F,
      0.8834F,
      -0.4687F,
      0.9002F,
      -0.4355F,
      0.9002F,
      -0.4355F,
      0.8952F,
      -0.4456F,
      0.8929F,
      -0.4503F,
      0.892F,
      -0.4521F,
      0.8795F,
      -0.4759F,
      0.8402F,
      -0.5423F,
      0.7338F,
      -0.6793F,
      0.5495F,
      -0.8355F,
      0.3087F,
      -0.9512F,
      0.0454F,
      -0.999F,
      -0.1819F,
      -0.9833F,
      -0.4394F,
      -0.8983F,
      -0.6526F,
      -0.7577F,
      -0.822F,
      -0.5695F,
      -0.8971F,
      -0.4417F,
      -0.928F,
      -0.3725F,
      -0.9341F,
      -0.357F,
      -0.9363F,
      -0.3511F,
      -0.9426F,
      -0.3339F,
      -0.9499F,
      -0.3126F,
      -0.9439F,
      -0.3303F,
      -0.9006F,
      -0.4346F,
      -0.7809F,
      -0.6247F,
      -0.535F,
      -0.8448F,
      -0.1409F,
      -0.99F,
      0.2416F,
      -0.9704F,
      0.6233F,
      -0.782F,
      0.8689F,
      -0.495F,
      0.992F,
      -0.1261F,
      0.9907F,
      0.1364F,
      0.9528F,
      0.3035F,
      0.9356F,
      0.353F,
      0.9405F,
      0.3399F,
      0.9502F,
      0.3115F,
      0.9535F,
      0.3012F,
      0.9484F,
      0.3172F,
      0.9392F,
      0.3433F,
      0.9355F,
      0.3534F,
      0.9393F,
      0.343F,
      0.9474F,
      0.3199F,
      0.9515F,
      0.3076F,
      0.9494F,
      0.314F,
      0.9434F,
      0.3316F,
      0.9382F,
      0.3461F,
      0.9397F,
      0.3421F,
      0.9468F,
      0.3219F,
      0.9506F,
      0.3105F,
      0.9414F,
      0.3373F,
      0.9102F,
      0.4142F,
      0.8306F,
      0.5569F,
      0.7066F,
      0.7076F,
      0.5098F,
      0.8603F,
      0.2861F,
      0.9582F
   };
   private static final short[] N_TRI = new short[]{
      32,
      33,
      34,
      30,
      31,
      32,
      93,
      94,
      95,
      90,
      91,
      92,
      61,
      62,
      63,
      1,
      2,
      3,
      56,
      57,
      58,
      116,
      117,
      118,
      119,
      0,
      1,
      34,
      35,
      36,
      100,
      101,
      102,
      68,
      69,
      70,
      59,
      60,
      61,
      49,
      50,
      51,
      114,
      115,
      116,
      65,
      66,
      67,
      109,
      110,
      111,
      40,
      41,
      42,
      107,
      108,
      109,
      3,
      4,
      5,
      54,
      55,
      56,
      5,
      6,
      7,
      26,
      27,
      28,
      95,
      96,
      97,
      63,
      64,
      65,
      9,
      10,
      11,
      97,
      98,
      99,
      47,
      48,
      49,
      102,
      103,
      104,
      42,
      43,
      44,
      86,
      87,
      88,
      36,
      37,
      38,
      88,
      89,
      90,
      28,
      29,
      30,
      90,
      92,
      93,
      25,
      26,
      28,
      113,
      114,
      116,
      8,
      9,
      11,
      85,
      86,
      88,
      53,
      54,
      56,
      36,
      38,
      39,
      56,
      58,
      59,
      116,
      118,
      119,
      106,
      107,
      109,
      46,
      47,
      49,
      42,
      44,
      45,
      102,
      104,
      105,
      30,
      32,
      34,
      93,
      95,
      97,
      119,
      1,
      3,
      59,
      61,
      63,
      112,
      113,
      116,
      52,
      53,
      56,
      3,
      5,
      7,
      63,
      65,
      67,
      8,
      11,
      12,
      88,
      90,
      93,
      30,
      34,
      36,
      111,
      112,
      116,
      51,
      52,
      56,
      25,
      28,
      30,
      3,
      7,
      8,
      63,
      67,
      68,
      106,
      109,
      111,
      100,
      102,
      105,
      46,
      49,
      51,
      40,
      42,
      45,
      93,
      97,
      99,
      45,
      46,
      51,
      100,
      105,
      106,
      24,
      25,
      30,
      39,
      40,
      45,
      56,
      59,
      63,
      116,
      119,
      3,
      93,
      99,
      100,
      30,
      36,
      39,
      85,
      88,
      93,
      23,
      24,
      30,
      63,
      68,
      70,
      84,
      85,
      93,
      63,
      70,
      71,
      22,
      23,
      30,
      83,
      84,
      93,
      21,
      22,
      30,
      3,
      8,
      12,
      82,
      83,
      93,
      106,
      111,
      116,
      51,
      56,
      63,
      81,
      82,
      93,
      21,
      30,
      39,
      20,
      21,
      39,
      19,
      20,
      39,
      19,
      39,
      45,
      18,
      19,
      45,
      17,
      18,
      45,
      16,
      17,
      45,
      80,
      81,
      93,
      16,
      45,
      51,
      15,
      16,
      51,
      14,
      15,
      51,
      80,
      93,
      100,
      79,
      80,
      100,
      78,
      79,
      100,
      78,
      100,
      106,
      77,
      78,
      106,
      76,
      77,
      106,
      75,
      76,
      106,
      116,
      3,
      12,
      116,
      12,
      13,
      51,
      63,
      71,
      51,
      71,
      72,
      51,
      72,
      73,
      51,
      73,
      74,
      116,
      13,
      14,
      51,
      74,
      75,
      75,
      106,
      116,
      14,
      51,
      75,
      14,
      75,
      116
   };

   private NwHud() {
   }

   private static void fonts() {
      if (font == null && !fontFail) {
         try {
            font = FontRenderers.create(14.0F, "sf_medium");
            bold = FontRenderers.create(14.0F, "sf_bold");
            small = FontRenderers.create(12.0F, "sf_medium");
         } catch (Throwable var1) {
            fontFail = true;
         }
      }
   }

   public static FontRenderer font() {
      fonts();
      return font != null ? font : FontRenderers.sf_medium_modules;
   }

   public static FontRenderer bold() {
      fonts();
      return bold != null ? bold : FontRenderers.sf_bold_mini;
   }

   public static FontRenderer small() {
      fonts();
      return small != null ? small : FontRenderers.sf_medium_mini;
   }

   public static float ty(float var0, float var1) {
      return NwDraw.textTop(var0, var1);
   }

   public static float w(String var0) {
      return font().getStringWidth(var0);
   }

   public static float wb(String var0) {
      return bold().getStringWidth(var0);
   }

   public static float ws(String var0) {
      return small().getStringWidth(var0);
   }

   public static void text(class_4587 var0, String var1, float var2, float var3, int var4) {
      NwDraw.text(font(), var0, var1, var2, ty(var3, 7.0F), var4);
   }

   public static void textBold(class_4587 var0, String var1, float var2, float var3, int var4) {
      NwDraw.text(bold(), var0, var1, var2, ty(var3, 7.0F), var4);
   }

   public static void textSmall(class_4587 var0, String var1, float var2, float var3, int var4) {
      NwDraw.text(small(), var0, var1, var2, ty(var3, 6.0F), var4);
   }

   private static <T> T val(Setting<T> var0, T var1) {
      try {
         Object var2 = var0.getValue();
         return (T)(var2 == null ? var1 : var2);
      } catch (Throwable var3) {
         return (T)var1;
      }
   }

   public static float radius() {
      return Math.max(0.0F, Math.min(7.0F, val(HudEditor.hudRound, 3.5F)));
   }

   public static int plateColor() {
      ColorSetting var0 = val(HudEditor.plateColor, null);
      return var0 == null ? -401600231 : var0.getColor();
   }

   public static int textColor() {
      ColorSetting var0 = val(HudEditor.textColor, null);
      int var1 = var0 == null ? -1 : var0.getColor();
      return var1 >>> 24 == 0 ? var1 | 0xFF000000 : var1;
   }

   public static int textColor2() {
      ColorSetting var0 = val(HudEditor.textColor2, null);
      int var1 = var0 == null ? -1 : var0.getColor();
      if (var1 >>> 24 == 0) {
         var1 |= -16777216;
      }

      return NwDraw.mulAlpha(var1, 0.55F);
   }

   public static boolean blurry() {
      try {
         return HudEditor.hudStyle.is(HudEditor.HudStyle.Blurry);
      } catch (Throwable var1) {
         return false;
      }
   }

   public static boolean light() {
      return val(HudEditor.glow, true);
   }

   public static int accent(int var0) {
      try {
         Color var1 = HudEditor.getColor(var0);
         return 0xFF000000 | var1.getRGB() & 16777215;
      } catch (Throwable var2) {
         return -8614657;
      }
   }

   public static boolean editing() {
      try {
         class_437 var0 = Module.mc.field_1755;
         return var0 instanceof HudEditorGui || var0 instanceof class_408;
      } catch (Throwable var1) {
         return false;
      }
   }

   public static void plate(class_4587 var0, float var1, float var2, float var3, float var4) {
      plate(var0, var1, var2, var3, var4, radius(), 1.0F);
   }

   public static void plate(class_4587 var0, float var1, float var2, float var3, float var4, float var5, float var6) {
      if (!(var3 <= 0.5F) && !(var4 <= 0.5F)) {
         float var7 = NwDraw.alpha;
         NwDraw.alpha = var7 * var6;
         var5 = Math.min(var5, Math.min(var3, var4) / 2.0F);
         int var8 = plateColor();
         float var9 = Math.max(0.15F, Math.min(1.0F, val(HudEditor.alpha, 0.7F) / 0.7F));
         int var10 = NwDraw.mulAlpha(var8, Math.min(1.0F, var9));
         NwDraw.shadow(var0, var1, var2, var3, var4, var5, 6.0F, 1174405120);
         if (light()) {
            float var11 = Math.max(0.0F, Math.min(1.0F, val(HudEditor.glow1, 0.2F)));
            NwDraw.shadow(var0, var1, var2, var3, var4, var5, 7.0F, NwDraw.withAlpha(accent(0), Math.round(30.0F + 90.0F * var11)));
         }

         if (blurry()) {
            if (NwDraw.alpha > 0.01F) {
               try {
                  ColorSetting var18 = val(HudEditor.blurColor, null);
                  blurAlpha = NwDraw.alpha;
                  Render2DEngine.drawRoundedBlur(var0, var1, var2, var3, var4, var5, var18 == null ? new Color(2960685) : var18.getColorObject());
               } catch (Throwable var15) {
               } finally {
                  blurAlpha = 1.0F;
               }
            }

            var10 = NwDraw.mulAlpha(var10, 0.55F);
         }

         int var19 = NwDraw.mix(var10, NwDraw.withAlpha(-14013120, NwDraw.alphaOf(var10)), 0.35F);
         NwDraw.round(var0, var1, var2, var3, var4, var5, var19, var10);
         float var12 = val(HudEditor.outline, 0.0F);
         NwDraw.border(
            var0, var1, var2, var3, var4, var5, 0.5F + Math.max(0.0F, Math.min(2.5F, var12)) * 0.3F, NwDraw.withAlpha(-1, Math.round(14.0F + var12 * 8.0F))
         );
         NwDraw.alpha = var7;
      }
   }

   public static void drawHudBase(class_4587 var0, float var1, float var2, float var3, float var4, float var5) {
      plate(var0, var1, var2, var3, var4, Math.max(var5, 0.0F) > 0.0F ? Math.min(var5, 7.0F) : radius(), 1.0F);
   }

   public static void drawHudBase(class_4587 var0, float var1, float var2, float var3, float var4, float var5, boolean var6) {
      drawHudBase(var0, var1, var2, var3, var4, var5);
   }

   public static void drawHudBase(class_4587 var0, float var1, float var2, float var3, float var4, float var5, float var6) {
      plate(var0, var1, var2, var3, var4, Math.min(Math.max(var5, 0.0F), 7.0F), Math.max(0.1F, Math.min(1.0F, var6)));
   }

   public static void drawHudBase2(class_4587 var0, float var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8) {
      drawHudBase(var0, var1, var2, var3, var4, var5);
   }

   public static float chip(class_4587 var0, String var1, float var2, float var3, int var4) {
      float var5 = ws(var1) + 7.0F;
      float var6 = 9.0F;
      NwDraw.round(var0, var2 - var5, var3 - var6 / 2.0F, var5, var6, Math.min(3.0F, radius()), 855638016);
      NwDraw.border(var0, var2 - var5, var3 - var6 / 2.0F, var5, var6, Math.min(3.0F, radius()), 0.5F, 318767103);
      textSmall(var0, var1, var2 - var5 + 3.5F, var3, var4);
      return var5;
   }

   public static void divider(class_4587 var0, float var1, float var2, float var3) {
      NwDraw.rect(var0, var1, var2 + 3.5F, 0.6F, var3 - 7.0F, 486539263);
   }

   public static void bar(class_4587 var0, float var1, float var2, float var3, float var4, float var5) {
      var5 = Math.max(0.0F, Math.min(1.0F, var5));
      NwDraw.round(var0, var1, var2, var3, var4, var4 / 2.0F, 822083583);
      if (var5 > 0.001F) {
         float var6 = Math.max(var4, var3 * var5);
         NwDraw.roundQuad(var0, var1, var2, var6, var4, var4 / 2.0F, accent(0), accent(180), accent(180), accent(0));
      }
   }

   public static float segBar(class_4587 var0, float var1, float var2, List<NwHud.Seg> var3, NwHud.Anim var4) {
      float var5 = 0.0F;

      for (NwHud.Seg var7 : var3) {
         var5 += var7.width();
      }

      float var15 = var4 == null ? var5 : var4.to(var5, 14.0F);
      plate(var0, var1, var2, var15, 15.0F);
      class_4587 var16 = var0;
      float var8 = var1;
      float var9 = var2 + 7.5F;
      int var10 = 0;

      for (NwHud.Seg var12 : var3) {
         float var13 = var12.width() * (var5 <= 0.0F ? 1.0F : var15 / var5);
         float var14 = var8 + 6.0F;
         if (var12.icon != null) {
            icon(var16, var12.icon, var14 + 4.5F, var9, 8.5F, accent(var10 * 60));
            var14 += 13.0F;
         }

         if (var12.boldValue) {
            textBold(var16, var12.value, var14, var9, textColor());
            var14 += wb(var12.value);
         } else {
            text(var16, var12.value, var14, var9, textColor());
            var14 += w(var12.value);
         }

         if (var12.label != null && !var12.label.isEmpty()) {
            text(var16, var12.label, var14 + 3.0F, var9, textColor2());
         }

         var8 += var13;
         if (var10 < var3.size() - 1) {
            divider(var16, var8, var2, 15.0F);
         }

         var10++;
      }

      return var15;
   }

   public static float panel(class_4587 var0, float var1, float var2, float var3, float var4, NwHud.Icon var5, String var6, String var7) {
      plate(var0, var1, var2, var3, var4);
      float var8 = var2 + 8.0F;
      float var9 = var1 + 6.0F;
      if (var5 != null) {
         icon(var0, var5, var9 + 4.5F, var8, 8.5F, accent(0));
         var9 += 13.0F;
      }

      textBold(var0, var6, var9, var8, textColor());
      if (var7 != null && !var7.isEmpty()) {
         textSmall(var0, var7, var1 + var3 - 6.0F - ws(var7), var8, textColor2());
      }

      if (var4 > 17.0F) {
         NwDraw.hGradient(var0, var1 + 5.0F, var2 + 16.0F, (var3 - 10.0F) / 2.0F, 0.6F, 16777215, 486539263);
         NwDraw.hGradient(var0, var1 + var3 / 2.0F, var2 + 16.0F, (var3 - 10.0F) / 2.0F, 0.6F, 486539263, 16777215);
      }

      return var2 + 16.0F + 2.0F;
   }

   public static void nLogo(class_4587 var0, float var1, float var2, float var3) {
      float var4 = var3 * 0.8F;
      int var5 = N_PTS.length / 2;
      float[] var6 = new float[var5 * 2];
      int[] var7 = new int[var5];
      int var8 = accent(0);
      int var9 = accent(180);

      for (int var10 = 0; var10 < var5; var10++) {
         float var11 = N_PTS[var10 * 2];
         float var12 = N_PTS[var10 * 2 + 1];
         var6[var10 * 2] = var1 + var11 * var4;
         var6[var10 * 2 + 1] = var2 + var12 * var4;
         float var13 = Math.max(0.0F, Math.min(1.0F, (var11 / 1.325F + var12) * 0.5F + 0.5F));
         int var14 = NwDraw.mix(var8, var9, var13);
         var14 = NwDraw.mix(var14, -1, Math.max(0.0F, -var12) * 0.35F);
         var7[var10] = var14;
      }

      NwDraw.mesh(var0, var6, N_NRM, N_TRI, var7);
   }

   public static void beginFade(EaseOutBack var0) {
      try {
         NwDraw.alpha = (float)Math.max(0.0, Math.min(1.0, var0.getAnimationd()));
      } catch (Throwable var2) {
         NwDraw.alpha = 1.0F;
      }
   }

   public static void endFade() {
      NwDraw.alpha = 1.0F;
   }

   public static void icon(class_4587 var0, NwHud.Icon var1, float var2, float var3, float var4, int var5) {
      float var6 = var4 / 2.0F;
      float var7 = Math.max(0.8F, var4 * 0.11F);
      switch (var1) {
         case LOGO:
            nLogo(var0, var2, var3, var4);
            break;
         case USER:
            NwDraw.circle(var0, var2, var3 - var6 * 0.38F, var4 * 0.21F, var5);
            NwDraw.round(var0, var2 - var6 * 0.78F, var3 + var6 * 0.12F, var6 * 1.56F, var6 * 0.88F, var6 * 0.44F, var5);
            break;
         case FPS:
            float var15 = var4 * 0.2F;
            NwDraw.round(var0, var2 - var6, var3 + var6 * 0.2F, var15, var6 * 0.8F, var15 / 2.0F, var5);
            NwDraw.round(var0, var2 - var15 / 2.0F, var3 - var6 * 0.3F, var15, var6 * 1.3F, var15 / 2.0F, var5);
            NwDraw.round(var0, var2 + var6 - var15, var3 - var6, var15, var6 * 2.0F, var15 / 2.0F, var5);
            break;
         case CLOCK:
            NwDraw.border(var0, var2 - var6, var3 - var6, var4, var4, var6, var7 * 1.1F, var5);
            NwDraw.line(var0, var2, var3, var2, var3 - var6 * 0.55F, var7, var5);
            NwDraw.line(var0, var2, var3, var2 + var6 * 0.42F, var3 + var6 * 0.2F, var7, var5);
            break;
         case PIN:
            NwDraw.border(var0, var2 - var6 * 0.72F, var3 - var6, var6 * 1.44F, var6 * 1.44F, var6 * 0.72F, var7 * 1.1F, var5);
            NwDraw.circle(var0, var2, var3 - var6 * 0.28F, var4 * 0.1F, var5);
            NwDraw.line(var0, var2 - var6 * 0.5F, var3 + var6 * 0.2F, var2, var3 + var6, var7 * 1.1F, var5);
            NwDraw.line(var0, var2 + var6 * 0.5F, var3 + var6 * 0.2F, var2, var3 + var6, var7 * 1.1F, var5);
            break;
         case PING:
            float var14 = var4 * 0.16F;

            for (int var17 = 0; var17 < 4; var17++) {
               float var10 = var4 * (0.3F + 0.23F * (float)var17);
               float var11 = var2 - var6 + (float)var17 * (var14 + var4 * 0.1F);
               NwDraw.round(var0, var11, var3 + var6 - var10, var14, var10, var14 / 2.0F, var17 < 3 ? var5 : NwDraw.mulAlpha(var5, 0.45F));
            }
            break;
         case TPS:
            NwDraw.line(var0, var2 + var6 * 0.3F, var3 - var6, var2 - var6 * 0.45F, var3 + var6 * 0.1F, var7 * 1.2F, var5);
            NwDraw.line(var0, var2 - var6 * 0.45F, var3 + var6 * 0.1F, var2 + var6 * 0.45F, var3 - var6 * 0.1F, var7 * 1.2F, var5);
            NwDraw.line(var0, var2 + var6 * 0.45F, var3 - var6 * 0.1F, var2 - var6 * 0.3F, var3 + var6, var7 * 1.2F, var5);
            break;
         case SPEED:
            NwDraw.chevron(var0, var2 - var6 * 0.35F, var3, var6 * 0.55F, -90.0F, var5);
            NwDraw.chevron(var0, var2 + var6 * 0.35F, var3, var6 * 0.55F, -90.0F, NwDraw.mulAlpha(var5, 0.6F));
            break;
         case MEMORY:
            NwDraw.border(var0, var2 - var6 * 0.62F, var3 - var6 * 0.62F, var6 * 1.24F, var6 * 1.24F, var6 * 0.25F, var7 * 1.1F, var5);
            NwDraw.round(var0, var2 - var6 * 0.25F, var3 - var6 * 0.25F, var6 * 0.5F, var6 * 0.5F, var6 * 0.1F, var5);

            for (int var13 = -1; var13 <= 1; var13++) {
               float var16 = (float)var13 * var6 * 0.38F;
               NwDraw.line(var0, var2 + var16, var3 - var6, var2 + var16, var3 - var6 * 0.62F, var7, var5);
               NwDraw.line(var0, var2 + var16, var3 + var6 * 0.62F, var2 + var16, var3 + var6, var7, var5);
            }
            break;
         case POTION:
            NwDraw.circle(var0, var2, var3 + var6 * 0.28F, var6 * 0.68F, var5);
            NwDraw.round(var0, var2 - var6 * 0.25F, var3 - var6, var6 * 0.5F, var6 * 0.9F, var6 * 0.12F, var5);
            NwDraw.circle(var0, var2 - var6 * 0.2F, var3 + var6 * 0.15F, var6 * 0.15F, 1728053247);
            break;
         case KEYBOARD:
            NwDraw.border(var0, var2 - var6, var3 - var6 * 0.65F, var4, var6 * 1.3F, var6 * 0.3F, var7 * 1.1F, var5);

            for (int var12 = 0; var12 < 3; var12++) {
               NwDraw.circle(var0, var2 - var6 * 0.5F + (float)var12 * var6 * 0.5F, var3 - var6 * 0.15F, var4 * 0.06F, var5);
            }

            NwDraw.round(var0, var2 - var6 * 0.45F, var3 + var6 * 0.18F, var6 * 0.9F, var7, var7 / 2.0F, var5);
            break;
         case TIMER:
            NwDraw.line(var0, var2 - var6 * 0.6F, var3 - var6, var2 + var6 * 0.6F, var3 - var6, var7 * 1.2F, var5);
            NwDraw.line(var0, var2 - var6 * 0.6F, var3 + var6, var2 + var6 * 0.6F, var3 + var6, var7 * 1.2F, var5);
            NwDraw.line(var0, var2 - var6 * 0.5F, var3 - var6, var2 + var6 * 0.5F, var3 + var6, var7, var5);
            NwDraw.line(var0, var2 + var6 * 0.5F, var3 - var6, var2 - var6 * 0.5F, var3 + var6, var7, var5);
            break;
         case SHIELD:
            NwDraw.line(var0, var2 - var6 * 0.75F, var3 - var6 * 0.7F, var2, var3 - var6, var7 * 1.1F, var5);
            NwDraw.line(var0, var2, var3 - var6, var2 + var6 * 0.75F, var3 - var6 * 0.7F, var7 * 1.1F, var5);
            NwDraw.line(var0, var2 - var6 * 0.75F, var3 - var6 * 0.7F, var2 - var6 * 0.6F, var3 + var6 * 0.2F, var7 * 1.1F, var5);
            NwDraw.line(var0, var2 + var6 * 0.75F, var3 - var6 * 0.7F, var2 + var6 * 0.6F, var3 + var6 * 0.2F, var7 * 1.1F, var5);
            NwDraw.line(var0, var2 - var6 * 0.6F, var3 + var6 * 0.2F, var2, var3 + var6, var7 * 1.1F, var5);
            NwDraw.line(var0, var2 + var6 * 0.6F, var3 + var6 * 0.2F, var2, var3 + var6, var7 * 1.1F, var5);
            break;
         case SWORD:
            NwDraw.line(var0, var2 - var6 * 0.8F, var3 + var6 * 0.8F, var2 + var6 * 0.85F, var3 - var6 * 0.85F, var7 * 1.3F, var5);
            NwDraw.line(var0, var2 - var6 * 0.75F, var3 + var6 * 0.1F, var2 - var6 * 0.1F, var3 + var6 * 0.75F, var7 * 1.3F, var5);
            break;
         case CHEST:
            NwDraw.border(var0, var2 - var6, var3 - var6 * 0.7F, var4, var6 * 1.4F, var6 * 0.25F, var7 * 1.1F, var5);
            NwDraw.line(var0, var2 - var6, var3 - var6 * 0.1F, var2 + var6, var3 - var6 * 0.1F, var7, var5);
            NwDraw.round(var0, var2 - var6 * 0.18F, var3 - var6 * 0.3F, var6 * 0.36F, var6 * 0.45F, var6 * 0.08F, var5);
            break;
         case LIST:
            for (int var8 = 0; var8 < 3; var8++) {
               float var9 = var3 - var6 * 0.6F + (float)var8 * var6 * 0.6F;
               NwDraw.circle(var0, var2 - var6 * 0.75F, var9, var4 * 0.07F, var5);
               NwDraw.round(var0, var2 - var6 * 0.4F, var9 - var7 / 2.0F, var6 * 1.35F, var7, var7 / 2.0F, var5);
            }
            break;
         case STAR:
            NwDraw.circle(var0, var2, var3, var6 * 0.6F, var5);
            break;
         case BOX:
            NwDraw.border(var0, var2 - var6, var3 - var6, var4, var4, var6 * 0.3F, var7, var5);
      }
   }

   public static final class Anim {
      private float v;
      private long t;
      private boolean init;

      public Anim() {
      }

      public Anim(float var1) {
         this.v = var1;
         this.init = true;
         this.t = System.nanoTime();
      }

      public float to(float var1, float var2) {
         long var3 = System.nanoTime();
         if (!this.init) {
            this.init = true;
            this.t = var3;
            this.v = var1;
            return this.v;
         } else {
            float var5 = Math.max(0.0F, Math.min(0.1F, (float)(var3 - this.t) / 1.0E9F));
            this.t = var3;
            this.v = this.v + (var1 - this.v) * (1.0F - (float)Math.exp((double)(-var2 * var5)));
            if (Math.abs(var1 - this.v) < 0.002F) {
               this.v = var1;
            }

            return this.v;
         }
      }

      public float get() {
         return this.v;
      }

      public void set(float var1) {
         this.v = var1;
         this.init = true;
         this.t = System.nanoTime();
      }
   }

   public static enum Icon {
      LOGO,
      USER,
      FPS,
      CLOCK,
      PIN,
      PING,
      TPS,
      SPEED,
      MEMORY,
      POTION,
      KEYBOARD,
      TIMER,
      SHIELD,
      SWORD,
      CHEST,
      LIST,
      STAR,
      BOX;
   }

   public static final class Seg {
      public final NwHud.Icon icon;
      public final String value;
      public final String label;
      public final boolean boldValue;

      public Seg(NwHud.Icon var1, String var2, String var3, boolean var4) {
         this.icon = var1;
         this.value = var2;
         this.label = var3;
         this.boldValue = var4;
      }

      public float width() {
         float var1 = 0.0F;
         if (this.icon != null) {
            var1 += 13.0F;
         }

         var1 += this.boldValue ? NwHud.wb(this.value) : NwHud.w(this.value);
         if (this.label != null && !this.label.isEmpty()) {
            var1 += 3.0F + NwHud.w(this.label);
         }

         return var1 + 12.0F;
      }
   }
}
