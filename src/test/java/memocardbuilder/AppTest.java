/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package memocardbuilder;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class AppTest {


    @Test
    void getAStringFromResourceBundle() {
        String str = Strings.get("app_name");
        assertNotNull(str);
        assertEquals(str, "Memory Card Game Builder");

    }

}
