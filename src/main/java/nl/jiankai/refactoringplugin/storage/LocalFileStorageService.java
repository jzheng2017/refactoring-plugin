package nl.jiankai.refactoringplugin.storage;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Stream;

public class LocalFileStorageService implements StorageService<String> {
    private static final Logger LOGGER = Logger.getLogger(LocalFileStorageService.class.getName());
    public static final String SEPARATOR = "\n";
    private final String fileLocation;
    private final boolean createIfMissing;

    public LocalFileStorageService(String fileLocation, boolean createIfMissing) {
        this.fileLocation = fileLocation;
        this.createIfMissing = createIfMissing;
    }

    @Override
    public Stream<String> read() {
        try {
            List<String> content = Files.readAllLines(Paths.get(fileLocation));
            return content.stream();
        } catch (IOException e) {
            LOGGER.warning("Could not read the contents of '%s'".formatted(fileLocation));
            return Stream.empty();
        }
    }

    @Override
    public void write(String content) {
        write(content, false);
    }

    @Override
    public void write(List<String> list) {
        writeList(list, false);
    }

    @Override
    public void append(String content) {
        write(content, true);
    }

    @Override
    public void append(List<String> list) {
        writeList(list, true);
    }

    private void writeList(List<String> list, boolean append) {
        write(String.join(SEPARATOR, list), append);
    }

    private void write(String content, boolean append) {
        createFileIfMissing();

        if (append) {
            content = SEPARATOR + content;
        }

        try (FileOutputStream outputStream = new FileOutputStream(fileLocation, append)) {
            outputStream.write(content.getBytes());
            LOGGER.info("Successfully written to '%s".formatted(fileLocation));
        } catch (IOException e) {
            LOGGER.warning("Could not write the content to the file '%s'. Reason: %s".formatted(fileLocation, e.getMessage()));
        }
    }

    private void createFileIfMissing() {
        File file = new File(fileLocation);
        if (createIfMissing && !file.exists()) {
            if (file.getParentFile().mkdirs()) {
                try {
                    if (file.createNewFile()) {
                        LOGGER.info("File created at location '%s'".formatted(fileLocation));
                    }
                } catch (IOException e) {
                    LOGGER.warning("Could not create file at location '%s'".formatted(fileLocation));
                }
            }
        }
    }
}
