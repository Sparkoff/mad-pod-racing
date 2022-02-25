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
        private final List<Point> checkpoints;
        private final List<Pod> opponents;
        private Point currentPosition;
        private Vector currentSpeed;
        private int currentAngle;
        private int currentCheckpointId;

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
        }

        private boolean canCollideWithOpponent() {
            for (Pod opponent : opponents) {
                if (Point.distance(evalNextPosition(), opponent.evalNextPosition()) < 800
                        && currentSpeed.norm() > 400
                        && opponent.currentSpeed.norm() > 400
                        && Math.abs(currentAngle - opponent.currentAngle) <= 45
                        && Point.distance(currentPosition, checkpoints.get(currentCheckpointId)) < Point.distance(opponent.currentPosition, checkpoints.get(currentCheckpointId))
                ) {
                    return true;
                }
            }
            return false;
        }

        public String compute() {
            Point target = checkpoints.get(currentCheckpointId);
            String thrust = "100";

            if (canCollideWithOpponent()) {
                thrust = "SHIELD";
            }

            return target.x + " " + target.y + " " + thrust;
        }

        public Point evalNextPosition() {
            return Point.sum(currentPosition, currentSpeed);
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

        public double distance(Point other) {
            return Math.sqrt(Math.pow(other.x - x, 2) + Math.pow(other.y - y, 2));
        }

        public static double distance(Point p1, Point p2) {
            return p1.distance(p2);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Point point = (Point) o;
            return x == point.x && y == point.y;
        }

        public static Point sum(Point p1, Point p2) {
            return new Point(p1.x + p2.x, p1.y + p2.y);
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

        public double norm() {
            return Math.sqrt(Math.pow(this.getX(), 2) + Math.pow(this.getY(), 2));
        }

        public double angleWith(Vector other) {
            double angle = Math.toDegrees(Math.abs(Math.atan2(other.getY(), other.getX()) - Math.atan2(this.getY(), this.getX())));
            return angle > 180 ? 360 - angle : angle;   // Keeps the angle between -180 and 180, like given checkpoint angle
        }
    }
}