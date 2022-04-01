package ch.killenberger.traitgenerator.model;

import com.google.gson.annotations.Expose;

import java.awt.image.BufferedImage;

public class Trait {
    @Expose
    private String        type;

    @Expose
    private String        name;

    private BufferedImage image;
    private boolean       recolorable;

    public Trait(final String type, final String name, final BufferedImage image, final boolean recolorable) {
        this.type        = type;
        this.name        = name;
        this.image       = image;
        this.recolorable = recolorable;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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
}
