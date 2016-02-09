package com.mcgars.imagefactory.objects;

import java.io.Serializable;

public class Thumb implements Serializable, IThumb {
    String thumb;
    String origin;
    private int position = -1;

    public Thumb(String thumb, String origin) {
        this.thumb = thumb;
        this.origin = origin;
    }

    public Thumb() {
    }

    @Override
    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }
    @Override
    public String getThumb() {
        return thumb;
    }

    public void setThumb(String thumb) {
        this.thumb = thumb;
    }

    public void setPosition(int i) {
        position = i;
    }

    public int getPosition() {
        return position;
    }
}