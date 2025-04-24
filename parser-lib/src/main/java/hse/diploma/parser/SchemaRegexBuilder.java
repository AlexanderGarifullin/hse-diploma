package hse.diploma.parser;

import hse.diploma.enums.BaseType;
import hse.diploma.enums.Container;
import hse.diploma.enums.props.PropKey;
import hse.diploma.map.PropMapBuilder;
import hse.diploma.model.Schema;
import hse.diploma.model.VarDescriptor;
import hse.diploma.nlp.TextProcessing;
import lombok.experimental.UtilityClass;

import java.util.*;
import java.util.regex.Matcher;

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
        List<VarDescriptor> vars = new LinkedList<>();
        Set<String> usedNames = new HashSet<>();

        var topLevelVars = vars;
        parseTestBlock(text, vars, usedNames);
        if (!vars.isEmpty()) {
            vars = (List<VarDescriptor>) vars.getFirst().props().get(PropKey.FIELDS.key());
        }
        parseScalarIntegers(text, vars, usedNames);
        return new Schema(topLevelVars);
    }

    private static void parseTestBlock(String text, List<VarDescriptor> vars, Set<String> usedNames) {
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
                .build();

        vars.add(new VarDescriptor(
                name,
                Container.SCALAR,
                BaseType.INTEGER,
                props
        ));

        usedNames.add(name);
    }

    private static void parseScalarIntegers(String text, List<VarDescriptor> vars, Set<String> usedNames) {
        if (text == null || vars == null || usedNames == null) return;

        Matcher m = ONE_SCALAR.matcher(text);
        while (m.find()) {
            String name = m.group("name");
            String minRaw = m.group("min");
            String maxRaw = m.group("max");

            if (name == null || minRaw == null || maxRaw == null) continue;

            name = name.trim();
            if (usedNames.contains(name)) continue;

            long min, max;
            try {
                min = parseNumber(minRaw.trim());
                max = parseNumber(maxRaw.trim());
            } catch (Exception e) {
                continue;
            }

            Map<String, Object> props = PropMapBuilder.create()
                    .put(PropKey.MIN.key(), min)
                    .put(PropKey.MAX.key(), max)
                    .build();

            BaseType type = max <= 1e9 ? BaseType.INTEGER : BaseType.LONG;

            vars.add(new VarDescriptor(
                    name,
                    Container.SCALAR,
                    type,
                    props
            ));

            usedNames.add(name);
        }
    }
}
