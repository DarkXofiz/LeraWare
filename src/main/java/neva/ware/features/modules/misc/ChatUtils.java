package neva.ware.features.modules.misc;

import java.lang.Character.UnicodeBlock;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_124;
import net.minecraft.class_1802;
import net.minecraft.class_2561;
import net.minecraft.class_2703;
import net.minecraft.class_2797;
import net.minecraft.class_3417;
import net.minecraft.class_3419;
import net.minecraft.class_640;
import net.minecraft.class_7439;
import net.minecraft.class_7828;
import net.minecraft.class_2703.class_2705;
import net.minecraft.class_2703.class_5893;
import neva.ware.core.Managers;
import neva.ware.events.impl.PacketEvent;
import neva.ware.events.impl.TotemPopEvent;
import neva.ware.features.modules.Module;
import neva.ware.features.modules.client.ClientSettings;
import neva.ware.gui.notification.Notification;
import neva.ware.injection.accesors.IGameMessageS2CPacket;
import neva.ware.setting.Setting;
import neva.ware.utility.Timer;
import org.jetbrains.annotations.NotNull;

public class ChatUtils extends Module {
    private final Setting<ChatUtils.Welcomer> welcomer = new Setting<>("Welcomer", ChatUtils.Welcomer.Off);
    private final Setting<ChatUtils.Prefix> prefix = new Setting<>("Prefix", ChatUtils.Prefix.None);
    private final Setting<Boolean> totems = new Setting<>("Totems", false);
    private final Setting<Boolean> time = new Setting<>("Time", false);
    private final Setting<Boolean> mention = new Setting<>("Mention", false);
    private final Setting<ChatUtils.PMSound> pmSound = new Setting<>("PMSound", ChatUtils.PMSound.Default);
    private final Setting<Boolean> zov = new Setting<>("ZOV", false);
    private final Setting<Boolean> wavy = new Setting<>("wAvY", false);
    private final Setting<Boolean> translit = new Setting<>("Translit", false);
    private final Setting<Boolean> antiCoordLeak = new Setting<>("AntiCoordLeak", false);
    private final Timer timer = new Timer();
    private final Timer antiSpam = new Timer();
    private final Timer messageTimer = new Timer();
    private final LinkedHashMap<UUID, String> nameMap = new LinkedHashMap();
    private String skip;
    Map<String, String> ruToEng = Map.ofEntries(
        Map.entry("а", "a"),
        Map.entry("б", "6"),
        Map.entry("в", "B"),
        Map.entry("г", "r"),
        Map.entry("д", "d"),
        Map.entry("е", "e"),
        Map.entry("ё", "e"),
        Map.entry("ж", ">I<"),
        Map.entry("з", "3"),
        Map.entry("и", "u"),
        Map.entry("й", "u"),
        Map.entry("к", "k"),
        Map.entry("л", "JI"),
        Map.entry("м", "m"),
        Map.entry("н", "H"),
        Map.entry("о", "o"),
        Map.entry("п", "n"),
        Map.entry("р", "p"),
        Map.entry("с", "c"),
        Map.entry("т", "T"),
        Map.entry("у", "y"),
        Map.entry("ф", "f"),
        Map.entry("х", "x"),
        Map.entry("ц", "lI"),
        Map.entry("ч", "4"),
        Map.entry("ш", "w"),
        Map.entry("щ", "w"),
        Map.entry("ь", "b"),
        Map.entry("ы", "bI"),
        Map.entry("ъ", "b"),
        Map.entry("э", "-)"),
        Map.entry("ю", "I-O"),
        Map.entry("я", "9I")
    );
    private final String[] bb = new String[]{"See you later, ", "Catch ya later, ", "See you next time, ", "Farewell, ", "Bye, ", "Good bye, ", "Later, "};
    private final String[] qq = new String[]{
        "Good to see you, ", "Greetings, ", "Hello, ", "Howdy, ", "Hey, ", "Good evening, ", "Welcome to SERVERIP1D5A9E, "
    };
    private final String[] popMessages = new String[]{
        " EZZZ POP <pop> TIMES PIECE OF SHIT GET GOOD",
        " ez pop <pop> times fuckin unbrain",
        " pop <pop> times get good kiddo ",
        " EZZZZZZZ pop <pop> times GO LEARN PVP PUSSY",
        " piece of shit popped <pop> times so ez",
        " easiest pop <pop> times in my life",
        " HAHAHAHA BRO POPPED <pop> TIMES SO EZ LMAO",
        " POP <pop> TIMES OMG MAN UR SO BAD LMAO",
        " my grandma has more skill than you nigga pop <pop> times",
        " trash pop <pop> times retard ",
        " ezz no skill dog pop <pop> times",
        " lame dude tryes to pvp with me but dyes) hahah pop <pop> times",
        " get better tbh bruh pop <pop> times",
        " pop <pop> times ur eyes don't work right? ",
        " cringelord popped <pop> times so ez "
    };

