import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


public class PointTest {
    @Test
    void testDistance() {
        Player.Point p1 = new Player.Point(0, 12);
        Player.Point p2 = new Player.Point(1, 1);
        Player.Point p3 = new Player.Point(3, 3);

        assertEquals(0, p1.distance(p1));
        assertEquals(2, p2.distance(p3)); // should 2*sqrt(2)
        assertEquals(2, p3.distance(p2)); // should 2*sqrt(2)
    }

    @Test
    void testMove() {
        Player.Point p = new Player.Point(1, 2);
        Player.Vector v1 = new Player.Vector(0, 0);
        Player.Vector v2 = new Player.Vector(2, 3);
        Player.Vector v3 = new Player.Vector(-2, -1);

        assertEquals(p, p.move(v1));
        assertEquals(p, p.move(v2, 0));

        assertEquals(new Player.Point(3, 5), p.move(v2));
        assertEquals(new Player.Point(5, 8), p.move(v2, 2));

        assertEquals(new Player.Point(-1, 1), p.move(v3));
        assertEquals(new Player.Point(-5, -1), p.move(v3, 3));

        assertEquals(new Player.Point(-1, -1), p.move(v2, -1));
        assertEquals(new Player.Point(2, 2), p.move(v3, -0.5));
    }

    @Test
    void testRotate() {
        Player.Point p1 = new Player.Point(0, 0);
        Player.Point p2 = new Player.Point(10, 0);

        assertEquals(p2, p2.rotate(p1, 0));
        assertEquals(new Player.Point(7, -7), p2.rotate(p1, 45));
        assertEquals(new Player.Point(7, 7), p2.rotate(p1, -45));
    }

    @Test
    void testIsCloser() {
        Player.Point p1 = new Player.Point(1, 2);
        Player.Point p2 = new Player.Point(1, 2);
        Player.Point p3 = new Player.Point(2, 2);
        Player.Point p4 = new Player.Point(2, 4);

        assertEquals(0, p2.isCloser(p2, p1));
        assertEquals(1, p2.isCloser(p3, p1));
        assertEquals(-1, p4.isCloser(p3, p1));
    }
}
