package com.agileengine.model;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Objects;

@Entity(name = "buttons")
@Table(name = "buttons")
public class Button extends BaseEntity {

    @Column(name = "number_of_attributes")
    private int numberOfAttributes;

    @Column(name = "right_id")
    private String rightId;

    public Button() {
    }

    public Button(int numberOfAttributes, String rightId) {
        this.numberOfAttributes = numberOfAttributes;
        this.rightId = rightId;
    }

    public int getNumberOfAttributes() {
        return numberOfAttributes;
    }

    public void setNumberOfAttributes(int numberOfAttributes) {
        this.numberOfAttributes = numberOfAttributes;
    }

    public String getRightId() {
        return rightId;
    }

    public void setRightId(String rightId) {
        this.rightId = rightId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Button button = (Button) o;
        return getNumberOfAttributes() == button.getNumberOfAttributes() &&
                Objects.equals(getRightId(), button.getRightId());
    }

    @Override
    public int hashCode() {

        return Objects.hash(getNumberOfAttributes(), getRightId());
    }
}
