package neva.ware.features.hud.impl;

import com.mojang.blaze3d.systems.RenderSystem;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.class_1291;
import net.minecraft.class_1293;
import net.minecraft.class_1294;
import net.minecraft.class_1309;
import net.minecraft.class_1657;
import net.minecraft.class_1799;
import net.minecraft.class_266;
import net.minecraft.class_2960;
import net.minecraft.class_332;
import net.minecraft.class_408;
import net.minecraft.class_4587;
import net.minecraft.class_5250;
import net.minecraft.class_742;
import net.minecraft.class_757;
import net.minecraft.class_8646;
import net.minecraft.class_9013;
import net.minecraft.class_9025;
import neva.ware.core.manager.client.ModuleManager;
import neva.ware.features.hud.HudElement;
import neva.ware.features.hud.NwEase;
import neva.ware.features.hud.NwHud;
import neva.ware.features.modules.client.HudEditor;
import neva.ware.features.modules.combat.Aura;
import neva.ware.features.modules.combat.AutoAnchor;
import neva.ware.features.modules.combat.AutoCrystal;
import neva.ware.features.modules.misc.NameProtect;
import neva.ware.gui.font.FontRenderers;
import neva.ware.gui.hud.HudEditorGui;
import neva.ware.setting.Setting;
import neva.ware.setting.impl.ColorSetting;
import neva.ware.utility.NevaUtility;
import neva.ware.utility.Timer;
import neva.ware.utility.math.MathUtility;
import neva.ware.utility.render.Render2DEngine;
import neva.ware.utility.render.Render3DEngine;
import neva.ware.utility.render.TextureStorage;
import neva.ware.utility.render.animation.EaseOutBack;
import neva.ware.utility.render.animation.EaseOutCirc;

public class TargetHud extends HudElement {
    private final Setting<Integer> blurRadius = new Setting<>("BallonBlur", 10, 1, 10);
    private final Setting<Integer> animX = new Setting<>("AnimationX", 0, -2000, 2000);
    private final Setting<Integer> animY = new Setting<>("AnimationY", 0, -2000, 2000);
    private final Setting<TargetHud.HPmodeEn> hpMode = new Setting<>("HP Mode", TargetHud.HPmodeEn.HP);
    private final Setting<TargetHud.ImageModeEn> imageMode = new Setting<>("Image", TargetHud.ImageModeEn.Anime);
    private final Setting<TargetHud.ModeEn> Mode = new Setting<>("Mode", TargetHud.ModeEn.NevaWare);
    private final Setting<ColorSetting> color = new Setting<>("Color1", new ColorSetting(-16492289), v -> this.Mode.getValue() == TargetHud.ModeEn.CelkaPasta);
    private final Setting<ColorSetting> color2 = new Setting<>("Color2", new ColorSetting(-16492289), v -> this.Mode.getValue() == TargetHud.ModeEn.CelkaPasta);
    private final Setting<Boolean> funTimeHP = new Setting<>("FunTimeHP", false);
    private final Setting<Boolean> mini = new Setting<>("Mini", false, v -> this.Mode.getValue() == TargetHud.ModeEn.NurikZapen);
    private final Setting<Boolean> absorp = new Setting<>("Absorption", true);
    private static class_2960 custom;
    public EaseOutBack animation = new NwEase();
    public static EaseOutCirc healthAnimation = new EaseOutCirc();
    public static EaseOutCirc headAnimation = new EaseOutCirc();
    private final ArrayList<Particles> particles = new ArrayList();
    private boolean sentParticles;
    private boolean direction = false;
    private class_1309 target;
    float ticks;
    private final Timer timer = new Timer();

    public TargetHud() {
        super("TargetHud", 150, 50);
    }

    @Override
    public void onEnable() {
        try {
            custom = NevaUtility.getCustomImg("thud");
        } catch (Exception var2) {
            this.sendMessage(".minecraft -> NevaWare -> misc -> images -> thud.png");
        }
    }

    public String getDurationString(class_1293 pe) {
        return pe.method_48559() ? "*:*" : pe.method_5584() / 1200 + ":" + pe.method_5584() % 1200 / 20;
    }

    @Override
    public void onUpdate() {
        this.animation.update(this.direction);
        healthAnimation.update();
        headAnimation.update();
    }

