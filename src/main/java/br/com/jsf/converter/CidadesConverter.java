/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.com.jsf.converter;

import br.com.jsf.model.Cidades;
import java.io.Serializable;
import javax.enterprise.inject.spi.CDI;
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

//    Não está conseguindo pegar por CDI diretamente!!! Usar por CDI.current().select.....
//    @Inject
//    private EntityManager entityManager;
//         
    //Retorna o Objeto Cidade (Quando for salvar chama esse metodo)
    @Override
    public Object getAsObject(FacesContext fc, UIComponent uic, String codigoCidade) {
        Cidades cidade = null;

        //Caso der problema ao pegar por cdi o entity manager usar >> EntityManager entityManager = CDI.current().select(EntityManager.class).get();
        EntityManager entityManager = CDI.current().select(EntityManager.class).get();
        EntityTransaction entityTransaction = entityManager.getTransaction();
        entityTransaction.begin();

        if (codigoCidade != null
                && !codigoCidade.isEmpty()
                && !codigoCidade.equals("...")) {
            //Caso receba ... é quando nao seleciona nada!!
            cidade = entityManager.find(Cidades.class, Long.parseLong(codigoCidade));
        }

        entityTransaction.commit();
        //Não vamos usar o close mais, deixar para o framework controlar isso automaticamente!!!
        //Caso não fechar fica várias conexões abertas.. arrebentando com o banco!! Aguardar a aula onde ele mostra como ficará!
        //entityManager.close(); como está injetado agora, não podemos dar o close assim mais!!

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
