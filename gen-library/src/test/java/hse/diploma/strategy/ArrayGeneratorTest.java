package hse.diploma.strategy;

import hse.diploma.enums.BaseType;
import hse.diploma.enums.Container;
import hse.diploma.model.TestCase;
import hse.diploma.model.VarDescriptor;
import hse.diploma.utility.helper.container.ArrayHelper;
import hse.diploma.utility.helper.container.ScalarHelper;
import hse.diploma.utility.structure.Pair;
import hse.diploma.utility.structure.SeededRandomGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.util.HashMap;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class ArrayGeneratorTest {

    private final VarDescriptor var = new VarDescriptor(
            "arr",
            Container.ARRAY,
            BaseType.INTEGER,
            new HashMap<>()
    );
    private final TestCase testCase = new TestCase(new HashMap<>(), new StringBuilder());
    private final SeededRandomGenerator generator = mock(SeededRandomGenerator.class);

    @BeforeEach
    void setUp() {
        testCase.setContext(new HashMap<>());
        testCase.setTest(new StringBuilder());
    }

    @Test
    void generateSmallArray_shouldAddSmallArrayToTestCase() {
        try (MockedStatic<ScalarHelper> scalarHelperMock = mockStatic(ScalarHelper.class);
             MockedStatic<ArrayHelper> arrayHelperMock = mockStatic(ArrayHelper.class)) {

            Pair<Long, Long> valBounds = Pair.of(10L, 20L);
            Pair<Long, Long> lenBounds = Pair.of(3L, 3L); // фиксированная длина 3
            Pair<Long, Long> smallValBounds = Pair.of(11L, 19L);
            Pair<Long, Long> smallLenBounds = Pair.of(3L, 3L);

            scalarHelperMock.when(() -> ScalarHelper.getBoundaries(eq(var), any()))
                    .thenReturn(valBounds);
            scalarHelperMock.when(() -> ScalarHelper.getSmallBoundaries(valBounds))
                    .thenReturn(smallValBounds);

            arrayHelperMock.when(() -> ArrayHelper.getLenBoundaries(eq(var), any()))
                    .thenReturn(lenBounds);
            scalarHelperMock.when(() -> ScalarHelper.getSmallBoundaries(lenBounds))
                    .thenReturn(smallLenBounds);

            when(generator.nextIntInclusive(smallLenBounds)).thenReturn(3L);
            when(generator.nextIntInclusive(smallValBounds)).thenReturn(15L, 16L, 17L);

            ArrayGenerator.generateSmallArray(var, testCase, generator);

            List<Long> expected = List.of(15L, 16L, 17L);
            String expectedString = "15 16 17\n";

            assertThat(testCase.getContext().get("arr")).isEqualTo(expected);
            assertThat(testCase.getTest().toString()).isEqualTo(expectedString);
        }
    }

    @Test
    void generateBigArray_shouldUseBigBoundaries() {
        try (MockedStatic<ScalarHelper> scalarHelperMock = mockStatic(ScalarHelper.class);
             MockedStatic<ArrayHelper> arrayHelperMock = mockStatic(ArrayHelper.class)) {

            Pair<Long, Long> valBounds = Pair.of(1L, 100L);
            Pair<Long, Long> lenBounds = Pair.of(5L, 5L);
            Pair<Long, Long> bigValBounds = Pair.of(0L, 200L);
            Pair<Long, Long> bigLenBounds = Pair.of(5L, 5L);

            scalarHelperMock.when(() -> ScalarHelper.getBoundaries(eq(var), any()))
                    .thenReturn(valBounds);
            scalarHelperMock.when(() -> ScalarHelper.getBigBoundaries(valBounds))
                    .thenReturn(bigValBounds);

            arrayHelperMock.when(() -> ArrayHelper.getLenBoundaries(eq(var), any()))
                    .thenReturn(lenBounds);
            scalarHelperMock.when(() -> ScalarHelper.getBigBoundaries(lenBounds))
                    .thenReturn(bigLenBounds);

            when(generator.nextIntInclusive(bigLenBounds)).thenReturn(5L);
            when(generator.nextIntInclusive(bigValBounds)).thenReturn(1L, 2L, 3L, 4L, 5L);

            ArrayGenerator.generateBigArray(var, testCase, generator);

            List<Long> expected = List.of(1L, 2L, 3L, 4L, 5L);
            String expectedString = "1 2 3 4 5\n";

            assertThat(testCase.getContext().get("arr")).isEqualTo(expected);
            assertThat(testCase.getTest().toString()).isEqualTo(expectedString);
        }
    }

    @Test
    void generateRandomArray_shouldUseDefaultBoundaries() {
        try (MockedStatic<ScalarHelper> scalarHelperMock = mockStatic(ScalarHelper.class);
             MockedStatic<ArrayHelper> arrayHelperMock = mockStatic(ArrayHelper.class)) {

            Pair<Long, Long> valBounds = Pair.of(0L, 10L);
            Pair<Long, Long> lenBounds = Pair.of(2L, 2L);

            scalarHelperMock.when(() -> ScalarHelper.getBoundaries(eq(var), any()))
                    .thenReturn(valBounds);
            arrayHelperMock.when(() -> ArrayHelper.getLenBoundaries(eq(var), any()))
                    .thenReturn(lenBounds);

            when(generator.nextIntInclusive(lenBounds)).thenReturn(2L);
            when(generator.nextIntInclusive(valBounds)).thenReturn(7L, 8L);

            ArrayGenerator.generateRandomArray(var, testCase, generator);

            List<Long> expected = List.of(7L, 8L);
            String expectedString = "7 8\n";

            assertThat(testCase.getContext().get("arr")).isEqualTo(expected);
            assertThat(testCase.getTest().toString()).isEqualTo(expectedString);
        }
    }
}
