/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.com.jsf.controller;

import br.com.jsf.hibernate.dao.DAOGenerico;
import br.com.jsf.model.Pessoa;
import br.com.jsf.repository.IDaoPessoa;
import br.com.jsf.repository.IDaoPessoaImpl;
import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author lucia
 */
@ManagedBean(name = "pessoaController")
@ViewScoped
public class PessoaController {

    private IDaoPessoa iDaoPessoa = new IDaoPessoaImpl();

    private Pessoa pessoa = new Pessoa();

    private DAOGenerico<Pessoa> daoGenerico = new DAOGenerico<>();

    private List<Pessoa> listPessoa = null;

    @javax.annotation.PostConstruct
    public void initManagedBean() {
        carregarPessoas();
    }

    public String salvar() {
        if (getPessoa() != null) {
            //Merge >> SaveOrUpdate - Vai salvar e Retornar o objeto salvo pra gente:
            setPessoa(daoGenerico.saveOrUpdate(pessoa));
        }

        //Atualiza Lista Pessoas:
        carregarPessoas();
        mostrarMsg("Cadastro salvo com sucesso!");
        //Retornando Vazio, vai ficar na mesma página
        return "";
    }

    public void pesquisaCep(AjaxBehaviorEvent event) {
        try {
            if (getPessoa() != null
                    && getPessoa().getCep() != null
                    && !getPessoa().getCep().isEmpty()) {
                //ViaCep usa RESTful, vamos chamar a URL dele apenas passando o CEP onde é necessário:
                //URL exemplo: https://viacep.com.br/ws/17203040/json/
                //Apenas devemos concatenar o CEP ai no espaço dele e chamar a URL para receber a resposta em JSON                
                URL url = new URL("https://viacep.com.br/ws/" + getPessoa().getCep() + "/json/");
                URLConnection connection = url.openConnection();
                InputStream inputStream = connection.getInputStream();
                //Passar UTF-8 para os acentos ficarem certinhos
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));

                //Varre o JSON recebido para trabalhar com os campos (logradouro, UF, bairro, etc...)
                String cepJson;
                StringBuilder jsonBuilder = new StringBuilder();
                while ((cepJson = bufferedReader.readLine()) != null) {
                    jsonBuilder.append(cepJson);
                }

                //Setando os campos complementares que vieram no JSON:
                //Converter o JSON para o objeto Pessoa (usando Gson), ele vai alimentar os campos que o JSON trouxe que são iguais o atributo na classe Pessoa (logradouro, bairro, etc...)
                //Através disso, vamos setar nosso objeto usando no managedBean
                Pessoa gsonPessoaConverter = new Gson().fromJson(jsonBuilder.toString(), Pessoa.class);
                if (gsonPessoaConverter != null) {
                    getPessoa().setLogradouro(gsonPessoaConverter.getLogradouro());
                    getPessoa().setBairro(gsonPessoaConverter.getBairro());
                    getPessoa().setComplemento(gsonPessoaConverter.getComplemento());
                    getPessoa().setGia(gsonPessoaConverter.getGia());
                    getPessoa().setLocalidade(gsonPessoaConverter.getLocalidade());
                    getPessoa().setUf(gsonPessoaConverter.getUf());
                    getPessoa().setIbge(gsonPessoaConverter.getIbge());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            mostrarMsg("Erro ao consultar o CEP!\n" + e.getMessage());
        }
    }

    public String deletar() {
        if (getPessoa() != null) {
            //Deletar o Objeto:
            daoGenerico.deletar(pessoa);

            if (getPessoa().getId() != null
                    && getPessoa().getNome() != null
                    && getPessoa().getSobrenome() != null) {
                //Instancia nova Pessoa após salvar - Tras na tela os campos em branco, nova pessoa:
                setPessoa(new Pessoa());
            }
        }

        //Atualiza Lista Pessoas:
        carregarPessoas();
        mostrarMsg("Registro removido com sucesso!");
        //Retornando Vazio, vai ficar na mesma página
        return "";
    }

    public void carregarPessoas() {
        setListPessoa(daoGenerico.listar(Pessoa.class));
    }

    public String limpar() {
        setPessoa(new Pessoa());
        return "";
    }

    public String resetar() {
        setPessoa(new Pessoa());
        return "";
    }

    /**
     * 1)Método para deslogar usuário e retirar da session o (usuario) carregado
     * anteriormente! 2)Inválida a sessão dele
     *
     * @return Página index para como redirecionamento
     */
    public String deslogar() {
        if (FacesContext.getCurrentInstance() != null
                && FacesContext.getCurrentInstance().getExternalContext() != null
                && FacesContext.getCurrentInstance().getExternalContext().getSessionMap() != null) {
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().remove("usuario");

            //Invalida sessão ao deslogar
            if (FacesContext.getCurrentInstance().getExternalContext().getRequest() != null) {
                HttpServletRequest httpServletRequest = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
                httpServletRequest.getSession().invalidate();
            }
        }

        return "/index";
    }

    /**
     * Método usado para Logar -- tela de login (index.xhtml)
     *
     * @return Direcionamento (Pagina principal ou volta para o Login)
     */
    public String logar() {
        try {
            if (getPessoa() != null
                    && getPessoa().getLogin() != null
                    && getPessoa().getSenha() != null) {
                Pessoa pessoaUser = iDaoPessoa.consultarUsuario(getPessoa().getLogin(), getPessoa().getSenha());

                if (pessoaUser != null) {
                    //Adicionar o usuário na sessão:
                    if (FacesContext.getCurrentInstance() != null
                            && FacesContext.getCurrentInstance().getExternalContext() != null
                            && FacesContext.getCurrentInstance().getExternalContext().getSessionMap() != null) {
                        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("usuario", pessoaUser);
                        return "/principal/primeiraPagina.xhtml";
                    }
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return "";
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

    public Pessoa getPessoa() {
        return pessoa;
    }

    public void setPessoa(Pessoa pessoa) {
        this.pessoa = pessoa;
    }

    public List<Pessoa> getListPessoa() {
        return listPessoa;
    }

    public void setListPessoa(List<Pessoa> listPessoa) {
        this.listPessoa = listPessoa;
    }
}
