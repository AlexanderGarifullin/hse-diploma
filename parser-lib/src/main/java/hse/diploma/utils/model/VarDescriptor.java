package hse.diploma.utils.model;

import hse.diploma.utils.enums.BaseType;
import hse.diploma.utils.enums.Container;

import java.util.Map;

public record VarDescriptor(
        String name,
        Container container,
        BaseType baseType,
        Map<String,Object> props
) {}

