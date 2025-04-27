package hse.diploma.parser;

import hse.diploma.enums.BaseType;
import hse.diploma.enums.Container;
import hse.diploma.enums.props.PropKey;
import hse.diploma.enums.props.values.LineType;
import hse.diploma.enums.props.values.SortedOrder;
import hse.diploma.map.PropMapBuilder;
import hse.diploma.model.Schema;
import hse.diploma.model.VarDescriptor;
import hse.diploma.nlp.TextProcessing;
import lombok.experimental.UtilityClass;

import java.util.*;
import java.util.regex.Matcher;

import static hse.diploma.parser.array.ArrayParser.detectOrder;
import static hse.diploma.parser.math.MathParser.parseNumber;
import static hse.diploma.parser.math.MathParser.parseRange;
import static hse.diploma.pattern.RegexPattern.*;

/**
 * Утилитный класс, собирающий структуру {@link Schema} по тексту на русском языке (обычно — раздел "Входные данные" задачи).
 * Использует регулярные выражения и эвристики для распознавания переменных, их типов, ограничений и структуры вложенности.
 * <p>
 * Основная точка входа — метод {@link #parse(String)}.
 */
@UtilityClass
public class SchemaRegexBuilder {

    /**
     * Главный метод: извлекает структуру входных данных из переданного текста задачи.
     * <p>
     * 1. Производит нормализацию текста (удаление LaTeX, пробелов, замену символов и т.п.).<br>
     * 2. Вызывает парсеры: тест-блок, скалярные переменные, массивы и т.д.<br>
     * 3. Упорядочивает переменные по позиции первого вхождения в тексте.<br>
     *
     * @param text текст раздела "Входные данные" на русском языке (возможно с LaTeX).
     * @return объект {@link Schema}, содержащий найденные переменные
     */
    public static Schema parse(String text) {
        text = TextProcessing.normalize(text);

        Map<String, Integer> posMap = new HashMap<>();
        List<VarDescriptor> vars = new LinkedList<>();

        Set<String> usedNames = new HashSet<>();

        var topLevelVars = vars;
        parseTestBlock(text, vars, usedNames, posMap);

        if (!vars.isEmpty()) {
            vars = (List<VarDescriptor>) vars.getFirst().props().get(PropKey.FIELDS.key());
        }
        parseScalarIntegers(text, vars, usedNames, posMap);
        parseArrayIntegers  (text, vars, usedNames, posMap);
        vars.sort(Comparator.comparingInt(v ->
                posMap.getOrDefault(v.name(), Integer.MAX_VALUE)));

        return new Schema(topLevelVars);
    }

    /**
     * Парсит описание количества тестов — например, строка вида:
     * "Первая строка содержит целое число t (1<=t<=10^4) — количество тестов".
     * <p>
     * Создаёт переменную с именем t, типом SCALAR и вложенными переменными внутри (fields).
     */
    private static void parseTestBlock(String text,
                                       List<VarDescriptor> vars,
                                       Set<String> usedNames,
                                       Map<String,Integer> posMap) {
        if (text == null || vars == null || usedNames == null) return;

        Matcher m = PAT_TEST_BLOCK.matcher(text);
        if (!m.find()) return;

        String name = m.group("name");
        String rangeRaw = m.group("range");

        if (name == null || rangeRaw == null) return;

        long[] rng = parseRange(rangeRaw);
        if (rng.length < 2) return;

        List<VarDescriptor> inner = new LinkedList<>();
        Map<String,Object> props = PropMapBuilder.create()
                .put(PropKey.MIN.key(), rng[0])
                .put(PropKey.MAX.key(), rng[1])
                .put(PropKey.FIELDS.key(), inner)
                .put(PropKey.LINE_TYPE.key(), LineType.SINGLE.key())
                .put(PropKey.IS_TEST_CASE_VAR.key(), true)
                .build();

        vars.add(new VarDescriptor(
                name,
                Container.SCALAR,
                BaseType.INTEGER,
                props
        ));

        usedNames.add(name);
        posMap.putIfAbsent(name, m.start());
    }

