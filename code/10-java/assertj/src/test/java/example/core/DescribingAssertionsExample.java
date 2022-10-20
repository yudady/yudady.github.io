package example.core;

import example.data.Race;
import example.data.TolkienCharacter;
import org.junit.jupiter.api.Test;


import static org.assertj.core.api.Assertions.assertThat;

public class DescribingAssertionsExample {

    @Test
    void describing_assertions() {
        try {
            // tag::user_guide[]
            TolkienCharacter frodo = new TolkienCharacter("Frodo", 33, Race.HOBBIT);

            // failing assertion, remember to call as() before the assertion!
            assertThat(frodo.getAge()).as("check %s's age", frodo.getName())
                .isEqualTo(100);
            // end::user_guide[]
        } catch (AssertionError error) {
            // do nothing
        }
    }

}
