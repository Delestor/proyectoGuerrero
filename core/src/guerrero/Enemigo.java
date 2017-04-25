package guerrero;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;

/**
 * Created by Albert on 25/04/2017.
 */

public abstract class Enemigo extends Sprite {
    protected World world;
    protected Pantalla pantalla;
    public Body b2body;
    public Vector2 velocity;

    public Enemigo(Pantalla pantalla, float x, float y){
        this.world = pantalla.getWorld();
        this.pantalla = pantalla;
        setPosition(x, y);
        defineEnemy();
        velocity = new Vector2(-1, -2);
        b2body.setActive(false);
    }

    protected abstract void defineEnemy();
    public abstract void update(float dt);
    public abstract void hitOnHead(Guerrero player);
    public abstract void hitByEnemy(Enemigo enemigo);

    public void reverseVelocity(boolean x, boolean y){
        if(x){
            velocity.x = -velocity.x;
        }
        if(y) {
            velocity.y = -velocity.y;
        }
    }
}
