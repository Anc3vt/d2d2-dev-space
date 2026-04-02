/*
 * Copyright (C) 2026 Ancevt.
 * See the notice.md file distributed with this work for additional
 * information regarding copyright ownership.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ancevt.d2d2.dev;

import com.ancevt.d2d2.ApplicationConfig;
import com.ancevt.d2d2.ApplicationContext;
import com.ancevt.d2d2.D2D2;
import com.ancevt.d2d2.debug.FpsMeter;
import com.ancevt.d2d2.lifecycle.Application;
import com.ancevt.d2d2.log.Log;
import com.ancevt.d2d2.scene.Stage;
import com.ancevt.d2d2.scene.text.BitmapFont;
import com.ancevt.d2d2.scene.text.BitmapFontBuilder;
import com.ancevt.d2d2.scene.text.BitmapText;

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
    public void start(ApplicationContext applicationContext) {
        Stage stage = applicationContext.stage();

        D2D2.log.setLevel(Log.DEBUG);


        BitmapFont f2 = new BitmapFontBuilder()
                .assetPath("PressStart2P-Regular.ttf")
                .fontSize(32)
                .build();
        BitmapText bitmapText = new BitmapText(f2);
        bitmapText.setText("123123 ");
        stage.addChild(bitmapText, 200, 200);


        stage.addChild(new FpsMeter());
    }
}
