package dev.pns.tntrun.game.guis;

import dev.pns.tntrun.Core;
import dev.pns.tntrun.game.constructors.GamePlayer;
import dev.pns.tntrun.utils.gui.MenuInterface;
import dev.pns.tntrun.utils.gui.misc.MenuInterfaceButton;
import dev.pns.tntrun.utils.gui.misc.OnClick;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Method;
import java.util.Arrays;

import static dev.pns.tntrun.utils.ChatUtils.formatMessage;
import static dev.pns.tntrun.utils.ItemUtils.itemFactory;

public class NPCMenu extends MenuInterface {
    private final Core core;
    private final Player player;

    public NPCMenu(Core core, Player player) {
        super(core.getGuiManager(), "Game Menu", 27);
        this.core = core;
        this.player = player;

        update();
    }

    @Override
    public void update() {
        fill(new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte) 15));
        GamePlayer gamePlayer = core.getGameManager().getGamePlayer(player);
        if (gamePlayer == null) {
            setMenuIcon(MenuItem.GAME_SEARCH, 11);
            setMenuIcon(MenuItem.CREATE_GAME, 15);
            return;
        }
        if (gamePlayer.getGame().getOwner().equals(gamePlayer.getPlayer())) {
            setMenuIcon(MenuItem.LEAVE_GAME, 11);
            setMenuIcon(MenuItem.MANAGE_PLAYERS, 13);
            setMenuIcon(MenuItem.MAPS_SELECTION, 15);
            setMenuIcon(MenuItem.SETTINGS, 17);
            return;
        }
        if (gamePlayer.getGame().getModerators().contains(gamePlayer.getPlayer())) {
            setMenuIcon(MenuItem.LEAVE_GAME, 11);
            setMenuIcon(MenuItem.MANAGE_PLAYERS, 14);
            setMenuIcon(MenuItem.MAPS_SELECTION, 17);
            return;
        }
        setMenuIcon(MenuItem.LEAVE_GAME, 13);
    }

    private void setMenuIcon(MenuItem menuItem, int slot) {
        set(slot + 9, new MenuInterfaceButton(new ItemStack(Material.STAINED_GLASS_PANE, 1, menuItem.glassColor)));
        set(slot - 9, new MenuInterfaceButton(new ItemStack(Material.STAINED_GLASS_PANE, 1, menuItem.glassColor)));
        set(slot, new MenuInterfaceButton(menuItem.item, (entity, stack, i, e) -> menuItem.menuAction.run(core, this, player)));
    }

    @AllArgsConstructor
    private enum MenuItem {
        CREATE_GAME(
                (short) 5,
                itemFactory(Material.PAINTING, "&aCreate Game", Arrays.asList("&7Create your own server with", "&7your own custom presets", " ", "&e ► Click to create")),
                (core, menu, player) -> {
                    if (core.getGameManager().getGamePlayer(player) != null) {
                        player.sendMessage(formatMessage("&cYou are already in a game!"));
                        return OnClick.ButtonAction.CLOSE;
                    }
                    core.getGameManager().createGame(player);
                    menu.update();
                    return OnClick.ButtonAction.CANCEL;
                }
        ),
        GAME_SEARCH(
                (short) 9,
                itemFactory(Material.WOOD_DOOR, "&bPublic Servers", Arrays.asList("&7Public servers hosted by", "&7other players with custom", "&7presets", " ", "&e ► Click to browse")),
                (core, menu, player) -> {
                    new OpenServers(core, player).open(player);
                    return OnClick.ButtonAction.CANCEL;
                }
        ),
        LEAVE_GAME(
                (short) 14,
                itemFactory(Material.IRON_DOOR, "&cLeave Game", Arrays.asList("&7Leave your current game", "&7and get sent to the", "&7lobby", " ", "&e ► Click to leave")),
                (core, menu, player) -> {
                    GamePlayer gamePlayer = core.getGameManager().getGamePlayer(player);
                    if (gamePlayer == null) player.sendMessage(formatMessage("&cYou are not in a game!"));
                    else {
                        gamePlayer.getGame().removeFromGame(gamePlayer);
                        core.getLobby().addPlayer(player);
                    }
                    return OnClick.ButtonAction.CLOSE;
                }
        ),
        MAPS_SELECTION(
                (short) 2,
                itemFactory(Material.EMPTY_MAP, "&dMap", Arrays.asList("&7Choose the map that will", "&7be played on this game", " ", "&e ► Click to browse")),
                (core, menu, player) -> {
                    return OnClick.ButtonAction.CANCEL;
                }
        ),
        MANAGE_PLAYERS(
                (short) 5,
                itemFactory(Material.SKULL, "&aManage Players", Arrays.asList("&7Manage the players currently", "&7in the game", " ", "&e ► Click to browse")),
                (core, menu, player) -> {
                    return OnClick.ButtonAction.CANCEL;
                }
        ),
        SETTINGS(
                (short) 3,
                itemFactory(Material.REDSTONE_COMPARATOR, "&bSettings", Arrays.asList("&7Change the settings for", "&7the game", " ", "&e ► Click to browse")),
                (core, menu, player) -> {
                    new MainSettings(core, player).open(player);
                    return OnClick.ButtonAction.CANCEL;
                }
        );

        private final short glassColor;
        private final ItemStack item;
        private final onClick menuAction;

        public interface onClick { OnClick.ButtonAction run(Core core, NPCMenu menu, Player player);}
    }

}
