package com.mygdx.game;

import java.util.Iterator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.TimeUtils;

public class GameScreen implements Screen {
    final Drop game;
    Texture dropImage;
    Texture bucketImage;
    Texture traverseCat;
    Texture background;
    Sound dropSound;
    Sound ironSound;
    Music rainMusic;
    OrthographicCamera camera;
    Rectangle staticTraverseCat;

    MovingTravCat mtc;
    Array<Rectangle> traversingCats;

    Array<Rectangle> stackedCats;
    long lastDropTime;
    int gameState;

    public GameScreen(final Drop game) {
        this.game = game;

        // Load images, sounds, and music
        dropImage = new Texture(Gdx.files.internal("droplet.png"));
        bucketImage = new Texture(Gdx.files.internal("bucket.png"));
        traverseCat = new Texture(Gdx.files.internal("Løbekat.png"));
        background = new Texture(Gdx.files.internal("backgroundpng.png"));

        dropSound = Gdx.audio.newSound(Gdx.files.internal("drop.wav"));
        ironSound = Gdx.audio.newSound(Gdx.files.internal("iron.wav"));
        rainMusic = Gdx.audio.newMusic(Gdx.files.internal("rain.mp3"));

        camera = new OrthographicCamera();
        camera.setToOrtho(false, 800, 600);

        staticTraverseCat = new Rectangle();
        staticTraverseCat.x = 800 / 2 - 64 / 2;
        staticTraverseCat.y = 0;
        staticTraverseCat.width = 64;
        staticTraverseCat.height = 64;

        traversingCats = new Array<Rectangle>();

        // Initialize the movingTraverseCat
        Rectangle movingCatRect = new Rectangle(50, 540, 64, 64);
        mtc = new MovingTravCat(movingCatRect, 150); // 200 pixels per second
    }

    public MovingTravCat createMovingRectangle(){

        return mtc;
    }

    private void spawnTraverseCats() {
        Rectangle traverseCat = new Rectangle();

        traverseCat.x = mtc.getRect().x;
        traverseCat.y = 600;
        traverseCat.width = 64;
        traverseCat.height = 64;
        traversingCats.add(traverseCat);
        lastDropTime = TimeUtils.nanoTime();

    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0, 0, 12.2f, 1);
        camera.update();
        game.batch.setProjectionMatrix(camera.combined);

        stackedCats = new Array<Rectangle>();

        stackedCats.add(staticTraverseCat);
        // Update moving cat's position
        mtc.update(delta);

        game.batch.begin();
        game.batch.draw(background,0,0, camera.viewportWidth, camera.viewportHeight);
        game.font.draw(game.batch, "Tryk på mellemrumstasten for at placere en løbekat", 0, 480);
        game.batch.draw(traverseCat, staticTraverseCat.x, staticTraverseCat.y, staticTraverseCat.width, staticTraverseCat.height);
        game.batch.draw(traverseCat, mtc.getRect().x, mtc.getRect().y, mtc.getRect().width, mtc.getRect().height);

        for (Rectangle raindrop : traversingCats) {
            game.batch.draw(traverseCat, raindrop.x, raindrop.y, staticTraverseCat.width, staticTraverseCat.height);
        }

        game.batch.end();

        if (Gdx.input.isKeyJustPressed(Keys.SPACE)) {
            spawnTraverseCats();
        }

        Iterator<Rectangle> iter = traversingCats.iterator();
        while (iter.hasNext()) {
            Rectangle droppingTraverse = iter.next();
            droppingTraverse.y -= 150 * Gdx.graphics.getDeltaTime();

            int arraySize = stackedCats.size;

            if (droppingTraverse.y + 64 < 0)
                iter.remove();


            if (droppingTraverse.overlaps(stackedCats.get(arraySize - 1))) {
                ironSound.play();
                stackedCats.add(droppingTraverse);
                droppingTraverse.y = 64 * arraySize;
                gameState++;
            }
        }
    }

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void show() {
        // start the playback of the background music
        // when the screen is shown
        //rainMusic.play();
    }

    @Override
    public void hide() {
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void dispose() {
        dropImage.dispose();
        bucketImage.dispose();
        dropSound.dispose();
        rainMusic.dispose();
        traverseCat.dispose();
        background.dispose();
    }

}

