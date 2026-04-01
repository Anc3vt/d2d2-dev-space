package com.ancevt.d2d2.dev;

import com.ancevt.d2d2.ApplicationConfig;
import com.ancevt.d2d2.ApplicationContext;
import com.ancevt.d2d2.D2D2;
import com.ancevt.d2d2.debug.FpsMeter;
import com.ancevt.d2d2.lifecycle.Application;
import com.ancevt.d2d2.log.Log;
import com.ancevt.d2d2.scene.BasicSprite;
import com.ancevt.d2d2.scene.Sprite;
import com.ancevt.d2d2.scene.Stage;
import com.ancevt.d2d2.scene.texture.TextureAtlas;
import com.ancevt.d2d2.scene.texture.TextureAtlasBuilder;

public class TextureAtlasDevApplication implements Application {

    public static void main(String[] args) {
        D2D2.init(
                new TextureAtlasDevApplication(),
                new ApplicationConfig()
                        .title("D2D2 Dev Application")
                        .size(800, 600)
                        .alwaysOnTop(true)
        );
    }

    @Override
    public void start(ApplicationContext appCtx) {
        Stage stage = appCtx.stage();

        D2D2.log.setLevel(Log.DEBUG);

        TextureAtlas textureAtlas = new TextureAtlasBuilder()
                .addFromAsset("sq-tiger", "sq-tiger.png")
                .addFromAsset("heart", "heart.png")
                .build();

        Sprite sprite = new BasicSprite(textureAtlas.getTextureRegion("heart"));
        stage.addChild(sprite);

        sprite.setScale(4f);

        stage.addChild(new FpsMeter());
    }
}
