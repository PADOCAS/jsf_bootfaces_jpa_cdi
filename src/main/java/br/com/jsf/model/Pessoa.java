/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.com.jsf.model;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.br.CPF;

/**
 *
 * @author lucia
 */
@Entity
public class Pessoa implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull(message = "Nome é requerido")
    @NotEmpty(message = "Nome é requerido")
    @Size(max = 20, message = "Nome deve ter no máximo 20 caracteres")
    private String nome;

    @NotEmpty(message = "Sobrenome é requerido")
    @NotNull(message = "Sobrenome é requerido")
    @Size(min = 2, max = 50, message = "Sobrenome deve ter de 2 a 50 caracteres")
    private String sobrenome;

    @NotEmpty(message = "Login é requerido")
    @NotNull(message = "Login é requerido")
    @Size(min = 3, max = 20, message = "Login deve ter de 3 a 20 caracteres")
    private String login;

    @NotEmpty(message = "Senha é requerida")
    @NotNull(message = "Senha é requerida")
    @Size(max = 20, message = "Senha deve ter no máximo 20 caracteres")
    private String senha;

    private String perfil;

    private Integer idade;

    @NotNull(message = "Sexo deve ser informado")
    private String sexo;

    private String cep;

    private String logradouro;

    private String numero;

    private String complemento;

    private String bairro;

    private String localidade;

    private String uf;

    private String ibge;

    private String gia;

    private String[] esportesPraticados;

    private String[] linguagensProgramacao;

    private Boolean admin;

    @CPF(message = "CPF inválido")
    private String cpf;

    @ManyToOne(optional = true)
    @org.hibernate.annotations.ForeignKey(name = "estado_fk1")
    private Estados estado;

    @ManyToOne(optional = true)
    @org.hibernate.annotations.ForeignKey(name = "cidade_fk1")
    private Cidades cidade;

    private Boolean ativo;

    @Temporal(TemporalType.DATE)
    private Date dataNascimento;

    private String observacao;

    @Column(columnDefinition = "text") //grava arquivo em base64
    private String fotoIconBase64;

    private String extencao; //jpg, pmg, jpeg etc..

    @Lob //Gravar Arquivos no banco de dados
    @Basic(fetch = FetchType.LAZY)
    private byte[] fotoIconBase64Original;

    public Pessoa() {
        this.admin = false;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getSobrenome() {
        return sobrenome;
    }

    public void setSobrenome(String sobrenome) {
        this.sobrenome = sobrenome;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public Integer getIdade() {
        return idade;
    }

    public void setIdade(Integer idade) {
        this.idade = idade;
    }

    public String getSexo() {
        return sexo;
    }

    public void setSexo(String sexo) {
        this.sexo = sexo;
    }

    public Date getDataNascimento() {
        return dataNascimento;
    }

    public void setDataNascimento(Date dataNascimento) {
        this.dataNascimento = dataNascimento;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }

    public String[] getEsportesPraticados() {
        return esportesPraticados;
    }

    public void setEsportesPraticados(String[] esportesPraticados) {
        this.esportesPraticados = esportesPraticados;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public String getPerfil() {
        return perfil;
    }

    public void setPerfil(String perfil) {
        this.perfil = perfil;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getCep() {
        return cep;
    }

    public void setCep(String cep) {
        this.cep = cep;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getLogradouro() {
        return logradouro;
    }

    public void setLogradouro(String logradouro) {
        this.logradouro = logradouro;
    }

    public String getComplemento() {
        return complemento;
    }

    public void setComplemento(String complemento) {
        this.complemento = complemento;
    }

    public String getBairro() {
        return bairro;
    }

    public void setBairro(String bairro) {
        this.bairro = bairro;
    }

    public String getLocalidade() {
        return localidade;
    }

    public void setLocalidade(String localidade) {
        this.localidade = localidade;
    }

    public String getUf() {
        return uf;
    }

    public void setUf(String uf) {
        this.uf = uf;
    }

    public String getIbge() {
        return ibge;
    }

    public void setIbge(String ibge) {
        this.ibge = ibge;
    }

    public String getGia() {
        return gia;
    }

    public void setGia(String gia) {
        this.gia = gia;
    }

    public String[] getLinguagensProgramacao() {
        return linguagensProgramacao;
    }

    public void setLinguagensProgramacao(String[] linguagensProgramacao) {
        this.linguagensProgramacao = linguagensProgramacao;
    }

    public Estados getEstado() {
        return estado;
    }

    public void setEstado(Estados estado) {
        this.estado = estado;
    }

    public Cidades getCidade() {
        return cidade;
    }

    public void setCidade(Cidades cidade) {
        this.cidade = cidade;
    }

    public String getFotoIconBase64() {
        return fotoIconBase64;
    }

    public void setFotoIconBase64(String fotoIconBase64) {
        this.fotoIconBase64 = fotoIconBase64;
    }

    public String getExtencao() {
        return extencao;
    }

    public void setExtencao(String extencao) {
        this.extencao = extencao;
    }

    public byte[] getFotoIconBase64Original() {
        return fotoIconBase64Original;
    }

    public void setFotoIconBase64Original(byte[] fotoIconBase64Original) {
        this.fotoIconBase64Original = fotoIconBase64Original;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public Boolean getAdmin() {
        return admin;
    }

    public void setAdmin(Boolean admin) {
        this.admin = admin;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + Objects.hashCode(this.id);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Pessoa other = (Pessoa) obj;
        return Objects.equals(this.id, other.id);
    }

}
