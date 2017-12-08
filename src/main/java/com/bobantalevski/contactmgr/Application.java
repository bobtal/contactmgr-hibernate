package com.bobantalevski.contactmgr;

import com.bobantalevski.contactmgr.model.Contact;
import com.bobantalevski.contactmgr.model.Contact.ContactBuilder;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.service.ServiceRegistry;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import java.util.List;

public class Application {
    // Hold a reusable reference to a SessionFactory (since we need only one)
    private static final SessionFactory sessionFactory = buildSessionFactory();

    private static SessionFactory buildSessionFactory() {
        // A SessionFactory is set up once for an application!
        final ServiceRegistry registry = new StandardServiceRegistryBuilder()
                .configure() // configures settings from hibernate.cfg.xml
                .build();
        return new MetadataSources(registry)
                .buildMetadata()
                .buildSessionFactory();
    }

    public static void main(String[] args) {
        Contact contact =
                new ContactBuilder("Boban", "Talevski")
                .withEmail("bobtal@email.com")
                .withPhone(22222222L)
                .build();
        int id = save(contact);

        // Display a list of contacts before the update
        System.out.printf("%n%nBefore the update%n%n");
        fetchAllContacts().forEach(System.out::println);

        // Get the persisted contact
        Contact c = findContactById(id);

        // Update the contact
        c.setFirstName("Mile");

        // Persist the changes
        System.out.printf("%nUpdating...%n");
        update(c);
        System.out.printf("%nUpdate complete!%n");

        // Display a list of contacts after the update
        System.out.printf("%nAfter the update%n");
        fetchAllContacts().forEach(System.out::println);

        // Get a contact for deletion
        Contact d = findContactById(id);

        // Delete the contact
        System.out.printf("%nDeleting...%n");
        delete(d);
        System.out.printf("%nDeletion complete!%n");

        // Display a list of contacts after the deletion
        System.out.printf("%nAfter the deletion%n");
        fetchAllContacts().forEach(System.out::println);

        sessionFactory.close();
    }

    private static Contact findContactById(int id) {
        // Open a session
        Session session = sessionFactory.openSession();

        // Retrieve the persistent object (or null if not found)
        Contact contact = session.get(Contact.class, id);

        // Close the session
        session.close();

        // Return the object
        return contact;
    }

    private static void update(Contact contact) {
        // Open a session
        Session session = sessionFactory.openSession();

        // Begin a transaction
        session.beginTransaction();

        // Use the session to update the contact
        session.update(contact);

        // Commit the transaction
        session.getTransaction().commit();

        // Close the session
        session.close();
    }

    private static void delete(Contact contact) {
        // Open a session
        Session session = sessionFactory.openSession();

        // Begin a transaction
        session.beginTransaction();

        // Use the session to delete the contact
        session.delete(contact);

        // Commit the transaction
        session.getTransaction().commit();

        // Close the session
        session.close();
    }

    @SuppressWarnings("unchecked")
    private static List<Contact> fetchAllContacts() {
        // Open a session
        Session session = sessionFactory.openSession();

        // DEPRECATED: Create a Criteria object
//        Criteria criteria = session.createCriteria(Contact.class);

        // DEPRECATED: Get a list of Contact objects according to the Criteria object
//        List<Contact> contacts = criteria.list();

        // UPDATED: Create CriteriaBuilder
        CriteriaBuilder builder = session.getCriteriaBuilder();

        // UPDATED: Create CriteriaQuery
        CriteriaQuery<Contact> criteriaQuery = builder.createQuery(Contact.class);

        // UPDATED: Specify criteria root
        criteriaQuery.from(Contact.class);

        // UPDATED: Execute query
        List<Contact> contacts = session.createQuery(criteriaQuery).getResultList();

        // Close the session
        session.close();

        return contacts;
    }

    private static int save(Contact contact) {
        // Open a session
        Session session = sessionFactory.openSession();

        // Begin a transaction
        session.beginTransaction();

        // Use the session to save the contact object
        int id = (int)session.save(contact);

        // Commit the transaction
        session.getTransaction().commit();

        // Close the session
        session.close();

        return id;
    }
}
