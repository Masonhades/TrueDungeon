package me.Masonhades.truedungeon.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class DungeonMapItem extends Item {
    public DungeonMapItem(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        if (stack.hasTag()) {
            var tag = stack.getTag();
            assert tag != null;
            if (tag.contains("dungeon_type")) {
                tooltip.add(Component.translatable("tooltip.truedungeon.dungeon_type")
                        .append(": ").append(Component.literal(tag.getString("dungeon_type"))));
            }
            if (tag.contains("dungeon_level")) {
                int dungeonLevel = tag.getInt("dungeon_level");

                dungeonLevel = Math.max(1, Math.min(5, dungeonLevel));

                // Определяем цвет уровня
                var levelColor = switch (dungeonLevel) {
                    case 1 -> ChatFormatting.GREEN;
                    case 2 -> ChatFormatting.DARK_GREEN;
                    case 3 -> ChatFormatting.GOLD;
                    case 4 -> ChatFormatting.RED;
                    case 5 -> ChatFormatting.DARK_RED;
                    default -> ChatFormatting.GRAY;
                };

                String stars = "★".repeat(dungeonLevel);
                tooltip.add(Component.translatable("tooltip.truedungeon.dungeon_level")
                        .append(": ").append(Component.literal(stars).withStyle(levelColor)));
            }
        } else {
            tooltip.add(Component.translatable("tooltip.truedungeon.empty_map"));
        }
        super.appendHoverText(stack, level, tooltip, flag);
    }
}
