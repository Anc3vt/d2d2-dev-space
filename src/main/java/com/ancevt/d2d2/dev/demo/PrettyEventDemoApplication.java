package com.ancevt.d2d2.dev.demo;

import com.ancevt.d2d2.ApplicationConfig;
import com.ancevt.d2d2.ApplicationContext;
import com.ancevt.d2d2.D2D2;
import com.ancevt.d2d2.D2D2_legacy;
import com.ancevt.d2d2.event.StageEvent;
import com.ancevt.d2d2.event.core.EventListener;
import com.ancevt.d2d2.lifecycle.Application;
import com.ancevt.d2d2.scene.Color;
import com.ancevt.d2d2.scene.Stage;
import com.ancevt.d2d2.scene.interactive.DragUtil;
import com.ancevt.d2d2.scene.interactive.InteractiveGroup;
import com.ancevt.d2d2.scene.shape.BorderedRectangleShape;
import com.ancevt.d2d2.scene.shape.CircleShape;
import com.ancevt.d2d2.scene.shape.RectangleShape;
import com.ancevt.d2d2.scene.shape.RoundedCornerShape;
import com.ancevt.d2d2.scene.text.BitmapText;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PrettyEventDemoApplication implements Application {

    private final Random random = new Random();

    @Override
    public void start(ApplicationContext context) {
        Stage stage = D2D2_legacy.getStage();

        stage.setBackgroundColor(Color.of(0x0F1226));

        float sw = stage.getWidth();
        float sh = stage.getHeight();

        // backdrop
        RectangleShape backdrop = new RectangleShape(sw, sh, Color.of(0x0F1226));
        stage.addChild(backdrop);

        // мягкие декоративные круги на фоне
        CircleShape glow1 = new CircleShape(140, 48);
        glow1.setColor(Color.of(0x1E2A78));
        glow1.setAlpha(0.18f);
        glow1.setPosition(80, 70);
        stage.addChild(glow1);

        CircleShape glow2 = new CircleShape(220, 64);
        glow2.setColor(Color.of(0x6E2BF2));
        glow2.setAlpha(0.12f);
        glow2.setPosition(sw - 260, 90);
        stage.addChild(glow2);

        CircleShape glow3 = new CircleShape(180, 56);
        glow3.setColor(Color.of(0x00B7FF));
        glow3.setAlpha(0.08f);
        glow3.setPosition(sw - 220, sh - 220);
        stage.addChild(glow3);

        // заголовок
        BitmapText title = BitmapText.builder()
                .text("<E8F1FF>D2D2 event demo")
                .position(24, 20)
                .autoSize(true)
                .multicolor(true)
                .build();
        title.setSpacing(1);
        stage.addChild(title);

        title.setScale(2);

        BitmapText subtitle = BitmapText.builder()
                .text("<8FA3C7>hover • click • drag • focus • tick • resize")
                .position(24, 46)
                .autoSize(true)
                .multicolor(true)
                .build();
        subtitle.setSpacing(1);
        stage.addChild(subtitle);
        subtitle.setScale(2);

        // статусная строка снизу
        BitmapText status = BitmapText.builder()
                .text("<BFCBFF>Готово")
                .position(24, sh - 28)
                .autoSize(true)
                .multicolor(true)
                .build();
        stage.addChild(status);
        status.setScale(2);

        // карточка в центре
        DemoCard card = new DemoCard(320, 180);
        card.setPosition((sw - card.getWidth()) / 2f, (sh - card.getHeight()) / 2f - 30);
        card.setGlobalZOrderIndex(10);
        stage.addChild(card);

        // тащить мышкой
        DragUtil.enableDrag(card);

        // информер справа
        DemoPanel info = new DemoPanel(280, 180, "Signals");
        info.setPosition(sw - 320, 110);
        info.setGlobalZOrderIndex(5);
        stage.addChild(info);

        info.setLines(
                "Mouse hover: waiting",
                "Mouse click: waiting",
                "Focus: no",
                "Card pos: " + (int) card.getX() + ", " + (int) card.getY()
        );

        // декоративные звездочки
        List<RectangleShape> stars = new ArrayList<>();
        for (int i = 0; i < 48; i++) {
            RectangleShape star = new RectangleShape(
                    2 + random.nextInt(3),
                    2 + random.nextInt(3),
                    random.nextBoolean() ? Color.of(0x8FB3FF) : Color.of(0xD6E4FF)
            );
            star.setAlpha(0.35f + random.nextFloat() * 0.45f);
            star.setPosition(random.nextInt((int) Math.max(sw, 1)), random.nextInt((int) Math.max(sh, 1)));
            star.setGlobalZOrderIndex(0);
            stars.add(star);
            stage.addChild(star);
        }

        // hover / click / focus события карточки
        card.onMouseHover(e -> {
            card.setAccent(Color.of(0x6EA8FF));
            card.setTitle("<F7FBFF>Карточка наведена");
            status.setText("<A6C8FF>Hover on card");
            info.setLine(0, "Mouse hover: yes");
        });

        card.onMouseOut(e -> {
            card.setAccent(Color.of(0x7C5CFF));
            card.setTitle("<F7FBFF>Перетащи меня");
            status.setText("<BFCBFF>Курсор ушёл");
            info.setLine(0, "Mouse hover: no");
        });

        card.onMouseDown(e -> {
            card.bump();
            card.setTitle("<FFF4D6>Ты нажал карточку");
            status.setText("<FFD38A>MouseDown: x=" + (int) e.getX() + ", y=" + (int) e.getY());
            info.setLine(1, "Mouse click: down");
        });

        card.onMouseUp(e -> {
            if (e.isOnArea()) {
                card.flash();
                card.setTitle("<DDFCE0>Клик зарегистрирован");
                status.setText("<9FFFB0>Click accepted");
                info.setLine(1, "Mouse click: accepted");
            } else {
                status.setText("<FFB4B4>MouseUp outside");
                info.setLine(1, "Mouse click: released outside");
            }
        });

        card.onFocusIn(e -> {
            card.setFocusedVisual(true);
            info.setLine(2, "Focus: yes");
        });

        card.onFocusOut(e -> {
            card.setFocusedVisual(false);
            info.setLine(2, "Focus: no");
        });

        card.onMouseDrag(e -> {
            info.setLine(3, "Card pos: " + (int) card.getX() + ", " + (int) card.getY());
        });

        // кнопка внутри карточки
        card.getButton().onMouseHover(e -> {
            card.getButton().setHover(true);
            status.setText("<8BE9FD>Hover button");
        });

        card.getButton().onMouseOut(e -> {
            card.getButton().setHover(false);
            status.setText("<BFCBFF>Button idle");
        });

        card.getButton().onClick(e -> {
            card.randomizeBadgeColor();
            card.ping();
            status.setText("<FFB3F0>Button click: sparkle!");
        });

        // resize stage
        stage.onResize(e -> {
            backdrop.setSize(e.getWidth(), e.getHeight());
            status.setY(e.getHeight() - 28);
            glow2.setX(e.getWidth() - 260);
            glow3.setPosition(e.getWidth() - 220, e.getHeight() - 220);
        });

        stage.onTick(new EventListener<>() {
            private float t;

            @Override
            public void onEvent(StageEvent.Tick e) {
                t += e.getDelta() * 0.001f; // если delta у тебя похожа на миллисекунды

                float pulse = 1.0f + (float) Math.sin(t * 2.0f) * 0.0035f;
                card.setScale(pulse, pulse);

                card.animateBadge(t);

                for (int i = 0; i < stars.size(); i++) {
                    RectangleShape star = stars.get(i);
                    float speed = 0.35f + (i % 5) * 0.18f;
                    star.moveY(speed);

                    if (star.getY() > stage.getHeight() + 6) {
                        star.setY(-6);
                        star.setX(random.nextInt((int) Math.max(stage.getWidth(), 1)));
                    }
                }
            }
        });

        // красивое стартовое сообщение
        stage.onStart(e -> status.setText("<9FFFB0>Stage started"));
    }

    @Override
    public void stop() {
        // ничего
    }

    public static void main(String[] args) {
        ApplicationConfig config = new ApplicationConfig();
        config.set(ApplicationConfig.WIDTH, "960");
        config.set(ApplicationConfig.HEIGHT, "600");
        config.set(ApplicationConfig.TITLE, "D2D2 Pretty Demo");

        D2D2.init(new PrettyEventDemoApplication(), config);
    }

    private static class DemoPanel extends InteractiveGroup {
        private final RoundedCornerShape bg;
        private final BorderedRectangleShape border;
        private final BitmapText title;
        private final BitmapText[] rows;

        public DemoPanel(float width, float height, String titleText) {
            super(width, height);
            setInteractionEnabled(false);

            bg = new RoundedCornerShape(width, height, 16, 12);
            bg.setColor(Color.of(0x1A1F3A));
            bg.setAlpha(0.94f);

            border = new BorderedRectangleShape(width, height, null, Color.of(0x303A6B));
            border.setBorderWidth(1.5f);

            title = BitmapText.builder()
                    .text("<E8F1FF>" + titleText)
                    .position(16, 12)
                    .autoSize(true)
                    .multicolor(true)
                    .build();
            title.setScale(2);


            rows = new BitmapText[4];
            for (int i = 0; i < rows.length; i++) {
                rows[i] = BitmapText.builder()
                        .text("<A9B8E8>-")
                        .position(16, 42 + i * 26)
                        .autoSize(true)
                        .multicolor(true)
                        .build();
                rows[i].setScale(2);
            }

            addChild(bg);
            addChild(border);
            addChild(title);
            for (BitmapText row : rows) {
                addChild(row);
            }
        }

        public void setLines(String... values) {
            for (int i = 0; i < values.length && i < rows.length; i++) {
                rows[i].setText("<A9B8E8>" + values[i]);
            }
        }

        public void setLine(int index, String value) {
            if (index >= 0 && index < rows.length) {
                rows[index].setText("<A9B8E8>" + value);
            }
        }
    }

    private static class DemoButton extends InteractiveGroup {
        private final RoundedCornerShape bg;
        private final BitmapText label;

        public DemoButton(float width, float height, String text) {
            super(width, height);

            bg = new RoundedCornerShape(width, height, 12, 10);
            bg.setColor(Color.of(0x2C3566));

            label = BitmapText.builder()
                    .text("<EAF2FF>" + text)
                    .autoSize(true)
                    .multicolor(true)
                    .build();
            label.setScale(2);

            addChild(bg);
            addChild(label);
            label.setPosition((width - label.getWidth()) / 2f, (height - label.getHeight()) / 2f - 1);
        }

        public void setHover(boolean hover) {
            bg.setColor(hover ? Color.of(0x4462D9) : Color.of(0x2C3566));
        }
    }

    private static class DemoCard extends InteractiveGroup {
        private final RoundedCornerShape bg;
        private final BorderedRectangleShape outline;
        private final BitmapText title;
        private final BitmapText body;
        private final CircleShape badge;
        private final DemoButton button;

        private float badgeBaseX = 255;
        private float badgeBaseY = 38;
        private float flashAlpha = 0f;

        public DemoCard(float width, float height) {
            super(width, height);

            bg = new RoundedCornerShape(width, height, 22, 16);
            bg.setColor(Color.of(0x1A2040));
            bg.setAlpha(0.97f);

            outline = new BorderedRectangleShape(width, height, null, Color.of(0x2B3569));
            outline.setBorderWidth(2f);

            badge = new CircleShape(12, 24);
            badge.setColor(Color.of(0x7C5CFF));
            badge.setPosition(badgeBaseX, badgeBaseY);

            title = BitmapText.builder()
                    .text("<F7FBFF>Перетащи меня")
                    .position(22, 18)
                    .autoSize(true)
                    .multicolor(true)
                    .build();

            title.setScale(2);

            body = BitmapText.builder()
                    .text("<AEBBE8>Эта карточка слушает hover, click,\nfocus, drag и stage tick.")
                    .position(22, 58)
                    .multicolor(true)
                    .size(width - 44, 60)
                    .build();

            body.setScale(2);

            button = new DemoButton(126, 36, "Нажми сюда");
            button.setPosition(22, height - 58);

            addChild(bg);
            addChild(outline);
            addChild(badge);
            addChild(title);
            addChild(body);
            addChild(button);

            setTabbingEnabled(true);
        }

        public DemoButton getButton() {
            return button;
        }

        public void setTitle(String text) {
            title.setText(text);
        }

        public void setAccent(Color color) {
            badge.setColor(color);
        }

        public void setFocusedVisual(boolean focused) {
            outline.setBorderColor(focused ? Color.of(0x78A6FF) : Color.of(0x2B3569));
        }

        public void bump() {
            setRotation(-1.5f);
        }

        public void flash() {
            flashAlpha = 1f;
            bg.setColor(Color.of(0x24315E));
            setRotation(0f);
        }

        public void ping() {
            badge.setScale(1.35f, 1.35f);
        }

        public void randomizeBadgeColor() {
            int[] colors = {
                    0x7C5CFF,
                    0x00C2FF,
                    0x00E5A8,
                    0xFF9E57,
                    0xFF67C4
            };
            badge.setColor(Color.of(colors[(int) (Math.random() * colors.length)]));
        }

        public void animateBadge(float t) {
            badge.setPosition(
                    badgeBaseX + (float) Math.sin(t * 2.8f) * 4f,
                    badgeBaseY + (float) Math.cos(t * 3.4f) * 3f
            );

            float badgeScale = Math.max(1f, badge.getScaleX() * 0.92f);
            badge.setScale(badgeScale, badgeScale);

            if (flashAlpha > 0f) {
                flashAlpha *= 0.92f;
                if (flashAlpha < 0.05f) {
                    flashAlpha = 0f;
                    bg.setColor(Color.of(0x1A2040));
                }
            }

            if (Math.abs(getRotation()) > 0.05f) {
                setRotation(getRotation() * 0.85f);
            } else {
                setRotation(0f);
            }
        }
    }
}