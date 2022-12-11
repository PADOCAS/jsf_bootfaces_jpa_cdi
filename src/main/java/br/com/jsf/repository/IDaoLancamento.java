/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.com.jsf.repository;

import br.com.jsf.model.Lancamento;
import br.com.jsf.model.Pessoa;
import java.util.List;
import java.util.Map;

/**
 *
 * @author lucia
 */
public interface IDaoLancamento {

    public List<Lancamento> listarLancamentos(Pessoa usuario) throws Exception;
    
    public List<Lancamento> listarLancamentosLimit10(Pessoa usuario) throws Exception;
    
    public List<Lancamento> buscarLancamentoRelatorio(Pessoa usuario, Map<String, Object> param) throws Exception;
}
