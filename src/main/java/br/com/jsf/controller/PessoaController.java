/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.com.jsf.controller;

import br.com.jsf.hibernate.dao.DAOGenerico;
import br.com.jsf.model.Pessoa;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

/**
 *
 * @author lucia
 */
@ManagedBean(name = "pessoaController")
@ViewScoped
public class PessoaController {

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
