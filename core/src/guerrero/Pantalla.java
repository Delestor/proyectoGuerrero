package guerrero;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.badlogic.gdx.InputProcessor;

import bemen3.cat.ActGuerreros;

/**
 * Created by Albert on 25/04/2017.
 */

public class Pantalla implements Screen, InputProcessor {

    private ActGuerreros juego;
    private TextureAtlas atlas;
    private TextureAtlas atlasRobot;
    public static boolean alreadyDestroyed = false;

    private OrthographicCamera gamecam;
    private Viewport gamePort;
    //private Hud hud;

    //Variables mapa
    private TmxMapLoader maploader;
    private TiledMap map;
    private OrthogonalTiledMapRenderer renderer;

    //Mundo con Box2d
    private World world;
    private Box2DDebugRenderer b2dr;
    private B2WorldCreator creador;

    //Sprites
    private Guerrero player;


    private Music music=  Gdx.audio.newMusic(Gdx.files.internal("sonic.mp3"));

    public Pantalla(ActGuerreros juego) {
        //atlas = new TextureAtlas("mario_and_enemies.pack");
        music.play();
        atlasRobot = new TextureAtlas("robot.txt");
        this.juego = juego;
        gamecam = new OrthographicCamera();
        gamePort = new FitViewport(ActGuerreros.V_WIDTH / ActGuerreros.PPM, ActGuerreros.V_HEIGHT / ActGuerreros.PPM, gamecam);

        //hud = new Hud(juego.batch);
        //Cargar el mapa
        maploader = new TmxMapLoader();
        map = maploader.load("untitled.tmx");
        renderer = new OrthogonalTiledMapRenderer(map, 1 / ActGuerreros.PPM);
        //Centrar la camara
        gamecam.position.set(gamePort.getWorldWidth() / 2, gamePort.getWorldHeight() / 2, 0);

        world = new World(new Vector2(0, -10), true);

        b2dr = new Box2DDebugRenderer();

        creador = new B2WorldCreator(this);

        player = new Guerrero(this);
        world.setContactListener(new WorldContactListener());
    }
    public TextureAtlas getAtlas(){
        return atlas;
    }
    public void handleInput(float dt){
        if(player.currentState != Guerrero.State.DEAD) {
            if (Gdx.input.isKeyJustPressed(Input.Keys.UP))
                player.saltar();
            if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) && player.b2body.getLinearVelocity().x <= 2)
                player.b2body.applyLinearImpulse(new Vector2(0.1f, 0), player.b2body.getWorldCenter(), true);
            if (Gdx.input.isKeyPressed(Input.Keys.LEFT) && player.b2body.getLinearVelocity().x >= -2)
                player.b2body.applyLinearImpulse(new Vector2(-0.1f, 0), player.b2body.getWorldCenter(), true);
            if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE))
                player.fire();
        }

    }

    @Override
    public boolean keyDown(int keycode) {
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    public boolean touchDown (int x, int y, int pointer, int button){
		/*
		System.out.println("Pulsado con el dedo");
		System.out.println("Pulsado en X = "+x);
		System.out.println("Pulsado en Y = "+y);
		System.out.println("Pos x = "+castillo.getJabato().getPosicion().x);
		System.out.println("Pos y = "+castillo.getJabato().getPosicion().y);*/
        //prepararSaltoJabato();
        System.out.println("Touch Down");
        if(x<Gdx.graphics.getWidth()/2 && y< Gdx.graphics.getHeight()/3){
            player.saltar();
            System.out.println("Salto");
        }else {
            if (x < Gdx.graphics.getWidth() / 2 && y > Gdx.graphics.getHeight() / 2){
                player.b2body.applyLinearImpulse(new Vector2(-0.1f, 0), player.b2body.getWorldCenter(), true);
                System.out.println("Izquierda");
            }

            if (x > Gdx.graphics.getWidth() / 2 && y > Gdx.graphics.getHeight() / 2){
                player.b2body.applyLinearImpulse(new Vector2(0.1f, 0), player.b2body.getWorldCenter(), true);
                System.out.println("Derecha");
            }

        }
        //if (x-ancho<castillo.getJabato().getPosicion().x && y-alto> castillo.getJabato().getPosicion().y) controlador.izquierdaPulsada();
        //if (x-ancho>castillo.getJabato().getPosicion().x && y-alto> castillo.getJabato().getPosicion().y) controlador.derechaPulsada();
        return true;
    }

    public boolean touchUp (int x, int y, int pointer, int button){
        //System.out.println("Dejando de pulsar con el dedo");
        //if (x<pintor.ancho/2 && y> pintor.alto/2) controlador.izquierdaLiberada();
        //if (x>pintor.ancho/2 && y> pintor.alto/2) controlador.derechaLiberada();
        //controlador.izquierdaLiberada();
        //controlador.derechaLiberada();
        return true;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }

    @Override
    public void show() {

    }
    public void update(float dt){

        handleInput(dt);
        world.step(1 / 60f, 6, 2);

        player.update(dt);
        for(Enemigo enemy : creador.getEnemies()) {
            enemy.update(dt);
            if(enemy.getX() < player.getX() + 224 / ActGuerreros.PPM) {
                enemy.b2body.setActive(true);
            }
        }

        /*for(Item item : items)
            item.update(dt);*/

        //hud.update(dt);
        if(player.currentState != Guerrero.State.DEAD) {
            gamecam.position.x = player.b2body.getPosition().x;
        }


        gamecam.update();
        renderer.setView(gamecam);

    }
    @Override
    public void render(float delta) {
        update(delta);
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        renderer.render();
        b2dr.render(world, gamecam.combined);

        juego.batch.setProjectionMatrix(gamecam.combined);
        juego.batch.begin();
        player.draw(juego.batch);
        /*for (Enemigo enemy : creador.getEnemies())
            enemy.draw(juego.batch);*/
        /*for (Item item : items)
            item.draw(juego.batch);*/
        juego.batch.end();

        //juego.batch.setProjectionMatrix(hud.stage.getCamera().combined);
        //hud.stage.draw();

        if(gameOver()){
            juego.setScreen(new GameOver(juego));
            dispose();
        }
    }
    public boolean gameOver(){
        if(player.currentState == Guerrero.State.DEAD && player.getStateTimer() > 3){
            return true;
        }
        return false;
    }
    @Override
    public void resize(int width, int height) {
        gamePort.update(width,height);
    }
    public TiledMap getMap(){
        return map;
    }
    public World getWorld(){
        return world;
    }
    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        map.dispose();
        renderer.dispose();
        world.dispose();
        b2dr.dispose();
        //hud.dispose();
    }
    /*public Hud getHud(){ return hud; }*/
    public TextureAtlas getAtlasRobot(){
        return this.atlasRobot;
    }
}
