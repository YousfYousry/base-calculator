package com.example.basecalculator;

import android.text.Spanned;

public class note {
    Spanned announce;
    long id;


    public long getId() {
        return id;
    }

    public Spanned getAnnounce() {
        return announce;
    }

    public note(Spanned announce, Long id) {
        this.announce = announce;
        this.id = id;
    }
}
