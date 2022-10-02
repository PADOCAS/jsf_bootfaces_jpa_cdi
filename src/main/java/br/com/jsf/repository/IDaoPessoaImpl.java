/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.com.jsf.repository;

import br.com.jsf.hibernate.util.JPAUtil;
import br.com.jsf.model.Pessoa;
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

}
