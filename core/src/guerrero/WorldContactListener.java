package guerrero;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;

import bemen3.cat.ActGuerreros;

/**
 * Created by Ferran on 19/04/2017.
 */
public class WorldContactListener implements ContactListener {
    @Override
    public void beginContact(Contact contact) {
        Fixture fixA = contact.getFixtureA();
        Fixture fixB = contact.getFixtureB();

        int cDef = fixA.getFilterData().categoryBits | fixB.getFilterData().categoryBits;

        switch (cDef){
            case ActGuerreros.PLAYER_HEAD_BIT:
                if(fixA.getFilterData().categoryBits == ActGuerreros.PLAYER_HEAD_BIT)
                    ((InteractiveTileObject) fixB.getUserData()).onHeadHit((Guerrero) fixA.getUserData());
                else
                    ((InteractiveTileObject) fixA.getUserData()).onHeadHit((Guerrero) fixB.getUserData());
                break;
            case ActGuerreros.ENEMY_HEAD_BIT | ActGuerreros.PLAYER_BIT:
                if(fixA.getFilterData().categoryBits == ActGuerreros.ENEMY_HEAD_BIT)
                    ((Enemigo)fixA.getUserData()).hitOnHead((Guerrero) fixB.getUserData());
                else
                    ((Enemigo)fixB.getUserData()).hitOnHead((Guerrero) fixA.getUserData());
                break;
            case ActGuerreros.ENEMY_BIT | ActGuerreros.OBJECT_BIT:
                if(fixA.getFilterData().categoryBits == ActGuerreros.ENEMY_BIT)
                    ((Enemigo)fixA.getUserData()).reverseVelocity(true, false);
                else
                    ((Enemigo)fixB.getUserData()).reverseVelocity(true, false);
                break;
            case ActGuerreros.PLAYER_BIT | ActGuerreros.ENEMY_BIT:
                if(fixA.getFilterData().categoryBits == ActGuerreros.PLAYER_BIT)
                    ((Guerrero) fixA.getUserData()).hit((Enemigo)fixB.getUserData());
                else
                    ((Guerrero) fixB.getUserData()).hit((Enemigo)fixA.getUserData());
                break;
            case ActGuerreros.ENEMY_BIT | ActGuerreros.ENEMY_BIT:
                ((Enemigo)fixA.getUserData()).hitByEnemy((Enemigo)fixB.getUserData());
                ((Enemigo)fixB.getUserData()).hitByEnemy((Enemigo)fixA.getUserData());
                break;
            case ActGuerreros.DISPARO_BIT | ActGuerreros.OBJECT_BIT:
                if(fixA.getFilterData().categoryBits == ActGuerreros.DISPARO_BIT)
                    ((Bullet)fixA.getUserData()).setToDestroy();
                else
                    ((Bullet)fixB.getUserData()).setToDestroy();
                break;
        }
    }

    @Override
    public void endContact(Contact contact) {
    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {
    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }
}
