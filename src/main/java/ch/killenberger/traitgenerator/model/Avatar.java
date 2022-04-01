package ch.killenberger.traitgenerator.model;

import ch.killenberger.traitgenerator.gson.ColorSerializer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;

import java.awt.*;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Avatar {
    private int id;

    @Expose
    private String name;

    @Expose
    private String description;

    @Expose
    private Long date;

    @Expose
    private List<Trait> attributes = new ArrayList<>();

    @Expose
    private Color backgroundColor;

    private Gson gson;

    public Avatar(final int id, final String name, final String description, final Color backgroundColor, final List<Trait> attributes) {
        this.id              = id;
        this.name            = name + "#" + id;
        this.description     = description;
        this.date            = new Timestamp(System.currentTimeMillis()).getTime();
        this.backgroundColor = backgroundColor;

        if(attributes != null) {
            this.attributes.addAll(attributes);
        }

        final GsonBuilder gsonBuilder = new GsonBuilder().setPrettyPrinting().excludeFieldsWithoutExposeAnnotation();
        gsonBuilder.registerTypeAdapter(Color.class, new ColorSerializer());
        this.gson = gsonBuilder.create();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getDate() {
        return date;
    }

    public void setDate(Long date) {
        this.date = date;
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
        return gson.toJson(this);
    }
}
