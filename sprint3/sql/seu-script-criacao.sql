-- ============================================================
-- Challenge Motiva - Sprint 3
-- Script de criacao das tabelas (Oracle)
-- Modelagem: uma tabela por hierarquia, com coluna discriminadora TIPO.
-- Re-executavel: apaga as tabelas antes de recriar (ignora se nao existirem).
-- ============================================================

-- Drops na ordem inversa das dependencias (FK primeiro)
BEGIN EXECUTE IMMEDIATE 'DROP TABLE RELATORIO_PRIORIDADE CASCADE CONSTRAINTS'; EXCEPTION WHEN OTHERS THEN NULL; END;
/
BEGIN EXECUTE IMMEDIATE 'DROP TABLE INTERVENCAO_OPERACIONAL CASCADE CONSTRAINTS'; EXCEPTION WHEN OTHERS THEN NULL; END;
/
BEGIN EXECUTE IMMEDIATE 'DROP TABLE TRECHO_RODOVIA CASCADE CONSTRAINTS'; EXCEPTION WHEN OTHERS THEN NULL; END;
/
BEGIN EXECUTE IMMEDIATE 'DROP TABLE EQUIPE_MANUTENCAO CASCADE CONSTRAINTS'; EXCEPTION WHEN OTHERS THEN NULL; END;
/

-- ------------------------------------------------------------
-- EQUIPE_MANUTENCAO  (EquipeManutencao + EquipeRocada)
-- ------------------------------------------------------------
CREATE TABLE EQUIPE_MANUTENCAO (
    ID                 NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    NOME               VARCHAR2(100) NOT NULL,
    QUANTIDADE_MEMBROS NUMBER(3)     NOT NULL,
    TIPO               VARCHAR2(20)  DEFAULT 'MANUTENCAO' NOT NULL,
    TIPO_EQUIPAMENTO   VARCHAR2(50),
    CONSTRAINT CK_EQUIPE_TIPO CHECK (TIPO IN ('MANUTENCAO', 'ROCADA')),
    CONSTRAINT CK_EQUIPE_MEMBROS CHECK (QUANTIDADE_MEMBROS >= 0)
);

-- ------------------------------------------------------------
-- TRECHO_RODOVIA  (TrechoRodovia + TrechoUmido + TrechoSeco)
-- ------------------------------------------------------------
CREATE TABLE TRECHO_RODOVIA (
    ID                 NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    IDENTIFICACAO      VARCHAR2(120) NOT NULL,
    QUILOMETRO_INICIAL NUMBER(8,2)   NOT NULL,
    QUILOMETRO_FINAL   NUMBER(8,2)   NOT NULL,
    NIVEL_VEGETACAO    NUMBER(6,2)   DEFAULT 0 NOT NULL,
    TIPO               VARCHAR2(20)  DEFAULT 'COMUM' NOT NULL,
    EQUIPE_ID          NUMBER,
    CONSTRAINT CK_TRECHO_TIPO CHECK (TIPO IN ('COMUM', 'UMIDO', 'SECO')),
    CONSTRAINT CK_TRECHO_VEGETACAO CHECK (NIVEL_VEGETACAO >= 0),
    CONSTRAINT FK_TRECHO_EQUIPE FOREIGN KEY (EQUIPE_ID)
        REFERENCES EQUIPE_MANUTENCAO (ID)
);

-- ------------------------------------------------------------
-- INTERVENCAO_OPERACIONAL  (RocadaMecanizada + Pulverizacao)
-- ------------------------------------------------------------
CREATE TABLE INTERVENCAO_OPERACIONAL (
    ID          NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    TIPO        VARCHAR2(20)  NOT NULL,
    RESPONSAVEL VARCHAR2(100) NOT NULL,
    CONSTRAINT CK_INTERV_TIPO CHECK (TIPO IN ('MECANIZADA', 'PULVERIZACAO'))
);

-- ------------------------------------------------------------
-- RELATORIO_PRIORIDADE  (entidade nova da Sprint 3 - historico)
-- ------------------------------------------------------------
CREATE TABLE RELATORIO_PRIORIDADE (
    ID                 NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    DATA_GERACAO       TIMESTAMP DEFAULT SYSTIMESTAMP NOT NULL,
    QT_MECANIZADA      NUMBER(5) DEFAULT 0 NOT NULL,
    QT_MANUAL          NUMBER(5) DEFAULT 0 NOT NULL,
    QT_SEM_NECESSIDADE NUMBER(5) DEFAULT 0 NOT NULL,
    RESUMO             VARCHAR2(500)
);

COMMIT;
