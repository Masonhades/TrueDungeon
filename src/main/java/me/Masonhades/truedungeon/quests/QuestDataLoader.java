package me.Masonhades.truedungeon.quests;

import com.google.gson.Gson;
import me.Masonhades.truedungeon.Truedungeon;
import net.minecraft.Util;
import net.minecraft.util.RandomSource;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

public class QuestDataLoader {



    public static final Gson GSON = new Gson();
    public static final Path QUESTS_PATH = Path.of("config", "truedungeon", "quests");

    public static Optional<QuestData> loadQuestData(String dealerName) {
        try {
            Path file = QUESTS_PATH.resolve(dealerName.toLowerCase() + ".json");
            System.out.println("Loading quests from path: " + QUESTS_PATH.toAbsolutePath());
            if (!Files.exists(file)) return Optional.empty();

            try (Reader reader = Files.newBufferedReader(file)) {
                return Optional.of(GSON.fromJson(reader, QuestData.class));
            }

        } catch (IOException e) {
            Truedungeon.LOGGER.error("Failed to load quest data for dealer: " + dealerName, e);
            return Optional.empty();
        }
    }

    public static DungeonQuest generateRandomQuest(QuestData data) {
        String type = Util.getRandom(data.types, RandomSource.create());
        int level = Util.getRandom(data.levels, RandomSource.create());
        List<String> rewardList = data.rewards.get(String.valueOf(level));
        String reward = Util.getRandom(rewardList, RandomSource.create());
        String objective = Util.getRandom(data.objectives, RandomSource.create());  // добавляем выбор цели
        return new DungeonQuest(type, level, reward, objective);
    }

}
