/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.com.jsf.repository;

import br.com.jsf.model.Lancamento;
import br.com.jsf.model.Pessoa;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
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

    @Override
    public List<Lancamento> listarLancamentosLimit10(Pessoa usuario) throws Exception {
        //Limitar em 10 os resultados para melhorar performance na tela do cadastro:
        List<Lancamento> listLancamento = null;

        if (usuario != null
                && usuario.getId() != null) {
            EntityTransaction entityTransaction = entityManager.getTransaction();

            entityTransaction.begin();

            try {
                listLancamento = entityManager.createQuery("SELECT l from Lancamento l WHERE pessoaUser.id = :userId ORDER BY id desc ")
                        .setParameter("userId", usuario.getId())
                        .setMaxResults(10)
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

    @Override
    public List<Lancamento> buscarLancamentoRelatorio(Pessoa usuario, Map<String, Object> param) throws Exception {
        List<Lancamento> listLancamento = null;

        if (usuario != null
                && usuario.getId() != null
                && param != null) {
            EntityTransaction entityTransaction = entityManager.getTransaction();

            entityTransaction.begin();

            try {
                StringBuilder sql = new StringBuilder();
                //Quando é data, passar como java.util.date para o hibernate, então converte a data para string depois para java.util.date:
                //new SimpleDateFormat("yyyy-MM-dd").parse(new SimpleDateFormat("yyyy-MM-dd").format(param.get("dataInicio")))

                if (param.get("dataInicio") != null
                        && param.get("dataFim") != null
                        && param.get("numero") != null) {
                    sql.append("SELECT l from Lancamento l WHERE pessoaUser.id = :userId and dataEmissao between :dataInicio and :dataFim and numeroNota = :numero");
                    listLancamento = entityManager.createQuery(sql.toString())
                            .setParameter("userId", usuario.getId())
                            .setParameter("dataInicio", new SimpleDateFormat("yyyy-MM-dd").parse(new SimpleDateFormat("yyyy-MM-dd").format(param.get("dataInicio"))))
                            .setParameter("dataFim", new SimpleDateFormat("yyyy-MM-dd").parse(new SimpleDateFormat("yyyy-MM-dd").format(param.get("dataFim"))))
                            .setParameter("numero", param.get("numero"))
                            .getResultList();
                } else if (param.get("dataInicio") != null
                        && param.get("dataFim") != null
                        && param.get("numero") == null) {
                    sql.append("SELECT l from Lancamento l WHERE pessoaUser.id = :userId and dataEmissao between :dataInicio and :dataFim");
                    listLancamento = entityManager.createQuery(sql.toString())
                            .setParameter("userId", usuario.getId())
                            .setParameter("dataInicio", new SimpleDateFormat("yyyy-MM-dd").parse(new SimpleDateFormat("yyyy-MM-dd").format(param.get("dataInicio"))))
                            .setParameter("dataFim", new SimpleDateFormat("yyyy-MM-dd").parse(new SimpleDateFormat("yyyy-MM-dd").format(param.get("dataFim"))))
                            .getResultList();
                } else if (param.get("dataInicio") == null
                        && param.get("dataFim") == null
                        && param.get("numero") != null) {
                    sql.append("SELECT l from Lancamento l WHERE pessoaUser.id = :userId and numeroNota = :numero");
                    listLancamento = entityManager.createQuery(sql.toString())
                            .setParameter("userId", usuario.getId())
                            .setParameter("numero", param.get("numero"))
                            .getResultList();
                } else {
                    sql.append("SELECT l from Lancamento l WHERE pessoaUser.id = :userId");
                    listLancamento = entityManager.createQuery(sql.toString())
                            .setParameter("userId", usuario.getId())
                            .getResultList();
                }
            } catch (Exception e) {
                e.printStackTrace();
                entityTransaction.rollback(); // desfaz transacao se ocorrer erro ao persitir
                throw new Exception("Erro ao gerar Relatório!\n" + e.getMessage());
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
