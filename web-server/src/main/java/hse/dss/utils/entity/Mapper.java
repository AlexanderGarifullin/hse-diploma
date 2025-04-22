package hse.dss.utils.entity;

public interface Mapper<F,T> {

    F fromEntity(T entity);

    T toEntity(F dto);
}
