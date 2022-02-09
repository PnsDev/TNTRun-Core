package dev.pns.tntrun.game.guis;

import dev.pns.tntrun.Core;
import dev.pns.tntrun.game.constructors.GamePlayer;
import dev.pns.tntrun.utils.gui.MenuInterface;
import dev.pns.tntrun.utils.gui.misc.MenuInterfaceButton;
import dev.pns.tntrun.utils.gui.misc.OnClick;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

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
        fill(Material.STAINED_GLASS_PANE);

    }

    @Override
    public void update() {
        GamePlayer gamePlayer = core.getGameManager().getGamePlayer(player);
        if (gamePlayer == null) {
            set(2, new MenuInterfaceButton(new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 9)));
            set(20, new MenuInterfaceButton(new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 9)));
            set(18, new MenuInterfaceButton(itemFactory(Material.WOOD_DOOR, "&bPublic Servers", Arrays.asList("&7Public servers hosted by other players", "&7with custom presets", " ", "&e ► Click to browse")),
                    (entity, stack, i, e) -> {
                        // todo open public servers
                        return OnClick.ButtonAction.CANCEL;
                    }
            ));

            set(5, new MenuInterfaceButton(new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 9)));
            set(23, new MenuInterfaceButton(new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 9)));
            set(21, new MenuInterfaceButton(itemFactory(Material.PAINTING, "&aCreate Server", Arrays.asList("&7Create your own server with your own", "&7custom presets", " ", "&e ► Click to create")),
                    (entity, stack, i, e) -> {
                        /*
                         * This should not be possible since the player can't
                         * run a command while in a GUI but just in case
                         */
                        if (core.getGameManager().getGamePlayer((Player) entity) != null) {
                            entity.sendMessage(formatMessage("&cYou are already in a game!"));
                            return OnClick.ButtonAction.CLOSE;
                        }
                        core.getGameManager().createGame((Player) entity);
                        update();
                        return OnClick.ButtonAction.CANCEL;
                    }
            ));
            return;
        }
    }
}
