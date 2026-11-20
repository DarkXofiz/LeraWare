package neva.ware.features.modules.client;

import java.awt.Color;
import java.util.Arrays;
import java.util.List;
import neva.ware.features.modules.Module;
import neva.ware.gui.hud.HudEditorGui;
import neva.ware.setting.Setting;
import neva.ware.setting.impl.ColorSetting;
import neva.ware.utility.render.Render2DEngine;

public final class HudEditor extends Module {
    public static final Setting<Boolean> sticky = new Setting<>("Sticky", true);
    public static final Setting<HudEditor.HudStyle> hudStyle = new Setting<>("HudStyle", HudEditor.HudStyle.Blurry);
    public static final Setting<HudEditor.ArrowsStyle> arrowsStyle = new Setting<>("ArrowsStyle", HudEditor.ArrowsStyle.New);
    public static final Setting<HudEditor.ColorMode> colorMode = new Setting<>("Theme", HudEditor.ColorMode.Lavender);
    public static final Setting<ColorSetting> customColor1 = NwTheme.createColor1();
    public static final Setting<ColorSetting> customColor2 = NwTheme.createColor2();
    public static final Setting<Integer> colorSpeed = new Setting<>("ColorSpeed", 5, 2, 54);
    public static final Setting<Boolean> glow = new Setting<>("Light", true);
    public static final Setting<ColorSetting> plateColor = new Setting<>("PlateColor", new ColorSetting(new Color(-419430400, true).getRGB()));
    public static final Setting<ColorSetting> textColor = new Setting<>("TextColor", new ColorSetting(new Color(-1, true).getRGB()));
    public static final Setting<ColorSetting> textColor2 = new Setting<>("TextColor2", new ColorSetting(new Color(-1, true).getRGB()));
    public static final Setting<ColorSetting> blurColor = new Setting<>("BlurColor", new ColorSetting(new Color(-13816531, true).getRGB()));
    public static final Setting<Float> hudRound = new Setting<>("HudRound", 3.5F, 0.1F, 7.0F);
    public static final Setting<Float> alpha = new Setting<>("Alpha", 0.7F, 0.0F, 1.0F);
    public static final Setting<Float> blend = new Setting<>("Blend", 15.0F, 1.0F, 15.0F);
    public static final Setting<Float> outline = new Setting<>("Outline", 0.0F, 0.0F, 2.5F);
    public static final Setting<Float> glow1 = new Setting<>("Glow", 0.2F, 0.0F, 1.0F);
    public static final Setting<Float> blurOpacity = new Setting<>("BlurOpacity", 0.1F, 0.0F, 1.0F);
    public static final Setting<Float> blurStrength = new Setting<>("BlurStrength", 5.0F, 0.0F, 50.0F);
    public static final List<HudEditor.Theme> THEMES = Arrays.asList(
        new HudEditor.Theme("Violet Blue", new Color(5518335), new Color(65535)),
        new HudEditor.Theme("Candy", new Color(16515327), new Color(56286)),
        new HudEditor.Theme("Lilac", new Color(13148872), new Color(12139407)),
        new HudEditor.Theme("Raspberry Mint", new Color(471946), new Color(30975)),
        new HudEditor.Theme("Superman", new Color(39415), new Color(15800082)),
        new HudEditor.Theme("Ruby", new Color(14684511), new Color(10162462)),
        new HudEditor.Theme("Cosmos", new Color(12793700), new Color(1910385)),
        new HudEditor.Theme("Amethyst Yellow", new Color(9323501), new Color(15781515)),
        new HudEditor.Theme("Peach", new Color(16763812), new Color(16022628)),
        new HudEditor.Theme("Lavender", new Color(11894492), new Color(8343744)),
        new HudEditor.Theme("Moonlight", new Color(5518335), new Color(10878667)),
        new HudEditor.Theme("Purple Sea", new Color(8609987), new Color(3063697)),
        new HudEditor.Theme("Sunset", new Color(16729344), new Color(16753920)),
        new HudEditor.Theme("Orange Pink", new Color(16737095), new Color(16738740)),
        new HudEditor.Theme("Blueberry", new Color(2697533), new Color(5921402)),
        new HudEditor.Theme("Eucalyptus", new Color(4511656), new Color(25666)),
        new HudEditor.Theme("Mint Chocolate", new Color(10025880), new Color(25600)),
        new HudEditor.Theme("Raspberry Smoothie", new Color(16758465), new Color(16716947)),
        new HudEditor.Theme("Frostbite", new Color(65535), new Color(13107)),
        new HudEditor.Theme("Cold berry", new Color(14011626), new Color(4792186)),
        new HudEditor.Theme("Stormy Sea", new Color(4282576), new Color(1381653)),
        new HudEditor.Theme("Sunset Glow", new Color(16729344), new Color(1969664)),
        new HudEditor.Theme("Crimson Dawn", new Color(11730971), new Color(17762319)),
        new HudEditor.Theme("Raspberry Beach", new Color(9175066), new Color(16734003)),
        new HudEditor.Theme("Moss", new Color(1265246), new Color(7451264)),
        new HudEditor.Theme("Coral", new Color(16744272), new Color(16729344)),
        new HudEditor.Theme("Golden Night", new Color(16765184), new Color(2105376))
    );

