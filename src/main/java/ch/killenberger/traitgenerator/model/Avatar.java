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
    private List<Trait> attributes = new ArrayList<>();

    @Expose
    private Color backgroundColor;

    public Avatar(final int id, final Color backgroundColor, final List<Trait> attributes) {
        this.id              = id;
        this.backgroundColor = backgroundColor;

        if(attributes != null) {
            this.attributes.addAll(attributes);
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

    public List<Trait> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<Trait> attributes) {
        this.attributes = attributes;
    }

    public String getAvatarPropertiesAsJSON() {
        final GsonBuilder gsonBuilder = new GsonBuilder().setPrettyPrinting().excludeFieldsWithoutExposeAnnotation();
        gsonBuilder.registerTypeAdapter(Color.class, new ColorSerializer());

        return gsonBuilder.create().toJson(this);
    }
}
