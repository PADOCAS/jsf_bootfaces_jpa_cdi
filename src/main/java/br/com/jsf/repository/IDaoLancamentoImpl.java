/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.com.jsf.repository;

import br.com.jsf.hibernate.util.JPAUtil;
import br.com.jsf.model.Lancamento;
import br.com.jsf.model.Pessoa;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

/**
 *
 * @author lucia
 */
public class IDaoLancamentoImpl implements IDaoLancamento {

    @Override
    public List<Lancamento> listarLancamentos(Pessoa usuario) throws Exception {
        List<Lancamento> listLancamento = null;

        if (usuario != null
                && usuario.getId() != null) {
            EntityManager entityManager = JPAUtil.getEntityManager();
            EntityTransaction entityTransaction = entityManager.getTransaction();

            entityTransaction.begin();

            try {
                listLancamento = entityManager.createQuery("SELECT l from Lancamento l WHERE pessoaUser.id = :userId")
                        .setParameter("userId", usuario.getId())
                        .getResultList();
            } catch (Exception e) {
                e.printStackTrace();
                entityTransaction.rollback(); // desfaz transacao se ocorrer erro ao persitir
                throw new Exception("Erro ao Listar lançamentos!\n" + e.getMessage());
            } finally {
                if (entityTransaction.isActive()) {
                    entityTransaction.commit();
                }
                entityManager.close();
            }
        }

        return listLancamento;
    }

}
