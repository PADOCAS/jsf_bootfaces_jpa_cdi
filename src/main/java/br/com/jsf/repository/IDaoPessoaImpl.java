/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.com.jsf.repository;

import br.com.jsf.model.Cidades;
import br.com.jsf.model.Estados;
import br.com.jsf.model.Pessoa;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.faces.model.SelectItem;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.NoResultException;

/**
 *
 * @author lucia
 */
@Named
public class IDaoPessoaImpl implements IDaoPessoa, Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    private EntityManager entityManager;

    @Override
    public Pessoa consultarUsuario(String login, String senha) throws Exception {
        Pessoa pessoa = null;

        if (login != null
                && !login.isEmpty()
                && senha != null
                && !senha.isEmpty()) {
            EntityTransaction entityTransaction = entityManager.getTransaction();
            entityTransaction.begin();

            try {
                pessoa = (Pessoa) entityManager.createQuery("SELECT p from Pessoa p where p.login = :login and p.senha = :senha")
                        .setParameter("login", login)
                        .setParameter("senha", senha)
                        .getSingleResult();
            } catch (NoResultException e) {
                e.printStackTrace();
                entityTransaction.rollback(); // desfaz transacao se ocorrer erro ao persitir
                throw new Exception("Usuário não encontrado!");
            } catch (Exception e) {
                e.printStackTrace();
                entityTransaction.rollback(); // desfaz transacao se ocorrer erro ao persitir
                throw new Exception("Erro ao consultar Usuário!\n" + e.getMessage());
            } finally {
                if (entityTransaction.isActive()) {
                    entityTransaction.commit();
                }
                //Não vamos usar o close mais, deixar para o framework controlar isso automaticamente!!!
                //Caso não fechar fica várias conexões abertas.. arrebentando com o banco!! Aguardar a aula onde ele mostra como ficará!
                //entityManager.close(); como está injetado agora, não podemos dar o close assim mais!!
            }
        }

        return pessoa;
    }

    @Override
    public List<SelectItem> listaEstados() throws Exception {
        List<SelectItem> listSelectItemEstado = new ArrayList<>();

        List<Estados> listEstados;

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
            //Não vamos usar o close mais, deixar para o framework controlar isso automaticamente!!!
            //Caso não fechar fica várias conexões abertas.. arrebentando com o banco!! Aguardar a aula onde ele mostra como ficará!
            //entityManager.close(); como está injetado agora, não podemos dar o close assim mais!!
        }

        return listSelectItemEstado;
    }

    @Override
    public List<SelectItem> listaCidades(Long estadoId) throws Exception {
        List<SelectItem> listSelectItemCidade = new ArrayList<>();

        EntityTransaction entityTransaction = entityManager.getTransaction();
        entityTransaction.begin();

        try {
            if (estadoId != null) {
                List<Cidades> listCidades = entityManager.createQuery("SELECT c FROM Cidades c WHERE estados.id = :estadoId")
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
            //Não vamos usar o close mais, deixar para o framework controlar isso automaticamente!!!
            //Caso não fechar fica várias conexões abertas.. arrebentando com o banco!! Aguardar a aula onde ele mostra como ficará!
            //entityManager.close(); como está injetado agora, não podemos dar o close assim mais!!
        }

        return listSelectItemCidade;
    }

    @Override
    public void deletar(Pessoa pessoa) throws Exception {
        if (pessoa != null
                && pessoa.getId() != null) {
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
                //Não vamos usar o close mais, deixar para o framework controlar isso automaticamente!!!
                //Caso não fechar fica várias conexões abertas.. arrebentando com o banco!! Aguardar a aula onde ele mostra como ficará!
                //entityManager.close(); como está injetado agora, não podemos dar o close assim mais!!
            }
        }
    }

    @Override
    public List<Pessoa> buscarPessoaRelatorio(Map<String, Object> param) throws Exception {
        List<Pessoa> listPessoa = null;

        if (param != null) {
            EntityTransaction entityTransaction = entityManager.getTransaction();

            entityTransaction.begin();

            try {
                StringBuilder sql = new StringBuilder();
                //Quando é data, passar como java.util.date para o hibernate, então converte a data para string depois para java.util.date:
                //new SimpleDateFormat("yyyy-MM-dd").parse(new SimpleDateFormat("yyyy-MM-dd").format(param.get("dataInicio")))

                //Usamos o LOWER PARA IGNORAR CASE DA BUSCA POR NOME > PASSANDO PARAMETRO SEMPRE COM LOWERCASE AI BUSCA SEMPRE TUDO!
                if (param.get("dataInicio") != null
                        && param.get("dataFim") != null
                        && param.get("nome") != null) {
                    sql.append("SELECT p from Pessoa p WHERE datanascimento between :dataInicio and :dataFim and lower(nome) LIKE :nome ORDER BY nome");
                    listPessoa = entityManager.createQuery(sql.toString())
                            .setParameter("dataInicio", new SimpleDateFormat("yyyy-MM-dd").parse(new SimpleDateFormat("yyyy-MM-dd").format(param.get("dataInicio"))))
                            .setParameter("dataFim", new SimpleDateFormat("yyyy-MM-dd").parse(new SimpleDateFormat("yyyy-MM-dd").format(param.get("dataFim"))))
                            .setParameter("nome", param.get("nome"))
                            .getResultList();
                } else if (param.get("dataInicio") != null
                        && param.get("dataFim") != null
                        && param.get("nome") == null) {
                    sql.append("SELECT p from Pessoa p WHERE datanascimento between :dataInicio and :dataFim ORDER BY nome");
                    listPessoa = entityManager.createQuery(sql.toString())
                            .setParameter("dataInicio", new SimpleDateFormat("yyyy-MM-dd").parse(new SimpleDateFormat("yyyy-MM-dd").format(param.get("dataInicio"))))
                            .setParameter("dataFim", new SimpleDateFormat("yyyy-MM-dd").parse(new SimpleDateFormat("yyyy-MM-dd").format(param.get("dataFim"))))
                            .getResultList();
                } else if (param.get("dataInicio") == null
                        && param.get("dataFim") == null
                        && param.get("nome") != null) {
                    sql.append("SELECT p from Pessoa p WHERE lower(nome) LIKE :nome ORDER BY nome");
                    listPessoa = entityManager.createQuery(sql.toString())
                            .setParameter("nome", param.get("nome"))
                            .getResultList();
                } else {
                    sql.append("SELECT p from Pessoa p ORDER BY nome");
                    listPessoa = entityManager.createQuery(sql.toString())
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

        return listPessoa;
    }
}
