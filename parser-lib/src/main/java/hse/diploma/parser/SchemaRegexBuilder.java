package hse.diploma.parser;

import hse.diploma.enums.BaseType;
import hse.diploma.enums.Container;
import hse.diploma.enums.props.PropKey;
import hse.diploma.map.PropMapBuilder;
import hse.diploma.model.Schema;
import hse.diploma.model.VarDescriptor;
import hse.diploma.nlp.TextProcessing;
import lombok.experimental.UtilityClass;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

import static hse.diploma.parser.math.MathParser.parseRange;
import static hse.diploma.pattern.RegexPattern.PAT_TEST_BLOCK;


/**
 * Собирает {@link Schema} по разделу "Входные данные" (LaTeX, русский).
 */
@UtilityClass
public class SchemaRegexBuilder {

    public static Schema parse(String text) {
        text = TextProcessing.normalize(text);
        List<VarDescriptor> vars = new LinkedList<>();
        var topLevelVars = vars;
        parseTestBlock(text, vars);
        if (!vars.isEmpty()) {
            vars = (List<VarDescriptor>) vars.getFirst().props().get(PropKey.FIELDS.key());
        }

        return new Schema(topLevelVars);
    }

    private static void parseTestBlock(String text, List<VarDescriptor> vars) {
        Matcher m = PAT_TEST_BLOCK.matcher(text);
        if (!m.find()) return;

        String name = m.group("name");
        long[] rng = parseRange(m.group("range"));


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
    }


}
