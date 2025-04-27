package hse.diploma.utility.helper.container;

import hse.diploma.enums.props.PropKey;
import hse.diploma.model.VarDescriptor;
import hse.diploma.utility.structure.Pair;
import hse.diploma.utility.Settings;
import lombok.experimental.UtilityClass;

import java.util.Map;

@UtilityClass
public class ScalarHelper {


    public static Pair<Long, Long> getBoundaries(VarDescriptor var, Map<String, Object> context) {
        Long left = 0L, right = left;

        if (var.props().containsKey(PropKey.MIN.key())) {
            left = (Long) var.props().get(PropKey.MIN.key());
        } else if (var.props().containsKey(PropKey.VAR_MIN.key())){
            String field = (String) var.props().get(PropKey.VAR_MIN.key());
            if (context.containsKey(field)) {
                left = (Long) context.get(field);
            }
        }

        if (var.props().containsKey(PropKey.MAX.key())) {
            right = (Long) var.props().get(PropKey.MAX.key());
        } else if (var.props().containsKey(PropKey.VAR_MAX.key())){
            String field = (String) var.props().get(PropKey.VAR_MAX.key());
            if (context.containsKey(field)) {
                right = (Long) context.get(field);
            }
        }

        return Pair.of(left, right);
    }

    public static Pair<Long, Long> getBoundaries(VarDescriptor var) {
        Long left = 0L, right = left;

        if (var.props().containsKey(PropKey.MIN.key())) {
            left = (Long) var.props().get(PropKey.MIN.key());
        }

        if (var.props().containsKey(PropKey.MAX.key())) {
            right = (Long) var.props().get(PropKey.MAX.key());
        }
        return Pair.of(left, right);
    }

    public static Pair<Long, Long> getSmallBoundaries(Pair<Long, Long> boundaries) {
        long min = boundaries.first, max = boundaries.second;
        long length = max - min + 1;
        max = Math.min(min + Settings.MIN_THRESHOLD_OFFSET, min + length / Settings.PERCENT_SMALL);
        return Pair.of(min, max);
    }

    public static Pair<Long, Long> getBigBoundaries(Pair<Long, Long> boundaries) {
        long min = boundaries.first, max = boundaries.second;
        long length = max - min + 1;
        min = Math.max(max - Settings.MAX_THRESHOLD_OFFSET, max - length / Settings.PERCENT_BIG);
        return Pair.of(min, max);
    }
}
