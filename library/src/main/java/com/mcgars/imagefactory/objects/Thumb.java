package com.mcgars.imagefactory.objects;

import java.io.Serializable;

public class Thumb implements Serializable {
        String thumb;
        String origin;
        public Thumb(String thumb, String origin){
            this.thumb = thumb;
            this.origin = origin;
        }
        public Thumb(){}

        public String getOrigin() {
            return origin;
        }

        public void setOrigin(String origin) {
            this.origin = origin;
        }

        public String getThumb() {
            return thumb;
        }

        public void setThumb(String thumb) {
            this.thumb = thumb;
        }
    }