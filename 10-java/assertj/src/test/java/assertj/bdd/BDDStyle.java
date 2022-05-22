package assertj.bdd;

import org.junit.jupiter.api.Test;


import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.BDDAssertions.then;
import static org.assertj.core.api.ThrowableAssert.catchThrowable;

public class BDDStyle {
    @Test
    void testBDD01() {
        // GIVEN
        String[] names = {"Pier ", "Pol", "Jak"};
        // WHEN
        Throwable thrown = catchThrowable(() -> System.out.println(names[9]));
        // THEN
        then(thrown).isInstanceOf(ArrayIndexOutOfBoundsException.class)
            .hasMessageContaining("9");

        // assertThat is also available but is less "BDD style"
        assertThat(thrown).isInstanceOf(ArrayIndexOutOfBoundsException.class)
            .hasMessageContaining("9");
    }
}
