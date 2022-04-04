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
import java.util.List;
import java.util.Random;

public abstract class ImageUtil {
    private static final Random RANDOM = new Random();

    private ImageUtil() {
    }

    public static void drawAvatar(final Avatar avatar, final File f) throws IOException {
        final List<Trait>   attributes = avatar.getAttributes();
        final BufferedImage firstTrait = attributes.stream().findFirst().get().getImage();
        final BufferedImage combined   = new BufferedImage(firstTrait.getWidth(), firstTrait.getHeight(), BufferedImage.TYPE_INT_ARGB);
        final Graphics      graphics   = combined.getGraphics();

        graphics.setColor(avatar.getBackgroundColor());
        graphics.fillRect(0, 0, firstTrait.getWidth(), firstTrait.getHeight());

        drawTraits(attributes, graphics);

        graphics.dispose();

        final File parent = f.getParentFile();
        if (!parent.exists()) {
            parent.mkdir();
        }

        ImageIO.write(combined, "PNG", f);
    }

    public static void drawTraits(List<Trait> traits, Graphics g) {
        BufferedImage recoloredCharacteristic;
        for (Trait characteristic : traits) {
            final BufferedImage original = characteristic.getImage();

            if (characteristic.isRecolorable()) {
                recoloredCharacteristic = recolorBufferedImageRandomly(original);

                g.drawImage(recoloredCharacteristic, 0, 0, null);
            } else {
                g.drawImage(original, 0, 0, null);
            }
        }
    }

    public static BufferedImage recolorBufferedImageRandomly(final BufferedImage bImage) {
        final int r = getRandomRGBInteger();
        final int g = getRandomRGBInteger();
        final int b = getRandomRGBInteger();

        return recolorBufferedImage(bImage, r, g, b);
    }

    public static BufferedImage recolorBufferedImage(final BufferedImage bImage, final int r, final int g, final int b) {
        final ColorModel     cm                   = bImage.getColorModel();
        final boolean        isAlphaPremultiplied = cm.isAlphaPremultiplied();
        final WritableRaster raster               = bImage.copyData(null);
        final BufferedImage  copy                 = new BufferedImage(cm, raster, isAlphaPremultiplied, null);

        for (int x = 0; x < copy.getWidth(); x++) {
            for (int y = 0; y < copy.getHeight(); y++) {
                final Color pixelColor = new Color(copy.getRGB(x, y), true);

                if ((pixelColor.getRGB() & 0x00FFFFFF) != 0 && (pixelColor.getRGB() >> 24) != 0x00) {
                    copy.setRGB(x, y, new Color(r, g, b, pixelColor.getAlpha()).getRGB());
                }
            }
        }

        return copy;
    }

    private static int getRandomRGBInteger() {
        return RANDOM.nextInt(256);
    }
}