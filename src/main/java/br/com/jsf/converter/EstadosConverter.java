/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.com.jsf.converter;

import br.com.jsf.model.Estados;
import java.io.Serializable;
import javax.enterprise.inject.spi.CDI;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

/**
 *
 * @author lucia
 */
@Named
@FacesConverter(forClass = Estados.class, value = "estadosConverter")
public class EstadosConverter implements Converter, Serializable {

    private static final long serialVersionUID = 1L;

//    Não está conseguindo pegar por CDI diretamente!!! Usar por CDI.current().select.....
//    @Inject
//    private EntityManager entityManager;
//         
    //Retorna o Objeto Estado (Quando for salvar chama esse metodo)
    @Override
    public Object getAsObject(FacesContext fc, UIComponent uic, String codigoEstado) {
        Estados estado = null;

        //Caso der problema ao pegar por cdi o entity manager usar >> EntityManager entityManager = CDI.current().select(EntityManager.class).get();
        EntityManager entityManager = CDI.current().select(EntityManager.class).get();
        EntityTransaction entityTransaction = entityManager.getTransaction();
        entityTransaction.begin();

        if (codigoEstado != null
                && !codigoEstado.isEmpty()
                && !codigoEstado.equals("...")) {
            //Caso receba ... é quando nao seleciona nada!!
            estado = entityManager.find(Estados.class, Long.parseLong(codigoEstado));
        }

        entityTransaction.commit();
        //Não vamos usar o close mais, deixar para o framework controlar isso automaticamente!!!
        //Caso não fechar fica várias conexões abertas.. arrebentando com o banco!! Aguardar a aula onde ele mostra como ficará!
        //entityManager.close(); como está injetado agora, não podemos dar o close assim mais!!

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
