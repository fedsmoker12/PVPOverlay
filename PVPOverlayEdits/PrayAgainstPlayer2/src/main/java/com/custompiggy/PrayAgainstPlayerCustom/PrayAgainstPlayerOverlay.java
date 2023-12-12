package com.custompiggy.PrayAgainstPlayerCustom;

import com.google.inject.Inject;
import lombok.Getter;
import net.runelite.api.Point;
import net.runelite.api.*;
import net.runelite.api.kit.KitType;
import net.runelite.client.ui.overlay.*;
import net.runelite.client.util.Text;

import javax.inject.Singleton;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ConcurrentModificationException;

@Singleton
class PrayAgainstPlayerOverlay extends Overlay
{

    private final PrayAgainstPlayerCPlugin plugin;
    private final PrayAgainstPlayerCConfig config;
    private final Client client;
    @Getter
    private Prayer prayerToActivate;

    @Inject
    private PrayAgainstPlayerOverlay(final PrayAgainstPlayerCPlugin plugin, final PrayAgainstPlayerCConfig config, final Client client)
    {
        this.plugin = plugin;
        this.config = config;
        this.client = client;
        setLayer(OverlayLayer.ABOVE_SCENE);
        setPosition(OverlayPosition.DYNAMIC);
        setPriority(OverlayPriority.HIGH);
    }

    @Override
    public Dimension render(Graphics2D graphics)
    {
        renderPotentialPlayers(graphics);
        renderAttackingPlayers(graphics);
        return null;
    }

    private void renderPotentialPlayers(Graphics2D graphics) {
        if (plugin.getPotentialPlayersAttackingMe() == null || plugin.getPotentialPlayersAttackingMe().isEmpty()) {
            return;
        }

        try {
            for (PlayerContainer container : plugin.getPotentialPlayersAttackingMe()) {
                if ((System.currentTimeMillis() > (container.getWhenTheyAttackedMe() + container.getMillisToExpireHighlight())) && (container.getPlayer().getInteracting() != client.getLocalPlayer())) {
                    plugin.removePlayerFromPotentialContainer(container);
                }

                if (config.drawPotentialTargetsName()) {
                    renderNameAboveHead(graphics, container.getPlayer(), config.potentialPlayerColor());
                }

                if (config.drawPotentialTargetHighlight()) {
                    double borderWidth = config.borderWidth(); // Retrieve border width from config
                    renderHighlightedPlayer(graphics, container.getPlayer(), config.potentialPlayerColor(), borderWidth);
                }

                if (config.drawPotentialTargetTile()) {
                    renderTileUnderPlayer(graphics, container.getPlayer(), config.potentialPlayerColor());
                }

                if (config.drawPotentialTargetPrayAgainst()) {
                    renderPrayAgainstOnPlayer(graphics, container.getPlayer(), config.potentialPlayerColor());
                }
            }
        } catch (ConcurrentModificationException ignored) {
            // Handle exception if necessary
        }
    }

    private Color getAttackingPlayerColor(Player player) {
        switch (WeaponType.checkWeaponOnPlayer(client, player)) {
            case WEAPON_MELEE:
                return config.meleeAttackerColor();
            case WEAPON_RANGED:
                return config.rangedAttackerColor();
            case WEAPON_MAGIC:
                return config.magicAttackerColor();
            default:
            case WEAPON_UNKNOWN:
                return config.attackerPlayerColor();
        }
    }

    private void renderAttackingPlayers(Graphics2D graphics) {
        if (plugin.getPlayersAttackingMe() == null || plugin.getPlayersAttackingMe().isEmpty()) {
            return;
        }

        try {
            for (PlayerContainer container : plugin.getPlayersAttackingMe()) {
                if ((System.currentTimeMillis() > (container.getWhenTheyAttackedMe() + container.getMillisToExpireHighlight())) && (container.getPlayer().getInteracting() != client.getLocalPlayer())) {
                    plugin.removePlayerFromAttackerContainer(container);
                }

                if (config.drawTargetsName()) {
                    renderNameAboveHead(graphics, container.getPlayer(), config.attackerPlayerColor());
                }

                if (config.drawTargetHighlight()) {
                    double borderWidth = config.borderWidth(); // Retrieve border width from config
                    renderHighlightedPlayer(graphics, container.getPlayer(), getAttackingPlayerColor(container.getPlayer()), borderWidth);
                }

                if (config.drawTargetTile()) {
                    renderTileUnderPlayer(graphics, container.getPlayer(), config.attackerPlayerColor());
                }

                if (config.drawTargetPrayAgainst()) {
                    renderPrayAgainstOnPlayer(graphics, container.getPlayer(), config.attackerPlayerColor());
                }
            }
        } catch (ConcurrentModificationException ignored) {
            // Handle exception if necessary
        }
    }

    private void renderNameAboveHead(Graphics2D graphics, Player player, Color color)
    {
        final String name = Text.sanitize(player.getName());
        final int offset = player.getLogicalHeight() + 40;
        Point textLocation = player.getCanvasTextLocation(graphics, name, offset);
        if (textLocation != null)
        {
            OverlayUtil.renderTextLocation(graphics, textLocation, name, color);
        }
    }

    private void renderHighlightedPlayer(Graphics2D graphics, Player player, Color color, double borderWidth) {
        try {
            Stroke stroke = new BasicStroke((float) borderWidth); // Create a BasicStroke with the desired border width
            OverlayUtil.renderPolygon(graphics, player.getConvexHull(), color, stroke);
        } catch (NullPointerException var5) {
            // Handle exception if necessary
        }
    }

    private void renderTileUnderPlayer(Graphics2D graphics, Player player, Color color)
    {
        Polygon poly = player.getCanvasTilePoly();
        OverlayUtil.renderPolygon(graphics, poly, color);
    }

    private void renderPrayAgainstOnPlayer(Graphics2D graphics, Player player, Color color)
    {
        final int offset = (player.getLogicalHeight() / 2) + 75;
        BufferedImage icon;

        switch (WeaponType.checkWeaponOnPlayer(client, player))
        {
            case WEAPON_MELEE:
                icon = plugin.getProtectionIcon(WeaponType.WEAPON_MELEE);
                prayerToActivate = Prayer.PROTECT_FROM_MELEE;
                break;
            case WEAPON_MAGIC:
                icon = plugin.getProtectionIcon(WeaponType.WEAPON_MAGIC);
                prayerToActivate = Prayer.PROTECT_FROM_MAGIC;
                break;
            case WEAPON_RANGED:
                icon = plugin.getProtectionIcon(WeaponType.WEAPON_RANGED);
                prayerToActivate = Prayer.PROTECT_FROM_MISSILES;
                break;
            default:
                icon = null;
                break;
        }
        try
        {
            if (icon != null)
            {
                Point point = player.getCanvasImageLocation(icon, offset);
                OverlayUtil.renderImageLocation(graphics, point, icon);
            }
            else
            {
                if (config.drawUnknownWeapons())
                {
                    int itemId = player.getPlayerComposition().getEquipmentId(KitType.WEAPON);
                    ItemComposition itemComposition = client.getItemDefinition(itemId);

                    final String str = itemComposition.getName().toUpperCase();
                    Point point = player.getCanvasTextLocation(graphics, str, offset);
                    OverlayUtil.renderTextLocation(graphics, point, str, color);
                }
            }
        }
        catch (Exception ignored)
        {
        }
    }
}
