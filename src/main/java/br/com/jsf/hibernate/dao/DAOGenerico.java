/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.com.jsf.hibernate.dao;

import br.com.jsf.hibernate.util.JPAUtil;
import java.io.Serializable;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

/**
 * Dao Generico para receber qualquer tipo de classe para trabalhar as rotinas
 * mais comuns com o banco (Salvar, Update, Deletar, Consultar, Listar) Por isso
 * receber <E> para receber qualquer objeto
 *
 * @author lucia
 */
@Named
public class DAOGenerico<E> implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    private EntityManager entityManager;

    @Inject
    private JPAUtil jpaUtil;

    /**
     * Método para retornar o entityManager para poder fazer queries especificas
     * para cada objeto chamando ele
     *
     * @return
     */
    public EntityManager getEntityManager() {
        return entityManager;
    }

    public void salvar(E entity) throws Exception {
        EntityTransaction entityTransaction = entityManager.getTransaction();

        //Inicia Transação:
        entityTransaction.begin();
        try {
            //Salvar:
            entityManager.persist(entity);
        } catch (Exception e) {
            e.printStackTrace();
            entityTransaction.rollback(); // desfaz transacao se ocorrer erro ao persitir
            throw new Exception("Erro ao Salvar!\n" + e.getMessage());
        } finally {
            //Comita:
            if (entityTransaction.isActive()) {
                entityTransaction.commit();
            }
            //Não vamos usar o close mais, deixar para o framework controlar isso automaticamente!!!
            //Caso não fechar fica várias conexões abertas.. arrebentando com o banco!! Aguardar a aula onde ele mostra como ficará!
            //entityManager.close(); como está injetado agora, não podemos dar o close assim mais!!
        }
    }

    public E saveOrUpdate(E entity) throws Exception {
        E entitySave = null;
        EntityTransaction transaction = entityManager.getTransaction();

        //Inicia Transação
        transaction.begin();
        try {
            //Merge: Vai Salvar ou dar Update se já existir
            //Retorna a entidade salva no banco
            entitySave = entityManager.merge(entity);
            entityManager.flush();
        } catch(javax.persistence.PersistenceException ex) {
            ex.printStackTrace();
            if(ex.getCause() != null
                    && ex.getCause().getCause() != null
                    && ex.getCause().getCause().toString().contains("uk_login")) {
                throw new Exception("Erro ao Salvar!\nJá existe esse mesmo login registrado no sistema! Não é permitido criar um login duplicado!\nVerifique!");
            } else {
                throw new Exception("Erro ao Salvar!\n" + ex.getMessage());
            }
        } catch (Exception ex1) {
            ex1.printStackTrace();
            transaction.rollback(); // desfaz transacao se ocorrer erro ao persitir
            throw new Exception("Erro ao Salvar!\n" + ex1.getMessage());
        } finally {
            //Comita:
            if (transaction.isActive()) {
                transaction.commit();
            }
            //Não vamos usar o close mais, deixar para o framework controlar isso automaticamente!!!
            //Caso não fechar fica várias conexões abertas.. arrebentando com o banco!! Aguardar a aula onde ele mostra como ficará!
            //entityManager.close(); como está injetado agora, não podemos dar o close assim mais!!
        }

        return entitySave;
    }

    public void deletar(E entity) throws Exception {
        EntityTransaction transaction = entityManager.getTransaction();
        Object primaryKey = jpaUtil.getPrimaryKey(entity);

        //O delete dessa forma por REMOVE (o objeto tem que estár identico ao banco de dados... se não vai dar erro ao deletar!
        //Por isso, ao inves de consultar o usuario pegar o objeto e remover! É melhor fazer direto pela PK dele.. (segunda forma abaixo)
        //Caso aparecerem problemas com relação a chave composta etc.. implementamos aqui o remove com uma consulta no banco antes!
        //Forma 1):
//        if (primaryKey != null) {
//            //Inicia Transação
//            transaction.begin();
//            //Remove: Vai deletar o Objeto no banco
//            entityManager.remove(entity);
//            //Comita:
//            transaction.commit();
//            entityManager.close();
//        }
        //Forma 2):
        //Caso for deletar direto pela PK manualmente:
        if (primaryKey != null) {
            transaction.begin();
            try {
                //Fazendo o processo manualmente por SQL:
                entityManager.createNativeQuery("DELETE FROM " + entity.getClass().getSimpleName().toLowerCase() + " WHERE ID = " + primaryKey).executeUpdate();
            } catch (Exception e) {
                e.printStackTrace();
                transaction.rollback(); // desfaz transacao se ocorrer erro ao persitir
                throw new Exception("Erro ao Excluir!\n" + e.getMessage());
            } finally {
                if (transaction.isActive()) {
                    transaction.commit();
                }
                //Não vamos usar o close mais, deixar para o framework controlar isso automaticamente!!!
                //Caso não fechar fica várias conexões abertas.. arrebentando com o banco!! Aguardar a aula onde ele mostra como ficará!
                //entityManager.close(); como está injetado agora, não podemos dar o close assim mais!!
            }
        }
    }

    public List<E> listar(Class<E> entity) throws Exception {
        List<E> list = null;
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();

        try {
            list = entityManager.createQuery("from " + entity.getName()).getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            transaction.rollback(); // desfaz transacao se ocorrer erro ao persitir
            throw new Exception("Erro ao Listar!\n" + e.getMessage());
        } finally {
            if (transaction.isActive()) {
                transaction.commit();
            }
            //Não vamos usar o close mais, deixar para o framework controlar isso automaticamente!!!
            //Caso não fechar fica várias conexões abertas.. arrebentando com o banco!! Aguardar a aula onde ele mostra como ficará!
            //entityManager.close(); como está injetado agora, não podemos dar o close assim mais!!
        }

        return list;
    }

    public E consultar(Class<E> entidade, Object primarykey) throws Exception {
        E objeto = null;
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();

        try {
            objeto = (E) entityManager.find(entidade, primarykey);
        } catch (Exception e) {
            e.printStackTrace();
            transaction.rollback(); // desfaz transacao se ocorrer erro ao persitir
            throw new Exception("Erro ao consultar!\n" + e.getMessage());
        } finally {
            if (transaction.isActive()) {
                transaction.commit();
            }
            //Não vamos usar o close mais, deixar para o framework controlar isso automaticamente!!!
            //Caso não fechar fica várias conexões abertas.. arrebentando com o banco!! Aguardar a aula onde ele mostra como ficará!
            //entityManager.close(); como está injetado agora, não podemos dar o close assim mais!!
        }

        return objeto;
    }

}
