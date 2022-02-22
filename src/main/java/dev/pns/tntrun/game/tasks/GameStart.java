package dev.pns.tntrun.game.tasks;

import dev.pns.tntrun.game.Game;
import dev.pns.tntrun.game.constructors.GamePlayer;
import dev.pns.tntrun.game.events.BlockRemoval;
import dev.pns.tntrun.game.events.LocationTracking;
import dev.pns.tntrun.game.events.PVPRunEvents;
import dev.pns.tntrun.game.events.PowerUpSpawn;
import dev.pns.tntrun.misc.Title;
import dev.pns.tntrun.misc.timer.TickTimer;
import dev.pns.tntrun.misc.timer.TimerEvent;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionType;

import java.util.ArrayList;
import java.util.List;

import static dev.pns.tntrun.utils.ChatUtils.coloredProgressBarMaker;
import static dev.pns.tntrun.utils.ChatUtils.sendActionBar;
import static dev.pns.tntrun.utils.ItemUtils.itemFactory;

public class GameStart implements Listener {
    private final Game game;
    private int ticksPassed = 0;

    public GameStart(Game game) {
        game.playSound(Sound.LEVEL_UP, 1, 1);
        this.game = game;
    }

    @EventHandler
    public void onTick(TimerEvent e){
        if (!e.getTimer().equals(TickTimer.TICK_1)) return;
        ticksPassed++;

        Title title = switch (ticksPassed){
            case 96 -> new Title("§e§lTNTRun", "§fLast player standing §awins§f!", 0, 66, 5);
            case 66 -> new Title("§e§lTNTRun", "§fDon't fall into the void.", 0, 70, 100);
            case 1 -> new Title("§e§lTNTRun", "§fThe blocks behind you are §cfalling§f!", 0, 70, 100);
            default -> null;
        };

        if (title != null) game.getPlayers().forEach(gamePlayer -> title.send(gamePlayer.getPlayer()));


        StringBuilder stringBuilder = new StringBuilder("&fGame Start ");
        stringBuilder.append(coloredProgressBarMaker((200f-ticksPassed)/200, 24));
        stringBuilder.append("&f ").append(Math.round(((200f-ticksPassed)/20) * 10.0) / 10.0).append(" Seconds");
        game.getPlayers().forEach(gamePlayer -> sendActionBar(gamePlayer.getPlayer(), stringBuilder.toString()));

        if (ticksPassed != 200) return;

        /*
         * Game start registration.
         * This essentially makes all the game related stuff like powerups
         * and block removal start working.
         */
        game.playSound(Sound.NOTE_PLING, 1, 1);
        game.setGameStart(System.currentTimeMillis());

        List<ItemStack> potions = new ArrayList<>();
        for (PotionEffect pot : game.getSplashPotionEffects()) {
            // Create a new potion
            Potion potion = new Potion(PotionType.getByEffect(pot.getType()));
            potion.setSplash(true);

            // Apply the potion effects to the potion
            ItemStack potionItem = potion.toItemStack(1);
            PotionMeta meta = (PotionMeta) potionItem.getItemMeta();
            meta.setMainEffect(pot.getType());
            meta.addCustomEffect(pot, true);
            potionItem.setItemMeta(meta);

            // Add the potion to the list if not already in it
            boolean found = false;
            for (ItemStack potionStack : potions) {
                if (!potionStack.isSimilar(potionItem)) continue;
                potionStack.setAmount(potionStack.getAmount() + 1);
                found = true;
                break;
            }

            if (!found) potions.add(potionItem);
        }

        for (GamePlayer gamePlayer : game.getPlayers()) {
            gamePlayer.getPlayer().setGameMode(GameMode.ADVENTURE);
            gamePlayer.getPlayer().setAllowFlight(true);
            gamePlayer.getPlayer().setExp(0);
            gamePlayer.getPlayer().setLevel(game.getDoubleJumpAmount());
            gamePlayer.getPlayer().getInventory().setItem(0, itemFactory(Material.FEATHER, "§aDouble Jump", null));
            potions.forEach(potion -> gamePlayer.getPlayer().getInventory().addItem(potion));
        }

        HandlerList.unregisterAll(this);

        PowerUpSpawn powerUpSpawn;
        if (game.isPowerUpsEnabled()) {
            powerUpSpawn = new PowerUpSpawn(game);
            Bukkit.getPluginManager().registerEvents(powerUpSpawn, game.getCore());
            game.getListeners().add(powerUpSpawn);
        } else powerUpSpawn = null;

        if (game.isPvpEnabled()) {
            PVPRunEvents pvpRunEvents = new PVPRunEvents(game);
            Bukkit.getPluginManager().registerEvents(pvpRunEvents, game.getCore());
            game.getListeners().add(pvpRunEvents);
        }

        ScoreboardUpdater scoreboardUpdater = new ScoreboardUpdater(game);
        Bukkit.getPluginManager().registerEvents(scoreboardUpdater, game.getCore());
        game.getListeners().add(scoreboardUpdater);

        LocationTracking locationTracking = new LocationTracking(game);
        Bukkit.getPluginManager().registerEvents(locationTracking, game.getCore());
        game.getListeners().add(locationTracking);

        BlockRemoval blockRemoval = new BlockRemoval(locationTracking, powerUpSpawn);
        Bukkit.getPluginManager().registerEvents(blockRemoval, game.getCore());
        game.getListeners().add(blockRemoval);
    }

}
