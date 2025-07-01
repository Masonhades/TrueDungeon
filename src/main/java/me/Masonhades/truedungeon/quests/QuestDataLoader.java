package me.Masonhades.truedungeon.quests;

import com.google.gson.Gson;
import me.Masonhades.truedungeon.Truedungeon;
import net.minecraft.Util;
import net.minecraft.util.RandomSource;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
        RandomSource random = RandomSource.create();

        String type = Util.getRandom(data.types, random);
        int level = Util.getRandom(data.levels, random);
        String objective = Util.getRandom(data.objectives, random);

        List<RewardGroup> levelRewards = data.rewards.get(String.valueOf(level));
        List<RewardItem> selectedRewards = selectRewards(levelRewards, random);

        return new DungeonQuest(type, level, selectedRewards, objective);
    }


    public static List<RewardItem> selectRewards(List<RewardGroup> rewardGroups, RandomSource random){

        List<RewardItem> finalRewards = new ArrayList<>();

        List<RewardGroup> baseGroups = rewardGroups.stream()
                .filter(group -> group.chance >= 1.0f)
                .toList();
        if (!baseGroups.isEmpty()) {
            RewardGroup chosenGroup = baseGroups.get(random.nextInt(baseGroups.size()));
            finalRewards.addAll(chosenGroup.items);
        }
        List<RewardGroup> optionalGroups = rewardGroups.stream()
                .filter(group -> group.chance < 1.0f)
                .toList();
        for (RewardGroup group : optionalGroups) {
            if (random.nextFloat() <= group.chance) {
                finalRewards.addAll(group.items);
            }
        }
        return finalRewards;
    }

}
