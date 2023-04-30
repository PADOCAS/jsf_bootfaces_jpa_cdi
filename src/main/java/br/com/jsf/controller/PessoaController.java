/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.com.jsf.controller;

import br.com.jsf.hibernate.dao.DAOGenerico;
import br.com.jsf.model.Estados;
import br.com.jsf.model.Pessoa;
import br.com.jsf.repository.IDaoPessoa;
import com.google.gson.Gson;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.model.SelectItem;
import javax.faces.view.ViewScoped;
import javax.imageio.ImageIO;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import javax.xml.bind.DatatypeConverter;
import net.bootsfaces.component.selectOneMenu.SelectOneMenu;

/**
 *
 * @author lucia
 */
@ViewScoped
@Named(value = "pessoaController")
public class PessoaController implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    private IDaoPessoa iDaoPessoa;

    private Pessoa pessoa = new Pessoa();

    @Inject
    private DAOGenerico<Pessoa> daoGenerico;

    private List<Pessoa> listPessoa = null;

    private List<SelectItem> listSelectItemEstados = null;

    private List<SelectItem> listSelectItemCidades = null;

    //Pega o arquivo que selecionou na tela, cria temporariamente no servidor para poder pega-lo depois
    private Part arquivoFoto;

    @javax.annotation.PostConstruct
    public void initManagedBean() {
        carregarPessoas();
    }

    public void carregarFotoMiniatura(AjaxBehaviorEvent event) {
        try {
            if (arquivoFoto != null
                    && arquivoFoto.getContentType() != null) {
                String extensaoVerify = arquivoFoto.getContentType().split("\\/")[1];

                if (extensaoVerify.equals("jpg")
                        || extensaoVerify.equals("jpeg")
                        || extensaoVerify.equals("png")) {
                    byte[] imagemByte = getByteFotoSel(arquivoFoto.getInputStream());

                    //Transformar em BufferImage:
                    BufferedImage bufferedImage = ImageIO.read(new ByteArrayInputStream(imagemByte));

                    //Tipo da Imagem:
                    int typeImg = bufferedImage.getType() == 0 ? BufferedImage.TYPE_INT_RGB : bufferedImage.getType();
                    int largura = 80;
                    int altura = 80;
                    //Criando a miniatura:
                    BufferedImage resizedImage = new BufferedImage(altura, largura, typeImg);
                    Graphics2D graphics2D = resizedImage.createGraphics();
                    graphics2D.drawImage(bufferedImage, 0, 0, largura, altura, null);
                    graphics2D.dispose();
                    //Escrever a imagem miniatura:
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    //Retorna assim o contentType /image/png por exemplo.. entao vamos quebrar em array e pegar apenas a extensao
                    String extensao = arquivoFoto.getContentType().split("\\/")[1];
                    ImageIO.write(resizedImage, extensao, baos);
                    String imagemMiniatura = "data:" + arquivoFoto.getContentType() + ";base64," + DatatypeConverter.printBase64Binary(baos.toByteArray());

                    getPessoa().setFotoIconBase64(imagemMiniatura);
                } else {
                    throw new Exception("Informe apenas imagens dos tipos (jpg, jpeg ou png).");
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            mostrarMsg(ex.getMessage());
        }
    }

    public String salvar() {
        try {
            if (getPessoa() != null) {
                if(getPessoa().getAdmin() != null
                        && getPessoa().getAdmin()) {
                    throw new Exception("Administrador do sistema não pode ser alterado!");
                }
                
                //Processar Imagem:
                if (arquivoFoto != null
                        && arquivoFoto.getContentType() != null) {
                    String extensaoVerify = arquivoFoto.getContentType().split("\\/")[1];

                    if (extensaoVerify.equals("jpg")
                            || extensaoVerify.equals("jpeg")
                            || extensaoVerify.equals("png")) {
                        byte[] imagemByte = getByteFotoSel(arquivoFoto.getInputStream());
                        getPessoa().setFotoIconBase64Original(imagemByte);

                        //Transformar em BufferImage:
                        BufferedImage bufferedImage = ImageIO.read(new ByteArrayInputStream(imagemByte));

                        //Tipo da Imagem:
                        int typeImg = bufferedImage.getType() == 0 ? BufferedImage.TYPE_INT_RGB : bufferedImage.getType();
                        int largura = 200;
                        int altura = 200;
                        //Criando a miniatura:
                        BufferedImage resizedImage = new BufferedImage(altura, largura, typeImg);
                        Graphics2D graphics2D = resizedImage.createGraphics();
                        graphics2D.drawImage(bufferedImage, 0, 0, largura, altura, null);
                        graphics2D.dispose();
                        //Escrever a imagem miniatura:
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        //Retorna assim o contentType /image/png por exemplo.. entao vamos quebrar em array e pegar apenas a extensao
                        String extensao = arquivoFoto.getContentType().split("\\/")[1];
                        ImageIO.write(resizedImage, extensao, baos);
                        String imagemMiniatura = "data:" + arquivoFoto.getContentType() + ";base64," + DatatypeConverter.printBase64Binary(baos.toByteArray());

                        getPessoa().setFotoIconBase64(imagemMiniatura);
                        getPessoa().setExtencao(extensao);
                    }
                }

                //Merge >> SaveOrUpdate - Vai salvar e Retornar o objeto salvo pra gente:
                setPessoa(daoGenerico.saveOrUpdate(pessoa));
                mostrarMsg("Cadastro salvo com sucesso!");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            mostrarMsg(ex.getMessage());
        }

        //Atualiza Lista Pessoas:
        carregarPessoas();
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

    public void editar() {
        try {
            //Se tiver cidade informado na Pessoa, carregar os selectItens de Cidade para o Estado 
            if (getPessoa().getCidade() != null
                    && getPessoa().getCidade().getId() != null
                    && getPessoa().getCidade().getEstados() != null
                    && getPessoa().getCidade().getEstados().getId() != null) {
                setListSelectItemCidades(iDaoPessoa.listaCidades(getPessoa().getCidade().getEstados().getId()));

                //Caso o Estado estiver NULO, setar o Estado vinculado a Cidade:
                if (getPessoa().getEstado() == null) {
                    getPessoa().setEstado(getPessoa().getCidade().getEstados());
                }
            }

            //Reseta arquivo foto para o editar.. 
            setArquivoFoto(null);
        } catch (Exception ex) {
            ex.printStackTrace();
            mostrarMsg("Erro ao carregar editar!\n" + ex.getMessage());
        }
    }

    public String deletar() {
        try {
            if (getPessoa() != null
                    && getPessoa().getId() != null) {
                //Só deixa deletar usuário não admin (testes gerais do sistema)
                if (getPessoa().getAdmin() == null
                        || !getPessoa().getAdmin()) {
                    //Deletar os lançamentos da Pessoa antes de excluir a Pessoa:
                    iDaoPessoa.deletar(pessoa);

                    if (getPessoa().getId() != null
                            && getPessoa().getNome() != null
                            && getPessoa().getSobrenome() != null) {
                        //Instancia nova Pessoa após salvar - Tras na tela os campos em branco, nova pessoa:
                        setPessoa(new Pessoa());
                    }

                    mostrarMsg("Registro removido com sucesso!");
                } else {
                    throw new Exception("Não é permitido excluir o administrador do sistema!");
                }
            } else {
                mostrarMsg("Selecione um usuário já cadastrado para excluir.");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            mostrarMsg("Erro ao excluir registro!\n" + ex.getMessage());
        }

        //Atualiza Lista Pessoas:
        carregarPessoas();
        //Retornando Vazio, vai ficar na mesma página
        return "";
    }

    public void carregarPessoas() {
        try {
            setListPessoa(daoGenerico.listar(Pessoa.class));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
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
            mostrarMsg(ex.getMessage());
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

    public List<SelectItem> getListSelItEstadoCharged() {
        try {
            setListSelectItemEstados(iDaoPessoa.listaEstados());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return getListSelectItemEstados();
    }

    public void selOneEstado(AjaxBehaviorEvent event) {
        //Estamos trabalhando com o selectItem retornando o objeto Estados direto:
        Estados estado = ((Estados) ((SelectOneMenu) event.getSource()).getValue());

        //Caso fosse pegar o resultado do combo por ID... NAO VAMOS USAR ASSIM AGORA:
//        String codigoEstado = (String) event.getComponent().getAttributes().get("submittedValue");
        try {
            if (estado != null
                    && estado.getId() != null) {
                setListSelectItemCidades(iDaoPessoa.listaCidades(estado.getId()));
            }
        } catch (Exception e) {
            e.printStackTrace();
            mostrarMsg("Erro ao carregar Cidades!\n" + e.getMessage());
        }
    }

    /**
     * Método que converte um inputStream para um byte[]
     *
     * @param inputStream
     * @return byte[]
     */
    private byte[] getByteFotoSel(InputStream inputStream) {
        int len;
        int size = 1024;
        byte[] buff = null;

        try {
            if (inputStream instanceof ByteArrayInputStream) {
                size = inputStream.available();
                buff = new byte[size];
//                len = inputStream.read(buff, 0, size);
            } else {
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                buff = new byte[size];

                while ((len = inputStream.read(buff, 0, size)) != -1) {
                    byteArrayOutputStream.write(buff, 0, len);
                }

                buff = byteArrayOutputStream.toByteArray();
            }
        } catch (IOException ex) {
            Logger.getLogger(PessoaController.class.getName()).log(Level.SEVERE, null, ex);
        }

        return buff;
    }

    public void downloadFoto() {
        try {
            Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
            String fileDownloadImg = params.get("fileDownloadImg");

            if (fileDownloadImg != null
                    && !fileDownloadImg.isEmpty()) {
                Pessoa pessoa = daoGenerico.consultar(Pessoa.class, Long.valueOf(fileDownloadImg));

                if (pessoa != null
                        && pessoa.getExtencao() != null
                        && pessoa.getFotoIconBase64Original() != null) {
                    HttpServletResponse httpServletResponse = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();
                    httpServletResponse.addHeader("Content-Disposition", "attachment; filename=download." + pessoa.getExtencao());
                    httpServletResponse.setContentType("application/octet-stream");
                    httpServletResponse.setContentLength(pessoa.getFotoIconBase64Original().length);
                    httpServletResponse.getOutputStream().write(pessoa.getFotoIconBase64Original());
                    httpServletResponse.getOutputStream().flush();
                    FacesContext.getCurrentInstance().responseComplete();
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
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

    public List<SelectItem> getListSelectItemEstados() {
        return listSelectItemEstados;
    }

    public void setListSelectItemEstados(List<SelectItem> listSelectItemEstados) {
        this.listSelectItemEstados = listSelectItemEstados;
    }

    public List<SelectItem> getListSelectItemCidades() {
        return listSelectItemCidades;
    }

    public void setListSelectItemCidades(List<SelectItem> listSelectItemCidades) {
        this.listSelectItemCidades = listSelectItemCidades;
    }

    public Part getArquivoFoto() {
        return arquivoFoto;
    }

    public void setArquivoFoto(Part arquivoFoto) {
        this.arquivoFoto = arquivoFoto;
    }

}
