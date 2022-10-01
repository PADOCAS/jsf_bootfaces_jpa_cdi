/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.com.jsf.hibernate.dao;

import br.com.jsf.hibernate.util.JPAUtil;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

/**
 * Dao Generico para receber qualquer tipo de classe para trabalhar as rotinas
 * mais comuns com o banco (Salvar, Update, Deletar, Consultar, Listar) Por isso
 * receber <E> para receber qualquer objeto
 *
 * @author lucia
 */
public class DAOGenerico<E> {

    //Sempre vai instanciar o entitymanager ao iniciar o DAO:
    private EntityManager entityManager = JPAUtil.getEntityManager();

    /**
     * Método para retornar o entityManager para poder fazer queries especificas
     * para cada objeto chamando ele
     *
     * @return
     */
    public EntityManager getEntityManager() {
        return entityManager;
    }

    public void salvar(E entity) {
        EntityTransaction entityTransaction = entityManager.getTransaction();

        //Inicia Transação:
        entityTransaction.begin();
        //Salvar:
        entityManager.persist(entity);
        //Comita:
        entityTransaction.commit();
    }

    public E saveOrUpdate(E entity) {
        EntityTransaction transaction = entityManager.getTransaction();

        //Inicia Transação
        transaction.begin();
        //Merge: Vai Salvar ou dar Update se já existir
        //Retorna a entidade salva no banco
        E entitySave = entityManager.merge(entity);
        //Comita:
        transaction.commit();

        return entitySave;
    }

    public void deletar(E entity) {
        EntityTransaction transaction = entityManager.getTransaction();
        Object primaryKey = JPAUtil.getPrimaryKey(entity);

        if (primaryKey != null) {
            //Inicia Transação
            transaction.begin();
            //Remove: Vai deletar o Objeto no banco
            entityManager.remove(entity);
            //Comita:
            transaction.commit();
        }

//        //Caso for deletar direto pela PK manualmente:
//        if (primaryKey != null) {
//            transaction.begin();
//            //Fazendo o processo manualmente por SQL:
//            entityManager.createNativeQuery("DELETE FROM " + entity.getClass().getSimpleName().toLowerCase() + " WHERE ID = " + primaryKey).executeUpdate();
//            transaction.commit();
//        }
    }

    public List<E> listar(Class<E> entity) {
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();
        List<E> list = entityManager.createQuery("from " + entity.getName()).getResultList();
        transaction.commit();

        return list;
    }
    
}
