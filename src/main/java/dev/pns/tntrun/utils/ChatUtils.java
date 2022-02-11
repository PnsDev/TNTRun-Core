package dev.pns.tntrun.utils;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

public final class ChatUtils {
    /**
     * Formats a message with the color codes
     * @param message The message to format
     * @return The formatted message
     */
    public static String formatMessage(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    /**
     * Sends an action bar to the specified player
     * @param p The player to send the message to
     * @param message The message to send
     */
    public static void sendActionBar(Player p, String message) {

        String craftBukkitPackage = p.getClass().getPackage().getName().replace(".entity", "");
        String mcPackage = "net.minecraft.server." + craftBukkitPackage.substring(craftBukkitPackage.lastIndexOf(".") + 1);
        try {
            Object messageComponent = Class.forName(mcPackage + ".IChatBaseComponent$ChatSerializer").getMethod("a", String.class).invoke(null, "{\"text\":\"" + message.replace("&", "§") + "\"}");
            Object packetPlayOutChat = Class.forName(mcPackage + ".PacketPlayOutChat").getConstructor(Class.forName(mcPackage + ".IChatBaseComponent"), byte.class).newInstance(messageComponent, (byte) 2);

            Object entityPlayer = p.getClass().getMethod("getHandle").invoke(p);
            Object playerConnection = entityPlayer.getClass().getField("playerConnection").get(entityPlayer);
            playerConnection.getClass().getMethod("sendPacket", Class.forName(mcPackage + ".Packet")).invoke(playerConnection, packetPlayOutChat);
        } catch (Exception e) {e.printStackTrace();}

    }

    /**
     * Format a time of milliseconds to a string that looks nice
     * @param millis The time in milliseconds
     * @return A string formatted as a time
     */
    public static String prettyTimeFormat(Long millis) {
        millis = ((long) Math.floor(millis / 1000f)) * 1000L;
        Duration duration = Duration.ofMillis(millis);
        if (duration.toDays() > 0)
            return String.format("%sd %sh %sm %ss", duration.toDays(),
                    duration.toHours() - TimeUnit.DAYS.toHours(duration.toDays()),
                    duration.toMinutes() - TimeUnit.HOURS.toMinutes(duration.toHours()),
                    duration.getSeconds() - TimeUnit.MINUTES.toSeconds(duration.toMinutes()));

        return duration.toString().substring(2).replaceAll("(\\d[DHMS])(?!$)", "$1 ").toLowerCase();
    }

    /**
     * Format a number with commas to make it look nicer
     * @param i The number to format
     * @return A string formatted with commas
     */
    public static String prettyNumberFormat(Long i){
        return String.format("%,d", i);
    }

    /**
     * Creates colored progress bar
     * @param percent The percent of the bar to fill
     * @param length The length of the bar
     * @return A string with the bar
     */
    public static String coloredProgressBarMaker(double percent, int length){
        StringBuilder stringBuilder = new StringBuilder("&a");
        double fillTo = Math.floor(length * percent);
        for (int i = 0; i < 24; i++) {
            if (i == fillTo) stringBuilder.append("&c");
            stringBuilder.append("▋");
        }
        return formatMessage(stringBuilder.toString());
    }

    private final static int CENTER_PX = 154;

    public static String getCenteredMessage(String message){
        if(message == null || message.equals("")) return "";
        message = ChatColor.translateAlternateColorCodes('&', message);

        int messagePxSize = 0;
        boolean previousCode = false;
        boolean isBold = false;

        for(char c : message.toCharArray()){
            if(c == '§'){
                previousCode = true;
                continue;
            }else if(previousCode == true){
                previousCode = false;
                if(c == 'l' || c == 'L'){
                    isBold = true;
                    continue;
                }else isBold = false;
            }else{
                DefaultFontInfo dFI = DefaultFontInfo.getDefaultFontInfo(c);
                messagePxSize += isBold ? dFI.getBoldLength() : dFI.getLength();
                messagePxSize++;
            }
        }

        int halvedMessageSize = messagePxSize / 2;
        int toCompensate = CENTER_PX - halvedMessageSize;
        int spaceLength = DefaultFontInfo.SPACE.getLength() + 1;
        int compensated = 0;
        StringBuilder sb = new StringBuilder();
        while(compensated < toCompensate){
            sb.append(" ");
            compensated += spaceLength;
        }
        return (sb.toString() + message);
    }
}
