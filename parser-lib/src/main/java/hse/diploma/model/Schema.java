package hse.diploma.model;

import java.util.List;

public record Schema (
        List<VarDescriptor> vars
) {
}
