import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Auto-generated code below aims at helping you parse
 * the standard input according to the problem statement.
 **/
class Player {

    public static class Point {
        private final int x;
        private final int y;

        public Point(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public double distance(Point other) {
            return Math.sqrt(Math.pow(other.x - x, 2) + Math.pow(other.y - y, 2));
        }

        public static double distance(Point p1, Point p2) {
            return p1.distance(p2);
        }

        public static Point rotation(Point origin, Point target, int angle) {
            return new Point(
                    (int) ((target.x - origin.x) * Math.cos(Math.toRadians(angle)) + (target.y - origin.y) * Math.sin(Math.toRadians(angle)) + origin.x),
                    (int) ((target.x - origin.x) * -Math.sin(Math.toRadians(angle)) + (target.y - origin.y) * Math.cos(Math.toRadians(angle)) + origin.y)
            );
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Point point = (Point) o;
            return x == point.x && y == point.y;
        }

        @Override
        public int hashCode() {
            return Objects.hash(x, y);
        }

        @Override
        public String toString() {
            return String.format("(%d,%d)", x, y);
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }
    }

    public static class Vector extends Point {

        public Vector(int x, int y) {
            super(x, y);
        }

        public int scalar(Vector other) {
            return this.getX() * other.getX() + this.getY() * other.getY();
        }

        public int angleWith(Vector other) {
            int angle = (int) Math.toDegrees(Math.abs(Math.atan2(other.getY(), other.getX()) - Math.atan2(this.getY(), this.getX())));
            return angle > 180 ? 360 - angle : angle;   // Keeps the angle between -180 and 180, like given checkpoint angle
        }
    }

    public static class RaceMap {
        private final List<Point> checkpoints = new ArrayList<>();
        private final Map<Point,Double> paths = new HashMap<>();
        private Point longestPath;
        private boolean mapFullyExplored = false;
        private int lap = 1;
        private Point currentTarget;
        private int tickCount = 1;

        public void nextCheckpoint(Point nextCheckpoint) {

            if (!mapFullyExplored) {
                addCheckpointIfNotExist(nextCheckpoint);
            }

            if (mapFullyExplored && checkpoints.get(0).equals(nextCheckpoint) && !currentTarget.equals(nextCheckpoint)) {
                lap++;
            }
            if (currentTarget == null || !currentTarget.equals(nextCheckpoint)) {
                currentTarget = nextCheckpoint;
            }
        }

        private void addCheckpointIfNotExist(Point checkpoint) {
            if (!checkpoints.contains(checkpoint)) {
                checkpoints.add(checkpoint);
            } else if (checkpoints.size() > 1 && checkpoint.equals(checkpoints.get(0))) {
                mapFullyExplored = true;
                this.findLongestPath();
            }
        }

        private void findLongestPath () {
            for (int i = 0; i < checkpoints.size(); i++) {
                if (i == checkpoints.size() - 1) {
                    paths.put(checkpoints.get(0), checkpoints.get(i).distance(checkpoints.get(0)));
                } else {
                    paths.put(checkpoints.get(i + 1), checkpoints.get(i).distance(checkpoints.get(i + 1)));
                }
            }
            longestPath = Collections.max(paths.entrySet(), Map.Entry.comparingByValue()).getKey();
        }

        public boolean isMapFullyExplored() {
            return mapFullyExplored;
        }

        public boolean isLongestPath() {
            // only provide longestPath if the map is fully explored
            return mapFullyExplored && currentTarget.equals(longestPath);
        }

        public boolean isLastPath(Point target) {
            return lap == 3 && checkpoints.get(checkpoints.size() - 1).equals(target);
        }

        public double getCurrentDistance() {
            return paths.get(currentTarget);
        }

        public Point getAfterNext() {
            int currentIndex = checkpoints.indexOf(currentTarget);

            return currentIndex == checkpoints.size() - 1 ? checkpoints.get(0) : checkpoints.get(currentIndex + 1);
        }

        public void tick() {
            tickCount++;
        }

        public boolean isFirstTick() {
            return tickCount == 1;
        }

        public String printStatus() {
            return String.format(
                    "raceMap(lap: %d, checkpoints count: %d, fully explored: %b, long path: %d, tick: %d)",
                    lap,
                    checkpoints.size(),
                    mapFullyExplored,
                    checkpoints.indexOf(longestPath),
                    tickCount);
        }
    }

    public static class Pod {
        private final List<Point> path = new ArrayList<>();
        private Point currentPosition;
        private Vector currentSpeedVector;
        private Point nextPoint;
        private Vector currentSpeed;
        private int currentAngle;
        private int nextCheckpointId;

        public void update(Point position, Vector speed, int angle, int checkpointId) {
            addPosition(position);
            currentSpeed = speed;
            currentAngle = angle;
            nextCheckpointId = checkpointId;
        }

        public void addPosition(Point currentPosition) {
            path.add(currentPosition);
            this.currentPosition = currentPosition;
            this.currentSpeedVector = currentSpeedVector();
            this.nextPoint = nextPoint();
        }

