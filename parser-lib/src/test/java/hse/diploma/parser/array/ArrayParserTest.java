package hse.diploma.parser.array;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;
import static hse.diploma.enums.props.values.SortedOrder.*;

class ArrayParserTest {

    @Test
    void detectOrder_simpleCompare() {
        assertThat(ArrayParser.detectOrder("a1 < a2")).isEqualTo(ASC);
    }

    @Test
    void detectOrder_nonDecreasing() {
        assertThat(ArrayParser.detectOrder("неубывающ")).isEqualTo(ASC);
        assertThat(ArrayParser.detectOrder("возрастающ")).isEqualTo(ASC);
    }

    @Test
    void detectOrder_decreasing() {
        assertThat(ArrayParser.detectOrder("убыв")).isEqualTo(DESC);
        assertThat(ArrayParser.detectOrder("невозраст")).isEqualTo(DESC);
    }

    @Test
    void detectOrder_none() {
        assertThat(ArrayParser.detectOrder("no order here")).isEqualTo(NONE);
    }
}
