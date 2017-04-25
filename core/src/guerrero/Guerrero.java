package guerrero;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.EdgeShape;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.graphics.Texture;

import bemen3.cat.ActGuerreros;

/**
 * Created by Albert on 25/04/2017.
 */

public class Guerrero extends Sprite{
    public enum State { FALLING, JUMPING, STANDING, RUNNING, DEAD };
    public State currentState;
    public State previousState;

    public World world;
    public Body b2body;

    private Animation guerreroStand;
    private Animation guerreroRun;
    private Animation guerreroJump;
    private TextureRegion guerreroDead;

    private float stateTimer;
    private boolean runningRight;

    private boolean timeToRedefineGuerrero;
    private boolean guerreroIsDead;

    private Pantalla pantalla;
    private Array<FireBall> fireballs;

    public Guerrero(Pantalla pantalla) {
        this.pantalla = pantalla;
        this.world = pantalla.getWorld();

        currentState = State.STANDING;
        previousState = State.STANDING;
        stateTimer = 0;
        runningRight = true;

        Array<TextureRegion> frames = new Array<TextureRegion>();
        //new TextureRegion(pantalla.getAtlas().findRegion("pie"), i * 65, 0, 65, 65)
        //Animacion estar de pie

        String raizAtlasGuerrero = "guerrero/";

        TextureAtlas atlasIdle = new TextureAtlas("idle.txt");

        for (int i = 0; i <= 9; i++){
            frames.add(new TextureRegion(atlasIdle.findRegion("Idle"+i)));
        }
        guerreroStand = new Animation(0.1f, frames);
        frames.clear();

        TextureAtlas atlasRun = new TextureAtlas("run.txt");
        //Animacion Correr
        for (int i = 1; i <= 10; i++) {
            frames.add(new TextureRegion(atlasRun.findRegion("Run_"+i)));
         }
        guerreroRun = new Animation(0.1f, frames);
        frames.clear();
        //Animacion Saltar
        TextureAtlas atlasJump = new TextureAtlas("jump.txt");
        for (int i = 1; i <= 10; i++) {
            frames.add(new TextureRegion(atlasJump.findRegion("Jump_"+i)));
        }
        guerreroJump = new Animation(0.1f, frames);
        frames.clear();

        guerreroDead = new TextureRegion(atlasRun.findRegion("Run_1"));

        definePlayer();
        setBounds(0, 0, 24 / ActGuerreros.PPM, 24 / ActGuerreros.PPM);
        //setRegion(guerreroStand);
        fireballs = new Array<FireBall>();

    }
    public TextureRegion getFrame(float dt){
        currentState = getState();
        TextureRegion region;
        switch(currentState){
            case DEAD:
                region = guerreroDead;
                break;
            case JUMPING:
                region = (TextureRegion) guerreroJump.getKeyFrame(stateTimer,false);
                setBounds(getX(), getY(), 0.32f, 0.32f);
                break;
            case RUNNING:
                region = (TextureRegion) guerreroRun.getKeyFrame(stateTimer, true);
                setBounds(getX(), getY(), 28 / ActGuerreros.PPM, 28 / ActGuerreros.PPM);
                break;
            case FALLING:
            case STANDING:
            default:
                region = (TextureRegion) guerreroStand.getKeyFrame(stateTimer,  true);
                setBounds(getX(), getY(), 24 / ActGuerreros.PPM, 24 / ActGuerreros.PPM);
                break;
        }
        if((b2body.getLinearVelocity().x < 0 || !runningRight) && !region.isFlipX()){
            region.flip(true, false);
            runningRight = false;
        }
        else if((b2body.getLinearVelocity().x > 0 || runningRight) && region.isFlipX()){
            region.flip(true, false);
            runningRight = true;
        }
        stateTimer = currentState == previousState ? stateTimer + dt : 0;
        previousState = currentState;
        return region;

    }
    public State getState(){
        if(guerreroIsDead)
            return State.DEAD;
        else if((b2body.getLinearVelocity().y > 0 && currentState == State.JUMPING) || (b2body.getLinearVelocity().y < 0 && previousState == State.JUMPING))
            return State.JUMPING;
        else if(b2body.getLinearVelocity().y < 0)
            return State.FALLING;
        else if(b2body.getLinearVelocity().x != 0)
            return State.RUNNING;
        else
            return State.STANDING;
    }

