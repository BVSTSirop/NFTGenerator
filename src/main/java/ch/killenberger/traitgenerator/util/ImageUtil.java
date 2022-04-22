package ch.killenberger.traitgenerator.util;

import ch.killenberger.traitgenerator.model.Avatar;
import ch.killenberger.traitgenerator.model.Trait;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.Random;

public abstract class ImageUtil {
    private static final Random RANDOM          = new Random();
    private static final File   SRC_DIR         = Paths.get("src").toFile();
    private static final File   IMG_OUTPUT_DIR  = new File(SRC_DIR, "images");
    private static final File   JSON_OUTPUT_DIR = new File(SRC_DIR, "json");
    private static final String IMG_FILE_EXT    = ".png";
    private static final String JSON_FILE_EXT   = ".json";

    private ImageUtil() { }

    public static Color getRandomColorFromList(final List<Color> colors) {
        return colors.get(RANDOM.nextInt(colors.size()));
    }

    public static void drawAvatars(final List<Avatar> avatars, final List<Color> colors) throws IOException {
        File imgFile;
        File jsonFile;
        for(Avatar a : avatars) {
            imgFile  = new File(IMG_OUTPUT_DIR, a.getId() + IMG_FILE_EXT);
            jsonFile = new File(JSON_OUTPUT_DIR, a.getId() + JSON_FILE_EXT);

            drawAvatar(a, imgFile, colors);
            FileUtil.writeToFile(jsonFile, a.getAvatarPropertiesAsJSON());
        }
    }

    public static void drawAvatar(final Avatar avatar, final File f, final List<Color> colors) throws IOException {
        final List<Trait>   attributes = avatar.getAttributes();
        final BufferedImage firstTrait = attributes.stream().filter(e -> e.getImage()!=null).findFirst().get().getImage();
        final BufferedImage combined   = new BufferedImage(firstTrait.getWidth(), firstTrait.getHeight(), BufferedImage.TYPE_INT_ARGB);
        final Graphics      graphics   = combined.getGraphics();

        graphics.setColor(avatar.getBackgroundColor());
        graphics.fillRect(0, 0, firstTrait.getWidth(), firstTrait.getHeight());

        drawTraits(attributes, graphics, colors);

        graphics.dispose();

        final File parent = f.getParentFile();
        if (!parent.exists()) {
            parent.mkdir();
        }

        ImageIO.write(combined, "PNG", f);
    }

    public static void drawTraits(List<Trait> traits, Graphics g, final List<Color> colors) {
        BufferedImage recoloredCharacteristic;
        for (Trait characteristic : traits) {
            final BufferedImage original = characteristic.getImage();

            if(characteristic != null) {
                if (characteristic.isRecolorable()) {
                    recoloredCharacteristic = recolorBufferedImage(original, getRandomColorFromList(colors));

                    g.drawImage(recoloredCharacteristic, 0, 0, null);
                } else {
                    g.drawImage(original, 0, 0, null);
                }
            }
        }
    }

    public static Color getRandomColor() {
        final int r = getRandomRGBInteger();
        final int g = getRandomRGBInteger();
        final int b = getRandomRGBInteger();

        return new Color(r,g,b);
    }

    public static BufferedImage recolorBufferedImageRandomly(final BufferedImage bImage) {
        return recolorBufferedImage(bImage, getRandomColor());
    }

    public static BufferedImage recolorBufferedImage(final BufferedImage bImage, final Color color) {
        final ColorModel     cm                   = bImage.getColorModel();
        final boolean        isAlphaPremultiplied = cm.isAlphaPremultiplied();
        final WritableRaster raster               = bImage.copyData(null);
        final BufferedImage  copy                 = new BufferedImage(cm, raster, isAlphaPremultiplied, null);

        for (int x = 0; x < copy.getWidth(); x++) {
            for (int y = 0; y < copy.getHeight(); y++) {
                final Color pixelColor = new Color(copy.getRGB(x, y), true);

                if ((pixelColor.getRGB() & 0x00FFFFFF) != 0 && (pixelColor.getRGB() >> 24) != 0x00) {
                    copy.setRGB(x, y, new Color(color.getRed(), color.getGreen(), color.getBlue(), pixelColor.getAlpha()).getRGB());
                }
            }
        }

        return copy;
    }

    private static int getRandomRGBInteger() {
        return RANDOM.nextInt(256);
    }
}