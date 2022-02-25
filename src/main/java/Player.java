import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Auto-generated code below aims at helping you parse
 * the standard input according to the problem statement.
 **/
class Player {

    public static void main(String args[]) {
        Scanner in = new Scanner(System.in);

        int laps = in.nextInt();
        int checkpointCount = in.nextInt();
        List<Point> checkpoints = new ArrayList<>();
        for (int i = 0; i < checkpointCount; i++) {
            checkpoints.add(new Point(in.nextInt(), in.nextInt()));
        }

        List<Pod> opponentPods = Arrays.asList(
                new Pod(checkpoints),
                new Pod(checkpoints)
        );
        List<Pod> ownPods = Arrays.asList(
                new Pod(checkpoints, opponentPods),
                new Pod(checkpoints, opponentPods)
        );

        // game loop
        while (true) {
            for (Pod ownPod : ownPods) {
                ownPod.update(new Point(in.nextInt(), in.nextInt()), new Vector(in.nextInt(), in.nextInt()), in.nextInt(), in.nextInt());
            }
            for (Pod opponentPod : opponentPods) {
                opponentPod.update(new Point(in.nextInt(), in.nextInt()), new Vector(in.nextInt(), in.nextInt()), in.nextInt(), in.nextInt());
            }

            System.out.println(ownPods.stream().map(Pod::compute).collect(Collectors.joining("\n")));
            System.err.println(ownPods.stream().map(Pod::debug).collect(Collectors.joining("\n")));
        }

    }

    public static class Pod {
        private final List<Pod> opponents;

        private final List<Point> checkpoints;
        private int currentCheckpointId;
        private Point currentCheckpoint;
        private final int checkpointDrift = 200;

        private Point currentPosition;
        private Vector currentSpeed;
        private int currentAngle;

        public Pod(List<Point> checkpoints) {
            this.checkpoints = checkpoints;
            this.opponents = new ArrayList<>();
        }
        public Pod(List<Point> checkpoints, List<Pod> opponents) {
            this.checkpoints = checkpoints;
            this.opponents = opponents;
        }

        public void update(Point position, Vector speed, int angle, int checkpointId) {
            currentPosition = position;
            currentSpeed = speed;
            currentAngle = angle;
            currentCheckpointId = checkpointId;
            currentCheckpoint = checkpoints.get(currentCheckpointId);
        }

        private boolean canCollideWithOpponent() {
            for (Pod opponent : opponents) {
                if (evalNextPosition().distance(opponent.evalNextPosition()) < 800
                        && currentSpeed.norm() > 400
                        && opponent.currentSpeed.norm() > 400
                        && Math.abs(currentAngle - opponent.currentAngle) <= 45
                        && currentPosition.isCloser(opponent.currentPosition, currentCheckpoint) > 0
                ) {
                    return true;
                }
            }
            return false;
        }

        public String compute() {
            Point target = checkpoints.get(currentCheckpointId);
            String thrust = "100";

            if (angleWithCurrentCheckpoint() != 0) {
                // create vector with norm=checkpointDrift perpendicular to checkpoint's direction, opposed to our current speed
                Vector checkpointDir = new Vector(currentPosition, currentCheckpoint);
                Vector driftDirection = checkpointDir.orientedNormedPerpendicular(checkpointDrift, currentSpeed);
                target = currentCheckpoint.move(driftDirection);
            }

            if (canCollideWithOpponent()) {
                thrust = "SHIELD";
            }

            return target.x + " " + target.y + " " + thrust;
        }

        public Point evalNextPosition() {
            return currentPosition.move(currentSpeed);
        }

        public int angleWithCurrentCheckpoint() {
            return currentSpeed.signedAngleBetween(new Vector(currentPosition, currentCheckpoint));
        }

        public String debug() {
            return String.join(", ", Arrays.asList(
                    "Pos" + currentPosition,
                    "Speed" + currentSpeed,
                    "Angle " + currentAngle,
                    "Checkpoint " + currentCheckpointId
            ));
        }
    }

    public static class Point {
        private final int x;
        private final int y;

        public Point(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public int distance(Point other) {
            return (new Vector(this, other)).norm();
        }

        public Point move(Vector v) {
            return new Point(x + v.getX(), y + v.getY());
        }

        public Point rotate(Point origin, int angle) {
            return new Point(
                    (int) ((x - origin.x) * Math.cos(Math.toRadians(angle)) + (y - origin.y) * Math.sin(Math.toRadians(angle)) + origin.x),
                    (int) ((x - origin.x) * -Math.sin(Math.toRadians(angle)) + (y - origin.y) * Math.cos(Math.toRadians(angle)) + origin.y)
            );
        }

        // Return positive value if p1 is closest to ref than p2, negative value if not.
        // Return 0 if equal
        public int isCloser(Point other, Point ref) {
            int dist1 = distance(ref);
            int dist2 = other.distance(ref);
            return Integer.compare(dist2, dist1);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Point point = (Point) o;
            return x == point.x && y == point.y;
        }
        @Override public int hashCode() { return Objects.hash(x, y); }
        @Override public String toString() { return String.format("(%d,%d)", x, y); }

        public int getX() { return x; }
        public int getY() { return y; }

    }
    public static class Vector extends Point {

        public Vector(int x, int y) {
            super(x, y);
        }
        public Vector(Point p1, Point p2) {
            super(p2.x - p1.x, p2.y - p1.y);
        }

        public int norm() {
            return (int) normDouble();
        }
        private double normDouble() {
            return Math.sqrt(Math.pow(getX(), 2) + Math.pow(getY(), 2));
        }

        public int dotProduct(Vector other) {
            return getX() * other.getX() + getY() * other.getY();
        }

        public int angleBetween(Vector other) {
            return (int) Math.toDegrees(Math.acos(dotProduct(other) / (normDouble() * other.normDouble())));
        }
        public int signedAngleBetween(Vector other) {
            return (int) Math.toDegrees(Math.atan2(
                    getX() * other.getY() - getY() * other.getX(),
                    getX() * other.getX() + getY() * other.getY()
            ));
        }

        // create a perpendicular vector (normalized to input norm), oriented by diff of baseRef and current vectors
        public Vector orientedNormedPerpendicular(int norm, Vector baseRef) {
            if (norm == 0 || normDouble() == 0 || baseRef.normDouble() == 0) return new Vector(0, 0);

            Vector diff = new Vector(getX() - baseRef.getX(), getY() - baseRef.getY());
            Vector perpendicular = new Vector(getY(), -getX());
            double dot = 1. * perpendicular.dotProduct(diff) / Math.abs(perpendicular.dotProduct(diff));

            return new Vector(
                    (int) (dot * perpendicular.getX() * norm / perpendicular.normDouble()),
                    (int) (dot * perpendicular.getY() * norm / perpendicular.normDouble())
            );
        }
    }
}