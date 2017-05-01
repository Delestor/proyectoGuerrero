package guerrero;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.utils.Array;

import bemen3.cat.ActGuerreros;

/**
 * Created by albert on 01/05/2017.
 */

public class Robot extends Enemigo{
    private float stateTime;
    private Animation walkAnimation;
    private Array<TextureRegion> frames;
    private boolean setToDestroy;
    private boolean destroyed;
    float angle;

    public Robot(Pantalla pantalla, float x, float y) {
        super(pantalla, x, y);
        frames = new Array<TextureRegion>();
        frames.add(new TextureRegion(pantalla.getAtlasRobot().findRegion("robot")));
        walkAnimation = new Animation(0.4f, frames);
        stateTime = 0;
        setBounds(getX(), getY(), 50 / ActGuerreros.PPM, 50 / ActGuerreros.PPM);
        setToDestroy = false;
        destroyed = false;
        angle = 0;
    }

    @Override
    protected void defineEnemy() {
        BodyDef bdef = new BodyDef();
        bdef.position.set(getX(), getY());
        bdef.type = BodyDef.BodyType.DynamicBody;
        b2body = world.createBody(bdef);

        FixtureDef fdef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(6 / ActGuerreros.PPM);
        fdef.filter.categoryBits = ActGuerreros.ENEMY_BIT;
        fdef.filter.maskBits = ActGuerreros.GROUND_BIT |
                ActGuerreros.BRICK_BIT |
                ActGuerreros.ENEMY_BIT |
                ActGuerreros.OBJECT_BIT |
                ActGuerreros.PLAYER_BIT;

        fdef.shape = shape;
        b2body.createFixture(fdef).setUserData(this);

        PolygonShape head = new PolygonShape();
        Vector2[] vertice = new Vector2[4];
        vertice[0] = new Vector2(-5, 8).scl(1 / ActGuerreros.PPM);
        vertice[1] = new Vector2(5, 8).scl(1 / ActGuerreros.PPM);
        vertice[2] = new Vector2(-3, 3).scl(1 / ActGuerreros.PPM);
        vertice[3] = new Vector2(3, 3).scl(1 / ActGuerreros.PPM);
        head.set(vertice);

        fdef.shape = head;
        fdef.restitution = 0.5f;
        fdef.filter.categoryBits = ActGuerreros.ENEMY_HEAD_BIT;
        b2body.createFixture(fdef).setUserData(this);
    }

    @Override
    public void update(float dt) {
        stateTime += dt;
        if(setToDestroy && !destroyed){
            world.destroyBody(b2body);
            destroyed = true;
            setRegion(new TextureRegion(pantalla.getAtlasRobot().findRegion("robot")));
            stateTime = 0;
        }
        else if(!destroyed) {
            b2body.setLinearVelocity(velocity);
            setPosition(b2body.getPosition().x - getWidth() / 2, b2body.getPosition().y - getHeight() / 2);
            setRegion((TextureRegion) walkAnimation.getKeyFrame(stateTime, true));
        }
    }

    @Override
    public void hitOnHead(Guerrero player) {
        setToDestroy = true;
    }

    @Override
    public void hitByEnemy(Enemigo enemigo) {
        reverseVelocity(true, false);
    }

    public void draw(Batch batch){
        if(!destroyed || stateTime < 1)
            super.draw(batch);
    }
}