    @Override
    public void onRender2D(class_332 context) {
        super.onRender2D(context);
        this.getTarget();
        this.animation.update(this.direction);
        NwHud.beginFade(this.animation);
        if (this.target == null) {
            NwHud.endFade();
        } else {
            float health = Math.min(this.target.method_6063(), this.getHealth());
            context.method_51448().method_22903();
            if (!HudEditor.hudStyle.is(HudEditor.HudStyle.Blurry)) {
                if (this.Mode.is(TargetHud.ModeEn.NurikZapen) && this.mini.getValue()) {
                    sizeAnimation(
                        context.method_51448(),
                        this.getPosX() + 45.0F + this.animX.getValue().intValue(),
                        this.getPosY() + 15.0F + this.animY.getValue().intValue(),
                        this.animation.getAnimationd()
                    );
                } else {
                    sizeAnimation(
                        context.method_51448(),
                        this.getPosX() + 75.0F + this.animX.getValue().intValue(),
                        this.getPosY() + 25.0F + this.animY.getValue().intValue(),
                        this.animation.getAnimationd()
                    );
                }
            }

            if (this.animation.getAnimationd() > 0.0) {
                float animationFactor = (float)MathUtility.clamp(this.animation.getAnimationd(), 0.0, 1.0);
                switch ((TargetHud.ModeEn)this.Mode.getValue()) {
                    case NevaWare:
                        this.renderNevaWare(context, health, animationFactor);
                        break;
                    case NurikZapen:
                        if (this.mini.getValue()) {
                            this.renderMiniNurik(context, health, animationFactor);
                        } else {
                            this.renderNurik(context, health, animationFactor);
                        }
                        break;
                    case CelkaPasta:
                        this.renderCelkaPasta(context, health);
                }
            }

            context.method_51448().method_22909();
            NwHud.endFade();
        }
    }

    private void getTarget() {
        if (AutoCrystal.target != null) {
            this.target = AutoCrystal.target;
            this.direction = true;
            if (AutoCrystal.target.method_29504()) {
                AutoCrystal.target = null;
                this.target = null;
            }
        } else if (Aura.target != null) {
            if (Aura.target instanceof class_1309) {
                this.target = (class_1309)Aura.target;
                this.direction = true;
            } else {
                this.target = null;
                this.direction = false;
            }
        } else if (AutoAnchor.target != null) {
            this.target = AutoAnchor.target;
            this.direction = true;
            if (AutoAnchor.target.method_29504()) {
                AutoAnchor.target = null;
                this.target = null;
            }
        } else if (!(mc.field_1755 instanceof class_408) && !(mc.field_1755 instanceof HudEditorGui)) {
            this.direction = false;
            if (this.animation.getAnimationd() < 0.02) {
                this.target = null;
            }
        } else {
            this.target = mc.field_1724;
            this.direction = true;
        }
    }

    private void renderCelkaPasta(class_332 context, float health) {
        float hurtPercent = this.target.field_6235 / 6.0F;
        Render2DEngine.drawBlurredShadow(
            context.method_51448(), this.getPosX() - 2.0F, this.getPosY() - 2.0F, 164.0F, 51.0F, 5, this.color.getValue().getColorObject()
        );
        Render2DEngine.drawRect(context.method_51448(), this.getPosX(), this.getPosY(), 160.0F, 47.0F, new Color(1711276032, true));
        this.setBounds(this.getPosX(), this.getPosY(), 160.0F, 47.0F);
        Render2DEngine.drawRect(context.method_51448(), this.getPosX() + 117.0F, this.getPosY() + 4.0F, 18.0F, 18.0F, new Color(1291845632, true));
        Render2DEngine.drawRect(context.method_51448(), this.getPosX() + 137.0F, this.getPosY() + 4.0F, 18.0F, 18.0F, new Color(1291845632, true));
        Render2DEngine.drawRect(context.method_51448(), this.getPosX() + 117.0F, this.getPosY() + 25.0F, 18.0F, 18.0F, new Color(1291845632, true));
        Render2DEngine.drawRect(context.method_51448(), this.getPosX() + 137.0F, this.getPosY() + 25.0F, 18.0F, 18.0F, new Color(1291845632, true));
        Render2DEngine.drawBlurredShadow(
            context.method_51448(),
            this.getPosX() + 49.0F,
            this.getPosY() + 29.0F,
            62.0F,
            12.0F,
            5,
            this.color.getValue().getColorObject().brighter().brighter().brighter()
        );
        Render2DEngine.drawRect(context.method_51448(), this.getPosX() + 50.0F, this.getPosY() + 30.0F, 60.0F, 10.0F, new Color(-1644167168, true));
        Render2DEngine.drawRect(
            context.method_51448(),
            this.getPosX() + 50.0F,
            this.getPosY() + 30.0F,
            MathUtility.clamp((int)(60.0F * (health / this.target.method_6063())), 0, 60),
            10.0F,
            this.color.getValue().getColorObject().brighter().brighter().brighter()
        );
        if (this.target instanceof class_1657) {
            RenderSystem.setShaderTexture(0, ((class_742)this.target).method_52814().comp_1626());
        } else {
            RenderSystem.setShaderTexture(0, mc.method_1561().method_3953(this.target).method_3931(this.target));
        }

        RenderSystem.setShaderColor(1.0F, 1.0F - hurtPercent, 1.0F - hurtPercent, 1.0F);
        Render2DEngine.renderTexture(
            context.method_51448(),
            this.getPosX() + 3.5F + hurtPercent,
            this.getPosY() + 3.5F + hurtPercent,
            40.0F - hurtPercent * 2.0F,
            40.0F - hurtPercent * 2.0F,
            8.0F,
            8.0F,
            8.0,
            8.0,
            64.0,
            64.0
        );
        Render2DEngine.renderTexture(
            context.method_51448(),
            this.getPosX() + 3.5F + hurtPercent,
            this.getPosY() + 3.5F + hurtPercent,
            40.0F - hurtPercent * 2.0F,
            40.0F - hurtPercent * 2.0F,
            40.0F,
            8.0F,
            8.0,
            8.0,
            64.0,
            64.0
        );
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        FontRenderers.modules
            .drawString(
                context.method_51448(),
                ModuleManager.media.isEnabled()
                    ? "Protected "
                    : (
                        ModuleManager.nameProtect.isEnabled() && this.target == mc.field_1724
                            ? NameProtect.getCustomName()
                            : this.target.method_5477().getString()
                    ),
                this.getPosX() + 50.0F,
                this.getPosY() + 7.0F,
                -1
            );
        FontRenderers.modules
            .drawCenteredString(
                context.method_51448(),
                this.hpMode.getValue() == TargetHud.HPmodeEn.HP
                    ? String.valueOf(Math.round(10.0 * this.getHealth()) / 10.0)
                    : Math.round(10.0 * this.getHealth()) / 10.0 / 20.0 * 100.0 + "%",
                this.getPosX() + 81.0F,
                this.getPosY() + 34.0F,
                -1
            );
        if (this.target instanceof class_1657 pe) {
            this.celestialArmor(context, pe, this.getPosX(), this.getPosY());
            this.celestialHands(context, pe, this.getPosX(), this.getPosY());
        }

        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
    }

