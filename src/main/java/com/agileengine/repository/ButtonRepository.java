package com.agileengine.repository;

import com.agileengine.HibernateUtill;
import com.agileengine.model.Button;
import org.hibernate.Hibernate;
import org.hibernate.Session;

public class ButtonRepository {

    public static Button getButtonById(Long id) {
        Session session = null;
        Button button = null;

        try {
            session = HibernateUtill.getSessionFactory().openSession();
            button = (Button) session.get(Button.class, id);
            Hibernate.initialize(button);
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
        return button;

    }

    public static void createButton(Button button, Session session) {
        try {
            session.beginTransaction();
            session.save(button);
            session.getTransaction().commit();


        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
