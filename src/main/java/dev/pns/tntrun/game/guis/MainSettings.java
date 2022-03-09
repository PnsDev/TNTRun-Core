package dev.pns.tntrun.game.guis;

import dev.pns.tntrun.Core;
import dev.pns.tntrun.utils.gui.MenuInterface;
import dev.pns.tntrun.utils.gui.misc.MenuInterfaceButton;
import dev.pns.tntrun.utils.gui.misc.OnClick;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import static dev.pns.tntrun.utils.ItemUtils.itemFactory;
import static dev.pns.tntrun.utils.TextUtil.wrap;

public class MainSettings extends MenuInterface {
    private final Core core;
    private final Player player;

    public MainSettings(Core core, Player player) {
        super(core.getGuiManager(), "Game Settings", 27);
        this.core = core;
        this.player = player;

        fill(new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte) 15));

        // Social Settings
        set(1, new MenuInterfaceButton(itemFactory(new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte) 1), "&8 ", null)));
        set(19, new MenuInterfaceButton(itemFactory(new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte) 1), "&8 ", null)));
        set(10, new MenuInterfaceButton(itemFactory(Material.NAME_TAG, "&6Social Settings", wrap("&7The social information that is shown in the open servers menu.", 40)),
            (entity, stack, i1, e) -> {
                return OnClick.ButtonAction.CANCEL;
            }
        ));

        // PowerUp Settings
        set(3, new MenuInterfaceButton(itemFactory(new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte) 2), "&8 ", null)));
        set(21, new MenuInterfaceButton(itemFactory(new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte) 2), "&8 ", null)));
        set(12, new MenuInterfaceButton(itemFactory(Material.NETHER_STAR, "&dPowerUp Settings", wrap("&7The PowerUps that spawn in during the game.", 40)),
                (entity, stack, i1, e) -> {
                    return OnClick.ButtonAction.CANCEL;
                }
        ));

        // Potion Settings
        set(5, new MenuInterfaceButton(itemFactory(new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte) 3), "&8 ", null)));
        set(23, new MenuInterfaceButton(itemFactory(new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte) 3), "&8 ", null)));
        set(15, new MenuInterfaceButton(itemFactory(Material.BREWING_STAND_ITEM, "&bPotion Settings", wrap("&7The potions/effects which are given to players during the game.", 40)),
                (entity, stack, i1, e) -> {
                    return OnClick.ButtonAction.CANCEL;
                }
        ));

        // Game Settings
        set(7, new MenuInterfaceButton(itemFactory(new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte) 4), "&8 ", null)));
        set(25, new MenuInterfaceButton(itemFactory(new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte) 4), "&8 ", null)));
        set(17, new MenuInterfaceButton(itemFactory(Material.TRIPWIRE_HOOK, "&eGame Settings", wrap("&7The main settings for the game (such as double jump amount and pvp).", 40)),
                (entity, stack, i1, e) -> {
                    return OnClick.ButtonAction.CANCEL;
                }
        ));
    }



}
