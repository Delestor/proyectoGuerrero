package guerrero;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;

import bemen3.cat.ActGuerreros;

/**
 * Created by Albert on 25/04/2017.
 */
public class B2WorldCreator {
    //private Array<Goomba> goombas;

    public B2WorldCreator(Pantalla pantalla){
        World world = pantalla.getWorld();
        TiledMap map = pantalla.getMap();

        BodyDef bdef = new BodyDef();
        PolygonShape shape = new PolygonShape();
        FixtureDef fdef = new FixtureDef();
        Body body;

        for(MapObject object : map.getLayers().get("suelo").getObjects().getByType(RectangleMapObject.class)){
            Rectangle rect = ((RectangleMapObject) object).getRectangle();

            bdef.type = BodyDef.BodyType.StaticBody;
            bdef.position.set((rect.getX() + rect.getWidth() / 2) / ActGuerreros.PPM, (rect.getY() + rect.getHeight() / 2) / ActGuerreros.PPM);

            body = world.createBody(bdef);

            shape.setAsBox(rect.getWidth() / 2 / ActGuerreros.PPM, rect.getHeight() / 2 / ActGuerreros.PPM);
            fdef.shape = shape;
            body.createFixture(fdef);
        }

        for(MapObject object : map.getLayers().get(1).getObjects().getByType(RectangleMapObject.class)){
            Rectangle rect = ((RectangleMapObject) object).getRectangle();

            bdef.type = BodyDef.BodyType.StaticBody;
            bdef.position.set((rect.getX() + rect.getWidth() / 2) / ActGuerreros.PPM, (rect.getY() + rect.getHeight() / 2) / ActGuerreros.PPM);

            body = world.createBody(bdef);

            shape.setAsBox(rect.getWidth() / 2 / ActGuerreros.PPM, rect.getHeight() / 2 / ActGuerreros.PPM);
            fdef.shape = shape;
            fdef.filter.categoryBits = ActGuerreros.OBJECT_BIT;
            body.createFixture(fdef);
        }
        /*goombas = new Array<Goomba>();
        for(MapObject object : map.getLayers().get(6).getObjects().getByType(RectangleMapObject.class)){
            Rectangle rect = ((RectangleMapObject) object).getRectangle();
            goombas.add(new Goomba(pantalla, rect.getX() / ActGuerreros.PPM, rect.getY() / ActGuerreros.PPM));
        }*/
    }
    /*public Array<Goomba> getGoombas() {
        return goombas;
    }*/

    public Array<Enemigo> getEnemies(){
        Array<Enemigo> enemies = new Array<Enemigo>();
        //enemies.addAll(goombas);
        return enemies;
    }
}
