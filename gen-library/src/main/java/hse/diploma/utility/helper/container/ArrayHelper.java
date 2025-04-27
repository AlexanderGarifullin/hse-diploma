package hse.diploma.utility.helper.container;

import hse.diploma.enums.props.PropKey;
import hse.diploma.model.VarDescriptor;
import hse.diploma.utility.structure.Pair;
import lombok.experimental.UtilityClass;

import java.util.Map;

@UtilityClass
public class ArrayHelper {

    public static Pair<Long, Long> getLenBoundaries(VarDescriptor var, Map<String, Object> context) {
        Long left = 0L, right = left;

        if (var.props().containsKey(PropKey.MIN_LEN.key())) {
            left = (Long) var.props().get(PropKey.MIN_LEN.key());
        } else if (var.props().containsKey(PropKey.VAR_MIN_LEN.key())){
            String field = (String) var.props().get(PropKey.VAR_MIN_LEN.key());
            if (context.containsKey(field)) {
                left = (Long) context.get(field);
            }
        }

        if (var.props().containsKey(PropKey.MAX_LEN.key())) {
            right = (Long) var.props().get(PropKey.MAX_LEN.key());
        } else if (var.props().containsKey(PropKey.VAR_MAX_LEN.key())){
            String field = (String) var.props().get(PropKey.VAR_MAX_LEN.key());
            if (context.containsKey(field)) {
                right = (Long) context.get(field);
            }
        }

        return Pair.of(left, right);
    }
}