    public HudEditor() {
        super("HudEditor", Module.Category.CLIENT);
    }

    public static Color getColor(int count) {
        switch ((HudEditor.ColorMode)colorMode.getValue()) {
            case VioletBlue:
                return getThemeDoubleColor(0, count);
            case Candy:
                return getThemeDoubleColor(1, count);
            case Lilac:
                return getThemeDoubleColor(2, count);
            case RaspberryMint:
                return getThemeDoubleColor(3, count);
            case Superman:
                return getThemeDoubleColor(4, count);
            case Ruby:
                return getThemeDoubleColor(5, count);
            case Cosmos:
                return getThemeDoubleColor(6, count);
            case AmethystYellow:
                return getThemeDoubleColor(7, count);
            case Peach:
                return getThemeDoubleColor(8, count);
            case Lavender:
                return getThemeDoubleColor(9, count);
            case Moonlight:
                return getThemeDoubleColor(10, count);
            case PurpleSea:
                return getThemeDoubleColor(11, count);
            case Sunset:
                return getThemeDoubleColor(12, count);
            case OrangePink:
                return getThemeDoubleColor(13, count);
            case Blueberry:
                return getThemeDoubleColor(14, count);
            case Eucalyptus:
                return getThemeDoubleColor(15, count);
            case MintChocolate:
                return getThemeDoubleColor(16, count);
            case RaspberrySmoothie:
                return getThemeDoubleColor(17, count);
            case Frostbite:
                return getThemeDoubleColor(18, count);
            case Coldberry:
                return getThemeDoubleColor(19, count);
            case StormySea:
                return getThemeDoubleColor(20, count);
            case SunsetGlow:
                return getThemeDoubleColor(21, count);
            case CrimsonDawn:
                return getThemeDoubleColor(22, count);
            case RaspberryBeach:
                return getThemeDoubleColor(23, count);
            case Moss:
                return getThemeDoubleColor(24, count);
            case Coral:
                return getThemeDoubleColor(25, count);
            case GoldenNight:
                return getThemeDoubleColor(26, count);
            default:
                return NwTheme.customColor(count);
        }
    }

    private static Color getThemeDoubleColor(int themeIdx, int count) {
        if (themeIdx < 0 || themeIdx >= THEMES.size()) {
            themeIdx = 0;
        }

        HudEditor.Theme theme = (HudEditor.Theme)THEMES.get(themeIdx);
        return Render2DEngine.TwoColoreffect(theme.color1, theme.color2, colorSpeed.getValue().intValue(), count);
    }

    public static HudEditor.Theme getCurrentTheme() {
        HudEditor.Theme var10000 = NwTheme.currentThemeOrNull();
        if (var10000 != null) {
            return var10000;
        } else {
            int themeOrdinalStart = HudEditor.ColorMode.VioletBlue.ordinal();
            int currentOrdinal = colorMode.getValue().ordinal();
            int idx = currentOrdinal - themeOrdinalStart;
            return idx >= 0 && idx < THEMES.size() ? (HudEditor.Theme)THEMES.get(idx) : (HudEditor.Theme)THEMES.get(0);
        }
    }

    @Override
    public void onEnable() {
        mc.method_1507(HudEditorGui.getHudGui());
        this.disable();
    }

    public static enum ArrowsStyle {
        Default,
        New;
    }

    public static enum ColorMode {
        VioletBlue,
        Candy,
        Lilac,
        RaspberryMint,
        Superman,
        Ruby,
        Cosmos,
        AmethystYellow,
        Peach,
        Lavender,
        Moonlight,
        PurpleSea,
        Sunset,
        OrangePink,
        Blueberry,
        Eucalyptus,
        MintChocolate,
        RaspberrySmoothie,
        Frostbite,
        Coldberry,
        StormySea,
        SunsetGlow,
        CrimsonDawn,
        RaspberryBeach,
        Moss,
        Coral,
        GoldenNight,
        Custom;
    }

    public static enum HudStyle {
        Blurry,
        Glowing;
    }

    public static class Theme {
        public final String name;
        public final Color color1;
        public final Color color2;

        public Theme(String name, Color color1, Color color2) {
            this.name = name;
            this.color1 = color1;
            this.color2 = color2;
        }
    }
}
