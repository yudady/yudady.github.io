/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package practise.workspace.app;

import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.assertEquals;

class MessageUtilsTest {
    @Test
    void testGetMessage() {
        System.out.println("MessageUtilsTest  ==================");
        assertEquals("Hello      World!", MessageUtils.getMessage());
    }
}
