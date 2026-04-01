package com.ancevt.d2d2.dev.demo;

import com.ancevt.d2d2.ApplicationConfig;
import com.ancevt.d2d2.ApplicationContext;
import com.ancevt.d2d2.D2D2;
import com.ancevt.d2d2.D2D2_legacy;
import com.ancevt.d2d2.lifecycle.Application;
import com.ancevt.d2d2.scene.BasicGroup;
import com.ancevt.d2d2.scene.Color;
import com.ancevt.d2d2.scene.Stage;
import com.ancevt.d2d2.scene.interactive.InteractiveGroup;
import com.ancevt.d2d2.scene.shape.BorderedRectangleShape;
import com.ancevt.d2d2.scene.shape.RectangleShape;
import com.ancevt.d2d2.scene.text.BitmapText;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class NeonRainGame implements Application {

    private final Random random = new Random();

    private Stage stage;

    private RectangleShape background;
    private final List<RectangleShape> stars = new ArrayList<>();

    private Player player;
    private final List<FallingItem> items = new ArrayList<>();

    private BitmapText titleText;
    private BitmapText hudText;
    private BitmapText infoText;
    private BitmapText centerText;
    private BitmapText centerSubText;

    private OverlayButton startButton;

    private boolean running;
    private boolean gameOver;
    private boolean moveLeft;
    private boolean moveRight;
    private float mouseTargetX = -1f;

    private int score;
    private int lives;
    private int level;
    private float spawnTimer;
    private float spawnInterval;
    private float baseFallSpeed;
    private float timeSeconds;

    @Override
    public void start(ApplicationContext applicationContext) {
        stage = D2D2_legacy.getStage();
        stage.setBackgroundColor(Color.of(0x0B1020));

        float sw = stage.getWidth();
        float sh = stage.getHeight();

        background = new RectangleShape(sw, sh, Color.of(0x0B1020));
        stage.addChild(background);

        createStars(60);
        createUi();
        createPlayer();

        hookInput();
        hookGameLoop();

        showMenu();
    }

    @Override
    public void stop() {
        // nothing
    }

    private void createStars(int count) {
        for (int i = 0; i < count; i++) {
            RectangleShape star = new RectangleShape(
                    2 + random.nextInt(3),
                    2 + random.nextInt(3),
                    random.nextBoolean() ? Color.of(0x8AA4FF) : Color.of(0xD8E4FF)
            );
            star.setAlpha(0.25f + random.nextFloat() * 0.5f);
            star.setPosition(
                    random.nextInt((int) Math.max(1, stage.getWidth())),
                    random.nextInt((int) Math.max(1, stage.getHeight()))
            );
            stars.add(star);
            stage.addChild(star);
        }
    }

    private void createUi() {
        titleText = BitmapText.builder()
                .text("<EAF2FF>NEON RAIN")
                .position(20, 16)
                .autoSize(true)
                .multicolor(true)
                .build();
        titleText.setSpacing(1);

        hudText = BitmapText.builder()
                .text("<B9C8FF>Score: 0   Lives: 3   Level: 1")
                .position(20, 44)
                .autoSize(true)
                .multicolor(true)
                .build();

        infoText = BitmapText.builder()
                .text("<7F93C8>← → / мышь — движение   Enter / клик — старт")
                .position(20, 68)
                .autoSize(true)
                .multicolor(true)
                .build();

        centerText = BitmapText.builder()
                .text("")
                .position(0, 0)
                .autoSize(true)
                .multicolor(true)
                .build();
        centerText.setSpacing(1);

        centerSubText = BitmapText.builder()
                .text("")
                .position(0, 0)
                .autoSize(true)
                .multicolor(true)
                .build();

        startButton = new OverlayButton(180, 42, "START");
        startButton.setPosition((stage.getWidth() - 180) / 2f, stage.getHeight() / 2f + 36);

        startButton.onClick(e -> {
            if (!running) {
                startGame();
            }
        });

        startButton.onMouseHover(e -> startButton.setHovered(true));
        startButton.onMouseOut(e -> startButton.setHovered(false));

        stage.addChild(titleText);
        stage.addChild(hudText);
        stage.addChild(infoText);
        stage.addChild(centerText);
        stage.addChild(centerSubText);
        stage.addChild(startButton);
    }

    private void createPlayer() {
        player = new Player(110, 18);
        player.setPosition((stage.getWidth() - player.getWidth()) / 2f, stage.getHeight() - 54);
        stage.addChild(player);
    }

    private void hookInput() {
        stage.onKeyDown(e -> {
            if (e.getKeyCode() == 263 || e.getKeyCode() == 37) { // left
                moveLeft = true;
            }
            if (e.getKeyCode() == 262 || e.getKeyCode() == 39) { // right
                moveRight = true;
            }
            if (e.getKeyCode() == 257 || e.getKeyCode() == 10 || e.getKeyCode() == 13) { // enter
                if (!running) {
                    startGame();
                }
            }
        });

        stage.onKeyUp(e -> {
            if (e.getKeyCode() == 263 || e.getKeyCode() == 37) {
                moveLeft = false;
            }
            if (e.getKeyCode() == 262 || e.getKeyCode() == 39) {
                moveRight = false;
            }
        });

        stage.onMouseMove(e -> mouseTargetX = e.getX());

        stage.onMouseDown(e -> {
            if (!running) {
                startGame();
            }
        });

        stage.onResize(e -> {
            background.setSize(e.getWidth(), e.getHeight());

            centerTitle("<EAF2FF>NEON RAIN", "<8EA4D9>Поймай зелёные. Красные — боль.");
            startButton.setPosition((e.getWidth() - startButton.getWidth()) / 2f, e.getHeight() / 2f + 36);

            if (player != null) {
                float maxX = e.getWidth() - player.getWidth();
                if (player.getX() > maxX) {
                    player.setX(maxX);
                }
                player.setY(e.getHeight() - 54);
            }
        });
    }

    private void hookGameLoop() {
        stage.onTick(e -> {
            float dt = normalizeDelta(e.getDelta());
            timeSeconds += dt;

            animateStars(dt);

            if (!running) {
                player.idlePulse(timeSeconds);
                return;
            }

            updatePlayer(dt);
            updateSpawning(dt);
            updateItems(dt);
            updateHud();
            updateDifficulty();
        });
    }

    private float normalizeDelta(float delta) {
        if (delta > 3f) {
            return delta / 1000f;
        }
        return delta;
    }

    private void animateStars(float dt) {
        for (int i = 0; i < stars.size(); i++) {
            RectangleShape star = stars.get(i);
            float speed = 20f + (i % 5) * 14f;
            star.moveY(speed * dt);

            if (star.getY() > stage.getHeight() + 6) {
                star.setY(-6);
                star.setX(random.nextInt((int) Math.max(1, stage.getWidth())));
            }
        }
    }

    private void showMenu() {
        running = false;
        gameOver = false;

        clearItems();
        updateHud();

        centerTitle(
                "<EAF2FF>NEON RAIN",
                "<8EA4D9>Поймай зелёные. Избегай красных."
        );
        startButton.setVisible(true);
        infoText.setText("<7F93C8>← → / мышь — движение   Enter / клик — старт");
    }

    private void startGame() {
        running = true;
        gameOver = false;

        clearItems();

        score = 0;
        lives = 3;
        level = 1;
        spawnTimer = 0f;
        spawnInterval = 0.8f;
        baseFallSpeed = 150f;
        timeSeconds = 0f;

        player.setX((stage.getWidth() - player.getWidth()) / 2f);
        player.resetVisual();

        startButton.setVisible(false);
        centerText.setText("");
        centerSubText.setText("");

        updateHud();
        infoText.setText("<7F93C8>Зелёные +1, красные -1 жизнь");
    }

    private void endGame() {
        running = false;
        gameOver = true;

        centerTitle(
                "<FFB3B3>GAME OVER",
                "<D6E0FF>Счёт: " + score + "   Уровень: " + level
        );
        startButton.setLabel("RESTART");
        startButton.setVisible(true);
        infoText.setText("<7F93C8>Нажми Enter или кликни для рестарта");
    }

    private void centerTitle(String main, String sub) {
        centerText.setText(main);
        centerSubText.setText(sub);

        centerText.setX((stage.getWidth() - centerText.getWidth()) / 2f);
        centerText.setY(stage.getHeight() / 2f - 28);

        centerSubText.setX((stage.getWidth() - centerSubText.getWidth()) / 2f);
        centerSubText.setY(stage.getHeight() / 2f + 2);
    }

    private void updateHud() {
        hudText.setText("<B9C8FF>Score: " + score + "   Lives: " + lives + "   Level: " + level);
    }

    private void updatePlayer(float dt) {
        float speed = 360f;

        if (moveLeft) {
            player.moveX(-speed * dt);
        }
        if (moveRight) {
            player.moveX(speed * dt);
        }

        if (mouseTargetX >= 0f) {
            float target = mouseTargetX - player.getWidth() / 2f;
            float dx = target - player.getX();
            player.moveX(dx * Math.min(1f, dt * 10f));
        }

        if (player.getX() < 0) {
            player.setX(0);
        }
        float maxX = stage.getWidth() - player.getWidth();
        if (player.getX() > maxX) {
            player.setX(maxX);
        }

        player.updateVisual(dt);
    }

    private void updateSpawning(float dt) {
        spawnTimer += dt;
        if (spawnTimer >= spawnInterval) {
            spawnTimer = 0f;
            spawnItem();
        }
    }

    private void spawnItem() {
        boolean bad = random.nextFloat() < Math.min(0.18f + level * 0.02f, 0.42f);
        float size = 18f + random.nextInt(12);

        FallingItem item = new FallingItem(size, size, bad);
        item.setX(random.nextInt((int) Math.max(1, stage.getWidth() - (int) size)));
        item.setY(-size - 4);
        item.speed = baseFallSpeed + random.nextFloat() * 100f + level * 18f;
        item.wobble = 0.5f + random.nextFloat() * 1.6f;
        item.phase = random.nextFloat() * 10f;

        items.add(item);
        stage.addChild(item);
    }

    private void updateItems(float dt) {
        Iterator<FallingItem> it = items.iterator();

        while (it.hasNext()) {
            FallingItem item = it.next();

            item.phase += dt * item.wobble;
            item.moveY(item.speed * dt);
            item.moveX((float) Math.sin(item.phase * 3.0f) * 20f * dt);
            item.spin(dt);

            if (intersects(player, item)) {
                if (item.bad) {
                    lives--;
                    player.hitFlash();
                } else {
                    score++;
                    player.goodFlash();
                }

                item.removeFromParent();
                it.remove();

                if (lives <= 0) {
                    endGame();
                    return;
                }
                continue;
            }

            if (item.getY() > stage.getHeight() + 20) {
                if (!item.bad) {
                    // пропустил полезный
                    lives--;
                    player.hitFlash();
                    if (lives <= 0) {
                        item.removeFromParent();
                        it.remove();
                        endGame();
                        return;
                    }
                }

                item.removeFromParent();
                it.remove();
            }
        }
    }

    private void updateDifficulty() {
        int newLevel = 1 + score / 8;
        if (newLevel != level) {
            level = newLevel;
        }

        spawnInterval = Math.max(0.24f, 0.8f - (level - 1) * 0.045f);
        baseFallSpeed = 150f + (level - 1) * 24f;
    }

    private boolean intersects(Player a, FallingItem b) {
        return a.getX() < b.getX() + b.getWidth()
                && a.getX() + a.getWidth() > b.getX()
                && a.getY() < b.getY() + b.getHeight()
                && a.getY() + a.getHeight() > b.getY();
    }

    private void clearItems() {
        for (FallingItem item : items) {
            item.removeFromParent();
        }
        items.clear();
    }

    public static void main(String[] args) {
        ApplicationConfig config = new ApplicationConfig();
        config.set(ApplicationConfig.WIDTH, 960);
        config.set(ApplicationConfig.HEIGHT, 600);
        config.set(ApplicationConfig.TITLE, "D2D2 - Neon Rain");

        D2D2.init(new NeonRainGame(), config);
    }

    private static class Player extends BasicGroup {
        private final float width;
        private final float height;

        private final RectangleShape body;
        private final RectangleShape glow;
        private final RectangleShape core;
        private float flashTimer;

        public Player(float width, float height) {
            this.width = width;
            this.height = height;

            glow = new RectangleShape(width + 10, height + 10, Color.of(0x1C4DFF));
            glow.setAlpha(0.18f);
            glow.setPosition(-5, -5);

            body = new RectangleShape(width, height, Color.of(0x2A4BFF));
            core = new RectangleShape(width - 28, height - 8, Color.of(0xB9D2FF));
            core.setPosition(14, 4);

            addChild(glow);
            addChild(body);
            addChild(core);
        }

        @Override
        public float getWidth() {
            return width;
        }

        @Override
        public float getHeight() {
            return height;
        }

        public void resetVisual() {
            body.setColor(Color.of(0x2A4BFF));
            glow.setColor(Color.of(0x1C4DFF));
            glow.setAlpha(0.18f);
            flashTimer = 0f;
        }

        public void hitFlash() {
            body.setColor(Color.of(0xFF4E63));
            glow.setColor(Color.of(0xFF4E63));
            glow.setAlpha(0.35f);
            flashTimer = 0.18f;
        }

        public void goodFlash() {
            body.setColor(Color.of(0x22D36E));
            glow.setColor(Color.of(0x22D36E));
            glow.setAlpha(0.32f);
            flashTimer = 0.12f;
        }

        public void idlePulse(float t) {
            float a = 0.14f + (float) Math.sin(t * 2.5f) * 0.04f;
            glow.setAlpha(a);
        }

        public void updateVisual(float dt) {
            if (flashTimer > 0f) {
                flashTimer -= dt;
                if (flashTimer <= 0f) {
                    resetVisual();
                }
            }
        }
    }

    private static class FallingItem extends BasicGroup {
        private final float width;
        private final float height;
        private final RectangleShape body;
        private final RectangleShape shine;

        private final boolean bad;
        private float speed;
        private float wobble;
        private float phase;
        private float rotationValue;

        public FallingItem(float width, float height, boolean bad) {
            this.width = width;
            this.height = height;
            this.bad = bad;

            int mainColor = bad ? 0xFF4E63 : 0x36E37A;
            int shineColor = bad ? 0xFFD2D7 : 0xDFFFF0;

            body = new RectangleShape(width, height, Color.of(mainColor));
            shine = new RectangleShape(width * 0.35f, height * 0.35f, Color.of(shineColor));
            shine.setPosition(width * 0.18f, height * 0.18f);

            addChild(body);
            addChild(shine);
        }

        @Override
        public float getWidth() {
            return width;
        }

        @Override
        public float getHeight() {
            return height;
        }

        public void spin(float dt) {
            rotationValue += (bad ? -120f : 90f) * dt;
            setRotation(rotationValue);
        }
    }

    private static class OverlayButton extends InteractiveGroup {
        private final float width;
        private final float height;

        private final RectangleShape bg;
        private final BorderedRectangleShape border;
        private final BitmapText label;

        public OverlayButton(float width, float height, String text) {
            super(width, height);
            this.width = width;
            this.height = height;

            bg = new RectangleShape(width, height, Color.of(0x202B57));
            border = new BorderedRectangleShape(width, height, null, Color.of(0x6D89FF));
            border.setBorderWidth(2f);

            label = BitmapText.builder()
                    .text("<EAF2FF>" + text)
                    .autoSize(true)
                    .multicolor(true)
                    .build();

            addChild(bg);
            addChild(border);
            addChild(label);

            centerLabel();
        }

        @Override
        public float getWidth() {
            return width;
        }

        @Override
        public float getHeight() {
            return height;
        }

        public void setHovered(boolean hovered) {
            bg.setColor(hovered ? Color.of(0x31469A) : Color.of(0x202B57));
        }

        public void setLabel(String text) {
            label.setText("<EAF2FF>" + text);
            centerLabel();
        }

        private void centerLabel() {
            label.setPosition(
                    (width - label.getWidth()) / 2f,
                    (height - label.getHeight()) / 2f - 1f
            );
        }
    }
}