import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PlayerTest {
    @Test
    void testAngle() {
        Player.Vector u = new Player.Vector(0, 12);
        assertEquals(45, u.angleWith(new Player.Vector(2, 2)));
        assertEquals(90, u.angleWith(new Player.Vector(4, 0)));
        assertEquals(135, u.angleWith(new Player.Vector(-2, -2)));
        assertEquals(45, u.angleWith(new Player.Vector(-2, 2)));
        assertEquals(135, u.angleWith(new Player.Vector(2, -2)));
    }
}