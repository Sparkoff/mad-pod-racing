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
                new Pod(checkpoints, opponentPods, false),
                new Pod(checkpoints, opponentPods, true)
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

    public static class Team {
        private final List<Pod> pods;

        public Team(List<Pod> pods) {
            this.pods = pods;
        }
    }

    public static class Pod {
        private final List<Pod> opponents;

        private final List<Point> checkpoints;
        private int currentCheckpointId = -1;
        private Point currentCheckpoint;
        private int angleWithCurrentCheckpoint = 0;
        private int checkpointPassedCount = 0;
        private boolean isAhead;
        private int frame = 0;

        private Point currentPosition;
        private Vector currentSpeed;
        private int currentAngle;

        private int shieldCount = 0;
        private boolean boostHappened = false;

        public Pod(List<Point> checkpoints) {
            this.checkpoints = checkpoints;
            this.opponents = new ArrayList<>();
        }
        public Pod(List<Point> checkpoints, List<Pod> opponents, boolean isAhead) {
            this.checkpoints = checkpoints;
            this.opponents = opponents;
            this.isAhead = isAhead;
        }

        public void update(Point position, Vector speed, int angle, int checkpointId) {
            currentPosition = position;
            currentSpeed = speed;
            currentAngle = angle;

            if (currentCheckpointId != checkpointId && currentCheckpointId != -1) {
                checkpointPassedCount++;
            }
            currentCheckpointId = checkpointId;

            currentCheckpoint = checkpoints.get(currentCheckpointId);
            angleWithCurrentCheckpoint = Math.abs(new Vector(currentPosition, currentCheckpoint).absoluteAngle()
                    - currentAngle);
            frame++;
        }

        private boolean canCollideWithOpponent() {
            for (Pod opponent : opponents) {
                if (evalNextPosition().distance(opponent.evalNextPosition()) < 800  // collision
                        // && opponent.currentSpeed.norm() > 400  // opponent speed > 400
                        // && currentSpeed.norm() > 400  // our speed > 400
                        // && Math.abs(currentAngle - opponent.currentAngle) <= 45  // opponent from rear
                        // && currentPosition.isCloser(opponent.currentPosition, currentCheckpoint) > 0  // we are closer to checkpoint
                ) {
                    return true;
                }
            }
            return false;
        }

        public String compute() {
            Point target = currentCheckpoint.move(currentSpeed, -3);
            String thrust = "100";

            if (frame == 1 && isAhead) {
                thrust = "BOOST";
                boostHappened = true;
            } else if (canCollideWithOpponent()) {
                thrust = "SHIELD";
                shieldCount++;
            } else if (angleWithCurrentCheckpoint < 5
                            && currentPosition.distance(currentCheckpoint) > 5000
                            && !boostHappened
                            && frame > 5) {
                thrust = "BOOST";
                boostHappened = true;
            } else if (angleWithCurrentCheckpoint > 90 && frame > 5) {
                thrust = "0";
            }
//            else if (Math.abs(currentCheckpointDir.angleRef() - currentAngle) > 90) {
//                thrust = "0";
//            }

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
                    "Checkpoint " + currentCheckpointId,
                    "Shield in use " + shieldCount,
                    boostHappened ? "Boost happened" : "",
                    "Angle with checkpoint " + Math.abs(new Vector(currentPosition, currentCheckpoint).absoluteAngle() - currentAngle),
                    "Frame " + frame,
                    "is ahead " + isAhead
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

        public Point move(Vector v, double factor) {
            return new Point((int) (x + factor * v.getX()), (int) (y + factor * v.getY()));
        }

        public Point move(Vector v) {
            return this.move(v, 1.);
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

        public int absoluteAngle() {
            int horizonAngle = - signedAngleBetween(new Vector(1, 0));
            if (horizonAngle >= 0) {
                return horizonAngle;
            } else {
                return 360 + horizonAngle;
            }
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