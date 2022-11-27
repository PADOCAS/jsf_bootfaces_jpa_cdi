/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.com.jsf.repository;

import br.com.jsf.model.Pessoa;
import java.util.List;
import javax.faces.model.SelectItem;

/**
 *
 * @author lucia
 */
public interface IDaoPessoa {

    public Pessoa consultarUsuario(String login, String senha) throws Exception;

    public List<SelectItem> listaEstados() throws Exception;

    public List<SelectItem> listaCidades(Long estadoId) throws Exception;

    public void deletar(Pessoa pessoa) throws Exception;
}
