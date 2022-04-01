package ch.killenberger.traitgenerator.model;

import ch.killenberger.traitgenerator.gson.ColorSerializer;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Avatar {
    @Expose
    private int id;

    @Expose
    private List<Trait> bodyCharacteristics = new ArrayList<>();

    @Expose
    private List<Trait> traits = new ArrayList<>();

    @Expose
    private Color backgroundColor;

    public Avatar(final int id, final Color backgroundColor, final List<Trait> bodyCharacteristics, final List<Trait> traits) {
        this.id              = id;
        this.backgroundColor = backgroundColor;

        if(bodyCharacteristics != null) {
            this.bodyCharacteristics.addAll(bodyCharacteristics);
        }

        if(traits != null) {
            this.traits.addAll(traits);
        }
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Color getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(Color backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public List<Trait> getBodyCharacteristics() {
        return bodyCharacteristics;
    }

    public void setBodyCharacteristics(List<Trait> bodyCharacteristics) {
        this.bodyCharacteristics = bodyCharacteristics;
    }

    public List<Trait> getTraits() {
        return traits;
    }

    public void setTraits(List<Trait> traits) {
        this.traits = traits;
    }

    public String getAvatarPropertiesAsJSON() {
        final GsonBuilder gsonBuilder = new GsonBuilder().setPrettyPrinting().excludeFieldsWithoutExposeAnnotation();
        gsonBuilder.registerTypeAdapter(Color.class, new ColorSerializer());

        return gsonBuilder.create().toJson(this);
    }

    @Override
    public String toString() {
        return "Avatar{" +
                "bodyCharacteristics=" + bodyCharacteristics +
                ", traits=" + traits +
                '}';
    }
}
