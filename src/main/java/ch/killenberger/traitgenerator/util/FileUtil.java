package ch.killenberger.traitgenerator.util;

import ch.killenberger.traitgenerator.model.Avatar;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class FileUtil {

    private FileUtil() { }

    public static String removeFileExtension(String filename) {
        if (filename == null || filename.isEmpty()) {
            return filename;
        }

        return filename.replaceAll("\\.\\w*", "");
    }

    public static void writeToFile(final File f, final String content) throws IOException {
        FileUtils.writeStringToFile(f, content, StandardCharsets.ISO_8859_1);
    }

    public static void createCSVFile(List<Avatar> avatars) throws IOException {
        final String[] HEADERS = {"name", "description", "attributes", "image", "animation_url", "background_color", "youtube_url", "external_url"};
        FileWriter out = new FileWriter("friends.csv");
        try (CSVPrinter printer = new CSVPrinter(out, CSVFormat.DEFAULT.withHeader(HEADERS))) {
            avatars.forEach((a) -> {
                try {
                    printer.printRecord(a.getName(), a.getDescription(), a.getAttributes(), a.getImage());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }
}
