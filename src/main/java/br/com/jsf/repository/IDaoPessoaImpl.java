/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.com.jsf.repository;

import br.com.jsf.hibernate.util.JPAUtil;
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

            pessoa = (Pessoa) entityManager.createQuery("SELECT p from Pessoa p where p.login = :login and p.senha = :senha")
                    .setParameter("login", login)
                    .setParameter("senha", senha)
                    .getSingleResult();

            entityTransaction.commit();
            entityManager.close();
        }

        return pessoa;
    }

    @Override
    public List<SelectItem> listaEstados() {
        List<SelectItem> listSelectItemEstado = new ArrayList<>();

        List<Estados> listEstados;

        EntityManager entityManager = JPAUtil.getEntityManager();
        EntityTransaction entityTransaction = entityManager.getTransaction();
        entityTransaction.begin();

        listEstados = entityManager.createQuery("SELECT e from Estados e ORDER BY nome").getResultList();

        if (listEstados != null
                && !listEstados.isEmpty()) {
            for (Estados estado : listEstados) {
                if (estado.getId() != null
                        && estado.getSigla() != null
                        && estado.getNome() != null) {
                    listSelectItemEstado.add(new SelectItem(estado.getId(), estado.getNome()));
                }
            }
        }

        entityTransaction.commit();
        entityManager.close();

        return listSelectItemEstado;
    }

}
