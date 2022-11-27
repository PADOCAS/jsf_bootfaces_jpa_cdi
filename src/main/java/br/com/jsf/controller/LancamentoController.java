/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.com.jsf.controller;

import br.com.jsf.hibernate.dao.DAOGenerico;
import br.com.jsf.model.Lancamento;
import br.com.jsf.model.Pessoa;
import br.com.jsf.repository.IDaoLancamento;
import java.io.Serializable;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

/**
 *
 * @author lucia
 */
@ViewScoped
@Named(value = "lancamentoController")
public class LancamentoController implements Serializable {

    private static final long serialVersionUID = 1L;

    //Bean sempre iniciad no controller:
    private Lancamento lancamento = new Lancamento();

    @Inject
    private IDaoLancamento daoLancamento;

    //DaoGenerico de Lancamento:
    @Inject
    private DAOGenerico<Lancamento> daoGenerico;

    private List<Lancamento> listLancamento = null;

    @javax.annotation.PostConstruct
    public void initManagedBean() {
        carregarLancamentos();
    }

    public String salvar() {
        try {
            if (FacesContext.getCurrentInstance() != null
                    && FacesContext.getCurrentInstance().getExternalContext() != null
                    && FacesContext.getCurrentInstance().getExternalContext().getSessionMap() != null
                    && FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("usuario") != null) {
                Pessoa pessoaUser = (Pessoa) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("usuario");

                if (pessoaUser != null) {
                    lancamento.setPessoaUser(pessoaUser);
                    setLancamento(daoGenerico.saveOrUpdate(lancamento));

                    mostrarMsg("Cadastro salvo com sucesso!");
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            mostrarMsg(ex.getMessage());
        }

        //Atualiza Lista Lancamentos:
        carregarLancamentos();
        //Retornando Vazio, vai ficar na mesma página
        return "";
    }

    public String limpar() {
        setLancamento(new Lancamento());
        return "";
    }

    public String resetar() {
        setLancamento(new Lancamento());
        return "";
    }

    public String deletar() {
        try {
            if (getLancamento() != null
                    && getLancamento().getId() != null) {
                //Deletar o Objeto:
                daoGenerico.deletar(getLancamento());

                if (getLancamento().getId() != null) {
                    //Instancia nova Pessoa após salvar - Tras na tela os campos em branco, nova pessoa:
                    setLancamento(new Lancamento());
                }
            } else {
                mostrarMsg("Selecione um lançamento já cadastrado para excluir.");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            mostrarMsg("Erro ao excluir registro!/n" + ex.getMessage());
        }

        //Atualiza Lista Lancamentos:
        carregarLancamentos();
        //Retornando Vazio, vai ficar na mesma página
        return "";
    }

    public void carregarLancamentos() {
        setListLancamento(null);

        try {
            if (FacesContext.getCurrentInstance() != null
                    && FacesContext.getCurrentInstance().getExternalContext() != null
                    && FacesContext.getCurrentInstance().getExternalContext().getSessionMap() != null
                    && FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("usuario") != null) {
                Pessoa pessoaUser = (Pessoa) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("usuario");

                if (pessoaUser != null) {
                    setListLancamento(daoLancamento.listarLancamentos(pessoaUser));
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public Boolean getRenderedUsuarioPerfilAdmin() {
        if (FacesContext.getCurrentInstance() != null
                && FacesContext.getCurrentInstance().getExternalContext() != null
                && FacesContext.getCurrentInstance().getExternalContext().getSessionMap() != null
                && FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("usuario") != null) {
            Pessoa pessoaUser = (Pessoa) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("usuario");

            if (pessoaUser != null
                    && pessoaUser.getPerfil() != null
                    && pessoaUser.getPerfil().equals("A")) {
                return true;
            }
        }

        return false;
    }

    public String getOperadorLogado() {
        if (FacesContext.getCurrentInstance() != null
                && FacesContext.getCurrentInstance().getExternalContext() != null
                && FacesContext.getCurrentInstance().getExternalContext().getSessionMap() != null
                && FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("usuario") != null) {
            Pessoa pessoaUser = (Pessoa) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("usuario");

            if (pessoaUser != null
                    && pessoaUser.getLogin() != null) {
                return "Operador Logado: " + pessoaUser.getLogin();
            }
        }

        return "";
    }

    private void mostrarMsg(String mensagem) {
        FacesMessage message = new FacesMessage(mensagem);
        //Pode ser dado a mensagem sobre algum componente especifico ou null quando é geral:
        FacesContext.getCurrentInstance().addMessage(null, message);
    }

    public Lancamento getLancamento() {
        return lancamento;
    }

    public void setLancamento(Lancamento lancamento) {
        this.lancamento = lancamento;
    }

    public List<Lancamento> getListLancamento() {
        return listLancamento;
    }

    public void setListLancamento(List<Lancamento> listLancamento) {
        this.listLancamento = listLancamento;
    }

}
