package ch.killenberger.traitgenerator.model;

import com.google.gson.annotations.Expose;

import java.awt.image.BufferedImage;

public class Trait {
    @Expose
    private String        name;
    private BufferedImage image;
    private boolean       recolorable;

    public Trait(final String name, final BufferedImage image, final boolean recolorable) {
        this.name        = name;
        this.image       = image;
        this.recolorable = recolorable;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BufferedImage getImage() {
        return image;
    }

    public void setImage(BufferedImage image) {
        this.image = image;
    }

    public boolean isRecolorable() {
        return recolorable;
    }

    public void setRecolorable(boolean recolorable) {
        this.recolorable = recolorable;
    }

    @Override
    public String toString() {
        return "Trait{" +
                "name='" + name + '\'' +
                ", hasImage=" + (image != null) +
                '}';
    }
}
