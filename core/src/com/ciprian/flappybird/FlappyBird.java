package com.ciprian.flappybird;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;

import java.util.Map;
import java.util.Random;

public class FlappyBird extends ApplicationAdapter {

    SpriteBatch batch;
	Texture background;
	Texture gameOver;

//    ShapeRenderer shapeRenderer;

	int score = 0;
	int bestScore = 0;
	int scoringTube = 0;

	Texture[] birds;
	int flapState = 0;
	float birdY = 0F;
    int gameState = 0;
	float velocity = 0F; //speed
	float gravity = 2F;

	Texture topTube;
	Texture bottomTube;
    float maxTubeOffset;
	float gap = 300F;
	Random randomGenerator;
	float tubeVelocity = 4;
    int numberOfTubes = 4;
	float[] tubeX = new float[numberOfTubes];
    float[] tubeOffset = new float[numberOfTubes];
	float distanceBetweenTubes;

	PreferencesFlappy preferences;

	int wait = 0;

	//collision
    Circle birdCircle;
    Rectangle[] topTubeRectangles;
    Rectangle[] bottomTubeRectangles;

    BitmapFont fontScore;
    BitmapFont fontBestScore;

	@Override
	public void create () {

        preferences = new PreferencesFlappy();
        bestScore = preferences.getBestScore();

        fontScore = new BitmapFont();
        fontScore.setColor(Color.WHITE);
        fontScore.getData().scale(10);

        fontBestScore = new BitmapFont();
        fontBestScore.setColor(Color.WHITE);
        fontBestScore.getData().scale(10);

		batch = new SpriteBatch();
		background = new Texture("bg.png");

		birds = new Texture[2];
		birds[0] = new Texture("bird.png");
        birds[1] = new Texture("bird2.png");
        gameOver = new Texture("gameover.png");

        topTube = new Texture("toptube.png");
        bottomTube = new Texture("bottomtube.png");
        maxTubeOffset = Gdx.graphics.getHeight() / 2 - gap / 2 - 300;
        randomGenerator = new Random();
        distanceBetweenTubes = Gdx.graphics.getWidth() / 2;

        birdCircle = new Circle();
//        shapeRenderer = new ShapeRenderer();

        topTubeRectangles = new Rectangle[numberOfTubes];
        bottomTubeRectangles = new Rectangle[numberOfTubes];

        startGame();
    }

    public void startGame() {

        birdY = (Gdx.graphics.getHeight()/2) - (birds[0].getHeight() / 2);
        for (int i = 0; i < numberOfTubes; i++) {
            tubeOffset[i] = (randomGenerator.nextFloat() - 0.5F) * (Gdx.graphics.getHeight() - gap - 900);
            tubeX[i] = Gdx.graphics.getWidth() / 2 - topTube.getWidth() / 2 + Gdx.graphics.getWidth() + i * distanceBetweenTubes;
            topTubeRectangles[i] = new Rectangle();
            bottomTubeRectangles[i] = new Rectangle();
        }
    }

	@Override
	public void render () {

        batch.begin();
        batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        if(gameState == 1) {

            if(tubeX[scoringTube] < Gdx.graphics.getWidth() / 2) {
                score++;
                Gdx.app.log("SCORE", String.valueOf(score));
                if(scoringTube < numberOfTubes - 1) {
                    scoringTube++;
                } else {
                    scoringTube = 0;
                }
            }

            if(Gdx.input.justTouched()) {
                velocity = -30;

            }
            for (int i = 0; i < numberOfTubes; i++) {

                if(tubeX[i] < -topTube.getWidth()) {
                    tubeX[i] += numberOfTubes * distanceBetweenTubes;
                    tubeOffset[i] = (randomGenerator.nextFloat() - 0.5F) * (Gdx.graphics.getHeight() - gap - 650);
                } else {
                    tubeX[i] -= 4;

                }

                batch.draw(topTube, tubeX[i], Gdx.graphics.getHeight() / 2 + gap / 2 + tubeOffset[i]);
                batch.draw(bottomTube, tubeX[i], Gdx.graphics.getHeight() / 2 - gap - bottomTube.getHeight() + tubeOffset[i]);

                topTubeRectangles[i] = new Rectangle(tubeX[i], Gdx.graphics.getHeight() / 2 + gap / 2 + tubeOffset[i], topTube.getWidth(), topTube.getHeight());
                bottomTubeRectangles[i] = new Rectangle(tubeX[i], Gdx.graphics.getHeight() / 2 - gap - bottomTube.getHeight() + tubeOffset[i], bottomTube.getWidth(), bottomTube.getHeight());
            }

            if(birdY > 0) {
                velocity = velocity + gravity;
                birdY -= velocity;
            } else {
                gameState = 2;
            }

        } else if (gameState == 0) {
            //do this if the screen was tapped
            if(Gdx.input.justTouched()) {
                gameState = 1;
            }
        } else if (gameState == 2) {
            batch.draw(gameOver, Gdx.graphics.getWidth() / 2 - gameOver.getWidth() / 2, Gdx.graphics.getHeight() / 2 - gameOver.getHeight() / 2);
            velocity = velocity + gravity;
            birdY -= velocity;
            wait++;

            if(score > bestScore) {
                bestScore = score;
                preferences.setBestScore(bestScore);
            }

            if(wait > 100) {
                if (Gdx.input.justTouched()) {
                    gameState = 1;
                    startGame();
                    score = 0;
                    scoringTube = 0;
                    velocity = 0;
                    wait = 0;
                }
            }
        }

        if (flapState == 0) flapState = 1;
        else flapState = 0;


        batch.draw(birds[flapState], (Gdx.graphics.getWidth() / 2) - (birds[flapState].getWidth() / 2), birdY);
        fontScore.draw(batch, String.valueOf(score), 100, 200);
        fontBestScore.draw(batch, String.valueOf(bestScore), 100, Gdx.graphics.getHeight() - 80);
        batch.end();

        birdCircle.set(Gdx.graphics.getWidth() / 2, birdY + birds[flapState].getHeight() / 2, birds[flapState].getWidth() / 2 - 16);
//        Gdx.app.log("RENDER", "radius: " + (birds[flapState].getWidth() / 2 - 15));

//        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
//        shapeRenderer.setColor(Color.RED);
//        shapeRenderer.setColor(255, 0, 0, 0F);
//        shapeRenderer.circle(birdCircle.x, birdCircle.y, birdCircle.radius);

        for (int i = 0; i < numberOfTubes; i++) {
//            shapeRenderer.rect(tubeX[i], Gdx.graphics.getHeight() / 2 + gap / 2 + tubeOffset[i], topTube.getWidth(), topTube.getHeight());
//            shapeRenderer.rect(tubeX[i], Gdx.graphics.getHeight() / 2 - gap - bottomTube.getHeight() + tubeOffset[i], bottomTube.getWidth(), bottomTube.getHeight());

            if(Intersector.overlaps(birdCircle, topTubeRectangles[i]) || Intersector.overlaps(birdCircle, bottomTubeRectangles[i]) ) {
                gameState = 2;
            }

        }

//	    shapeRenderer.end();

	}
	
	@Override
	public void dispose () {
		batch.dispose();
	}
}
