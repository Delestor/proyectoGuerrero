package guerrero;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;

import bemen3.cat.ActGuerreros;

/**
 * Created by Albert on 25/04/2017.
 */
public class Bullet extends Sprite {

    Pantalla pantalla;
    World world;
    Array<TextureRegion> frames;
    Animation fireAnimation;
    float stateTime;
    boolean destroyed;
    boolean setToDestroy;
    boolean fireRight;

    Body b2body;
    public Bullet(Pantalla pantalla, float x, float y, boolean fireRight){
        this.fireRight = fireRight;
        this.pantalla = pantalla;
        this.world = pantalla.getWorld();
        frames = new Array<TextureRegion>();

        TextureAtlas atlasKunai = new TextureAtlas("kunai.txt");

        frames.add(new TextureRegion(atlasKunai.findRegion("Kunai")));

        fireAnimation = new Animation(0.2f, frames);
        setRegion((TextureRegion) fireAnimation.getKeyFrame(0));
        setBounds(x, y, 12 / ActGuerreros.PPM, 12 / ActGuerreros.PPM);
        defineFireBall();
    }

    public void defineFireBall(){
        BodyDef bdef = new BodyDef();
        bdef.position.set(fireRight ? getX() + 11.02f /ActGuerreros.PPM : getX() - 11.05f /ActGuerreros.PPM, getY());
        bdef.type = BodyDef.BodyType.DynamicBody;
        if(!world.isLocked())
        b2body = world.createBody(bdef);

        FixtureDef fdef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(3 / ActGuerreros.PPM);
        fdef.filter.categoryBits = ActGuerreros.DISPARO_BIT;
        fdef.filter.maskBits = ActGuerreros.GROUND_BIT |
                ActGuerreros.ENEMY_BIT |
                ActGuerreros.OBJECT_BIT;

        fdef.shape = shape;
        fdef.restitution = 0;
        fdef.friction = 1;
        b2body.createFixture(fdef).setUserData(this);
        b2body.setLinearVelocity(new Vector2(fireRight ? 4 : -4 , 3));

    }

    public void update(float dt){
        stateTime += dt;
        setRegion((TextureRegion) fireAnimation.getKeyFrame(stateTime, false));
        setPosition(b2body.getPosition().x - getWidth() / 2.5f, b2body.getPosition().y - getHeight() / 2.5f);
        if((stateTime == 1 || setToDestroy) && !destroyed) {
            world.destroyBody(b2body);
            destroyed = true;
        }
        if(b2body.getLinearVelocity().y >  -4)
            b2body.setLinearVelocity(b2body.getLinearVelocity().x, 0);
            //setToDestroy();
        if((fireRight && b2body.getLinearVelocity().x < 4) || (!fireRight && b2body.getLinearVelocity().x > -4))
            setToDestroy();
    }

    public void setToDestroy(){
        setToDestroy = true;
    }

    public boolean isDestroyed(){
        return destroyed;
    }


}
