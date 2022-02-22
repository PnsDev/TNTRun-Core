package dev.pns.tntrun.game.guis;

import dev.pns.tntrun.Core;
import dev.pns.tntrun.game.Game;
import dev.pns.tntrun.utils.gui.MenuInterface;
import dev.pns.tntrun.utils.gui.misc.MenuInterfaceButton;
import dev.pns.tntrun.utils.gui.misc.OnClick;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static dev.pns.tntrun.utils.ChatUtils.formatMessage;
import static dev.pns.tntrun.utils.ItemUtils.itemFactory;
import static dev.pns.tntrun.utils.TextUtil.titleCase;
import static dev.pns.tntrun.utils.TextUtil.wrap;

public class OpenServers extends MenuInterface {
    private final Core core;
    private final Player player;
    private int page = 0;

    public OpenServers(Core core, Player player) {
        super(core.getGuiManager(), "Game Finder", 54);
        this.core = core;
        this.player = player;

        for (int i = 45; i < 54; i++) {
            set(i, new MenuInterfaceButton(new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte) 15)));
        }
        set(49, new MenuInterfaceButton(itemFactory(Material.BUCKET, "&eRefresh", null),
                (entity, stack, i, e) -> {
                    update();
                    return OnClick.ButtonAction.CANCEL;
                }
        ));
        update();
    }

    @Override
    public void update() {
        List<Game> games = core.getGameManager().getGames();
        // sort list based on players
        games.sort((g1, g2) -> {
            if (g1.getPlayers().size() == g2.getPlayers().size()) return 0;
            return g1.getPlayers().size() > g2.getPlayers().size() ? -1 : 1;
        });

        // Back button
        if (page > 0) set(45, new MenuInterfaceButton(itemFactory(Material.ARROW, "&eBack", List.of("&7Click to go back one page")), (entity, stack, i, e) -> {page--;return OnClick.ButtonAction.CANCEL;}));
        else set(45, new MenuInterfaceButton(new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte) 15)));

        // Next button
        if (games.size() > (page + 1) * 9) set(53, new MenuInterfaceButton(itemFactory(Material.ARROW, "&eNext", List.of("&7Click to go forward one page")), (entity, stack, i, e) -> {page++;return OnClick.ButtonAction.CANCEL;}));
        else set(53, new MenuInterfaceButton(new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte) 15)));

        for (int i = page * 45 + (page * 9) ; i < (page + 1) * 45 + (page * 9); i++) {
            int realSlot = i - (page * 45 + (page * 9));
            if (i >= games.size()) {
                if (inventory.getContents()[realSlot] == null) break;
                set(realSlot, new MenuInterfaceButton(new ItemStack(Material.AIR)));
                continue;
            }

            Game game = games.get(i);
            boolean isFull = game.getPlayers().size() >= game.getMaxPlayers();

            set(realSlot, new MenuInterfaceButton(
                    itemFactory(Material.EXPLOSIVE_MINECART,
                            "&f" + ChatColor.stripColor(game.getName()) + " " + "&8(&" + (isFull ? "d" : "a") + game.getPlayers().size() + "&8/&7" + game.getMaxPlayers() + "&8)" + (isFull ? "&c&lFULL" : ""),
                            Stream.concat(
                                            wrap("&7" + ChatColor.stripColor(game.getDescription()), 40).stream(),
                                            List.of(" ", "&7State &8- &f" + titleCase(game.getState().name().toLowerCase()), "&7Mode &8- &f" + (game.isPvpEnabled() ? "PVP" : "TNT") + "Run", "&7PowerUps &8- " + (game.isPowerUpsEnabled() ? "&aON" : "&cOFF"), " ", "&e ► Click to join " + (isFull ? "&8(&d&lPremium Only&8)" : "")).stream())
                                    .collect(Collectors.toList())
                    ),
                    (entity, stack, i1, e) -> {
                        if (isFull) {
                            //todo upgrade to premium menu
                            return OnClick.ButtonAction.CLOSE;
                        }
                        if (core.getGameManager().getGamePlayer((Player) entity) != null) {
                            entity.sendMessage(formatMessage("&cYou are already in a game!"));
                            return OnClick.ButtonAction.CLOSE;
                        }
                        game.joinGame((Player) entity);
                        return OnClick.ButtonAction.CANCEL;
                    }
            ));
        }

    }


}