    public void update(float dt){
        /*
        if (pantalla.getHud().isTimeUp() && !isDead()) {
            die();
        }*/
        setPosition(b2body.getPosition().x - getWidth() / 2, b2body.getPosition().y - getHeight() / 2);
        setRegion(getFrame(dt));
        if(timeToRedefineGuerrero){
            redefinePlayer();
        }
        for(FireBall  ball : fireballs) {
            ball.update(dt);
            if(ball.isDestroyed())
                fireballs.removeValue(ball, true);
        }

    }
    public float getStateTimer(){
        return stateTimer;
    }
    public void saltar(){
        if ( currentState != State.JUMPING ) {
            b2body.applyLinearImpulse(new Vector2(0, 3.6f), b2body.getWorldCenter(), true);
            currentState = State.JUMPING;
        }
    }
    public void die() {
        if (!isDead()) {
            guerreroIsDead = true;
            Filter filter = new Filter();
            filter.maskBits = ActGuerreros.NOTHING_BIT;

            for (Fixture fixture : b2body.getFixtureList()) {
                fixture.setFilterData(filter);
            }

            b2body.applyLinearImpulse(new Vector2(0, 4f), b2body.getWorldCenter(), true);
        }
    }
    public void hit(Enemigo enemy){
        die();
    }
    public void redefinePlayer(){
        Vector2 position = b2body.getPosition();
        world.destroyBody(b2body);

        BodyDef bdef = new BodyDef();
        bdef.position.set(position);
        bdef.type = BodyDef.BodyType.DynamicBody;
        b2body = world.createBody(bdef);

        FixtureDef fdef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(10 / ActGuerreros.PPM);
        fdef.filter.categoryBits = ActGuerreros.PLAYER_BIT;
        fdef.filter.maskBits = ActGuerreros.GROUND_BIT |
                ActGuerreros.BRICK_BIT |
                ActGuerreros.ENEMY_BIT |
                ActGuerreros.OBJECT_BIT |
                ActGuerreros.ENEMY_HEAD_BIT;

        fdef.shape = shape;
        b2body.createFixture(fdef).setUserData(this);

        EdgeShape head = new EdgeShape();
        head.set(new Vector2(-2 / ActGuerreros.PPM, 10 / ActGuerreros.PPM), new Vector2(2 / ActGuerreros.PPM, 10 / ActGuerreros.PPM));
        fdef.filter.categoryBits = ActGuerreros.PLAYER_HEAD_BIT;
        fdef.shape = head;
        fdef.isSensor = true;

        b2body.createFixture(fdef).setUserData(this);

        timeToRedefineGuerrero = false;

    }
    public void definePlayer(){
        BodyDef bdef = new BodyDef();
        bdef.position.set(32 / ActGuerreros.PPM, 32 / ActGuerreros.PPM);
        bdef.type = BodyDef.BodyType.DynamicBody;
        b2body = world.createBody(bdef);

        FixtureDef fdef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(10 / ActGuerreros.PPM);
        fdef.filter.categoryBits = ActGuerreros.PLAYER_BIT;
        fdef.filter.maskBits = ActGuerreros.GROUND_BIT |
                ActGuerreros.BRICK_BIT |
                ActGuerreros.ENEMY_BIT |
                ActGuerreros.OBJECT_BIT |
                ActGuerreros.ENEMY_HEAD_BIT;

        fdef.shape = shape;
        b2body.createFixture(fdef).setUserData(this);

        EdgeShape head = new EdgeShape();
        head.set(new Vector2(-2 / ActGuerreros.PPM, 10 / ActGuerreros.PPM), new Vector2(2 / ActGuerreros.PPM, 10 / ActGuerreros.PPM));
        fdef.filter.categoryBits = ActGuerreros.PLAYER_HEAD_BIT;
        fdef.shape = head;
        fdef.isSensor = true;

        b2body.createFixture(fdef).setUserData(this);
    }
    public void fire(){
        fireballs.add(new FireBall(pantalla, b2body.getPosition().x, b2body.getPosition().y, runningRight ? true : false));
    }
    public boolean isDead(){
        return guerreroIsDead;
    }

    public void draw(Batch batch){
        super.draw(batch);
        for(FireBall ball : fireballs) {
            ball.draw(batch);
        }
    }


}
