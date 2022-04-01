package ch.killenberger.traitgenerator.util;

import ch.killenberger.traitgenerator.model.Avatar;
import ch.killenberger.traitgenerator.model.Trait;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.IndexColorModel;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Random;

public abstract class ImageUtil {
    private static final Random RANDOM = new Random();

    private ImageUtil() {
    }

    public static void drawAvatar(final Avatar avatar, final File f) throws IOException {
        final List<Trait> attributes = avatar.getAttributes();

        final BufferedImage firstTrait = attributes.stream().findFirst().get().getImage();
        final BufferedImage combined = new BufferedImage(firstTrait.getWidth(), firstTrait.getHeight(), BufferedImage.TYPE_INT_ARGB);
        final Graphics graphics = combined.getGraphics();

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
        BufferedImage recoloredImage;
        for (Trait characteristic : traits) {
            final BufferedImage original = characteristic.getImage();

            if (characteristic.isRecolorable()) {
                recoloredImage = recolorBufferedImageRandomly(original);
                recoloredImage.getGraphics().drawImage(original, 0, 0, null);

                g.drawImage(recoloredImage, 0, 0, null);
            } else {
                g.drawImage(original, 0, 0, null);
            }
        }
    }

    public static Color getRandomColor() {
        final int r = getRandomRGBInteger();
        final int g = getRandomRGBInteger();
        final int b = getRandomRGBInteger();

        return new Color(r, g, b);
    }

    public static BufferedImage recolorBufferedImageRandomly(final BufferedImage bImage) {
        final int r = getRandomRGBInteger();
        final int g = getRandomRGBInteger();
        final int b = getRandomRGBInteger();

        return recolorBufferedImage(bImage, r, g, b);
    }

    public static BufferedImage recolorBufferedImage(final BufferedImage bImage, final int r, final int g, final int b) {
        for (int x = 0; x < bImage.getWidth(); x++) {
            for (int y = 0; y < bImage.getHeight(); y++) {
                final Color pixelColor = new Color(bImage.getRGB(x, y), true);

                if ((pixelColor.getRGB() & 0x00FFFFFF) != 0 && (pixelColor.getRGB() >> 24) != 0x00) {
                    bImage.setRGB(x, y, new Color(r, g, b, pixelColor.getAlpha()).getRGB());
                }
            }
        }
        return bImage;
    }

    private static IndexColorModel createCustomColorModel(final int rValue, final int gValue, final int bValue) {
        int size = 256;

        byte[] r = new byte[size];
        byte[] g = new byte[size];
        byte[] b = new byte[size];
        byte[] a = new byte[size];

        for (int i = 0; i < size; i++) {
            r[i] = (byte) rValue;
            g[i] = (byte) gValue;
            b[i] = (byte) bValue;
            a[i] = (byte) i;
        }

        return new IndexColorModel(16, size, r, g, b, a);
    }

    private static int getRandomRGBInteger() {
        return RANDOM.nextInt(256);
    }
}