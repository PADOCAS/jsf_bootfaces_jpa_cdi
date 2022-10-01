/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.com.jsf.hibernate.util;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 *
 * @author lucia
 */
public class JPAUtil {

    private static EntityManagerFactory factory = null;

    static {
        //Sempre que chamar essa classe vai iniciar o factory (metodo init())!
        init();
    }

    public static void init() {
        try {
            if (factory == null) {
                //Vai ler o arquivo persistence com o nome (jsf_persistence_unit) >> No arquivo persistence.xml: <persistence-unit name="jsf_persistence_unit">
                //Cria apenas 1 vez, quando o factory é NULO ainda...
                factory = Persistence.createEntityManagerFactory("jsf_persistence_unit");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static EntityManager getEntityManager() {
        return factory.createEntityManager();
    }

    /**
     * Método para retornar a primary key de uma entidade
     *
     * @param entity
     * @return Primary Key da entidade
     */
    public static Object getPrimaryKey(Object entity) {
        return factory.getPersistenceUnitUtil().getIdentifier(entity);
    }
}
