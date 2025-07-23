package me.Masonhades.truedungeon.structure;

import com.google.common.reflect.TypeToken;
import com.google.gson.*;
import net.minecraft.core.Direction;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class RoomDefinitionLoader {
    private static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(Direction.class, new DirectionDeserializer())
            .registerTypeAdapter(RoomDefinition.class, new RoomDefinitionDeserializer())
            .create();

    public static List<RoomDefinition> loadFromJson(InputStream inputStream) {
        if (inputStream == null) {
            System.err.println("❌ InputStream is null");
            return Collections.emptyList();
        }

        try (InputStreamReader reader = new InputStreamReader(inputStream)) {
            Type listType = new TypeToken<List<RoomDefinition>>() {}.getType();
            List<RoomDefinition> rooms = GSON.fromJson(reader, listType);

            if (rooms == null || rooms.isEmpty()) {
                System.err.println("❌ Не удалось десериализовать комнаты. Проверьте структуру JSON и поля в RoomDefinition");
                return Collections.emptyList();
            }

            System.out.println("✅ Загружено " + rooms.size() + " комнат:");
            for (RoomDefinition room : rooms) {
                System.out.println(room);
            }

            return rooms.stream()
                    .filter(Objects::nonNull)
                    .filter(r -> r.getStructureId() != null && !r.getStructureId().isEmpty())
                    .toList();
        } catch (Exception e) {
            System.err.println("❌ Ошибка десериализации JSON: " + e.getMessage());
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    private static class DirectionDeserializer implements JsonDeserializer<Direction> {
        @Override
        public Direction deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException {
            String dirName = json.getAsString().toUpperCase();
            try {
                return Direction.valueOf(dirName);
            } catch (IllegalArgumentException e) {
                throw new JsonParseException("❌ Направление не найдено: " + dirName);
            }
        }
    }

    private static class RoomDefinitionDeserializer implements JsonDeserializer<RoomDefinition> {
        @Override
        public RoomDefinition deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject obj = json.getAsJsonObject();

            String structureId = obj.get("structureId").getAsString();
            int weight = obj.get("weight").getAsInt();
            int maxUsages = obj.get("maxUsages").getAsInt();

            // Типы
            List<String> types = new ArrayList<>();
            JsonArray typesArray = obj.getAsJsonArray("types");
            for (JsonElement typeEl : typesArray) {
                types.add(typeEl.getAsString());
            }

            // Маршруты
            List<Route> routes = new ArrayList<>();
            JsonArray routesArray = obj.getAsJsonArray("routes");
            for (JsonElement routeEl : routesArray) {
                JsonObject routeObj = routeEl.getAsJsonObject();

                Direction entrance = context.deserialize(routeObj.get("entrance"), Direction.class);
                Direction exit = context.deserialize(routeObj.get("exit"), Direction.class);

                int entranceChunkX = routeObj.has("entranceChunkX") ? routeObj.get("entranceChunkX").getAsInt() : 0;
                int entranceChunkZ = routeObj.has("entranceChunkZ") ? routeObj.get("entranceChunkZ").getAsInt() : 0;
                int exitChunkX = routeObj.has("exitChunkX") ? routeObj.get("exitChunkX").getAsInt() : 0;
                int exitChunkZ = routeObj.has("exitChunkZ") ? routeObj.get("exitChunkZ").getAsInt() : 0;

                routes.add(new Route(entrance, exit, entranceChunkX, entranceChunkZ, exitChunkX, exitChunkZ));
            }


            // levelScaling (опционально)
            boolean levelScaling = obj.has("levelScaling") && obj.get("levelScaling").getAsBoolean();

            // widthChunks и heightChunks (опционально, по умолчанию 1)
            int widthChunks = obj.has("widthChunks") ? obj.get("widthChunks").getAsInt() : 1;
            int heightChunks = obj.has("heightChunks") ? obj.get("heightChunks").getAsInt() : 1;



            // Создание и настройка
            RoomDefinition room = new RoomDefinition(structureId, weight, maxUsages, types, routes, levelScaling);
            room.setWidthChunks(widthChunks);
            room.setHeightChunks(heightChunks);
            return room;
        }
    }

}