        public int currentSpeed() {
            if (path.size() < 2)
                return 0;
            return (int) path.get(path.size() - 1).distance(path.get(path.size() - 2));
        }

        public Vector currentSpeedVector() {
            if (path.size() < 2)
                return new Vector(0,0);

            Point p0 = path.get(path.size() - 1);
            Point p_1 = path.get(path.size() - 2);
            return new Vector(p0.x - p_1.x, p0.y - p_1.y);
        }

        public Point nextPoint() {
            if (currentPosition != null && currentSpeedVector != null) {
                return new Point(currentPosition.x + currentSpeedVector.getX(), currentPosition.y + currentSpeedVector.getY());
            } else {
                return new Point(-1, -1);
            }
        }

        public Vector getCurrentSpeedVector() {
            return this.currentSpeedVector;
        }

        public Point getNextPoint() {
            return this.nextPoint;
        }

        public Point getCurrentPosition() {
            return currentPosition;
        }
    }

    public static class OwnPod extends Pod {
        private final RaceMap raceMap;
        private final List<Pod> opponents;
        private List<String> optimisationDebug = new ArrayList<>();

        public OwnPod(RaceMap raceMap, List<Pod> opponents) {
            this.raceMap = raceMap;
            this.opponents = opponents;
        }

        public boolean canCollideWithOpponent(Point nextCheckpoint, int nextCheckpointDist) {
            for (Pod opponent : opponents) {
                if (getCurrentPosition().distance(opponent.getCurrentPosition()) < 840
                        && currentSpeed() > 400
                        && opponent.currentSpeed() > 400
                        && currentSpeedVector().scalar(opponent.currentSpeedVector()) >= 0
                        && nextCheckpointDist < opponent.getCurrentPosition().distance(nextCheckpoint)
                ) {
                    return true;
                }
            }
            return false;
        }

        public String computeCommand() {
            optimisationDebug = new ArrayList<>();
            String thrust = "100";
            Point target = nextCheckpoint;

            // Coordinate drift
            int theta = 0;
            if (!raceMap.isFirstTick() && nextCheckpointAngle != 0) {
                theta = (int) Math.abs(Math.toDegrees(Math.atan2(200, nextCheckpointDist))) * (-1 * nextCheckpointAngle / Math.abs(nextCheckpointAngle));
                target = Point.rotation(getCurrentPosition(), nextCheckpoint, theta);
            }

            if (canCollideWithOpponent(nextCheckpoint, nextCheckpointDist)) {
                thrust = "SHIELD";
                optimisationDebug.add("shield");
            } else if (nextCheckpointDist < 1200) {
                if (raceMap.mapFullyExplored && nextCheckpointDist < 800) {
                    target = raceMap.getAfterNext();
                    optimisationDebug.add("afterNext");
                } else {
                    thrust = "50";
                    optimisationDebug.add("pace down, dist to checkpoint");
                }
            } else if (Math.abs(nextCheckpointAngle) > 90) {
                thrust = "0";
                optimisationDebug.add("pace down, changing tack");
            } else {
                if (Math.abs(nextCheckpointAngle) < 5 && raceMap.isLongestPath()) {
                    thrust = "BOOST";
                    optimisationDebug.add("BOOST !!");
                } else {
                    optimisationDebug.add("none");
                }
            }

            return target.x + " " + target.y + " " + thrust;
        }

        public List<String> getOptimisationDebug() {
            return optimisationDebug;
        }
    }

    public static void main(String args[]) {
        Scanner in = new Scanner(System.in);

        RaceMap raceMap = new RaceMap();

        List<Pod> opponentPods = new ArrayList<>();
        opponentPods.add(new Pod());
        opponentPods.add(new Pod());

        List<OwnPod> ownPods = new ArrayList<>();
        ownPods.add(new OwnPod(raceMap, opponentPods));
        ownPods.add(new OwnPod(raceMap, opponentPods));

        // game loop
        while (true) {
            for (OwnPod ownPod : ownPods) {
                ownPod.update(new Point(in.nextInt(), in.nextInt()), new Vector(in.nextInt(), in.nextInt()), in.nextInt(), in.nextInt());
            }
            for (Pod opponentPod : opponentPods) {
                opponentPod.update(new Point(in.nextInt(), in.nextInt()), new Vector(in.nextInt(), in.nextInt()), in.nextInt(), in.nextInt());
            }

            System.out.println(ownPods.stream().map(OwnPod::computeCommand).collect(Collectors.joining("\n")));

            List<String> debugs = new ArrayList<>();
            debugs.add(raceMap.printStatus());
            for (int i = 0; i < ownPods.size(); i++) {
                debugs.add("Pod " + i + ":");
                debugs.addAll(ownPods.get(i).optimisationDebug);
            }
            System.err.println(String.join("\n", debugs));

            raceMap.tick();
        }
    }
}