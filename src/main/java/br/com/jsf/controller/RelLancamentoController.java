/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.com.jsf.controller;

import br.com.jsf.model.Lancamento;
import br.com.jsf.model.Pessoa;
import br.com.jsf.repository.IDaoLancamento;
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
@Named(value = "relLancamentoController")
@ViewScoped
public class RelLancamentoController implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<Lancamento> listLancamento;

    @Inject
    private IDaoLancamento daoLancamento;

    private Date dataInicio;

    private Date dataFim;

    private Long numeroNota;

    @javax.annotation.PostConstruct
    public void initManagedBean() {
        limpar();
    }

    public void pesquisarLancamentos() {
        setListLancamento(null);

        try {
            //Validação:
            if(dataInicio != null
                    && dataFim == null) {
                mostrarMsg("Período Final deve ser informado!");
                return;
            }
            
            if(dataFim != null
                    && dataInicio == null) {
                mostrarMsg("Período Inicial deve ser informado!");
                return;
            }
            
            if(dataInicio != null
                   && dataFim != null) {
                if(dataInicio.after(dataFim)) {
                    mostrarMsg("Período Inicial deve ser anterior ao final!");
                    return;
                }
            }
            
            Map<String, Object> paramRel = new HashMap<>();
            paramRel.put("dataInicio", dataInicio);
            paramRel.put("dataFim", dataFim);
            paramRel.put("numero", numeroNota);
            
            if (FacesContext.getCurrentInstance() != null
                    && FacesContext.getCurrentInstance().getExternalContext() != null
                    && FacesContext.getCurrentInstance().getExternalContext().getSessionMap() != null
                    && FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("usuario") != null) {
                Pessoa pessoaUser = (Pessoa) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("usuario");

                if (pessoaUser != null) {
                    setListLancamento(daoLancamento.buscarLancamentoRelatorio(pessoaUser, paramRel));
                }
            }

            if (getListLancamento() == null
                    || getListLancamento().isEmpty()) {
                mostrarMsg("Nenhum Registro encontrado!");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void limpar() {
        setListLancamento(null);
        setDataInicio(null);
        setDataFim(null);
        setNumeroNota(null);
    }

    private void mostrarMsg(String mensagem) {
        FacesMessage message = new FacesMessage(mensagem);
        //Pode ser dado a mensagem sobre algum componente especifico ou null quando é geral:
        FacesContext.getCurrentInstance().addMessage(null, message);
    }

    public List<Lancamento> getListLancamento() {
        return listLancamento;
    }

    public void setListLancamento(List<Lancamento> listLancamento) {
        this.listLancamento = listLancamento;
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

    public Long getNumeroNota() {
        return numeroNota;
    }

    public void setNumeroNota(Long numeroNota) {
        this.numeroNota = numeroNota;
    }

}
