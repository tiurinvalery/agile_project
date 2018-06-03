package com.agileengine.model;


import javax.persistence.*;

@Entity(name = "Attributes")
@Table(name = "Attributes")
public class AttributeEntity extends BaseEntity {

    @Column(name = "name")
    private String name;

    @Column(name = "value")
    private String value;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    @JoinColumn(name = "button_id", referencedColumnName = "id")
    private Button button;

    public AttributeEntity() {
    }

    public AttributeEntity(String name, String value, Button button) {
        this.name = name;
        this.value = value;
        this.button = button;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Button getButton() {
        return button;
    }

    public void setButton(Button button) {
        this.button = button;
    }
}
