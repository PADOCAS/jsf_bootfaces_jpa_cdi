/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.com.jsf.converter;

import br.com.jsf.hibernate.util.JPAUtil;
import br.com.jsf.model.Cidades;
import java.io.Serializable;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

/**
 *
 * @author lucia
 */
@FacesConverter(forClass = Cidades.class, value = "cidadesConverter")
public class CidadesConverter implements Converter, Serializable {

    private static final long serialVersionUID = 1L;

    //Retorna o Objeto Cidade (Quando for salvar chama esse metodo)
    @Override
    public Object getAsObject(FacesContext fc, UIComponent uic, String codigoCidade) {
        Cidades cidade = null;

        EntityManager entityManager = JPAUtil.getEntityManager();
        EntityTransaction entityTransaction = entityManager.getTransaction();
        entityTransaction.begin();

        if (codigoCidade != null
                && !codigoCidade.isEmpty()
                && !codigoCidade.equals("...")) {
            //Caso receba ... é quando nao seleciona nada!!
            cidade = JPAUtil.getEntityManager().find(Cidades.class, Long.parseLong(codigoCidade));
        }

        entityTransaction.commit();
        entityManager.close();

        return cidade;
    }

    //Retorna apenas o código (ID) em String
    @Override
    public String getAsString(FacesContext fc, UIComponent uic, Object cidade) {
        if (cidade != null
                && cidade instanceof Cidades
                && ((Cidades) cidade).getId() != null) {
            return ((Cidades) cidade).getId().toString();
        }

        return null;
    }

}
