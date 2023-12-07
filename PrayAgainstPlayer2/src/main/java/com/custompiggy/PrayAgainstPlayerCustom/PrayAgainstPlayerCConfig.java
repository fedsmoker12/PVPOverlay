package com.custompiggy.PrayAgainstPlayerCustom;

import java.awt.Color;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("CustomPrayVsPlayer")
public interface PrayAgainstPlayerCConfig extends Config
{
    @ConfigItem(
            position = 0,
            keyName = "attackerPlayerColor",
            name = "Attacker color",
            description = "This color will be used for attackers wielding an unknown weapon"
    )
    default Color attackerPlayerColor()
    {
        return new Color(0xFF0006);
    }

    @ConfigItem(
            position = 1,
            keyName = "potentialPlayerColor",
            name = "Potential Attacker color",
            description = "This is the color that will be used to highlight potential attackers."
    )
    default Color potentialPlayerColor()
    {
        return new Color(0xFFFF00);
    }

    @ConfigItem(
            position = 2,
            keyName = "meleePlayerColor",
            name = "Melee Attacker color",
            description = "This color will be used for attackers wielding melee weapons."
    )
    default Color meleePlayerColor()
    {
        return new Color(255, 0, 0); // Red
    }

    @ConfigItem(
            position = 3,
            keyName = "rangedPlayerColor",
            name = "Ranged Attacker color",
            description = "This color will be used for attackers wielding ranged weapons."
    )
    default Color rangedPlayerColor()
    {
        return new Color(0, 255, 0); // Green
    }

    @ConfigItem(
            position = 4,
            keyName = "magicPlayerColor",
            name = "Magic Attacker color",
            description = "This color will be used for attackers wielding magic weapons."
    )
    default Color magicPlayerColor()
    {
        return new Color(0, 0, 255); // Blue
    }

    @ConfigItem(
            position = 5,
            keyName = "attackerTargetTimeout",
            name = "Attacker Timeout",
            description = "Seconds until attacker is no longer highlighted."
    )
    default int attackerTargetTimeout()
    {
        return 10;
    }

    // ... (other config options remain unchanged)

    @ConfigItem(
            position = 19, // Adjust position as needed
            keyName = "activatePrayer",
            name = "Activate Prayer",
            description = "Automatically activates your protection prayer"
    )
    default boolean activatePrayer() {
        return false;
    }
}