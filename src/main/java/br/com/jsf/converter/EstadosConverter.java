/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.com.jsf.converter;

import br.com.jsf.hibernate.util.JPAUtil;
import br.com.jsf.model.Cidades;
import br.com.jsf.model.Estados;
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
@FacesConverter(forClass = Estados.class, value = "estadosConverter")
public class EstadosConverter implements Converter, Serializable {

    private static final long serialVersionUID = 1L;

    //Retorna o Objeto Estado (Quando for salvar chama esse metodo)
    @Override
    public Object getAsObject(FacesContext fc, UIComponent uic, String codigoEstado) {
        Estados estado = null;

        EntityManager entityManager = JPAUtil.getEntityManager();
        EntityTransaction entityTransaction = entityManager.getTransaction();
        entityTransaction.begin();

        if (codigoEstado != null) {
            estado = JPAUtil.getEntityManager().find(Estados.class, Long.parseLong(codigoEstado));
        }

        entityTransaction.commit();
        entityManager.close();

        return estado;
    }

    //Retorna apenas o código (ID) em String
    @Override
    public String getAsString(FacesContext fc, UIComponent uic, Object estado) {
        if (estado != null
                && estado instanceof Estados
                && ((Estados) estado).getId() != null) {
            return ((Estados) estado).getId().toString();
        }

        return null;
    }

}
