/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.com.jsf.controller;

import br.com.jsf.hibernate.dao.DAOGenerico;
import br.com.jsf.model.Pessoa;
import br.com.jsf.repository.IDaoPessoa;
import br.com.jsf.repository.IDaoPessoaImpl;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

/**
 *
 * @author lucia
 */
@ManagedBean(name = "pessoaController")
@ViewScoped
public class PessoaController {

    private IDaoPessoa iDaoPessoa = new IDaoPessoaImpl();

    private Pessoa pessoa = new Pessoa();

    private DAOGenerico<Pessoa> daoGenerico = new DAOGenerico<>();

    private List<Pessoa> listPessoa = null;

    @javax.annotation.PostConstruct
    public void initManagedBean() {
        carregarPessoas();
    }

    public String salvar() {
        if (getPessoa() != null) {
            //Merge >> SaveOrUpdate - Vai salvar e Retornar o objeto salvo pra gente:
            setPessoa(daoGenerico.saveOrUpdate(pessoa));
        }

        //Atualiza Lista Pessoas:
        carregarPessoas();
        //Retornando Vazio, vai ficar na mesma página
        return "";
    }

    public String deletar() {
        if (getPessoa() != null) {
            //Deletar o Objeto:
            daoGenerico.deletar(pessoa);

            if (getPessoa().getId() != null
                    && getPessoa().getNome() != null
                    && getPessoa().getSobrenome() != null) {
                //Instancia nova Pessoa após salvar - Tras na tela os campos em branco, nova pessoa:
                setPessoa(new Pessoa());
            }
        }

        //Atualiza Lista Pessoas:
        carregarPessoas();
        //Retornando Vazio, vai ficar na mesma página
        return "";
    }

    public void carregarPessoas() {
        setListPessoa(daoGenerico.listar(Pessoa.class));
    }

    public String limpar() {
        setPessoa(new Pessoa());
        return "";
    }

    /**
     * Método usado para Logar -- tela de login (index.xhtml)
     *
     * @return Direcionamento (Pagina principal ou volta para o Login)
     */
    public String logar() {
        try {
            if (getPessoa() != null
                    && getPessoa().getLogin() != null
                    && getPessoa().getSenha() != null) {
                Pessoa pessoa = iDaoPessoa.consultarUsuario(getPessoa().getLogin(), getPessoa().getSenha());

                if (pessoa != null) {
                    //Adicionar o usuário na sessão:
                    if (FacesContext.getCurrentInstance() != null
                            && FacesContext.getCurrentInstance().getExternalContext() != null
                            && FacesContext.getCurrentInstance().getExternalContext().getSessionMap() != null) {
                        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("usuario", pessoa);
                        return "/principal/primeiraPagina.xhtml";
                    }
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();            
        }

        return "";
    }

    public Pessoa getPessoa() {
        return pessoa;
    }

    public void setPessoa(Pessoa pessoa) {
        this.pessoa = pessoa;
    }

    public List<Pessoa> getListPessoa() {
        return listPessoa;
    }

    public void setListPessoa(List<Pessoa> listPessoa) {
        this.listPessoa = listPessoa;
    }
}
