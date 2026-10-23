import java.util.ArrayList;

public class Level {
    private ArrayList<GameObject> objects;
    private ArrayList<Solid> solids;
    private ArrayList<Obstacle> obstacles;

    public Level() {
        objects=new ArrayList<GameObject>();
        solids=new ArrayList<Solid>();
        obstacles=new ArrayList<Obstacle>();
    }

    public void addObject(GameObject obj) {
        objects.add(obj);
    }

    public void addSolid(Solid sol) {
        solids.add(sol);
        objects.add(sol);
    }

    public void addObstacle(Obstacle obs) {
        obstacles.add(obs);
        objects.add(obs);
    }

    public void removeObject(GameObject obj) {
        objects.remove(obj);
        solids.remove(obj);
        obstacles.remove(obj);
    }

    public ArrayList<GameObject> getObjects() {
        return objects;
    }

    public ArrayList<Solid> getSolids() {
        return solids;
    }

    public ArrayList<Obstacle> getObstacles() {
        return obstacles;
    }
}
