package ch.killenberger.traitgenerator.util;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

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
}