    /**
     * Парсит отдельные скалярные переменные — например:
     * "целое число n (1<=n<=2*10^5)"
     * <p>
     * Добавляет переменную SCALAR с типом INTEGER или LONG, в зависимости от диапазона.
     */
    private static void parseScalarIntegers(String text,
                                            List<VarDescriptor> out,
                                            Set<String> used,
                                            Map<String,Integer> posMap) {

        Matcher m = ONE_SCALAR.matcher(text);
        while (m.find()) {
            String name = m.group("name");
            if (name == null || used.contains(name)) continue;

            long min = parseNumber(m.group("min").trim());
            long max = parseNumber(m.group("max").trim());

            VarDescriptor vd = new VarDescriptor(
                    name,
                    Container.SCALAR,
                    max <= 1e9 ? BaseType.INTEGER : BaseType.LONG,
                    PropMapBuilder.create()
                            .put(PropKey.MIN.key(), min)
                            .put(PropKey.MAX.key(), max)
                            .put(PropKey.LINE_TYPE.key(), LineType.SINGLE.key())
                            .build()
            );

            out.add(vd);
            used.add(name);
            posMap.putIfAbsent(name, m.start());
        }
    }


    /**
     * Парсит массивы целых чисел — например:
     * "n целых чисел a1, a2, ..., a_n (1<=a_i<=n)"
     * <p>
     * Распознаёт:
     * <ul>
     *     <li>Длину массива: зависящую от переменной</li>
     *     <li>Ограничения на значения элементов</li>
     *     <li>Флаги: уникальность, перестановка, порядок сортировки</li>
     * </ul>
     */
    private static void parseArrayIntegers(String text,
                                           List<VarDescriptor> out,
                                           Set<String> used,
                                           Map<String,Integer> posMap) {

        Matcher m = ARR_SCALAR.matcher(text);
        while (m.find()) {

            String name = m.group("name");
            if (name == null || used.contains(name)) continue;

            String lenRaw = m.group("len").trim();
            Long fixedLen = null;
            try { fixedLen = parseNumber(lenRaw); } catch (Exception ignore) {}

            Object varMinLen = (fixedLen != null ? fixedLen : lenRaw);
            Object varMaxLen = varMinLen;


            String leftRaw  = m.group("min").trim();
            String rightRaw = m.group("max").trim();

            Long minVal = null, maxVal = null;
            try { minVal = parseNumber(leftRaw);  } catch (Exception ignore) {}
            try { maxVal = parseNumber(rightRaw); } catch (Exception ignore) {}


            PropMapBuilder b = PropMapBuilder.create()
                    .put(PropKey.VAR_MIN_LEN.key(),  varMinLen)
                    .put(PropKey.VAR_MAX_LEN.key(),  varMaxLen);


            if (minVal != null) b.put(PropKey.MIN.key(),     minVal);
            else                b.put(PropKey.VAR_MIN.key(), leftRaw);


            if (maxVal != null) b.put(PropKey.MAX.key(),     maxVal);
            else                b.put(PropKey.VAR_MAX.key(), rightRaw);


            String chunk = m.group(0).toLowerCase();
            b.put(PropKey.IS_DISTINCT.key(),    chunk.contains("различн"))
                    .put(PropKey.IS_PERMUTATION.key(), chunk.contains("перестанов"))
                    .put(PropKey.SORTED_ORDER.key(),   detectOrder(chunk))
                    .put(PropKey.LINE_TYPE.key(),      LineType.SINGLE.key());

            Map<String,Object> props = b.build();


            BaseType bt;
            if (maxVal != null && maxVal <= 1_000_000_000L) bt = BaseType.INTEGER;
            else                                            bt = BaseType.LONG;


            VarDescriptor vd = new VarDescriptor(name, Container.ARRAY, bt, props);
            out.add(vd);

            used.add(name);
            posMap.putIfAbsent(name, m.start());
        }
    }

}
