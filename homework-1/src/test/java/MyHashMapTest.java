import org.example.MyHashMap;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class MyHashMapTest {

    @Test
    void putAndGet() {
        MyHashMap<String, Integer> map = new MyHashMap<>();
        map.put("a", 1);
        map.put("b", 2);

        assertEquals(1, map.get("a"));
        assertEquals(2, map.get("b"));
        assertNull(map.get("c"));
    }

    @Test
    void putOverwritesValue() {
        MyHashMap<String, Integer> map = new MyHashMap<>();
        assertNull(map.put("a", 1));
        assertEquals(1, map.put("a", 2));
        assertEquals(2, map.get("a"));
        assertEquals(1, map.size());
    }

    @Test
    void remove() {
        MyHashMap<String, Integer> map = new MyHashMap<>();
        map.put("a", 1);
        map.put("b", 2);

        assertEquals(1, map.remove("a"));
        assertNull(map.get("a"));
        assertNull(map.remove("a"));
        assertEquals(1, map.size());
    }

    @Test
    void collisionsAreHandled() {
        MyHashMap<Integer, String> map = new MyHashMap<>();
        for (int i = 0; i < 100; i += 16) {
            map.put(i, "v" + i);
        }
        for (int i = 0; i < 100; i += 16) {
            assertEquals("v" + i, map.get(i));
        }
    }

    @Test
    void resizeWorks() {
        MyHashMap<Integer, Integer> map = new MyHashMap<>();
        for (int i = 0; i < 1000; i++) {
            map.put(i, i * 2);
        }
        assertEquals(1000, map.size());
        for (int i = 0; i < 1000; i++) {
            assertEquals(i * 2, map.get(i));
        }
    }

    @Test
    void nullKeySupported() {
        MyHashMap<String, String> map = new MyHashMap<>();
        map.put(null, "nullValue");
        assertEquals("nullValue", map.get(null));
        assertEquals("nullValue", map.remove(null));
        assertNull(map.get(null));
    }
}