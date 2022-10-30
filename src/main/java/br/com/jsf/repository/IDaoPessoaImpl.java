/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.com.jsf.repository;

import br.com.jsf.hibernate.util.JPAUtil;
import br.com.jsf.model.Cidades;
import br.com.jsf.model.Estados;
import br.com.jsf.model.Pessoa;
import java.util.ArrayList;
import java.util.List;
import javax.faces.model.SelectItem;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

/**
 *
 * @author lucia
 */
public class IDaoPessoaImpl implements IDaoPessoa {

    @Override
    public Pessoa consultarUsuario(String login, String senha) throws Exception {
        Pessoa pessoa = null;

        if (login != null
                && !login.isEmpty()
                && senha != null
                && !senha.isEmpty()) {
            EntityManager entityManager = JPAUtil.getEntityManager();
            EntityTransaction entityTransaction = entityManager.getTransaction();
            entityTransaction.begin();

            try {
                pessoa = (Pessoa) entityManager.createQuery("SELECT p from Pessoa p where p.login = :login and p.senha = :senha")
                        .setParameter("login", login)
                        .setParameter("senha", senha)
                        .getSingleResult();
            } catch (Exception e) {
                e.printStackTrace();
                entityTransaction.rollback(); // desfaz transacao se ocorrer erro ao persitir
                throw new Exception("Erro ao consultar Usuário!\n" + e.getMessage());
            } finally {
                if (entityTransaction.isActive()) {
                    entityTransaction.commit();
                }
                entityManager.close();
            }
        }

        return pessoa;
    }

    @Override
    public List<SelectItem> listaEstados() throws Exception {
        List<SelectItem> listSelectItemEstado = new ArrayList<>();

        List<Estados> listEstados;

        EntityManager entityManager = JPAUtil.getEntityManager();
        EntityTransaction entityTransaction = entityManager.getTransaction();
        entityTransaction.begin();

        try {
            listEstados = entityManager.createQuery("SELECT e from Estados e ORDER BY nome").getResultList();

            if (listEstados != null
                    && !listEstados.isEmpty()) {
                for (Estados estado : listEstados) {
                    if (estado.getId() != null
                            && estado.getSigla() != null
                            && estado.getNome() != null) {
                        listSelectItemEstado.add(new SelectItem(estado, estado.getNome()));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            entityTransaction.rollback(); // desfaz transacao se ocorrer erro ao persitir
            throw new Exception("Erro ao listar estados!\n" + e.getMessage());
        } finally {
            if (entityTransaction.isActive()) {
                entityTransaction.commit();
            }
            entityManager.close();
        }

        return listSelectItemEstado;
    }

    @Override
    public List<SelectItem> listaCidades(Long estadoId) throws Exception {
        List<SelectItem> listSelectItemCidade = new ArrayList<>();

        EntityManager entityManager = JPAUtil.getEntityManager();
        EntityTransaction entityTransaction = entityManager.getTransaction();
        entityTransaction.begin();

        try {
            if (estadoId != null) {
                List<Cidades> listCidades = JPAUtil.getEntityManager().createQuery("SELECT c FROM Cidades c WHERE estados.id = :estadoId")
                        .setParameter("estadoId", estadoId)
                        .getResultList();

                if (listCidades != null
                        && !listCidades.isEmpty()) {
                    for (Cidades cidade : listCidades) {
                        listSelectItemCidade.add(new SelectItem(cidade, cidade.getNome()));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            entityTransaction.rollback(); // desfaz transacao se ocorrer erro ao persitir
            throw new Exception("Erro ao listar cidades!\n" + e.getMessage());
        } finally {
            if (entityTransaction.isActive()) {
                entityTransaction.commit();
            }
            entityManager.close();
        }

        return listSelectItemCidade;
    }

    @Override
    public void deletar(Pessoa pessoa) throws Exception {
        if (pessoa != null
                && pessoa.getId() != null) {
            EntityManager entityManager = JPAUtil.getEntityManager();
            EntityTransaction entityTransaction = entityManager.getTransaction();
            entityTransaction.begin();

            try {
                entityManager.createNativeQuery("DELETE from Lancamento WHERE pessoauser_id = " + pessoa.getId()).executeUpdate();
                entityManager.createNativeQuery("DELETE FROM Pessoa WHERE ID = " + pessoa.getId()).executeUpdate();
            } catch (Exception e) {
                e.printStackTrace();
                entityTransaction.rollback(); // desfaz transacao se ocorrer erro ao persitir
            } finally {
                if (entityTransaction.isActive()) {
                    entityTransaction.commit();
                }
                entityManager.close();
            }
        }
    }
}
