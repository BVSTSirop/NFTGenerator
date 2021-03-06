package ch.killenberger.traitgenerator;

import ch.killenberger.traitgenerator.model.Avatar;
import ch.killenberger.traitgenerator.model.Trait;
import ch.killenberger.traitgenerator.util.FileUtil;
import ch.killenberger.traitgenerator.util.ImageUtil;
import ch.killenberger.traitgenerator.util.RandomCollection;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class Main {
    private static final int    AMOUNT_TO_GENERATE = 1000;
    private static final String RECOLOR_INDICATOR  = "c_";

    private static final File RES_DIR         = Paths.get("src", "main", "resources").toFile();
    public static final File IMAGES_DIR      = new File(RES_DIR, "images");
    private static final File BODY_PARTS_DIR  = new File(IMAGES_DIR, "body_parts");
    private static final File TRAITS_DIR      = new File(IMAGES_DIR, "traits");
    private static final File COLORS_FILE     = new File(RES_DIR, "material_colors.json");

    private static final Random RANDOM        = new Random();

    private static final String AVATAR_NAME   = "Friend";
    private static final String AVATAR_DESC   = "Yay, a new friend!";


    private static List<Color> materialColors;
    private static int         currentAvatarId = 0;

    public static void main(String... args) throws IOException {
        final Map<String, RandomCollection<Trait>> bodyParts = readTraits(BODY_PARTS_DIR, false);
        final Map<String, RandomCollection<Trait>> traits    = readTraits(TRAITS_DIR, true);

        materialColors = readColorsFile(COLORS_FILE);

        final List<Avatar> avatars = new ArrayList<>();
        for(int i = 0; i < AMOUNT_TO_GENERATE; i++) {
            avatars.add(createAvatar(bodyParts, traits));
        }

        ImageUtil.drawAvatars(avatars, materialColors);
        FileUtil.createCSVFile(avatars);
    }

    private static Avatar createAvatar(final Map<String, RandomCollection<Trait>> bodyParts, final Map<String, RandomCollection<Trait>> traits) {
        final List<Trait> attributes = new ArrayList<>();

        // Bodyparts; Always mandatory
        for(Map.Entry<String, RandomCollection<Trait>> entry : bodyParts.entrySet()) {
            final RandomCollection<Trait> value = entry.getValue();

            attributes.add(value.next());
        }

        // Traits; Optional
        for(Map.Entry<String, RandomCollection<Trait>> entry : traits.entrySet()) {
            final RandomCollection<Trait> value = entry.getValue();

            // This is ensures not everytime a trait is chosen
            final Trait trait = value.next();
            if(value.size() > 1 && RANDOM.nextBoolean()) {
                if(trait.getImage() != null) {
                    attributes.add(value.next());
                }
            } else {
                if(trait.getImage() != null) {
                    attributes.add(value.next());
                }
            }
        }

        currentAvatarId++;

        return new Avatar(currentAvatarId, AVATAR_NAME, AVATAR_DESC, materialColors.get(RANDOM.nextInt(materialColors.size())), attributes);
    }

    private static List<Color> readColorsFile(final File f) {
        final JSONParser jsonParser = new JSONParser();

        try (FileReader reader = new FileReader(f)) {
            final Object obj = jsonParser.parse(reader);

            return convertToColorList((JSONArray) obj);
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }

        return new ArrayList<>();
    }

    private static List<Color> convertToColorList(JSONArray colorArray) {
        final List<Color> colors = new ArrayList<>();

        for (Object jsonObject : colorArray) {
            final JSONObject color    = (JSONObject) jsonObject;
            final JSONArray  rgbArray = (JSONArray) color.get("rgb");

            final float r = ((Long) rgbArray.get(0)).floatValue() / 255;
            final float g = ((Long) rgbArray.get(1)).floatValue() / 255;
            final float b = ((Long) rgbArray.get(2)).floatValue() / 255;

            colors.add(new Color(r, g, b));
        }

        return colors;
    }

    private static HashMap<String, RandomCollection<Trait>> readTraits(final File directory, boolean noTraitPossible) throws IOException {
        final HashMap<String, RandomCollection<Trait>> traits = new LinkedHashMap<>();

        try (Stream<Path> directories = Files.walk(directory.toPath())) {
                directories.map(Path::toFile).skip(1).forEach(f -> {
                    if (f.isDirectory()) {
                        final String dirName = getDirectoryNameWithoutPriority(f.getName());

                        if (!traits.containsKey(dirName)) {
                            traits.put(StringUtils.capitalize(dirName), new RandomCollection<>());

                            // Adds a "no-trait" to the list of traits so no-trait is possible
                            if(noTraitPossible) {
                                traits.get(StringUtils.capitalize(dirName)).add(50, new Trait(dirName, "No Trait", null, false));
                            }
                        }
                    } else {
                        final String type   = StringUtils.capitalize(getDirectoryNameWithoutPriority(f.getParentFile().getName()));
                        final double rarity = getPriorityFromFileName(f.getName());

                        try {
                            final Trait  trait = createTraitFromFile(f, type);
                            traits.get(type).add(rarity, trait);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
        } catch(IOException e) {
            System.out.println("Exception while processing traits! " + e);
            throw e;
        }

        return traits;
    }

    private static Trait createTraitFromFile(final File f, final String type) throws IOException {
        if(f.isDirectory()) {
            throw new IllegalArgumentException(f.getAbsolutePath() + " is a directory!");
        }

        final String  name;
        final boolean recolorable;
        final String  filename = f.getName();

        if(filename.startsWith(RECOLOR_INDICATOR)) {
            recolorable = true;
        } else {
            recolorable = false;
        }

        name = StringUtils.capitalize(FileUtil.removeFileExtension(filename).substring(filename.lastIndexOf("_") + 1));

        final BufferedImage image;
        try {
            image = ImageIO.read(f);
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        }

        return new Trait(type, name, image, recolorable);
    }

    private static double getPriorityFromFileName(final String filename) {
        final Pattern pattern = Pattern.compile("^(\\d{1,3})|(\\d{1,3})|(\\d{1,3}\\.)");
        final Matcher matcher = pattern.matcher(filename);

        final double rarity;
        if(matcher.find()) {
            final String group = matcher.group();
            if(group.startsWith(RECOLOR_INDICATOR)) {
                rarity = Float.parseFloat(group.substring(RECOLOR_INDICATOR.length()));
            } else {
                rarity = Float.parseFloat(group);
            }

            return rarity;
        }

        return 100;
    }

    private static String getDirectoryNameWithoutPriority(final String dirName) {
        final Pattern pattern = Pattern.compile("^(\\d\\.\\d_)|^(\\d_)");
        final Matcher matcher = pattern.matcher(dirName);
        if(matcher.find()) {
            return dirName.substring(matcher.end());
        }

        return dirName;
    }
}
