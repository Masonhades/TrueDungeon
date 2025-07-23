package me.Masonhades.truedungeon;

import me.Masonhades.truedungeon.structure.DungeonNode;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;

public class DungeonGraphExporter {

    public static void exportToDot(DungeonNode root, Path outputPath) {
        try {
            // ✅ Создаём директории, если нужно
            Files.createDirectories(outputPath.getParent());

            try (PrintWriter writer = new PrintWriter(Files.newBufferedWriter(outputPath))) {
                writer.println("digraph Dungeon {");
                writer.println("  node [shape=box];");
                writeNodeRecursive(writer, root);
                writer.println("}");
                System.out.println("📄 Dungeon graph exported to: " + outputPath.toAbsolutePath());
            }

        } catch (IOException e) {
            System.err.println("❌ Error exporting dungeon graph: " + e.getMessage());
        }
    }

    private static void writeNodeRecursive(PrintWriter writer, DungeonNode node) {
        String nodeId = getNodeId(node);
        String label = node.getStructureId() + "\\n" + node.getPosition();
        writer.printf("  \"%s\" [label=\"%s\"];\n", nodeId, label);

        for (DungeonNode child : node.getChildren()) {
            String childId = getNodeId(child);
            writer.printf("  \"%s\" -> \"%s\";\n", nodeId, childId);
            writeNodeRecursive(writer, child);
        }
    }
    private static String getNodeId(DungeonNode node) {
        return node.getStructureId() + "_" + node.getPosition().x + "_" + node.getPosition().z;
    }
}
