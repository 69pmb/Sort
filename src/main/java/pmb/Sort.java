package pmb;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.WordUtils;

public class Sort {

    public static final String PATH_ENTRY = System.getProperty("user.dir");


    public static final String PATH_CLEANED = System.getProperty("user.dir");

    private static final String CLEAN_FILE_NAME = " - Cleaned.";

    public static void main(String[] args) throws Exception {
        System.out.println("Debut");
        Path dir = Paths.get(PATH_ENTRY);
        List<Path> files = new ArrayList<Path>();
        listFilesForFolder(dir, files, "txt", true, CLEAN_FILE_NAME);
        if (files.isEmpty()) {
            throw new Exception("Dossier: " + dir + " vide");
        }
        for (Path file : files) {
            List<String> collect = null;
            try {
                collect = Files.lines(file).map(WordUtils::capitalize).map(StringUtils::trim)
                        .collect(Collectors.toList());
            } catch (Exception e) {
                System.out.println(file.getFileName().toString() + " failed: " + e.getMessage());
                System.out.println(e.getClass());
                if (e instanceof UncheckedIOException) {
                    System.out.println("Try encoding the file in UTF-8");
                }
                continue;
            }
            Collections.sort(collect, String.CASE_INSENSITIVE_ORDER);
            collect = collect.stream().distinct().collect(Collectors.toList());
            Files.write(file, collect, StandardOpenOption.TRUNCATE_EXISTING);
            System.out.println("Sucess: " + file.getFileName().toString());
        }
        System.out.println("Fin");
    }

    /**
     * Recupere la liste des fichiers d'un dossier.
     *
     * @param folder le dossier a chercher
     * @param files la liste qui contiendra les resultats
     * @param extension l'extension des fichiers a chercher
     * @param recursive si la recherche doit etre recursive ou non
     * @param exclude extension a ignorer
     * @throws IOException
     */
    public static void listFilesForFolder(final Path folder, List<Path> files, String extension, boolean recursive,
            String exclude) throws IOException {
        if (!Files.isDirectory(folder)) {
            files.add(folder);
            return;
        }
        List<Path> collect = Files.list(folder).collect(Collectors.toList());
        for (final Path fileEntry : collect) {
            if (recursive && Files.isDirectory(fileEntry)) {
                listFilesForFolder(fileEntry, files, extension, recursive, exclude);
            } else if (StringUtils.endsWith(fileEntry.getFileName().toString(), extension)
                    && !StringUtils.endsWithIgnoreCase(fileEntry.getFileName().toString(), exclude + extension)) {
                files.add(fileEntry);
            }
        }
    }

}