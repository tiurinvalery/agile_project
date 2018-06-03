package com.agileengine.repository;

import com.agileengine.HibernateUtill;
import com.agileengine.model.AttributeEntity;
import com.agileengine.model.Button;
import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.Session;

import java.util.ArrayList;
import java.util.List;

public class AttributeRepository {

    public static AttributeEntity getAttributeById(Long id) {
        Session session = null;
        AttributeEntity attributeEntity = null;

        try {
            session = HibernateUtill.getSessionFactory().openSession();
            attributeEntity = (AttributeEntity) session.get(AttributeEntity.class, id);
            Hibernate.initialize(attributeEntity);
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
        return attributeEntity;

    }


    public static void createAttribute(AttributeEntity attributeEntity, Session session) {
        try {
            session.beginTransaction();

            session.save(attributeEntity);
            session.getTransaction().commit();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static List<AttributeEntity> getAttributesByBtnId(Long btnId, Session session) {
        try {
            session.beginTransaction();
            List<AttributeEntity> entities = new ArrayList<>();
            List<Object[]> attributeEntities = session.createNativeQuery("SELECT id, name, value, button_id FROM attributes").list();
            for (Object[] attribute : attributeEntities) {
                AttributeEntity entity = new AttributeEntity();
                entity.setId((Long) attribute[0]);
                entity.setName((String) attribute[1]);
                entity.setValue((String) attribute[2]);
                entity.setButton(ButtonRepository.getButtonById((Long) attribute[3]));
                entities.add(entity);
            }
            return entities;
        } catch (Exception ex) {
            return null;
        }
    }
}