    private void renderNurik(class_332 context, float health, float animationFactor) {
        float hurtPercent = Render2DEngine.interpolateFloat(
                MathUtility.clamp(this.target.field_6235 == 0 ? 0 : this.target.field_6235 + 1, 0, 10), this.target.field_6235, Render3DEngine.getTickDelta()
            )
            / 8.0F;
        healthAnimation.setValue(health);
        health = (float)healthAnimation.getAnimationD();
        if (this.animation.getAnimationd() != 1.0 && !HudEditor.hudStyle.is(HudEditor.HudStyle.Blurry)) {
            Render2DEngine.drawGradientBlurredShadow1(
                context.method_51448(),
                this.getPosX() + 4.0F,
                this.getPosY() + 4.0F,
                131.0F,
                40.0F,
                14,
                HudEditor.getColor(270),
                HudEditor.getColor(0),
                HudEditor.getColor(180),
                HudEditor.getColor(90)
            );
            Render2DEngine.renderRoundedGradientRect(
                context.method_51448(),
                HudEditor.getColor(270),
                HudEditor.getColor(0),
                HudEditor.getColor(180),
                HudEditor.getColor(90),
                this.getPosX(),
                this.getPosY(),
                137.0F,
                47.5F,
                9.0F
            );
            Render2DEngine.drawRound(
                context.method_51448(), this.getPosX() + 0.5F, this.getPosY() + 0.5F, 136.0F, 46.0F, 9.0F, Render2DEngine.injectAlpha(Color.BLACK, 220)
            );
        } else {
            NwHud.drawHudBase2(
                context.method_51448(),
                this.getPosX(),
                this.getPosY(),
                137.0F,
                47.5F,
                9.0F,
                HudEditor.blurStrength.getValue(),
                HudEditor.blurOpacity.getValue(),
                animationFactor
            );
        }

        this.setBounds(this.getPosX(), this.getPosY(), 137.0F, 47.5F);
        if (this.target instanceof class_1657) {
            RenderSystem.setShaderTexture(0, ((class_742)this.target).method_52814().comp_1626());
        } else {
            RenderSystem.setShaderTexture(0, mc.method_1561().method_3953(this.target).method_3931(this.target));
        }

        context.method_51448().method_22903();
        context.method_51448().method_46416(this.getPosX() + 3.5F + 20.0F, this.getPosY() + 3.5F + 20.0F, 0.0F);
        context.method_51448().method_22905(1.0F - hurtPercent / 15.0F, 1.0F - hurtPercent / 15.0F, 1.0F);
        context.method_51448().method_46416(-(this.getPosX() + 3.5F + 20.0F), -(this.getPosY() + 3.5F + 20.0F), 0.0F);
        RenderSystem.enableBlend();
        RenderSystem.colorMask(false, false, false, true);
        RenderSystem.clearColor(0.0F, 0.0F, 0.0F, 0.0F);
        RenderSystem.clear(16384, false);
        RenderSystem.colorMask(true, true, true, true);
        RenderSystem.setShader(class_757::method_34540);
        Render2DEngine.renderRoundedQuadInternal(
            context.method_51448().method_23760().method_23761(),
            animationFactor,
            animationFactor,
            animationFactor,
            animationFactor,
            this.getPosX() + 3.5F,
            this.getPosY() + 3.5F,
            this.getPosX() + 3.5F + 40.0F,
            this.getPosY() + 3.5F + 40.0F,
            7.0,
            10.0
        );
        RenderSystem.blendFunc(772, 773);
        RenderSystem.setShaderColor(
            animationFactor,
            animationFactor - hurtPercent / 2.0F,
            animationFactor - hurtPercent / 2.0F,
            (float)MathUtility.clamp(this.animation.getAnimationd(), 0.0, 1.0)
        );
        Render2DEngine.renderTexture(context.method_51448(), this.getPosX() + 3.5F, this.getPosY() + 3.5F, 40.0, 40.0, 8.0F, 8.0F, 8.0, 8.0, 64.0, 64.0);
        Render2DEngine.renderTexture(context.method_51448(), this.getPosX() + 3.5F, this.getPosY() + 3.5F, 40.0, 40.0, 40.0F, 8.0F, 8.0, 8.0, 64.0, 64.0);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.defaultBlendFunc();
        context.method_51448().method_22909();
        if (HudEditor.hudStyle.is(HudEditor.HudStyle.Blurry)) {
            Render2DEngine.drawRect(
                context.method_51448(), this.getPosX() + 48.0F, this.getPosY() + 32.0F, 85.0F, 11.0F, 4.0F, (float)(0.15F * this.animation.getAnimationd())
            );
            Render2DEngine.drawRect(
                context.method_51448(),
                this.getPosX() + 48.0F,
                this.getPosY() + 32.0F,
                MathUtility.clamp(85.0F * (health / this.target.method_6063()), 8.0F, 85.0F),
                11.0F,
                4.0F,
                (float)this.animation.getAnimationd()
            );
        } else {
            Render2DEngine.drawGradientRound(
                context.method_51448(),
                this.getPosX() + 48.0F,
                this.getPosY() + 32.0F,
                85.0F,
                11.0F,
                4.0F,
                HudEditor.getColor(0).darker().darker(),
                HudEditor.getColor(0).darker().darker().darker().darker(),
                HudEditor.getColor(0).darker().darker().darker().darker(),
                HudEditor.getColor(0).darker().darker().darker().darker()
            );
            Render2DEngine.renderRoundedGradientRect(
                context.method_51448(),
                HudEditor.getColor(270),
                HudEditor.getColor(0),
                HudEditor.getColor(0),
                HudEditor.getColor(270),
                this.getPosX() + 48.0F,
                this.getPosY() + 32.0F,
                (int)MathUtility.clamp(85.0F * (health / this.target.method_6063()), 8.0F, 85.0F),
                11.0F,
                4.0F
            );
        }

        FontRenderers.sf_bold
            .drawCenteredString(
                context.method_51448(),
                this.hpMode.getValue() == TargetHud.HPmodeEn.HP
                    ? String.valueOf(Math.round(10.0 * this.getHealth()) / 10.0)
                    : Math.round(10.0 * this.getHealth()) / 10.0 / 20.0 * 100.0 + "%",
                this.getPosX() + 92.0F,
                this.getPosY() + 35.0F,
                Render2DEngine.applyOpacity(-1, animationFactor)
            );
        FontRenderers.sf_bold
            .drawString(
                context.method_51448(),
                ModuleManager.media.isEnabled()
                    ? "Protected "
                    : (
                        ModuleManager.nameProtect.isEnabled() && this.target == mc.field_1724
                            ? NameProtect.getCustomName()
                            : this.target.method_5477().getString()
                    ),
                this.getPosX() + 48.0F,
                this.getPosY() + 7.0F,
                Render2DEngine.applyOpacity(-1, animationFactor)
            );
        if (this.target instanceof class_1657) {
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, (float)MathUtility.clamp(this.animation.getAnimationd(), 0.0, 1.0));
            List<class_1799> armor = ((class_1657)this.target).method_31548().field_7548;
            class_1799[] items = new class_1799[]{
                this.target.method_6047(),
                (class_1799)armor.get(3),
                (class_1799)armor.get(2),
                (class_1799)armor.get(1),
                (class_1799)armor.get(0),
                this.target.method_6079()
            };
            float xItemOffset = this.getPosX() + 48.0F;

            for (class_1799 itemStack : items) {
                context.method_51448().method_22903();
                context.method_51448().method_46416(xItemOffset, this.getPosY() + 15.0F, 0.0F);
                context.method_51448().method_22905(0.75F, 0.75F, 0.75F);
                context.method_51427(itemStack, 0, 0);
                context.method_51431(mc.field_1772, itemStack, 0, 0);
                context.method_51448().method_22909();
                xItemOffset += 12.0F;
            }

            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        }
    }

    private void renderMiniNurik(class_332 context, float health, float animationFactor) {
        float hurtPercent = Render2DEngine.interpolateFloat(
                MathUtility.clamp(this.target.field_6235 == 0 ? 0 : this.target.field_6235 + 1, 0, 10), this.target.field_6235, Render3DEngine.getTickDelta()
            )
            / 8.0F;
        healthAnimation.setValue(health);
        health = (float)healthAnimation.getAnimationD();
        if (this.animation.getAnimationd() != 1.0 && !HudEditor.hudStyle.is(HudEditor.HudStyle.Blurry)) {
            Render2DEngine.drawGradientBlurredShadow1(
                context.method_51448(),
                this.getPosX() + 2.0F,
                this.getPosY() + 2.0F,
                91.0F,
                31.0F,
                12,
                HudEditor.getColor(270),
                HudEditor.getColor(0),
                HudEditor.getColor(180),
                HudEditor.getColor(90)
            );
            Render2DEngine.renderRoundedGradientRect(
                context.method_51448(),
                HudEditor.getColor(270),
                HudEditor.getColor(0),
                HudEditor.getColor(180),
                HudEditor.getColor(90),
                this.getPosX(),
                this.getPosY(),
                95.0F,
                35.0F,
                7.0F
            );
            Render2DEngine.drawRound(
                context.method_51448(), this.getPosX() + 0.5F, this.getPosY() + 0.5F, 94.0F, 34.0F, 7.0F, Render2DEngine.injectAlpha(Color.BLACK, 220)
            );
        } else {
            NwHud.drawHudBase2(
                context.method_51448(),
                this.getPosX(),
                this.getPosY(),
                95.0F,
                35.5F,
                8.0F,
                HudEditor.blurStrength.getValue(),
                HudEditor.blurOpacity.getValue(),
                animationFactor
            );
        }

        this.setBounds(this.getPosX(), this.getPosY(), 95.0F, 35.5F);
        if (this.target instanceof class_1657) {
            RenderSystem.setShaderTexture(0, ((class_742)this.target).method_52814().comp_1626());
        } else {
            RenderSystem.setShaderTexture(0, mc.method_1561().method_3953(this.target).method_3931(this.target));
        }

        context.method_51448().method_22903();
        context.method_51448().method_22904(this.getPosX() + 2.5 + 15.0, this.getPosY() + 2.5 + 15.0, 0.0);
        context.method_51448().method_22905(1.0F - hurtPercent / 20.0F, 1.0F - hurtPercent / 20.0F, 1.0F);
        context.method_51448().method_22904(-(this.getPosX() + 2.5 + 15.0), -(this.getPosY() + 2.5 + 15.0), 0.0);
        RenderSystem.enableBlend();
        RenderSystem.colorMask(false, false, false, true);
        RenderSystem.clearColor(0.0F, 0.0F, 0.0F, 0.0F);
        RenderSystem.clear(16384, false);
        RenderSystem.colorMask(true, true, true, true);
        RenderSystem.setShader(class_757::method_34540);
        Render2DEngine.renderRoundedQuadInternal(
            context.method_51448().method_23760().method_23761(),
            animationFactor,
            animationFactor,
            animationFactor,
            animationFactor,
            this.getPosX() + 2.5,
            this.getPosY() + 2.5,
            this.getPosX() + 2.5 + 30.0,
            this.getPosY() + 2.5 + 30.0,
            5.0,
            10.0
        );
        RenderSystem.blendFunc(772, 773);
        RenderSystem.setShaderColor(animationFactor, animationFactor - hurtPercent / 2.0F, animationFactor - hurtPercent / 2.0F, animationFactor);
        Render2DEngine.renderTexture(context.method_51448(), this.getPosX() + 2.5, this.getPosY() + 2.5, 30.0, 30.0, 8.0F, 8.0F, 8.0, 8.0, 64.0, 64.0);
        Render2DEngine.renderTexture(context.method_51448(), this.getPosX() + 2.5, this.getPosY() + 2.5, 30.0, 30.0, 40.0F, 8.0F, 8.0, 8.0, 64.0, 64.0);
        RenderSystem.defaultBlendFunc();
        context.method_51448().method_22909();
        if (HudEditor.hudStyle.is(HudEditor.HudStyle.Blurry)) {
            Render2DEngine.drawRect(
                context.method_51448(), this.getPosX() + 38.0F, this.getPosY() + 25.0F, 52.0F, 7.0F, 2.0F, (float)(0.15F * this.animation.getAnimationd())
            );
            Render2DEngine.drawRect(
                context.method_51448(),
                this.getPosX() + 38.0F,
                this.getPosY() + 25.0F,
                MathUtility.clamp(52.0F * (health / this.target.method_6063()), 8.0F, 52.0F),
                7.0F,
                2.0F,
                (float)this.animation.getAnimationd()
            );
        } else {
            Render2DEngine.drawGradientRound(
                context.method_51448(),
                this.getPosX() + 38.0F,
                this.getPosY() + 25.0F,
                52.0F,
                7.0F,
                2.0F,
                HudEditor.getColor(0).darker().darker(),
                HudEditor.getColor(0).darker().darker().darker().darker(),
                HudEditor.getColor(0).darker().darker().darker().darker(),
                HudEditor.getColor(0).darker().darker().darker().darker()
            );
            Render2DEngine.renderRoundedGradientRect(
                context.method_51448(),
                HudEditor.getColor(270),
                HudEditor.getColor(0),
                HudEditor.getColor(0),
                HudEditor.getColor(270),
                this.getPosX() + 38.0F,
                this.getPosY() + 25.0F,
                (int)MathUtility.clamp(52.0F * (health / this.target.method_6063()), 8.0F, 52.0F),
                7.0F,
                2.0F
            );
        }

        FontRenderers.sf_bold_mini
            .drawCenteredString(
                context.method_51448(),
                this.hpMode.getValue() == TargetHud.HPmodeEn.HP
                    ? String.valueOf(Math.round(10.0 * this.getHealth()) / 10.0)
                    : Math.round(10.0 * this.getHealth()) / 10.0 / 20.0 * 100.0 + "%",
                this.getPosX() + 65.0F,
                this.getPosY() + 27.0F,
                Render2DEngine.applyOpacity(-1, animationFactor)
            );
        FontRenderers.sf_bold_mini
            .drawString(
                context.method_51448(),
                ModuleManager.media.isEnabled()
                    ? "Protected "
                    : (
                        ModuleManager.nameProtect.isEnabled() && this.target == mc.field_1724
                            ? NameProtect.getCustomName()
                            : this.target.method_5477().getString()
                    ),
                this.getPosX() + 38.0F,
                this.getPosY() + 5.0F,
                Render2DEngine.applyOpacity(-1, animationFactor)
            );
        if (this.target instanceof class_1657) {
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, (float)MathUtility.clamp(this.animation.getAnimationd(), 0.0, 1.0));
            List<class_1799> armor = ((class_1657)this.target).method_31548().field_7548;
            class_1799[] items = new class_1799[]{
                this.target.method_6047(),
                (class_1799)armor.get(3),
                (class_1799)armor.get(2),
                (class_1799)armor.get(1),
                (class_1799)armor.get(0),
                this.target.method_6079()
            };
            float xItemOffset = this.getPosX() + 38.0F;

            for (class_1799 itemStack : items) {
                context.method_51448().method_22903();
                context.method_51448().method_46416(xItemOffset, this.getPosY() + 13.0F, 0.0F);
                context.method_51448().method_22905(0.5F, 0.5F, 0.5F);
                context.method_51427(itemStack, 0, 0);
                context.method_51431(mc.field_1772, itemStack, 0, 0);
                context.method_51448().method_22909();
                xItemOffset += 9.0F;
            }

            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        }
    }

    private void renderNevaWare(class_332 context, float health, float animationFactor) {
        float hurtPercent;
        hurtPercent = this.target.field_6235 / 6.0F;
        Render2DEngine.drawRound(context.method_51448(), this.getPosX(), this.getPosY(), 70.0F, 50.0F, 6.0F, new Color(0, 0, 0, 139));
        Render2DEngine.drawRound(context.method_51448(), this.getPosX() + 50.0F, this.getPosY(), 100.0F, 50.0F, 6.0F, new Color(0, 0, 0, 255));
        this.setBounds(this.getPosX(), this.getPosY(), 150.0F, 50.0F);
        label125:
        if (!this.imageMode.is(TargetHud.ImageModeEn.None)) {
            if (this.imageMode.is(TargetHud.ImageModeEn.Anime)) {
                RenderSystem.setShaderTexture(0, TextureStorage.thudPic);
            } else {
                if (custom == null) {
                    break label125;
                }

                RenderSystem.setShaderTexture(0, custom);
            }

            context.method_51448().method_22903();
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            Render2DEngine.drawRound(context.method_51448(), this.getPosX() + 50.0F, this.getPosY(), 100.0F, 50.0F, 12.0F, new Color(0, 0, 0, 255));
            RenderSystem.disableBlend();
            RenderSystem.setShaderColor(0.3F, 0.3F, 0.3F, 1.0F);
            Render2DEngine.renderTexture(context.method_51448(), this.getPosX() + 50.0F, this.getPosY(), 95.0, 50.0, 0.0F, 0.0F, 100.0, 50.0, 100.0, 50.0);
            context.method_51448().method_22909();
        }

        for (Particles p : this.particles) {
            if (p.opacity > 4.0) {
                p.render2D(context.method_51448());
            }
        }

        if (this.timer.passedMs(16L)) {
            this.ticks += 0.1F;

            for (Particles px : this.particles) {
                px.updatePosition();
                if (px.opacity < 1.0) {
                    this.particles.remove(px);
                }
            }

            this.timer.reset();
        }

        ArrayList<Particles> removeList = new ArrayList();

        for (Particles pxx : this.particles) {
            if (pxx.opacity <= 1.0) {
                removeList.add(pxx);
            }
        }

        for (Particles pxxx : removeList) {
            this.particles.remove(pxxx);
        }

        if (this.target.field_6235 == 9 && !this.sentParticles) {
            for (int i = 0; i <= 6; i++) {
                Particles pxxx = new Particles();
                Color c = Particles.mixColors(
                    this.color.getValue().getColorObject(),
                    this.color2.getValue().getColorObject(),
                    (Math.sin(this.ticks + this.getPosX() * 0.4F + i) + 1.0) * 0.5
                );
                pxxx.init(this.getPosX(), this.getPosY(), MathUtility.random(-3.0F, 3.0F), MathUtility.random(-3.0F, 3.0F), 20.0, c);
                this.particles.add(pxxx);
            }

            this.sentParticles = true;
        }

        if (this.target.field_6235 == 8) {
            this.sentParticles = false;
        }

        headAnimation.setValue(hurtPercent);
        if (this.target instanceof class_1657) {
            RenderSystem.setShaderTexture(0, ((class_742)this.target).method_52814().comp_1626());
        } else {
            RenderSystem.setShaderTexture(0, mc.method_1561().method_3953(this.target).method_3931(this.target));
        }

        context.method_51448().method_22903();
        context.method_51448().method_22904(this.getPosX() + 2.5 + 15.0, this.getPosY() + 2.5 + 15.0, 0.0);
        context.method_51448().method_22905(1.0F - hurtPercent / 20.0F, 1.0F - hurtPercent / 20.0F, 1.0F);
        context.method_51448().method_22904(-(this.getPosX() + 2.5 + 15.0), -(this.getPosY() + 2.5 + 15.0), 0.0);
        RenderSystem.enableBlend();
        RenderSystem.colorMask(false, false, false, true);
        RenderSystem.clearColor(0.0F, 0.0F, 0.0F, 0.0F);
        RenderSystem.clear(16384, false);
        RenderSystem.colorMask(true, true, true, true);
        RenderSystem.setShader(class_757::method_34540);
        Render2DEngine.renderRoundedQuadInternal(
            context.method_51448().method_23760().method_23761(),
            animationFactor,
            animationFactor,
            animationFactor,
            animationFactor,
            this.getPosX() + 2.5,
            this.getPosY() + 2.5,
            this.getPosX() + 2.5 + 45.0,
            this.getPosY() + 2.5 + 45.0,
            5.0,
            10.0
        );
        RenderSystem.blendFunc(772, 773);
        RenderSystem.setShaderColor(animationFactor, animationFactor - hurtPercent / 2.0F, animationFactor - hurtPercent / 2.0F, animationFactor);
        Render2DEngine.renderTexture(context.method_51448(), this.getPosX() + 2.5, this.getPosY() + 2.5, 45.0, 45.0, 8.0F, 8.0F, 8.0, 8.0, 64.0, 64.0);
        Render2DEngine.renderTexture(context.method_51448(), this.getPosX() + 2.5, this.getPosY() + 2.5, 45.0, 45.0, 40.0F, 8.0F, 8.0, 8.0, 64.0, 64.0);
        RenderSystem.defaultBlendFunc();
        context.method_51448().method_22909();
        healthAnimation.setValue(health);
        health = (float)healthAnimation.getAnimationD();
        Render2DEngine.drawBlurredShadow(
            context.method_51448(), this.getPosX() + 55.0F, this.getPosY() + 22.0F, 90.0F, 8.0F, this.blurRadius.getValue(), HudEditor.getColor(0)
        );
        Render2DEngine.drawGradientRound(
            context.method_51448(),
            this.getPosX() + 55.0F,
            this.getPosY() + 35.0F - 14.0F,
            90.0F,
            10.0F,
            2.0F,
            HudEditor.getColor(0).darker().darker(),
            HudEditor.getColor(0).darker().darker().darker().darker(),
            HudEditor.getColor(0).darker().darker().darker().darker(),
            HudEditor.getColor(0).darker().darker().darker().darker()
        );
        Render2DEngine.renderRoundedGradientRect(
            context.method_51448(),
            HudEditor.getColor(270),
            HudEditor.getColor(0),
            HudEditor.getColor(0),
            HudEditor.getColor(270),
            this.getPosX() + 55.0F,
            this.getPosY() + 35.0F - 14.0F,
            (int)MathUtility.clamp(90.0F * (health / this.target.method_6063()), 3.0F, 90.0F),
            10.0F,
            2.0F
        );
        FontRenderers.sf_bold
            .drawCenteredString(
                context.method_51448(),
                this.hpMode.getValue() == TargetHud.HPmodeEn.HP
                    ? String.valueOf(Math.round(10.0 * this.getHealth()) / 10.0)
                    : (int)(Math.round(10.0 * health) / 10.0 / 20.0 * 100.0) + "%",
                this.getPosX() + 102.0F,
                this.getPosY() + 24.0F,
                Render2DEngine.applyOpacity(-1, animationFactor)
            );
        FontRenderers.sf_bold
            .drawString(
                context.method_51448(),
                ModuleManager.media.isEnabled()
                    ? "Protected "
                    : (
                        ModuleManager.nameProtect.isEnabled() && this.target == mc.field_1724
                            ? NameProtect.getCustomName()
                            : this.target.method_5477().getString()
                    ),
                this.getPosX() + 55.0F,
                this.getPosY() + 5.0F,
                Render2DEngine.applyOpacity(-1, animationFactor)
            );
        if (this.target instanceof class_1657) {
            List<class_1799> armor = ((class_1657)this.target).method_31548().field_7548;
            class_1799[] items = new class_1799[]{
                this.target.method_6047(),
                (class_1799)armor.get(3),
                (class_1799)armor.get(2),
                (class_1799)armor.get(1),
                (class_1799)armor.get(0),
                this.target.method_6079()
            };
            float xItemOffset = this.getPosX() + 60.0F;

            for (class_1799 itemStack : items) {
                if (!itemStack.method_7960()) {
                    context.method_51448().method_22903();
                    context.method_51448().method_46416(xItemOffset, this.getPosY() + 35.0F, 0.0F);
                    context.method_51448().method_22905(0.75F, 0.75F, 0.75F);
                    context.method_51427(itemStack, 0, 0);
                    context.method_51431(mc.field_1772, itemStack, 0, 0);
                    context.method_51448().method_22909();
                    xItemOffset += 14.0F;
                }
            }

            this.drawPotionEffect(context.method_51448(), (class_1657)this.target);
        }
    }

    private void celestialArmor(class_332 context, class_1657 target, float posX, float posY) {
        for (int i = 0; i < 4; i++) {
            if (!((class_1799)target.method_31548().field_7548.get(3 - i)).method_7960()) {
                context.method_51448().method_22903();
                context.method_51448().method_46416(posX + (i > 1 ? 138 : 118), posY + (i % 2 == 0 ? 5 : 26), 0.0F);
                context.method_51427((class_1799)target.method_31548().field_7548.get(3 - i), 0, 0);
                context.method_51431(mc.field_1772, (class_1799)target.method_31548().field_7548.get(3 - i), 0, 0);
                context.method_51448().method_22909();
            }
        }
    }

    private void celestialHands(class_332 context, class_1657 target, float posX, float posY) {
        for (int i = 0; i < 2; i++) {
            if (!(i == 0 ? target.method_6047() : target.method_6079()).method_7960()) {
                context.method_51448().method_22903();
                context.method_51448().method_46416(posX + (i == 0 ? 50 : 77), posY + 14.0F, 0.0F);
                context.method_51448().method_22905(0.75F, 0.75F, 1.0F);
                context.method_51427(i == 0 ? target.method_6047() : target.method_6079(), 0, 0);
                context.method_51448().method_22909();
                FontRenderers.settings
                    .drawString(
                        context.method_51448(),
                        "x" + (i == 0 ? target.method_6047() : target.method_6079()).method_7947(),
                        posX + (i == 0 ? 50 : 77) + 12.0F,
                        posY + 21.0F,
                        -1
                    );
            }
        }
    }

    private void drawPotionEffect(class_4587 ms, class_1657 entity) {
        StringBuilder finalString = new StringBuilder();

        for (class_1293 potionEffect : entity.method_6026()) {
            class_1291 potion = (class_1291)potionEffect.method_5579().comp_349();
            if (potion == class_1294.field_5924.comp_349()
                || potion == class_1294.field_5904.comp_349()
                || potion == class_1294.field_5910.comp_349()
                || potion == class_1294.field_5911.comp_349()) {
                boolean potRanOut = potionEffect.method_5584() != 0.0;
                if (entity.method_6059(potionEffect.method_5579()) && potRanOut) {
                    finalString.append(getPotionName(potion))
                        .append(potionEffect.method_5578() < 1 ? "" : potionEffect.method_5578() + 1)
                        .append(" ")
                        .append(this.getDurationString(potionEffect))
                        .append(" ");
                }
            }
        }

        FontRenderers.settings.drawString(ms, finalString.toString(), this.getPosX() + 55.0F, this.getPosY() + 15.0F, new Color(9276813).getRGB());
    }

    public float getHealth() {
        if (!(
            this.target instanceof class_1657 ent
                && (
                    mc.method_1562() != null && mc.method_1562().method_45734() != null && mc.method_1562().method_45734().field_3761.contains("funtime")
                        || this.funTimeHP.getValue()
                )
        )) {
            return this.absorp.getValue() ? this.target.method_6032() + this.target.method_6067() : this.target.method_6032();
        } else {
            class_266 scoreBoard = null;
            String resolvedHp = "";
            if (ent.method_7327().method_1189(class_8646.field_45158) != null) {
                scoreBoard = ent.method_7327().method_1189(class_8646.field_45158);
                if (scoreBoard != null) {
                    class_9013 readableScoreboardScore = ent.method_7327().method_55430(ent, scoreBoard);
                    class_5250 text2 = class_9013.method_55398(readableScoreboardScore, scoreBoard.method_55380(class_9025.field_47566));
                    resolvedHp = text2.getString();
                }
            }

            float numValue = 0.0F;

            try {
                numValue = Float.parseFloat(resolvedHp);
            } catch (NumberFormatException var6) {
            }

            return this.absorp.getValue() ? numValue + this.target.method_6067() : numValue;
        }
    }

    public static void sizeAnimation(class_4587 matrixStack, double width, double height, double animation) {
        matrixStack.method_22904(width, height, 0.0);
        matrixStack.method_22905((float)animation, (float)animation, 1.0F);
        matrixStack.method_22904(-width, -height, 0.0);
    }

    public static String getPotionName(class_1291 p) {
        if (p == class_1294.field_5924.comp_349()) {
            return "Reg";
        } else if (p == class_1294.field_5910.comp_349()) {
            return "Str";
        } else if (p == class_1294.field_5904.comp_349()) {
            return "Spd";
        } else if (p == class_1294.field_5917.comp_349()) {
            return "H";
        } else if (p == class_1294.field_5911.comp_349()) {
            return "W";
        } else {
            return p == class_1294.field_5907.comp_349() ? "Res" : "pon";
        }
    }

    public static enum HPmodeEn {
        HP,
        Percentage;
    }

    public static enum ImageModeEn {
        None,
        Anime,
        Custom;
    }

    public static enum ModeEn {
        NevaWare,
        NurikZapen,
        CelkaPasta;
    }
}
