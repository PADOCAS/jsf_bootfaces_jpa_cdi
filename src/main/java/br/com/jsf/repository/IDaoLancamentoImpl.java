/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.com.jsf.repository;

import br.com.jsf.model.Lancamento;
import br.com.jsf.model.Pessoa;
import java.io.Serializable;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

/**
 *
 * @author lucia
 */
@Named
public class IDaoLancamentoImpl implements IDaoLancamento, Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    private EntityManager entityManager;

    @Override
    public List<Lancamento> listarLancamentos(Pessoa usuario) throws Exception {
        List<Lancamento> listLancamento = null;

        if (usuario != null
                && usuario.getId() != null) {
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
                //Não vamos usar o close mais, deixar para o framework controlar isso automaticamente!!!
                //Caso não fechar fica várias conexões abertas.. arrebentando com o banco!! Aguardar a aula onde ele mostra como ficará!
                //entityManager.close(); como está injetado agora, não podemos dar o close assim mais!!
            }
        }

        return listLancamento;
    }
}
