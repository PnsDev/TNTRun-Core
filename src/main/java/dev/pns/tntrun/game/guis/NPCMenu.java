package dev.pns.tntrun.game.guis;

import dev.pns.tntrun.Core;
import dev.pns.tntrun.game.constructors.GamePlayer;
import dev.pns.tntrun.utils.gui.GuiManager;
import dev.pns.tntrun.utils.gui.MenuInterface;
import dev.pns.tntrun.utils.gui.misc.MenuInterfaceButton;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

import static dev.pns.tntrun.utils.ItemUtils.itemFactory;

public class NPCMenu extends MenuInterface {
    public NPCMenu(Core core, GamePlayer gamePlayer) {
        super(core.getGuiManager(), gamePlayer == null ? "NPC Menu" : "Game Menu", 27);

        fill(Material.STAINED_GLASS_PANE);

        if (gamePlayer == null) {
            set(2, new MenuInterfaceButton(new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 9)));
            set(20, new MenuInterfaceButton(new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 9)));
            set(18, new MenuInterfaceButton(itemFactory(Material.WOOD_DOOR, "&bPublic Servers", Arrays.asList("&7Public servers hosted by other players ", "&7with custom presets", " ", "&e ► Click to browse"))));
            return;
        }
    }
}
