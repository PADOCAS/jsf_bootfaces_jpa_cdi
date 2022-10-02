/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.com.jsf.repository;

import br.com.jsf.model.Pessoa;

/**
 *
 * @author lucia
 */
public interface IDaoPessoa {
    
    public Pessoa consultarUsuario(String login, String senha) throws Exception;
}
