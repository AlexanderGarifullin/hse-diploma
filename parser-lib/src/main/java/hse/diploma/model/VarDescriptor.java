package hse.diploma.model;

import hse.diploma.enums.BaseType;
import hse.diploma.enums.Container;

import java.util.Map;

public record VarDescriptor(
        String name,
        Container container,
        BaseType baseType,
        Map<String,Object> props
) {}

