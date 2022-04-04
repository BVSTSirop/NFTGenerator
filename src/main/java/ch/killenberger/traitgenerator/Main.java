package ch.killenberger.traitgenerator;

import ch.killenberger.traitgenerator.model.Avatar;
import ch.killenberger.traitgenerator.model.Trait;
import ch.killenberger.traitgenerator.util.FileUtil;
import ch.killenberger.traitgenerator.util.ImageUtil;
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

public class Main {
    private static final int    STARTING_INDEX     = 0;
    private static final int    AMOUNT_TO_GENERATE = 150;
    private static final String RECOLORABLE_INDICATOR = "c_";

    private static final File RES_DIR         = Paths.get("src", "main", "resources").toFile();
    private static final File OUTPUT_DIR      = new File(RES_DIR, "output");
    private static final File IMG_OUTPUT_DIR  = new File(OUTPUT_DIR, "images");
    private static final File JSON_OUTPUT_DIR = new File(OUTPUT_DIR, "json");
    private static final File IMAGES_DIR      = new File(RES_DIR, "images");
    private static final File BODY_PARTS_DIR  = new File(IMAGES_DIR, "body_parts");
    private static final File BODY_DIR        = new File(BODY_PARTS_DIR, "body");
    private static final File TRAITS_DIR      = new File(IMAGES_DIR, "traits");
    private static final File COLORS_FILE     = new File(RES_DIR, "material_colors.json");

    private static final Random RANDOM        = new Random();
    private static final String IMG_FILE_EXT  = ".png";
    private static final String JSON_FILE_EXT = ".json";

    private static final String AVATAR_NAME   = "Crested Gecko";
    private static final String AVATAR_DESC   = "A procedurally generated crested gecko";


    private static List<Color> materialColors;
    private static int         currentAvatarId = 0;

    public static void main(String... args) throws IOException {
        final Map<String, List<Trait>> bodyParts = readTraits(BODY_PARTS_DIR);
        final Map<String, List<Trait>> traits    = readTraits(TRAITS_DIR);

        materialColors = readColorsFile(COLORS_FILE);

        Avatar avatar;
        for(int i = 0; i < AMOUNT_TO_GENERATE; i++) {
            avatar = createAvatar(bodyParts, traits);
            ImageUtil.drawAvatar(avatar, new File(IMG_OUTPUT_DIR, avatar.getId() + IMG_FILE_EXT), materialColors);
            FileUtil.writeToFile(new File(JSON_OUTPUT_DIR, avatar.getId() + JSON_FILE_EXT), avatar.getAvatarPropertiesAsJSON());
        }
    }

    private static Avatar createAvatar(final Map<String, List<Trait>> bodyParts, final Map<String, List<Trait>> traits) {
        final List<Trait> attributes = new ArrayList<>();
        for(Map.Entry<String, List<Trait>> entry : bodyParts.entrySet()) {
            final List<Trait> value = entry.getValue();

            attributes.add(value.get(RANDOM.nextInt(value.size())));
        }

        for(Map.Entry<String, List<Trait>> entry : traits.entrySet()) {
            final List<Trait> value = entry.getValue();

            if(value.size() > 1 && RANDOM.nextBoolean()) {
                attributes.add(value.get(RANDOM.nextInt(value.size())));
            } else {
                int selection = RANDOM.nextInt(value.size() + 1);
                if(selection < value.size()) { // This provides the option that no trait will be added
                    attributes.add(value.get(RANDOM.nextInt(value.size())));
                }
            }
        }

        currentAvatarId++;

        return new Avatar(currentAvatarId, AVATAR_NAME, AVATAR_DESC, materialColors.get(RANDOM.nextInt(materialColors.size())), attributes);
    }

    private static List<Color> readColorsFile(final File f) {
        final JSONParser jsonParser = new JSONParser();

        try (FileReader reader = new FileReader(f))
        {
            //Read JSON file
            Object obj = jsonParser.parse(reader);

            JSONArray colorArray = (JSONArray) obj;

            return convertToColorList(colorArray);
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }

        return new ArrayList<>();
    }

    private static List<Color> convertToColorList(JSONArray colorArray) {
        final List<Color> colors = new ArrayList<>();

        for (Object jsonObject : colorArray) {
            final JSONObject color = (JSONObject) jsonObject;

            JSONArray rgbArray = (JSONArray) color.get("rgb");

            final float r = ((Long) rgbArray.get(0)).floatValue() / 255;
            final float g = ((Long) rgbArray.get(1)).floatValue() / 255;
            final float b = ((Long) rgbArray.get(2)).floatValue() / 255;

            colors.add(new Color(r, g, b));
        }

        return colors;
    }

    private static TreeMap<String, List<Trait>> readTraits(final File directory) throws IOException {
        final TreeMap<String, List<Trait>> traits = new TreeMap<>();

        Files.walk(directory.toPath()).map(Path::toFile).skip(1).forEach(f -> {
            if (f.isDirectory()) {
                final String dirName = f.getName();

                if (!traits.containsKey(dirName)) {
                    traits.put(dirName, new ArrayList<>());
                }
            } else {
                final String parentDir = f.getParentFile().getName();


                final String        name;
                final BufferedImage image;
                final boolean       recolorable;

                final String filename = f.getName();
                if(filename.startsWith(RECOLORABLE_INDICATOR)) {
                    recolorable = true;
                    name        = FileUtil.removeFileExtension(filename).substring(RECOLORABLE_INDICATOR.length());
                } else {
                    recolorable = false;
                    name        = FileUtil.removeFileExtension(filename);
                }

                System.out.println(name);

                try {
                    image = ImageIO.read(f);
                } catch (IOException e) {
                    e.printStackTrace();

                    return;
                }

                traits.get(parentDir).add(new Trait(parentDir, name, image, recolorable));
            }
        });

        return traits;
    }
}
