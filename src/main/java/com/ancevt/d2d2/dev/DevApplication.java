package com.ancevt.d2d2.dev;

import com.ancevt.d2d2.ApplicationConfig;
import com.ancevt.d2d2.ApplicationContext;
import com.ancevt.d2d2.D2D2;
import com.ancevt.d2d2.debug.FpsMeter;
import com.ancevt.d2d2.lifecycle.Application;
import com.ancevt.d2d2.scene.Stage;

public class DevApplication implements Application {

    public static void main(String[] args) {
        D2D2.init(
                new DevApplication(),
                new ApplicationConfig()
                        .title("D2D2 Dev Application")
                        .size(800, 600)
                        .alwaysOnTop(true)
        );
    }


    @Override
    public void start(ApplicationContext appCtx) {
        Stage stage = appCtx.stage();

        stage.addChild(new FpsMeter());
    }
}
