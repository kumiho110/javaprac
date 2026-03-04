package javaprac.model;

public interface Identifiable<ID> {
    ID getId();
    void setId(ID id);
}