    public ChatUtils() {
        super("ChatUtils", Module.Category.MISC);
    }

    @Override
    public void onDisable() {
        this.nameMap.clear();
    }

    @Override
    public void onUpdate() {
        if (this.timer.passedMs(15000L)) {
            for (class_640 b : mc.field_1724.field_3944.method_2880()) {
                if (!this.nameMap.containsKey(b.method_2966().getId())) {
                    this.nameMap.put(b.method_2966().getId(), b.method_2966().getName());
                }
            }

            this.timer.reset();
        }
    }

    @EventHandler
    public void onPacketReceive(PacketEvent.Receive event) {
        if (this.welcomer.getValue() != ChatUtils.Welcomer.Off && this.antiSpam.passedMs(3000L)) {
            if (event.getPacket() instanceof class_2703 pck) {
                int n2 = (int)Math.floor(Math.random() * this.qq.length);
                String string1;
                if (mc.field_1724.field_3944.method_45734() != null) {
                    string1 = this.qq[n2].replace("SERVERIP1D5A9E", mc.field_1724.field_3944.method_45734().field_3761);
                } else {
                    string1 = "server";
                }

                if (pck.method_46327().contains(class_5893.field_29136)) {
                    for (class_2705 ple : pck.method_46330()) {
                        if (this.antiBot(ple.comp_1107().getName())) {
                            return;
                        }

                        if (Objects.equals(ple.comp_1107().getName(), mc.field_1724.method_5477().getString())) {
                            return;
                        }

                        if (this.welcomer.getValue() == ChatUtils.Welcomer.Server) {
                            mc.field_1724.field_3944.method_45729(this.getPrefix() + string1 + ple.comp_1107().getName());
                            this.antiSpam.reset();
                        } else {
                            this.sendMessage(string1 + ple.comp_1107().getName());
                        }

                        this.nameMap.put(ple.comp_1107().getId(), ple.comp_1107().getName());
                    }
                }
            }

            if (event.getPacket() instanceof class_7828 pac) {
                for (UUID uuid2 : pac.comp_1105) {
                    if (!this.nameMap.containsKey(uuid2)) {
                        return;
                    }

                    if (this.antiBot((String)this.nameMap.get(uuid2))) {
                        return;
                    }

                    if (Objects.equals(this.nameMap.get(uuid2), mc.field_1724.method_5477().getString())) {
                        return;
                    }

                    int n = (int)Math.floor(Math.random() * this.bb.length);
                    if (this.welcomer.getValue() == ChatUtils.Welcomer.Server) {
                        mc.field_1724.field_3944.method_45729(this.getPrefix() + this.bb[n] + (String)this.nameMap.get(uuid2));
                        this.antiSpam.reset();
                    } else {
                        this.sendMessage(this.bb[n] + (String)this.nameMap.get(uuid2));
                    }

                    this.nameMap.remove(uuid2);
                }
            }
        }

        if (event.getPacket() instanceof class_7439 pac) {
            if (this.time.getValue()) {
                IGameMessageS2CPacket pac2 = event.getPacket();
                pac2.setContent(
                    class_2561.method_30163(
                            "["
                                + class_124.field_1080
                                + new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime())
                                + class_124.field_1070
                                + "] "
                        )
                        .method_27661()
                        .method_10852(pac.comp_763)
                );
            }

            if (this.mention.getValue() && pac.comp_763.getString().contains(mc.field_1724.method_5477().getString()) && this.messageTimer.passedMs(1000L)) {
                Managers.NOTIFICATION
                    .publicity("ChatUtils", ClientSettings.isRu() ? "Тебя помянули в чате!" : "You were mentioned in the chat!", 4, Notification.Type.WARNING);
                mc.field_1687.method_8396(mc.field_1724, mc.field_1724.method_24515(), class_3417.field_14709, class_3419.field_15245, 5.0F, 1.0F);
            }

            String content = pac.comp_763.getString().toLowerCase();
            if (!this.pmSound.is(ChatUtils.PMSound.Off)
                && (
                    content.contains("whisper")
                        || content.contains("-> я")
                        || content.contains("-> " + NameProtect.getCustomName())
                        || content.contains("-> me")
                        || content.contains(" says:")
                )) {
                Managers.SOUND.playPmSound(this.pmSound.getValue());
            }
        }
    }

    @EventHandler
    public void onTotem(TotemPopEvent e) {
        if (this.totems.getValue() && this.antiSpam.passedMs(3000L) && e.getEntity() != mc.field_1724) {
            int n = (int)Math.floor(Math.random() * this.popMessages.length);
            String s = this.popMessages[n].replace("<pop>", e.getPops() + "");
            mc.field_1724.field_3944.method_45729(this.getPrefix() + e.getEntity().method_5477().getString() + s);
            this.antiSpam.reset();
        }
    }

    @NotNull
    private String getPrefix() {
        return switch ((ChatUtils.Prefix)this.prefix.getValue()) {
            case Green -> ">";
            case Global -> "!";
            case None -> "";
        };
    }

    public boolean antiBot(@NotNull String s) {
        if (!s.contains("soon_") && !s.contains("_npc") && !s.contains("CIT-")) {
            for (int i = 0; i < s.length(); i++) {
                if (UnicodeBlock.of(s.charAt(i)).equals(UnicodeBlock.CYRILLIC)) {
                    return true;
                }
            }

            return false;
        } else {
            return true;
        }
    }

    @EventHandler
    public void onPacketSend(@NotNull PacketEvent.Send e) {
        if (e.getPacket() instanceof class_2797 pac) {
            if (this.antiCoordLeak.getValue() && pac.comp_945.replaceAll("\\D", "").length() >= 6) {
                this.sendMessage("[ChatUtils] " + (ClientSettings.isRu() ? "В сообщении содержатся координаты!" : "The message contains coordinates!"));
                e.cancel();
            }

            this.messageTimer.reset();
        }

        if (!fullNullCheck()) {
            if (e.getPacket() instanceof class_2797 pac && (this.zov.getValue() || this.wavy.getValue() || this.translit.getValue())) {
                if (Objects.equals(pac.comp_945(), this.skip)) {
                    return;
                }

                if (mc.field_1724.method_6047().method_7909() == class_1802.field_8204 || mc.field_1724.method_6079().method_7909() == class_1802.field_8204) {
                    return;
                }

                if (pac.comp_945().startsWith("/") || pac.comp_945().startsWith(Managers.COMMAND.getPrefix())) {
                    return;
                }

                String message = pac.comp_945();
                if (this.zov.getValue()) {
                    StringBuilder builder = new StringBuilder();

                    for (char Z : message.toCharArray()) {
                        if (1047 == Z || 1079 == Z) {
                            builder.append("Z");
                        } else if (1042 != Z && 1074 != Z) {
                            builder.append(Z);
                        } else {
                            builder.append("V");
                        }
                    }

                    message = builder.toString();
                }

                if (this.wavy.getValue()) {
                    StringBuilder builder = new StringBuilder();
                    boolean up = false;

                    for (char C : message.toCharArray()) {
                        if (up) {
                            builder.append(Character.toUpperCase(C));
                        } else {
                            builder.append(Character.toLowerCase(C));
                        }

                        up = Character.isLetter(C) != up;
                    }

                    message = builder.toString();
                }

                if (this.translit.getValue()) {
                    message = this.transliterate(message);
                }

                this.skip = message;
                mc.field_1724.field_3944.method_45729(this.skip);
                e.cancel();
            }
        }
    }

    public String transliterate(String text) {
        StringBuilder result = new StringBuilder();

        for (char ch : text.toCharArray()) {
            String str = (String)this.ruToEng.get(ch + "");
            result.append(str != null ? str : ch);
        }

        return result.toString();
    }

    public static enum PMSound {
        Off,
        Default,
        Custom;
    }

    private static enum Prefix {
        Green,
        Global,
        None;
    }

    private static enum Welcomer {
        Off,
        Server,
        Client;
    }
}
