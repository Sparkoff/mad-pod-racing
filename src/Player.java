import java.util.*;
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

        public int scalaire(Vector other) {
            return getX() * other.getX() + getY() * other.getY();
        }
    }

    public static class RaceMap {
        private final List<Point> checkpoints = new ArrayList<>();
        private final Map<Point,Double> paths = new HashMap<>();
        private Point longestPath;
        private boolean mapFullyExplored = false;
        private int lap = 1;
        private Point currentTarget;

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

        public String printStatus() {
            return String.format(
                    "raceMap(lap: %d, checkpoints count: %d, fully explored: %b, long path: %d)",
                    lap,
                    checkpoints.size(),
                    mapFullyExplored,
                    checkpoints.indexOf(longestPath));
        }
    }

    public static class Pod {
        private final List<Point> path = new ArrayList<>();

        public void addPosition(Point currentPosition) {
            path.add(currentPosition);
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
    }

    public static void main(String args[]) {
        Scanner in = new Scanner(System.in);

        RaceMap raceMap = new RaceMap();
        Pod ownPod = new Pod();
        Pod opponentPod = new Pod();

        boolean boostHappened = false;
        int shieldHappened = 0;

        // game loop
        while (true) {
            int x = in.nextInt();
            int y = in.nextInt();
            int nextCheckpointX = in.nextInt(); // x position of the next check point
            int nextCheckpointY = in.nextInt(); // y position of the next check point
            int nextCheckpointDist = in.nextInt(); // distance to the next checkpoint
            int nextCheckpointAngle = in.nextInt(); // angle between your pod orientation and the direction of the next checkpoint
            int opponentX = in.nextInt();
            int opponentY = in.nextInt();

            Point currentPosition = new Point(x, y);
            Point nextCheckpoint = new Point(nextCheckpointX, nextCheckpointY);
            Point opponent = new Point(opponentX, opponentY);

            // explore map
            raceMap.nextCheckpoint(nextCheckpoint);

            // update Pods
            ownPod.addPosition(currentPosition);
            opponentPod.addPosition(opponent);

            // Write an action using System.out.println()
            // To debug: System.err.println("Debug messages...");


            // You have to output the target position
            // followed by the power (0 <= thrust <= 100)
            // i.e.: "x y thrust"
            List<String> optimisationDebug = new ArrayList<>();
            if (currentPosition.distance(opponent) < 840
                    && ownPod.currentSpeed() > 400
                    && opponentPod.currentSpeed() > 400
                    && ownPod.currentSpeedVector().scalaire(opponentPod.currentSpeedVector()) >= 0
                    && nextCheckpointDist < opponent.distance(nextCheckpoint)
            ) {
                System.out.println(nextCheckpoint.x + " " + nextCheckpoint.y + " SHIELD");
                optimisationDebug.add("shield");
                shieldHappened++;
            } else if (nextCheckpointDist < 1200) {
                if (raceMap.mapFullyExplored && nextCheckpointDist < 800) {
                    Point afterNext = raceMap.getAfterNext();
                    System.out.println(afterNext.x + " " + afterNext.y + " 100");
                    optimisationDebug.add("afterNext");
                } else {
                    System.out.println(nextCheckpoint.x + " " + nextCheckpoint.y + " 50");
                    optimisationDebug.add("pace down, dist to checkpoint");
                }
            } else if (Math.abs(nextCheckpointAngle) > 90) {
                System.out.println(nextCheckpoint.x + " " + nextCheckpoint.y + " 0");
                optimisationDebug.add("pace down, changing tack");
            } else {
                if (!boostHappened && Math.abs(nextCheckpointAngle) < 5 && raceMap.isLongestPath()) {
                    System.out.println(nextCheckpoint.x + " " + nextCheckpoint.y + " BOOST");
                    boostHappened = true;
                    optimisationDebug.add("BOOST !!");
                } else {
                    System.out.println(nextCheckpoint.x + " " + nextCheckpoint.y + " 100");
                    optimisationDebug.add("none");
                }
            }

            // Optimize target destination
//            Point target = nextCheckpoint;
//            if (raceMap.mapFullyExplored && nextCheckpointDist < 800) {
//                //target = raceMap.getAfterNext();
//                optimisationDebug.add("afterNext");
//            } else if (Math.abs(nextCheckpointAngle) > 10
//                    && Math.abs(nextCheckpointAngle) < 90
//                    && nextCheckpointDist > 1200
//                    && !raceMap.isLastPath(nextCheckpoint)) {
//                // dynamic target correction : target a point with greater angle (doubled) to force pod to flip faster
//                target = Point.rotation(currentPosition, nextCheckpoint, -nextCheckpointAngle);
//                optimisationDebug.add("dynamicTarget");
//            }
//
//            // Optimize speed
//            int longRange = 1500;
//            int shortRange = 200;
//            double distOptim = 1. * (Math.max(Math.min(nextCheckpointDist, longRange), shortRange) - shortRange) / (longRange - shortRange);
//            if (distOptim != 1) {
//                optimisationDebug.add("approach (" + distOptim + ")");
//            }
//
//            int openedAngle = 90;
//            int closedAngle = 10;
//            double angleOptim = 1. * (openedAngle - Math.max(Math.min(Math.abs(nextCheckpointAngle), openedAngle), closedAngle)) / (openedAngle - closedAngle);
//            if (angleOptim != 1) {
//                optimisationDebug.add("tack (" + angleOptim + ")");
//            }
//
//            int minThrust = 5;
//            int thrust = (int) ((100 - minThrust) * Math.min(distOptim, angleOptim)) + minThrust;
//
//
//            // Propagate pod commands
//            if (!boostHappened && Math.abs(nextCheckpointAngle) < 2 && raceMap.isLongestPath()) {
//                System.out.println(target.x + " " + target.y + " BOOST");
//                boostHappened = true;
//                optimisationDebug.add("BOOST !!");
//            } else {
//                System.out.println(target.x + " " + target.y + " " + thrust);
//            }

            // Debug
            System.err.println(String.join("\n",
                    raceMap.printStatus(),
                    "optim: " + String.join(" ", optimisationDebug),
                    (boostHappened ? "boosh happened" : "no boost yet"),
                    "shiled activated: " + shieldHappened,
                    "pod speed: " + ownPod.currentSpeed() + ", opponent speed: " + opponentPod.currentSpeed(),
                    "input: " + String.format("(pod: %s, checkpoint: %s, dist: %d, angle: %d)", currentPosition, nextCheckpoint, nextCheckpointDist, nextCheckpointAngle)
            ));

        }
    }
}