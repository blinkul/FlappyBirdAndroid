package com.ciprian.flappybird;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

public class PreferencesFlappy {

    private Preferences pref;
    private int bestScore;

    public PreferencesFlappy() {
        pref = Gdx.app.getPreferences("My Preferences");
//        bestScore = pref.getInteger("bestScore");
    }

    public void setBestScore(int bestScore) {
        pref.putInteger("bestScore", bestScore);
        pref.flush();
    }

    public int getBestScore() {
        return pref.getInteger("bestScore");
    }

}
