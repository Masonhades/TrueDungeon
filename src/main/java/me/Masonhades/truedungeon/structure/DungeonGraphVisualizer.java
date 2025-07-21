//package me.Masonhades.truedungeon.structure;
//
//import net.minecraft.core.Direction;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Map;
//
//public class DungeonGraphVisualizer {
//    public static void printGraph(DungeonNode node) {
//        if (node == null) {
//            System.err.println("❌ Корневой узел равен null");
//            return;
//        }
//
//        printGraph(node, "", true, null);
//    }
//
//    private static void printGraph(DungeonNode node, String prefix, boolean isLast, RoomDefinition.Direction fromDirection) {
//        if (node == null) {
//            System.err.println("❌ node == null в printGraph(...)");
//            return;
//        }
//
//        String dirInfo = fromDirection == null ? "" : " <- " + fromDirection;
//        String rotationInfo = node.getRotation() != null ? " [rot=" + node.getRotation().name() + "]" : "";
//        String linePrefix = prefix + (isLast ? "└─ " : "├─ ");
//
//        System.out.println(linePrefix + node.getStructureId() + dirInfo + rotationInfo);
//
//        String childPrefix = prefix + (isLast ? "   " : "│  ");
//        Map<RoomDefinition.Direction, DungeonNode> connections = node.getConnections();
//
//        if (connections == null || connections.isEmpty()) {
//            System.out.println(childPrefix + "└── (нет дочерних комнат)");
//            return;
//        }
//
//        List<Map.Entry<RoomDefinition.Direction, DungeonNode>> entries = new ArrayList<>(connections.entrySet());
//
//        for (int i = 0; i < entries.size(); i++) {
//            Map.Entry<RoomDefinition.Direction, DungeonNode> entry = entries.get(i);
//            boolean lastChild = i == entries.size() - 1;
//            DungeonNode child = entry.getValue();
//
//            if (child == null) {
//                System.err.println(childPrefix + (lastChild ? "└─ " : "├─ ") + "❌ null-узел в связях");
//                continue;
//            }
//
//            printGraph(child, childPrefix, lastChild, entry.getKey());
//        }
//    }
//}

