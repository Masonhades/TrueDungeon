package me.Masonhades.truedungeon.structure;

import com.google.gson.Gson;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class DungeonTypeDefinition {
    public String type;
    public boolean level_scaling;
    public RoomSettings rooms;
    public List<MobEntry> mobs;
    public BossSettings boss;
    public LootSettings loot;
    public QuestItems quest_items;
    public EnvironmentSettings environment;

    public static class RoomSettings {
        public int min;
        public int max;
    }

    public static class MobEntry {
        public String id;
        public int weight;
    }

    public static class BossSettings {
        public String id;
        public float health_multiplier;
        public String loot_table;
    }

    public static class LootSettings {
        public String chests;
        public List<SpecialItem> special_items;

        public static class SpecialItem {
            public String id;
            public float chance;
        }
    }

    public static class QuestItems {
        public String objective;
        public List<QuestItem> items;

        public static class QuestItem {
            public String id;
            public String drop_from;
        }
    }

    public static class EnvironmentSettings {
        public String fog_color;
        public String ambient_sound;
    }

    public static DungeonTypeDefinition loadDungeonType(ResourceLocation id, ResourceManager resourceManager) throws IOException {
        Resource resource = resourceManager.getResource(id).orElseThrow(() -> new FileNotFoundException("Dungeon config not found: " + id));
        try (Reader reader = new InputStreamReader(resource.open(), StandardCharsets.UTF_8)) {
            return new Gson().fromJson(reader, DungeonTypeDefinition.class);
        }
    }
}
