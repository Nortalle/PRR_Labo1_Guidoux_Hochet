import org.junit.jupiter.api.Test;

        import static org.junit.jupiter.api.Assertions.*;

class ProtocolTest {


    @Test
    void testSYNC() {
        assertEquals("SYNC 8", Protocol.toSYNC(8));
        assertEquals("8", Protocol.fromSYNC("SYNC 8"));
    }

    @Test
    void testFOLLOW_UP() {
        assertEquals("FOLLOW_UP 3 8", Protocol.toFOLLOW_UP(3,8));
        String tab[] = {"FOLLOW_UP", "6", "9"};

        System.out.println(Protocol.fromFOLLOW_UP("FOLLOW_UP 6 9"));

        assertTrue(tab.equals(Protocol.fromFOLLOW_UP("FOLLOW_UP 6 9")));

    }

}