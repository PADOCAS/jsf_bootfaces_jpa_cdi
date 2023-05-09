CREATE TABLE public.cidades (
    id bigint NOT NULL,
    nome character varying(100) NOT NULL,
    estados_id bigint NOT NULL
);

CREATE TABLE public.estados (
    id bigint NOT NULL,
    nome character varying(45) NOT NULL,
    sigla character varying(2) NOT NULL
);

CREATE SEQUENCE public.hibernate_sequence
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE TABLE public.lancamento (
    id bigint NOT NULL,
    empresadestino character varying(255),
    empresaorigem character varying(255),
    numeronota bigint,
    pessoauser_id bigint NOT NULL,
    dataemissao date
);


CREATE TABLE public.pessoa (
    id bigint NOT NULL,
    datanascimento date,
    idade integer,
    nome character varying(255),
    observacao character varying(255),
    senha character varying(255),
    sobrenome character varying(255),
    sexo character varying(255),
    esportespraticados bytea,
    ativo boolean,
    login character varying(255),
    perfil character varying(255),
    linguagensprogramacao bytea,
    cep character varying(255),
    bairro character varying(255),
    complemento character varying(255),
    gia character varying(255),
    ibge character varying(255),
    localidade character varying(255),
    logradouro character varying(255),
    uf character varying(255),
    numero character varying(255),
    cidade_id bigint,
    estado_id bigint,
    extencao character varying(255),
    fotoiconbase64 text,
    fotoiconbase64original oid,
    cpf character varying(255),
    admin boolean
);