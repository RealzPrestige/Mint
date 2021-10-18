package mint.managers;

import mint.Mint;
import mint.modules.Module;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FileManager {
    private final Path base = getMkDirectory(getRoot(), "mint");

    public FileManager() {
        assert Mint.moduleManager != null;
        for (Module.Category category : Mint.moduleManager.getCategories()) {
            getMkDirectory(base, category.getName());
        }
    }

    public static void appendTextFile(String data, String file) {
        try {
            Path path = Paths.get(file);
            Files.write(path, Collections.singletonList(data), StandardCharsets.UTF_8, Files.exists(path) ? StandardOpenOption.APPEND : StandardOpenOption.CREATE);
        } catch (IOException e) {
            System.out.println("WARNING: Unable to write file: " + file);
        }
    }

    public static List<String> readTextFileAllLines(String file) {
        try {
            Path path = Paths.get(file);
            return Files.readAllLines(path, StandardCharsets.UTF_8);
        } catch (IOException e) {
            System.out.println("WARNING: Unable to read file, creating new file: " + file);
            FileManager.appendTextFile("", file);
            return Collections.emptyList();
        }
    }

    private String[] expandPath(String fullPath) {
        return fullPath.split(":?\\\\\\\\|/");
    }

    private Stream<String> expandPaths(String... paths) {
        return Arrays.stream(paths).map(this::expandPath).flatMap(Arrays::stream);
    }

    private Path lookupPath(Path root, String... paths) {
        return Paths.get(root.toString(), paths);
    }

    private Path getRoot() {
        return Paths.get("");
    }

    private void createDirectory(Path dir) {
        try {
            if (!Files.isDirectory(dir)) {
                if (Files.exists(dir)) {
                    Files.delete(dir);
                }
                Files.createDirectories(dir);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Path getMkDirectory(Path parent, String... paths) {
        if (paths.length < 1) {
            return parent;
        }
        Path dir = lookupPath(parent, paths);
        createDirectory(dir);
        return dir;
    }

    public Path getBasePath() {
        return base;
    }

    public Path getBaseResolve(String... paths) {
        String[] names = expandPaths(paths).toArray(String[]::new);
        if (names.length < 1) {
            throw new IllegalArgumentException("missing path");
        }
        return lookupPath(getBasePath(), names);
    }

    public Path getMkBaseResolve(String... paths) {
        Path path = getBaseResolve(paths);
        createDirectory(path.getParent());
        return path;
    }

    public Path getConfig() {
        return getBasePath().resolve("config");
    }

    public Path getCache() {
        return getBasePath().resolve("cache");
    }

    public Path getMkConfigDirectory(String... names) {
        return getMkDirectory(getConfig(), expandPaths(names).collect(Collectors.joining(File.separator)));
    }
}