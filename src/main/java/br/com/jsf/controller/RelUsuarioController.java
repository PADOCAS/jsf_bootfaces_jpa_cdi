/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.com.jsf.controller;

import br.com.jsf.model.Pessoa;
import br.com.jsf.repository.IDaoPessoa;
import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

/**
 *
 * @author lucia
 */
@Named(value = "relUsuarioController")
@ViewScoped
public class RelUsuarioController implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<Pessoa> listPessoa;

    @Inject
    private IDaoPessoa daoPessoa;

    private Date dataInicio;

    private Date dataFim;

    private String nome;

    @javax.annotation.PostConstruct
    public void initManagedBean() {
        limpar();
    }

    public void pesquisarUsuario() {
        setListPessoa(null);

        try {
            //Validação:
            if (dataInicio != null
                    && dataFim == null) {
                mostrarMsg("Período Nascimento Final deve ser informado!");
                return;
            }

            if (dataFim != null
                    && dataInicio == null) {
                mostrarMsg("Período Nascimento Inicial deve ser informado!");
                return;
            }

            if (dataInicio != null
                    && dataFim != null) {
                if (dataInicio.after(dataFim)) {
                    mostrarMsg("Período Nascimento Inicial deve ser anterior ao final!");
                    return;
                }
            }

            Map<String, Object> paramRel = new HashMap<>();
            paramRel.put("dataInicio", dataInicio);
            paramRel.put("dataFim", dataFim);
            paramRel.put("nome", (nome != null && !nome.isEmpty() ? nome.toLowerCase() + "%" : null));

            setListPessoa(daoPessoa.buscarPessoaRelatorio(paramRel));

            if (getListPessoa() == null
                    || getListPessoa().isEmpty()) {
                mostrarMsg("Nenhum Registro encontrado!");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void limpar() {
        setListPessoa(null);
        setDataInicio(null);
        setDataFim(null);
        setNome(null);
    }

    private void mostrarMsg(String mensagem) {
        FacesMessage message = new FacesMessage(mensagem);
        //Pode ser dado a mensagem sobre algum componente especifico ou null quando é geral:
        FacesContext.getCurrentInstance().addMessage(null, message);
    }

    public List<Pessoa> getListPessoa() {
        return listPessoa;
    }

    public void setListPessoa(List<Pessoa> listPessoa) {
        this.listPessoa = listPessoa;
    }

    public Date getDataInicio() {
        return dataInicio;
    }

    public void setDataInicio(Date dataInicio) {
        this.dataInicio = dataInicio;
    }

    public Date getDataFim() {
        return dataFim;
    }

    public void setDataFim(Date dataFim) {
        this.dataFim = dataFim;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

}
