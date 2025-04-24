package hse.diploma.parser;

import hse.diploma.utils.enums.BaseType;
import hse.diploma.utils.enums.Container;
import hse.diploma.utils.enums.props.PropKey;
import hse.diploma.utils.map.PropMapBuilder;
import hse.diploma.utils.model.Schema;
import hse.diploma.utils.model.VarDescriptor;
import hse.diploma.utils.nlp.TextProcessing;
import lombok.experimental.UtilityClass;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static hse.diploma.parser.math.MathParser.parseRange;
import static hse.diploma.utils.pattern.RegexPattern.PAT_TEST_BLOCK;
import static hse.diploma.utils.pattern.RegexPattern.RANGE_BLOCK;


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
