package mint.managers;

import mint.Mint;
import mint.modules.Feature;
import mint.modules.Module;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileManager
        extends Feature {
    private final Path base = this.getMkDirectory(this.getRoot(), "mint");

    public FileManager() {
        for (Module.Category category : Mint.moduleManager.getCategories()) {
            Path config = this.getMkDirectory(this.base, "config");
            this.getMkDirectory(config, category.getName());
        }
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
        Path dir = this.lookupPath(parent, paths);
        this.createDirectory(dir);
        return dir;
    }

    public Path getBasePath() {
        return this.base;
    }


    public Path getCache() {
        return this.getBasePath().resolve("cache");
    }

}

