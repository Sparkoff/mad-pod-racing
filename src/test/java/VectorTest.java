import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


public class VectorTest {
    @Test
    void testNorm() {
        Player.Vector v1 = new Player.Vector(0, 0);
        Player.Vector v2 = new Player.Vector(1, 1);

        assertEquals(0, v1.norm());
        assertEquals(1, v2.norm()); // should be sqrt(2)
    }

    @Test
    void testDotProduct() {
        Player.Vector v1 = new Player.Vector(1, 0);
        Player.Vector v2 = new Player.Vector(1, 1);
        Player.Vector v3 = new Player.Vector(2, 1);
        Player.Vector v4 = new Player.Vector(3, 4);

        assertEquals(1, v1.dotProduct(v2));
        assertEquals(1, v2.dotProduct(v1));
        assertEquals(10, v3.dotProduct(v4));
        assertEquals(10, v4.dotProduct(v3));
    }

    @Test
    void testAngleBetween() {
        Player.Vector v1 = new Player.Vector(1, 0);
        Player.Vector v2 = new Player.Vector(2, 2);
        Player.Vector v3 = new Player.Vector(0, 1);
        Player.Vector v4 = new Player.Vector(3, 4);

        assertEquals(0, v1.angleBetween(v1));
        assertEquals(45, v1.angleBetween(v2));
        assertEquals(45, v2.angleBetween(v1));
        assertEquals(90, v1.angleBetween(v3));
        assertEquals(90, v3.angleBetween(v1));
        assertEquals(53, v1.angleBetween(v4));
        assertEquals(53, v4.angleBetween(v1));
    }

    @Test
    void testSignedAngleBetween() {
        Player.Vector v1 = new Player.Vector(1, 0);
        Player.Vector v2 = new Player.Vector(2, 2);
        Player.Vector v3 = new Player.Vector(0, 1);
        Player.Vector v4 = new Player.Vector(3, 4);

        assertEquals(0, v1.signedAngleBetween(v1));
        assertEquals(45, v1.signedAngleBetween(v2));
        assertEquals(-45, v2.signedAngleBetween(v1));
        assertEquals(90, v1.signedAngleBetween(v3));
        assertEquals(-90, v3.signedAngleBetween(v1));
        assertEquals(53, v1.signedAngleBetween(v4));
        assertEquals(-53, v4.signedAngleBetween(v1));
    }

    @Test
    void testOrientedNormedPerpendicular() {
        Player.Vector v0 = new Player.Vector(0, 0);
        Player.Vector v1 = new Player.Vector(-1, 4);
        Player.Vector v2 = new Player.Vector(1, 2);
        Player.Vector v3 = new Player.Vector(-1, 2);
        Player.Vector v4 = new Player.Vector(10, 50);

        assertEquals(v0, v1.orientedNormedPerpendicular(0, v2));
        assertEquals(v0, v0.orientedNormedPerpendicular(10, v2));
        assertEquals(v0, v1.orientedNormedPerpendicular(10, v0));
        assertEquals(v0, v1.orientedNormedPerpendicular(10, v1));
        assertEquals(new Player.Vector(-1, 0), v1.orientedNormedPerpendicular(2, v2));
        assertEquals(new Player.Vector(1, 0), v1.orientedNormedPerpendicular(2, v3));
        assertEquals(new Player.Vector(-1, 0), v1.orientedNormedPerpendicular(2, v4));
    }
}
