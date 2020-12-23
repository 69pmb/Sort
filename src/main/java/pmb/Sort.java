package pmb;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.WordUtils;

public class Sort {

    private static final String PATH_ENTRY = System.getProperty("user.dir");
    private static final String PROPERTY_KEY_EXTENSION = "extension";
    private static final String PROPERTY_KEY_RECURSIVE = "recursive";
    private static final String PROPERTY_KEY_CAPITALIZE = "capitalize";
    private static final String CONFIG_FILE_NAME = "config.properties";
    private static String extension;
    private static Boolean recursive;
    private static Boolean capitalize;
    private static Properties prop;

    public static void main(String[] args) throws IOException {
        System.out.println("Start");
        initProperties(args);
        Path dir = Paths.get(PATH_ENTRY);
        System.out.println("Start processing folder: " + dir.toFile().getAbsolutePath());
        List<Path> files = new ArrayList<>();
        listFilesInFolder(dir, files, extension, recursive);
        if (files.isEmpty()) {
            throw new RuntimeException("Folder: " + dir + " has no " + extension + " files !");
        }
        files.forEach(file -> {
            try {
                process(file);
            } catch (UncheckedIOException e) {
                System.out.println("Try encoding the file " + file.getFileName() + " in UTF-8");
            }
        });
        System.out.println("End");
    }

    private static void initProperties(String[] args) throws IOException {
        Path configPath = Path.of(CONFIG_FILE_NAME);
        if (!configPath.toFile().exists()) {
            InputStream config = Optional.ofNullable(Sort.class.getClassLoader().getResourceAsStream(CONFIG_FILE_NAME))
                    .orElseThrow(() -> new RuntimeException("Can't find property file"));
            Files.copy(config, configPath);
        }
        prop = new Properties();
        try(InputStream config = Files.newInputStream(configPath)) {
            prop.load(config);
            updatePropertiesFromArgs(args);
            extension = Optional.ofNullable(prop.getProperty(PROPERTY_KEY_EXTENSION)).orElse("txt");
            recursive = Optional.ofNullable(prop.getProperty(PROPERTY_KEY_RECURSIVE)).map(BooleanUtils::toBoolean)
                    .orElse(true);
            capitalize = Optional.ofNullable(prop.getProperty(PROPERTY_KEY_CAPITALIZE)).map(BooleanUtils::toBoolean)
                    .orElse(true);
        }
    }

    private static void updatePropertiesFromArgs(String[] args) {
        if (args.length > 0 && args.length % 2 == 0) {
            for (int i = 0; i < args.length; i += 2) {
                String key = args[i];
                if (List.of(PROPERTY_KEY_EXTENSION, PROPERTY_KEY_RECURSIVE, PROPERTY_KEY_CAPITALIZE).contains(key)) {
                    prop.put(key, args[i + 1]);
                } else {
                    System.out.println("Property key '" + key + "' is unkown");
                }
            }
        }
        try (FileOutputStream out = new FileOutputStream(CONFIG_FILE_NAME, false)) {
            prop.store(out, null);
        } catch (IOException e) {
            throw new RuntimeException("Error when saving properties", e);
        }
    }

    private static void process(Path file) {
        try (Stream<String> lines = Files.lines(file)
                .map(line -> Boolean.TRUE.equals(capitalize) ? WordUtils.capitalize(line) : line).map(StringUtils::trim)
                .distinct()) {
            List<String> collect = lines.collect(Collectors.toList());
            Collections.sort(collect, String.CASE_INSENSITIVE_ORDER);
            Files.write(file, collect, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException(file.getFileName() + " failed", e);
        }
        System.out.println("Successfully processed file: " + file.getFileName());
    }

    /**
     * Recupere la liste des fichiers d'un dossier.
     *
     * @param folder    le dossier a chercher
     * @param files     la liste qui contiendra les resultats
     * @param extension l'extension des fichiers a chercher
     * @param recursive si la recherche doit etre recursive ou non
     * @throws IOException
     */
    private static void listFilesInFolder(final Path folder, List<Path> files, String extension, boolean recursive)
            throws IOException {
        if (!Files.isDirectory(folder)) {
            files.add(folder);
            return;
        }
        try (Stream<Path> collect = Files.list(folder)) {
            for (final Path fileEntry : collect.collect(Collectors.toList())) {
                if (recursive && Files.isDirectory(fileEntry)) {
                    listFilesInFolder(fileEntry, files, extension, recursive);
                } else if (StringUtils.endsWith(fileEntry.getFileName().toString(), extension)) {
                    files.add(fileEntry);
                }
            }
        }
    }

}