package org.coode.www.service;

import org.coode.www.cloud.CloudModel;
import org.semanticweb.owlapi.model.OWLEntity;

import java.awt.*;
import java.util.Set;

public class CloudHelper<O extends OWLEntity> {

    // capped maximum size of the font used to display entities
    private static final int MAX_SIZE = 60;

    private int threshold = 0;
    private int zoom = 0;
    private boolean normalise = false;
    private boolean inverted = false;

    private CloudModel<O> model;

    public CloudHelper(CloudModel<O> cloudModel, int threshold, int zoom) {
        this.model = cloudModel;
        this.threshold = threshold;
        this.zoom = zoom;
    }

    public void setNormalise(boolean normalise) {
        this.normalise = normalise;
    }

    public void setInverted(boolean inverted) {
        this.inverted = inverted;
    }

    public Set<O> getEntities() {
        return model.getEntities(threshold);
    }

    public String getColor(O entity) {
        int value = model.getValue(entity);
        int score;
        if (normalise) {
            int relativeScore = value - model.getMin();
            int scoreRange = model.getRange();
            score = 50 + ((relativeScore * 205) / scoreRange);
        }
        else {
            score = Math.min(255, 50 + (zoom * value / 2));
        }
        if (!inverted) {
            score = 255 - score;
        }
        Color color = new Color(score, score, score);
        String rgb = Integer.toHexString(color.getRGB());
        return "#" + rgb.substring(2, rgb.length());
    }

    public int getFontSize(O entity) {
        int value = model.getValue(entity);
        int size;
        if (normalise) {
            int displayMin = zoom;
            int displayRange = MAX_SIZE - displayMin;
            int scoreRange = model.getRange();
            int relativeScore = value - model.getMin();
            size = displayMin + ((relativeScore * displayRange) / scoreRange);
        }
        else {
            size = Math.min(MAX_SIZE, zoom + (value / 2));
        }

        if (size > MAX_SIZE) {
            throw new RuntimeException("ERROR, OVER MAX SIZE: " + size);
        }

        return size;
    }
}
