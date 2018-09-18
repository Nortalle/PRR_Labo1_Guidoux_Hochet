import org.junit.jupiter.api.Test;

        import static org.junit.jupiter.api.Assertions.*;

class ProtocoleTest {


    @Test
    void testSYNC() {
        assertEquals("SYNC 8", Protocole.toSYNC(8));
        assertEquals("8", Protocole.fromSYNC("SYNC 8"));
    }

    @Test
    void testFOLLOW_UP() {
        assertEquals("FOLLOW_UP 3 8", Protocole.toFOLLOW_UP(3,8));
        String tab[] = {"FOLLOW_UP", "6", "9"};

        System.out.println(Protocole.fromFOLLOW_UP("FOLLOW_UP 6 9"));

        assertTrue(tab.equals(Protocole.fromFOLLOW_UP("FOLLOW_UP 6 9")));

    }

}