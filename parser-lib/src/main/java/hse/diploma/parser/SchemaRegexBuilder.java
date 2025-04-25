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
 * Собирает {@link Schema} по разделу "Входные данные" (LaTeX, русский).
 */
@UtilityClass
public class SchemaRegexBuilder {

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
