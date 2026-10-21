import java.util.ArrayList;

public class Level {
    private ArrayList<Object> objects;
    private ArrayList<Solid> solids;
    private ArrayList<Obstacle> obstacles;

    public Level() {
        objects=new ArrayList<Object>();
        solids=new ArrayList<Solid>();
        obstacles=new ArrayList<Obstacle>();
    }

    public void addObject(Object obj) {
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

    public void removeObject(Object obj) {
        objects.remove(obj);
        solids.remove(obj);
        obstacles.remove(obj);
    }

    public ArrayList<Object> getObjects() {
        return objects;
    }

    public ArrayList<Solid> getSolids() {
        return solids;
    }

    public ArrayList<Obstacle> getObstacles() {
        return obstacles;
    }
}
