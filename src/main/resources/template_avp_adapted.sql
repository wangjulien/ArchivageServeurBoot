--
-- PostgreSQL database dump
--

-- Dumped from database version 10.3
-- Dumped by pg_dump version 10.3

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET client_min_messages = warning;
SET row_security = off;

--
-- Name: plpgsql; Type: EXTENSION; Schema: -; Owner: 
--

CREATE EXTENSION IF NOT EXISTS plpgsql WITH SCHEMA pg_catalog;


--
-- Name: EXTENSION plpgsql; Type: COMMENT; Schema: -; Owner: 
--

COMMENT ON EXTENSION plpgsql IS 'PL/pgSQL procedural language';


--
-- Name: pgcrypto; Type: EXTENSION; Schema: -; Owner: 
--

CREATE EXTENSION IF NOT EXISTS pgcrypto WITH SCHEMA public;


--
-- Name: EXTENSION pgcrypto; Type: COMMENT; Schema: -; Owner: 
--

COMMENT ON EXTENSION pgcrypto IS 'cryptographic functions';


--
-- Name: getar_profiles(text); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION public.getar_profiles(text, OUT ar_profile text, OUT par_id integer, OUT doctype_archivage text, OUT categorie text, OUT userid text, OUT par_canread boolean, OUT par_candeposit boolean, OUT content_types text[], OUT keywords text) RETURNS SETOF record
    LANGUAGE sql STABLE
    AS $_$

select distinct g.ar_profile,a.par_id,a.doctype_archivage, a.categorie, d.userid, d.par_canread, d.par_candeposit, array_agg(f.content_type), a.keywordslist 
from doctypes a 
--join doctypes b on a.doctype_archivage = b.doctype_archivage and a.categorie = b.categorie
join par_rights d on d.par_id = a.par_id
join profils g on g.par_id = d.par_id
join mime_doctypes e on a.doctypeid = e.doctypeid
left join mime_type f on f.mime_type_id = e.mime_type_id
where userid = $1 and d.par_candeposit is true
group by g.ar_profile,a.par_id,a.doctype_archivage, a.categorie, d.userid, d.par_canread, d.par_candeposit, a.keywordslist;
$_$;


ALTER FUNCTION public.getar_profiles(text, OUT ar_profile text, OUT par_id integer, OUT doctype_archivage text, OUT categorie text, OUT userid text, OUT par_canread boolean, OUT par_candeposit boolean, OUT content_types text[], OUT keywords text) OWNER TO postgres;

--
-- Name: getformatted_write_profiles(text); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION public.getformatted_write_profiles(text, OUT ar_profile text, OUT par_id integer, OUT typekey text, OUT doctype_archivage text, OUT categorie text, OUT userid text, OUT par_canread boolean, OUT par_candeposit boolean, OUT content_types text[], OUT keywords text) RETURNS SETOF record
    LANGUAGE sql STABLE
    AS $_$

select distinct g.ar_profile,b.par_id,(b.doctype_archivage || '/' || b.categorie) as typekey,b.doctype_archivage, b.categorie, d.userid, d.par_canread, d.par_candeposit, array_agg(f.content_type), b.keywordslist 
--from classifarchivage a 
from doctypes b 
join par_rights d on d.par_id = b.par_id
join profils g on g.par_id = d.par_id
join mime_doctypes e on b.doctypeid = e.doctypeid
left join mime_type f on f.mime_type_id = e.mime_type_id
where userid = $1 and d.par_candeposit is true
group by g.ar_profile,b.par_id,b.doctype_archivage, b.categorie, d.userid, d.par_canread, d.par_candeposit, b.keywordslist;
$_$;


ALTER FUNCTION public.getformatted_write_profiles(text, OUT ar_profile text, OUT par_id integer, OUT typekey text, OUT doctype_archivage text, OUT categorie text, OUT userid text, OUT par_canread boolean, OUT par_candeposit boolean, OUT content_types text[], OUT keywords text) OWNER TO postgres;

--
-- Name: getformatted_write_profiles(text, integer); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION public.getformatted_write_profiles(text, integer, OUT ar_profile text, OUT par_id integer, OUT typekey text, OUT doctype_archivage text, OUT categorie text, OUT userid text, OUT par_canread boolean, OUT par_candeposit boolean, OUT content_types text[], OUT keywords text) RETURNS SETOF record
    LANGUAGE sql STABLE
    AS $_$

select distinct g.ar_profile,b.par_id,(b.doctype_archivage || '/' || b.categorie) as typekey,b.doctype_archivage, b.categorie, d.userid, d.par_canread, d.par_candeposit, array_agg(f.content_type), b.keywordslist 
--from classifarchivage a 
from doctypes b 
join par_rights d on d.par_id = b.par_id
join profils g on g.par_id = d.par_id
join mime_doctypes e on b.doctypeid = e.doctypeid
left join mime_type f on f.mime_type_id = e.mime_type_id
where userid = $1 and d.par_candeposit is true and b.par_id = $2
group by g.ar_profile,b.par_id,b.doctype_archivage, b.categorie, d.userid, d.par_canread, d.par_candeposit, b.keywordslist;
$_$;


ALTER FUNCTION public.getformatted_write_profiles(text, integer, OUT ar_profile text, OUT par_id integer, OUT typekey text, OUT doctype_archivage text, OUT categorie text, OUT userid text, OUT par_canread boolean, OUT par_candeposit boolean, OUT content_types text[], OUT keywords text) OWNER TO postgres;

SET default_tablespace = '';

SET default_with_oids = false;

--
-- Name: applications; Type: TABLE; Schema: public; Owner: cdms
--

CREATE TABLE public.applications (
    applicationcode text NOT NULL,
    applicationname text,
    applicationvalidation boolean
);


ALTER TABLE public.applications OWNER TO cdms;

--
-- Name: COLUMN applications.applicationcode; Type: COMMENT; Schema: public; Owner: cdms
--

COMMENT ON COLUMN public.applications.applicationcode IS 'Code application';


--
-- Name: COLUMN applications.applicationname; Type: COMMENT; Schema: public; Owner: cdms
--

COMMENT ON COLUMN public.applications.applicationname IS 'Nom de l''application';


--
-- Name: COLUMN applications.applicationvalidation; Type: COMMENT; Schema: public; Owner: cdms
--

COMMENT ON COLUMN public.applications.applicationvalidation IS 'Validée';


--
-- Name: authmails; Type: TABLE; Schema: public; Owner: cdms
--

CREATE TABLE public.authmails (
    iduser integer NOT NULL,
    usermail text NOT NULL
);


ALTER TABLE public.authmails OWNER TO cdms;

--
-- Name: bgservices; Type: TABLE; Schema: public; Owner: cdms
--

CREATE TABLE public.bgservices (
    bgs_cod text NOT NULL,
    bgs_descr text,
    bgs_on boolean,
    bgs_param text,
    bgs_start timestamp(6) with time zone,
    bgs_process text,
    bgs_encours boolean DEFAULT false
);


ALTER TABLE public.bgservices OWNER TO cdms;

--
-- Name: COLUMN bgservices.bgs_cod; Type: COMMENT; Schema: public; Owner: cdms
--

COMMENT ON COLUMN public.bgservices.bgs_cod IS 'Service';


--
-- Name: COLUMN bgservices.bgs_descr; Type: COMMENT; Schema: public; Owner: cdms
--

COMMENT ON COLUMN public.bgservices.bgs_descr IS 'Description';


--
-- Name: COLUMN bgservices.bgs_on; Type: COMMENT; Schema: public; Owner: cdms
--

COMMENT ON COLUMN public.bgservices.bgs_on IS 'Background services ON';


--
-- Name: COLUMN bgservices.bgs_param; Type: COMMENT; Schema: public; Owner: cdms
--

COMMENT ON COLUMN public.bgservices.bgs_param IS 'Paramètres';


--
-- Name: COLUMN bgservices.bgs_start; Type: COMMENT; Schema: public; Owner: cdms
--

COMMENT ON COLUMN public.bgservices.bgs_start IS 'Horodatage';


--
-- Name: COLUMN bgservices.bgs_process; Type: COMMENT; Schema: public; Owner: cdms
--

COMMENT ON COLUMN public.bgservices.bgs_process IS 'Processus';


--
-- Name: COLUMN bgservices.bgs_encours; Type: COMMENT; Schema: public; Owner: cdms
--

COMMENT ON COLUMN public.bgservices.bgs_encours IS 'Le service est il en cours d''execution';


--
-- Name: chiffrement; Type: TABLE; Schema: public; Owner: cdms
--

CREATE TABLE public.chiffrement (
    cryptid uuid NOT NULL,
    algorythm text,
    idcrypkey uuid
);


ALTER TABLE public.chiffrement OWNER TO cdms;

--
-- Name: COLUMN chiffrement.cryptid; Type: COMMENT; Schema: public; Owner: cdms
--

COMMENT ON COLUMN public.chiffrement.cryptid IS 'Id du procédé de chiffrement';


--
-- Name: COLUMN chiffrement.algorythm; Type: COMMENT; Schema: public; Owner: cdms
--

COMMENT ON COLUMN public.chiffrement.algorythm IS 'Algorythme de chiffrement utilisé';


--
-- Name: COLUMN chiffrement.idcrypkey; Type: COMMENT; Schema: public; Owner: cdms
--

COMMENT ON COLUMN public.chiffrement.idcrypkey IS 'Id de la clé utiliser pour le chiffrement';


--
-- Name: communicationlist; Type: TABLE; Schema: public; Owner: cdms
--

CREATE TABLE public.communicationlist (
    communicationid uuid NOT NULL,
    docid uuid NOT NULL,
    communique boolean DEFAULT false,
    title text
);


ALTER TABLE public.communicationlist OWNER TO cdms;

--
-- Name: COLUMN communicationlist.communicationid; Type: COMMENT; Schema: public; Owner: cdms
--

COMMENT ON COLUMN public.communicationlist.communicationid IS 'Numéro de communication';


--
-- Name: COLUMN communicationlist.docid; Type: COMMENT; Schema: public; Owner: cdms
--

COMMENT ON COLUMN public.communicationlist.docid IS 'Identification archive';


--
-- Name: COLUMN communicationlist.communique; Type: COMMENT; Schema: public; Owner: cdms
--

COMMENT ON COLUMN public.communicationlist.communique IS 'Communiqué';


--
-- Name: COLUMN communicationlist.title; Type: COMMENT; Schema: public; Owner: cdms
--

COMMENT ON COLUMN public.communicationlist.title IS 'Titre du document';


--
-- Name: communications; Type: TABLE; Schema: public; Owner: cdms
--

CREATE TABLE public.communications (
    communicationid uuid NOT NULL,
    communicationmotif text,
    communicationstatus text,
    userid text,
    domnnom text,
    horodatage timestamp(6) with time zone,
    destinataire text,
    communication_end timestamp(6) with time zone
);


ALTER TABLE public.communications OWNER TO cdms;

--
-- Name: COLUMN communications.communicationid; Type: COMMENT; Schema: public; Owner: cdms
--

COMMENT ON COLUMN public.communications.communicationid IS 'Numéro de communication';


--
-- Name: COLUMN communications.communicationmotif; Type: COMMENT; Schema: public; Owner: cdms
--

COMMENT ON COLUMN public.communications.communicationmotif IS 'Motif de communication';


--
-- Name: COLUMN communications.communicationstatus; Type: COMMENT; Schema: public; Owner: cdms
--

COMMENT ON COLUMN public.communications.communicationstatus IS 'Statut de communication';


--
-- Name: COLUMN communications.userid; Type: COMMENT; Schema: public; Owner: cdms
--

COMMENT ON COLUMN public.communications.userid IS 'ID utilisateur';


--
-- Name: COLUMN communications.domnnom; Type: COMMENT; Schema: public; Owner: cdms
--

COMMENT ON COLUMN public.communications.domnnom IS 'Silo';


--
-- Name: COLUMN communications.horodatage; Type: COMMENT; Schema: public; Owner: cdms
--

COMMENT ON COLUMN public.communications.horodatage IS 'Horodatage';


--
-- Name: conversions; Type: TABLE; Schema: public; Owner: cdms
--

CREATE TABLE public.conversions (
    conversion_id integer NOT NULL,
    conversion_name text,
    conversion_program text,
    conversion_source text,
    conversion_target text DEFAULT 'application/pdf'::text
);


ALTER TABLE public.conversions OWNER TO cdms;

--
-- Name: COLUMN conversions.conversion_id; Type: COMMENT; Schema: public; Owner: cdms
--

COMMENT ON COLUMN public.conversions.conversion_id IS 'Conversion id';


--
-- Name: COLUMN conversions.conversion_name; Type: COMMENT; Schema: public; Owner: cdms
--

COMMENT ON COLUMN public.conversions.conversion_name IS 'Conversion';


--
-- Name: COLUMN conversions.conversion_program; Type: COMMENT; Schema: public; Owner: cdms
--

COMMENT ON COLUMN public.conversions.conversion_program IS 'Programme';


--
-- Name: COLUMN conversions.conversion_source; Type: COMMENT; Schema: public; Owner: cdms
--

COMMENT ON COLUMN public.conversions.conversion_source IS 'Source';


--
-- Name: COLUMN conversions.conversion_target; Type: COMMENT; Schema: public; Owner: cdms
--

COMMENT ON COLUMN public.conversions.conversion_target IS 'Cible';


--
-- Name: conversions_conversion_id_seq; Type: SEQUENCE; Schema: public; Owner: cdms
--

CREATE SEQUENCE public.conversions_conversion_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.conversions_conversion_id_seq OWNER TO cdms;

--
-- Name: conversions_conversion_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: cdms
--

ALTER SEQUENCE public.conversions_conversion_id_seq OWNED BY public.conversions.conversion_id;


--
-- Name: depots; Type: TABLE; Schema: public; Owner: cdms
--

CREATE TABLE public.depots (
    iddepot uuid NOT NULL,
    demandeur text,
    status text,
    message text,
    horodatage timestamp(6) with time zone
);


ALTER TABLE public.depots OWNER TO cdms;


--
-- Name: destinataires; Type: TABLE; Schema: public; Owner: cdms
--

CREATE TABLE public.destinataires (
    destinataire text NOT NULL,
    infosdestinataires text
);


ALTER TABLE public.destinataires OWNER TO cdms;

--
-- Name: COLUMN destinataires.destinataire; Type: COMMENT; Schema: public; Owner: cdms
--

COMMENT ON COLUMN public.destinataires.destinataire IS 'Destinataire';


--
-- Name: COLUMN destinataires.infosdestinataires; Type: COMMENT; Schema: public; Owner: cdms
--

COMMENT ON COLUMN public.destinataires.infosdestinataires IS 'Infos';


--
-- Name: destructioncriterias; Type: TABLE; Schema: public; Owner: cdms
--

CREATE TABLE public.destructioncriterias (
    destructioncriteriaid integer NOT NULL,
    destructioncriteria text,
    mindestructiondelay integer
);


ALTER TABLE public.destructioncriterias OWNER TO cdms;

--
-- Name: COLUMN destructioncriterias.destructioncriteriaid; Type: COMMENT; Schema: public; Owner: cdms
--

COMMENT ON COLUMN public.destructioncriterias.destructioncriteriaid IS 'Id critère de destruction';


--
-- Name: COLUMN destructioncriterias.destructioncriteria; Type: COMMENT; Schema: public; Owner: cdms
--

COMMENT ON COLUMN public.destructioncriterias.destructioncriteria IS 'Critère de destruction';


--
-- Name: COLUMN destructioncriterias.mindestructiondelay; Type: COMMENT; Schema: public; Owner: cdms
--

COMMENT ON COLUMN public.destructioncriterias.mindestructiondelay IS 'Délai minimum de conservation';


--
-- Name: destructioncriterias_destructioncriteriaid_seq; Type: SEQUENCE; Schema: public; Owner: cdms
--

CREATE SEQUENCE public.destructioncriterias_destructioncriteriaid_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.destructioncriterias_destructioncriteriaid_seq OWNER TO cdms;

--
-- Name: destructioncriterias_destructioncriteriaid_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: cdms
--

ALTER SEQUENCE public.destructioncriterias_destructioncriteriaid_seq OWNED BY public.destructioncriterias.destructioncriteriaid;


--
-- Name: doctypes; Type: TABLE; Schema: public; Owner: cdms
--

CREATE TABLE public.doctypes (
    doctypeid integer NOT NULL,
    doctype_archivage text,
    categorie text,
    keywordslist text,
	par_id integer
);


ALTER TABLE public.doctypes OWNER TO cdms;

--
-- Name: COLUMN doctypes.doctypeid; Type: COMMENT; Schema: public; Owner: cdms
--

COMMENT ON COLUMN public.doctypes.doctypeid IS 'Id type de document';


--
-- Name: COLUMN doctypes.doctype_archivage; Type: COMMENT; Schema: public; Owner: cdms
--

COMMENT ON COLUMN public.doctypes.doctype_archivage IS 'Type de document';


--
-- Name: COLUMN doctypes.categorie; Type: COMMENT; Schema: public; Owner: cdms
--

COMMENT ON COLUMN public.doctypes.categorie IS 'Catégorie';


--
-- Name: COLUMN doctypes.keywordslist; Type: COMMENT; Schema: public; Owner: cdms
--

COMMENT ON COLUMN public.doctypes.keywordslist IS 'Mots clés';


--
-- Name: doctypes_doctypeid_seq; Type: SEQUENCE; Schema: public; Owner: cdms
--

CREATE SEQUENCE public.doctypes_doctypeid_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.doctypes_doctypeid_seq OWNER TO cdms;

--
-- Name: doctypes_doctypeid_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: cdms
--

ALTER SEQUENCE public.doctypes_doctypeid_seq OWNED BY public.doctypes.doctypeid;


--
-- Name: document; Type: TABLE; Schema: public; Owner: cdms
--

CREATE TABLE public.document (
    docid uuid NOT NULL,
    title text,
    date timestamp(6) with time zone,
    archiver_id text,
    content_type text,
    content_length integer,
    keywords text,
    doctype text,
    archive_date timestamp(6) with time zone,
    application text,
    idsource text,
    categorie text,
    archive_end timestamp(6) with time zone,
    author text,
    mailowner text,
    domaineowner text,
    archiver_mail text,
    par_id integer,
    elasticid text,
    content bytea,
    domnnom text,
    conteneur text,
    lot text,
    iddepot uuid,
    serviceverseur text,
    description text,
    cryptage boolean DEFAULT false,
    cryptage_algo text,
    organisationverseuse text,
    organisationversante text,
    logicaldelete boolean,
    logicaldeletedate timestamp(6) with time zone,
    md5 text,
    cryptage_algoid uuid,
    cryptage_iv bytea,
    statut integer DEFAULT 0 NOT NULL,
    pronom_type text,
    pronom_id text,
    num_archive text,
	"timestamp" timestamp with time zone
);


ALTER TABLE public.document OWNER TO cdms;

--
-- Name: COLUMN document.docid; Type: COMMENT; Schema: public; Owner: cdms
--

COMMENT ON COLUMN public.document.docid IS 'Id document archivé';


--
-- Name: COLUMN document.title; Type: COMMENT; Schema: public; Owner: cdms
--

COMMENT ON COLUMN public.document.title IS 'Titre du document';


--
-- Name: COLUMN document.date; Type: COMMENT; Schema: public; Owner: cdms
--

COMMENT ON COLUMN public.document.date IS 'Date du document';


--
-- Name: COLUMN document.archiver_id; Type: COMMENT; Schema: public; Owner: cdms
--

COMMENT ON COLUMN public.document.archiver_id IS 'Archiveur';


--
-- Name: COLUMN document.content_type; Type: COMMENT; Schema: public; Owner: cdms
--

COMMENT ON COLUMN public.document.content_type IS 'Mime type';


--
-- Name: COLUMN document.content_length; Type: COMMENT; Schema: public; Owner: cdms
--

COMMENT ON COLUMN public.document.content_length IS 'Taille du document';


--
-- Name: COLUMN document.keywords; Type: COMMENT; Schema: public; Owner: cdms
--

COMMENT ON COLUMN public.document.keywords IS 'Mots clés';


--
-- Name: COLUMN document.doctype; Type: COMMENT; Schema: public; Owner: cdms
--

COMMENT ON COLUMN public.document.doctype IS 'Type de document';


--
-- Name: COLUMN document.archive_date; Type: COMMENT; Schema: public; Owner: cdms
--

COMMENT ON COLUMN public.document.archive_date IS 'Date d''archivage';


--
-- Name: COLUMN document.application; Type: COMMENT; Schema: public; Owner: cdms
--

COMMENT ON COLUMN public.document.application IS 'Application';


--
-- Name: COLUMN document.idsource; Type: COMMENT; Schema: public; Owner: cdms
--

COMMENT ON COLUMN public.document.idsource IS 'Id document source';


--
-- Name: COLUMN document.categorie; Type: COMMENT; Schema: public; Owner: cdms
--

COMMENT ON COLUMN public.document.categorie IS 'Catégorie';


--
-- Name: COLUMN document.archive_end; Type: COMMENT; Schema: public; Owner: cdms
--

COMMENT ON COLUMN public.document.archive_end IS 'Date d''expiration';


--
-- Name: COLUMN document.author; Type: COMMENT; Schema: public; Owner: cdms
--

COMMENT ON COLUMN public.document.author IS 'Auteur';


--
-- Name: COLUMN document.mailowner; Type: COMMENT; Schema: public; Owner: cdms
--

COMMENT ON COLUMN public.document.mailowner IS 'Propriétaire';


--
-- Name: COLUMN document.domaineowner; Type: COMMENT; Schema: public; Owner: cdms
--

COMMENT ON COLUMN public.document.domaineowner IS 'Domaine';


--
-- Name: COLUMN document.archiver_mail; Type: COMMENT; Schema: public; Owner: cdms
--

COMMENT ON COLUMN public.document.archiver_mail IS 'Mail archiveur';


--
-- Name: COLUMN document.par_id; Type: COMMENT; Schema: public; Owner: cdms
--

COMMENT ON COLUMN public.document.par_id IS 'Id profil d''archivage';


--
-- Name: COLUMN document.elasticid; Type: COMMENT; Schema: public; Owner: cdms
--

COMMENT ON COLUMN public.document.elasticid IS 'Id GED';


--
-- Name: COLUMN document.logicaldelete; Type: COMMENT; Schema: public; Owner: cdms
--

COMMENT ON COLUMN public.document.logicaldelete IS 'suppression logique';


--
-- Name: COLUMN document.logicaldeletedate; Type: COMMENT; Schema: public; Owner: cdms
--

COMMENT ON COLUMN public.document.logicaldeletedate IS 'date de suppression logique';


--
-- Name: COLUMN document.cryptage_algoid; Type: COMMENT; Schema: public; Owner: cdms
--

COMMENT ON COLUMN public.document.cryptage_algoid IS 'Id de l algorythme de chiffrement utilisé';


--
-- Name: COLUMN document.cryptage_iv; Type: COMMENT; Schema: public; Owner: cdms
--

COMMENT ON COLUMN public.document.cryptage_iv IS 'vecteur d initialisation utilisé pour le chiffrement';


--
-- Name: COLUMN document.pronom_type; Type: COMMENT; Schema: public; Owner: cdms
--

COMMENT ON COLUMN public.document.pronom_type IS 'le format PRONOM du fichier';


--
-- Name: COLUMN document.pronom_id; Type: COMMENT; Schema: public; Owner: cdms
--

COMMENT ON COLUMN public.document.pronom_id IS 'l''identifiant PRONOM du format du fichier';


--
-- Name: COLUMN document.num_archive; Type: COMMENT; Schema: public; Owner: cdms
--

COMMENT ON COLUMN public.document.num_archive IS 'Rajouter pour RC pour un screenshot';



--
-- Name: domn; Type: TABLE; Schema: public; Owner: cdms
--

CREATE TABLE public.domn (
    domnnom text NOT NULL,
    domnlib text,
    orgaparent text,
    estobservable boolean DEFAULT true,
    externe boolean DEFAULT false
);


ALTER TABLE public.domn OWNER TO cdms;

--
-- Name: drafts; Type: TABLE; Schema: public; Owner: cdms
--

CREATE TABLE public.drafts (
    docid uuid NOT NULL,
    doctype text,
    categorie text,
    keywords text,
    content bytea,
    content_length integer,
    content_type text,
    domaineowner text,
    organisationversante text,
    docsdate timestamp(6) with time zone,
    description text,
    title text,
    domnnom text,
    mailowner text,
    transmis boolean,
    statut text,
    motif text,
    userid text,
    draftdate timestamp(6) with time zone,
    archiveid uuid,
    pronom_type text,
    pronom_id text
);


ALTER TABLE public.drafts OWNER TO cdms;

--
-- Name: COLUMN drafts.docid; Type: COMMENT; Schema: public; Owner: cdms
--

COMMENT ON COLUMN public.drafts.docid IS 'N° d''archive';


--
-- Name: COLUMN drafts.doctype; Type: COMMENT; Schema: public; Owner: cdms
--

COMMENT ON COLUMN public.drafts.doctype IS 'Type de document';


--
-- Name: COLUMN drafts.categorie; Type: COMMENT; Schema: public; Owner: cdms
--

COMMENT ON COLUMN public.drafts.categorie IS 'Catégorie';


--
-- Name: COLUMN drafts.keywords; Type: COMMENT; Schema: public; Owner: cdms
--

COMMENT ON COLUMN public.drafts.keywords IS 'Mots clés';


--
-- Name: COLUMN drafts.content; Type: COMMENT; Schema: public; Owner: cdms
--

COMMENT ON COLUMN public.drafts.content IS 'Contenu';


--
-- Name: COLUMN drafts.content_length; Type: COMMENT; Schema: public; Owner: cdms
--

COMMENT ON COLUMN public.drafts.content_length IS 'Taille du document';


--
-- Name: COLUMN drafts.content_type; Type: COMMENT; Schema: public; Owner: cdms
--

COMMENT ON COLUMN public.drafts.content_type IS 'Mime type';


--
-- Name: COLUMN drafts.domaineowner; Type: COMMENT; Schema: public; Owner: cdms
--

COMMENT ON COLUMN public.drafts.domaineowner IS 'Service versant';


--
-- Name: COLUMN drafts.organisationversante; Type: COMMENT; Schema: public; Owner: cdms
--

COMMENT ON COLUMN public.drafts.organisationversante IS 'Organisation versante';


--
-- Name: COLUMN drafts.docsdate; Type: COMMENT; Schema: public; Owner: cdms
--

COMMENT ON COLUMN public.drafts.docsdate IS 'Date';


--
-- Name: COLUMN drafts.description; Type: COMMENT; Schema: public; Owner: cdms
--

COMMENT ON COLUMN public.drafts.description IS 'Description';


--
-- Name: COLUMN drafts.title; Type: COMMENT; Schema: public; Owner: cdms
--

COMMENT ON COLUMN public.drafts.title IS 'Titre du document';


--
-- Name: COLUMN drafts.domnnom; Type: COMMENT; Schema: public; Owner: cdms
--

COMMENT ON COLUMN public.drafts.domnnom IS 'Silo';


--
-- Name: COLUMN drafts.mailowner; Type: COMMENT; Schema: public; Owner: cdms
--

COMMENT ON COLUMN public.drafts.mailowner IS 'Propriétaire';


--
-- Name: COLUMN drafts.transmis; Type: COMMENT; Schema: public; Owner: cdms
--

COMMENT ON COLUMN public.drafts.transmis IS 'Transmis';


--
-- Name: COLUMN drafts.statut; Type: COMMENT; Schema: public; Owner: cdms
--

COMMENT ON COLUMN public.drafts.statut IS 'Statut';


--
-- Name: COLUMN drafts.motif; Type: COMMENT; Schema: public; Owner: cdms
--

COMMENT ON COLUMN public.drafts.motif IS 'Motif de refus';


--
-- Name: COLUMN drafts.userid; Type: COMMENT; Schema: public; Owner: cdms
--

COMMENT ON COLUMN public.drafts.userid IS 'ID utilisateur';


--
-- Name: COLUMN drafts.draftdate; Type: COMMENT; Schema: public; Owner: cdms
--

COMMENT ON COLUMN public.drafts.draftdate IS 'Date de demande';


--
-- Name: COLUMN drafts.archiveid; Type: COMMENT; Schema: public; Owner: cdms
--

COMMENT ON COLUMN public.drafts.archiveid IS 'L''identifiant du draft archivé';


--
-- Name: COLUMN drafts.pronom_type; Type: COMMENT; Schema: public; Owner: cdms
--

COMMENT ON COLUMN public.drafts.pronom_type IS 'le format PRONOM du fichier';


--
-- Name: COLUMN drafts.pronom_id; Type: COMMENT; Schema: public; Owner: cdms
--

COMMENT ON COLUMN public.drafts.pronom_id IS 'l''identifiant PRONOM du format du fichier';


--
-- Name: droo; Type: TABLE; Schema: public; Owner: cdms
--

CREATE TABLE public.droo (
    profid character varying(30) NOT NULL,
    objecod text NOT NULL,
    drooread character varying(1),
    droowrite character varying(1),
    droorewrite character varying(1),
    droodelete character varying(1),
    drooprint boolean DEFAULT true,
    drooexport boolean DEFAULT true,
    droodownload boolean DEFAULT true,
    secusup1 boolean,
    secusup2 boolean,
    secusup3 boolean
);


ALTER TABLE public.droo OWNER TO cdms;

--
-- Name: empreintes; Type: TABLE; Schema: public; Owner: cdms
--

CREATE TABLE public.empreintes (
    docid uuid NOT NULL,
    empreinte text,
    empreinte_algo text,
    empreinte_unique text,
    empreinte_telino text
);


ALTER TABLE public.empreintes OWNER TO cdms;

--
-- Name: COLUMN empreintes.docid; Type: COMMENT; Schema: public; Owner: cdms
--

COMMENT ON COLUMN public.empreintes.docid IS 'Id document archivé';


--
-- Name: COLUMN empreintes.empreinte; Type: COMMENT; Schema: public; Owner: cdms
--

COMMENT ON COLUMN public.empreintes.empreinte IS 'Empreinte';


--
-- Name: COLUMN empreintes.empreinte_algo; Type: COMMENT; Schema: public; Owner: cdms
--

COMMENT ON COLUMN public.empreintes.empreinte_algo IS 'Algoritme';


--
-- Name: COLUMN empreintes.empreinte_unique; Type: COMMENT; Schema: public; Owner: cdms
--

COMMENT ON COLUMN public.empreintes.empreinte_unique IS 'empreinte unique : id de stockage du document';


--
-- Name: COLUMN empreintes.empreinte_telino; Type: COMMENT; Schema: public; Owner: cdms
--

COMMENT ON COLUMN public.empreintes.empreinte_telino IS 'empreinte telino avec deux secrets';


--
-- Name: exp_comments; Type: TABLE; Schema: public; Owner: cdms
--

CREATE TABLE public.exp_comments (
    comid uuid NOT NULL,
    comdate timestamp(6) without time zone,
    comment character varying(255),
    userid character varying(255),
    task_id uuid
);


ALTER TABLE public.exp_comments OWNER TO cdms;

--
-- Name: exp_task; Type: TABLE; Schema: public; Owner: cdms
--

CREATE TABLE public.exp_task (
    taskid uuid NOT NULL,
    datedeb timestamp(6) without time zone,
    datefin timestamp(6) without time zone,
    docid uuid,
    horodatage timestamp(6) without time zone,
    logid uuid,
    nbtries integer NOT NULL,
    state character varying(255),
    tasktypeid integer,
    userid character varying(255)
);


ALTER TABLE public.exp_task OWNER TO cdms;

--
-- Name: exp_task_type; Type: TABLE; Schema: public; Owner: cdms
--

CREATE TABLE public.exp_task_type (
    typeid integer NOT NULL,
    taskname text,
    expirationtime integer,
    maxnbtries smallint
);


ALTER TABLE public.exp_task_type OWNER TO cdms;

--
-- Name: getarchivagetypes; Type: VIEW; Schema: public; Owner: cdms
--

CREATE VIEW public.getarchivagetypes AS
 SELECT ((doctypes.doctype_archivage || '/'::text) || doctypes.categorie) AS typekey,
    doctypes.doctype_archivage AS doctype,
    doctypes.categorie,
    doctypes.keywordslist
   FROM public.doctypes;


ALTER TABLE public.getarchivagetypes OWNER TO cdms;

--
-- Name: getkeywords; Type: VIEW; Schema: public; Owner: cdms
--

CREATE VIEW public.getkeywords AS
 SELECT x.docid,
    x.domnnom,
    x.doctype,
    replace(x.zone[1], '<'::text, ''::text) AS keyword1,
    replace(x.zone[2], '<'::text, ''::text) AS keyword2,
    replace(x.zone[3], '<'::text, ''::text) AS keyword3,
    replace(x.zone[4], '<'::text, ''::text) AS keyword4,
    replace(x.zone[5], '<'::text, ''::text) AS keyword5,
    replace(x.zone[6], '<'::text, ''::text) AS keyword6,
    replace(x.zone[7], '<'::text, ''::text) AS keyword7,
    replace(x.zone[39], '<'::text, ''::text) AS keyword8,
    x.keywords,
    x.categorie,
    x.title,
    x.date,
    x.archiver_id,
    x.content_type,
    x.content_length,
    x.archive_date,
    x.archive_end,
    x.application,
    x.archiver_mail,
    x.description,
    x.par_id,
    x.elasticid,
    x.logicaldelete,
    x.logicaldeletedate,
    x.statut,
    x.num_archive,
    x.num_keyword AS keyword13
   FROM ( SELECT document.docid,
            document.doctype,
            document.domnnom,
            document.keywords,
            document.categorie,
            document.title,
            document.date,
            document.archiver_id,
            document.content_type,
            document.content_length,
            document.archive_date,
            document.archive_end,
            document.application,
            document.archiver_mail,
            document.description,
            document.par_id,
            document.elasticid,
            document.logicaldelete,
            document.logicaldeletedate,
            document.statut,
            document.num_archive,
            document.num_archive AS num_keyword,
            regexp_split_to_array(document.keywords, '>'::text) AS zone
           FROM public.document) x;


ALTER TABLE public.getkeywords OWNER TO cdms;

--
-- Name: gettypes; Type: VIEW; Schema: public; Owner: cdms
--

CREATE VIEW public.gettypes AS
 SELECT
        CASE
            WHEN (doctypes.categorie IS NULL) THEN doctypes.doctype_archivage
            ELSE ((doctypes.doctype_archivage || '/'::text) || doctypes.categorie)
        END AS cletype,
    doctypes.doctypeid,
    doctypes.doctype_archivage AS doctype,
    doctypes.categorie
   FROM public.doctypes;


ALTER TABLE public.gettypes OWNER TO cdms;


--
-- Name: gettypesfiltered; Type: VIEW; Schema: public; Owner: cdms
--

CREATE VIEW public.gettypesfiltered AS
 SELECT
        CASE
            WHEN (doctypes.categorie IS NULL) THEN doctypes.doctype_archivage
            ELSE ((doctypes.doctype_archivage || '/'::text) || doctypes.categorie)
        END AS cletype,
    doctypes.doctypeid,
    doctypes.doctype_archivage AS doctype,
    doctypes.categorie
   FROM public.doctypes
  WHERE (doctypes.par_id IS NULL);


ALTER TABLE public.gettypesfiltered OWNER TO cdms;

--
-- Name: hibernate_sequence; Type: SEQUENCE; Schema: public; Owner: cdms
--

CREATE SEQUENCE public.hibernate_sequence
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.hibernate_sequence OWNER TO cdms;

--
-- Name: keyword; Type: TABLE; Schema: public; Owner: cdms
--

CREATE TABLE public.keyword (
    keyword text NOT NULL,
    doctype text NOT NULL,
    categorie text NOT NULL,
    keywordvalues text
);


ALTER TABLE public.keyword OWNER TO cdms;

--
-- Name: COLUMN keyword.keyword; Type: COMMENT; Schema: public; Owner: cdms
--

COMMENT ON COLUMN public.keyword.keyword IS 'Mot clé';


--
-- Name: COLUMN keyword.keywordvalues; Type: COMMENT; Schema: public; Owner: cdms
--

COMMENT ON COLUMN public.keyword.keywordvalues IS 'Valeur du mot clé';


--
-- Name: log_archive; Type: TABLE; Schema: public; Owner: cdms
--

CREATE TABLE public.log_archive (
    logid uuid NOT NULL,
    horodatage timestamp(6) with time zone,
    operation text,
    userid text,
    docid uuid,
    mailid text,
    docsname text,
    logtype text,
    timestamptoken bytea,
    hash text,
    attestation uuid,
	"timestamp" timestamp with time zone
);


ALTER TABLE public.log_archive OWNER TO cdms;

--
-- Name: COLUMN log_archive.logid; Type: COMMENT; Schema: public; Owner: cdms
--

COMMENT ON COLUMN public.log_archive.logid IS 'Id log';


--
-- Name: COLUMN log_archive.horodatage; Type: COMMENT; Schema: public; Owner: cdms
--

COMMENT ON COLUMN public.log_archive.horodatage IS 'Date d''archivage';


--
-- Name: COLUMN log_archive.operation; Type: COMMENT; Schema: public; Owner: cdms
--

COMMENT ON COLUMN public.log_archive.operation IS 'Opération';


--
-- Name: COLUMN log_archive.userid; Type: COMMENT; Schema: public; Owner: cdms
--

COMMENT ON COLUMN public.log_archive.userid IS 'ID utilisateur';


--
-- Name: COLUMN log_archive.docid; Type: COMMENT; Schema: public; Owner: cdms
--

COMMENT ON COLUMN public.log_archive.docid IS 'Id document archivé';


--
-- Name: COLUMN log_archive.mailid; Type: COMMENT; Schema: public; Owner: cdms
--

COMMENT ON COLUMN public.log_archive.mailid IS 'Adresse mail';


--
-- Name: COLUMN log_archive.docsname; Type: COMMENT; Schema: public; Owner: cdms
--

COMMENT ON COLUMN public.log_archive.docsname IS 'Nom de l''archive';



--
-- Name: log_event; Type: TABLE; Schema: public; Owner: cdms
--

CREATE TABLE public.log_event (
    logid uuid NOT NULL,
    origin text,
    processus text,
    action text,
    horodatage timestamp(6) with time zone,
    detail text,
    customer_name text,
    operateur text,
    versionprocessus text,
    logtype text DEFAULT 'E'::text,
    timestamptoken bytea,
    archiveid uuid,
    journalid uuid,
    trace text,
    methode text,
    hash text,
    journalxmlid uuid,
    statexp character varying (1) DEFAULT 'I',
	"timestamp" timestamp with time zone
);


ALTER TABLE public.log_event OWNER TO cdms;

--
-- Name: COLUMN log_event.logid; Type: COMMENT; Schema: public; Owner: cdms
--

COMMENT ON COLUMN public.log_event.logid IS 'l''identifiant du journal concernée';


--
-- Name: COLUMN log_event.origin; Type: COMMENT; Schema: public; Owner: cdms
--

COMMENT ON COLUMN public.log_event.origin IS 'Origine de l''événement';


--
-- Name: COLUMN log_event.processus; Type: COMMENT; Schema: public; Owner: cdms
--

COMMENT ON COLUMN public.log_event.processus IS 'Application';


--
-- Name: COLUMN log_event.action; Type: COMMENT; Schema: public; Owner: cdms
--

COMMENT ON COLUMN public.log_event.action IS 'Action';


--
-- Name: COLUMN log_event.horodatage; Type: COMMENT; Schema: public; Owner: cdms
--

COMMENT ON COLUMN public.log_event.horodatage IS 'Date d''archivage';


--
-- Name: COLUMN log_event.detail; Type: COMMENT; Schema: public; Owner: cdms
--

COMMENT ON COLUMN public.log_event.detail IS 'Détail de l''événement';


--
-- Name: COLUMN log_event.customer_name; Type: COMMENT; Schema: public; Owner: cdms
--

COMMENT ON COLUMN public.log_event.customer_name IS 'Nom du client';


--
-- Name: COLUMN log_event.archiveid; Type: COMMENT; Schema: public; Owner: cdms
--

COMMENT ON COLUMN public.log_event.archiveid IS 'L''identifiant de l''archive concernée';


--
-- Name: COLUMN log_event.trace; Type: COMMENT; Schema: public; Owner: cdms
--

COMMENT ON COLUMN public.log_event.trace IS 'Trace complète de l''exception rencontrée';


--
-- Name: COLUMN log_event.methode; Type: COMMENT; Schema: public; Owner: cdms
--

COMMENT ON COLUMN public.log_event.methode IS 'Method à l''origine de l''exception';


--
-- Name: COLUMN log_event.hash; Type: COMMENT; Schema: public; Owner: cdms
--

COMMENT ON COLUMN public.log_event.hash IS 'digest du contenu du journal';


--
-- Name: COLUMN log_event.journalxmlid; Type: COMMENT; Schema: public; Owner: cdms
--

COMMENT ON COLUMN public.log_event.journalxmlid IS 'Archive id du fichier xml correspondant au journal';


--
-- Name: log_mo; Type: TABLE; Schema: public; Owner: cdms
--

CREATE TABLE public.log_mo (
    logid integer NOT NULL,
    userid text,
    mailid text,
    profdroits integer,
    logip text,
    loghost text,
    logreason text,
    logbrisdeglace boolean DEFAULT false,
    logtime timestamp(6) with time zone
);


ALTER TABLE public.log_mo OWNER TO cdms;

--
-- Name: log_mo_logid_seq; Type: SEQUENCE; Schema: public; Owner: cdms
--

CREATE SEQUENCE public.log_mo_logid_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.log_mo_logid_seq OWNER TO cdms;

--
-- Name: log_mo_logid_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: cdms
--

ALTER SEQUENCE public.log_mo_logid_seq OWNED BY public.log_mo.logid;


--
-- Name: login; Type: TABLE; Schema: public; Owner: cdms
--

CREATE TABLE public.login (
    userid text NOT NULL,
    usermail text,
    userpassword text,
    nom text,
    prenom text
);


ALTER TABLE public.login OWNER TO cdms;

--
-- Name: COLUMN login.userid; Type: COMMENT; Schema: public; Owner: cdms
--

COMMENT ON COLUMN public.login.userid IS 'ID utilisateur';


--
-- Name: COLUMN login.usermail; Type: COMMENT; Schema: public; Owner: cdms
--

COMMENT ON COLUMN public.login.usermail IS 'Mail utilisateur';


--
-- Name: COLUMN login.userpassword; Type: COMMENT; Schema: public; Owner: cdms
--

COMMENT ON COLUMN public.login.userpassword IS 'Mot de passe';


--
-- Name: mail; Type: TABLE; Schema: public; Owner: cdms
--

CREATE TABLE public.mail (
    mailid text NOT NULL,
    maillib text,
    mailorg text,
    mailinterne boolean,
    idmail integer NOT NULL,
    estobservable boolean DEFAULT true
);


ALTER TABLE public.mail OWNER TO cdms;

--
-- Name: COLUMN mail.mailid; Type: COMMENT; Schema: public; Owner: cdms
--

COMMENT ON COLUMN public.mail.mailid IS 'Identification du mail sur le serveur';


--
-- Name: COLUMN mail.maillib; Type: COMMENT; Schema: public; Owner: cdms
--

COMMENT ON COLUMN public.mail.maillib IS 'libell? utilisateur du mail';


--
-- Name: COLUMN mail.mailorg; Type: COMMENT; Schema: public; Owner: cdms
--

COMMENT ON COLUMN public.mail.mailorg IS 'Organisation';


--
-- Name: COLUMN mail.mailinterne; Type: COMMENT; Schema: public; Owner: cdms
--

COMMENT ON COLUMN public.mail.mailinterne IS 'Adresse mail interne';


--
-- Name: mail_idmail_seq; Type: SEQUENCE; Schema: public; Owner: cdms
--

CREATE SEQUENCE public.mail_idmail_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.mail_idmail_seq OWNER TO cdms;

--
-- Name: mail_idmail_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: cdms
--

ALTER SEQUENCE public.mail_idmail_seq OWNED BY public.mail.idmail;


--
-- Name: mime_doctypes; Type: TABLE; Schema: public; Owner: cdms
--

CREATE TABLE public.mime_doctypes (
    mime_type_id integer NOT NULL,
    doctypeid integer NOT NULL
);


ALTER TABLE public.mime_doctypes OWNER TO cdms;


--
-- Name: COLUMN mime_doctypes.mime_type_id; Type: COMMENT; Schema: public; Owner: cdms
--

COMMENT ON COLUMN public.mime_doctypes.mime_type_id IS 'Id mime type';


--
-- Name: COLUMN mime_doctypes.doctypeid; Type: COMMENT; Schema: public; Owner: cdms
--

COMMENT ON COLUMN public.mime_doctypes.doctypeid IS 'Id type de document';


--
-- Name: mime_type; Type: TABLE; Schema: public; Owner: cdms
--

CREATE TABLE public.mime_type (
    mime_type_id integer NOT NULL,
    content_type text,
    mime_description text
);


ALTER TABLE public.mime_type OWNER TO cdms;

--
-- Name: COLUMN mime_type.mime_type_id; Type: COMMENT; Schema: public; Owner: cdms
--

COMMENT ON COLUMN public.mime_type.mime_type_id IS 'Id mime type';


--
-- Name: COLUMN mime_type.content_type; Type: COMMENT; Schema: public; Owner: cdms
--

COMMENT ON COLUMN public.mime_type.content_type IS 'Mime type';


--
-- Name: mime_type_mime_type_id_seq; Type: SEQUENCE; Schema: public; Owner: cdms
--

CREATE SEQUENCE public.mime_type_mime_type_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.mime_type_mime_type_id_seq OWNER TO cdms;

--
-- Name: mime_type_mime_type_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: cdms
--

ALTER SEQUENCE public.mime_type_mime_type_id_seq OWNED BY public.mime_type.mime_type_id;


--
-- Name: modu; Type: TABLE; Schema: public; Owner: cdms
--

CREATE TABLE public.modu (
    moducod character(4) NOT NULL,
    modulib character varying(30),
    modubasins character varying(1),
    moducolor text
);


ALTER TABLE public.modu OWNER TO cdms;

--
-- Name: par_rights; Type: TABLE; Schema: public; Owner: cdms
--

CREATE TABLE public.par_rights (
    par_id integer NOT NULL,
    userid text NOT NULL,
    par_candeposit boolean,
    par_candelay boolean,
    par_candestroy boolean,
    par_canmodprof boolean,
    par_canread boolean,
    can_communicate boolean,
    can_restitute boolean,
    par_cancommunicate boolean,
    par_canrestitute boolean
);


ALTER TABLE public.par_rights OWNER TO cdms;

--
-- Name: COLUMN par_rights.par_id; Type: COMMENT; Schema: public; Owner: cdms
--

COMMENT ON COLUMN public.par_rights.par_id IS 'Id profil d''archivage';


--
-- Name: COLUMN par_rights.userid; Type: COMMENT; Schema: public; Owner: cdms
--

COMMENT ON COLUMN public.par_rights.userid IS 'ID utilisateur';


--
-- Name: COLUMN par_rights.par_candeposit; Type: COMMENT; Schema: public; Owner: cdms
--

COMMENT ON COLUMN public.par_rights.par_candeposit IS 'Dépôt';


--
-- Name: COLUMN par_rights.par_candelay; Type: COMMENT; Schema: public; Owner: cdms
--

COMMENT ON COLUMN public.par_rights.par_candelay IS 'Prolongation dépôt';


--
-- Name: COLUMN par_rights.par_candestroy; Type: COMMENT; Schema: public; Owner: cdms
--

COMMENT ON COLUMN public.par_rights.par_candestroy IS 'Destruction';


--
-- Name: COLUMN par_rights.par_canmodprof; Type: COMMENT; Schema: public; Owner: cdms
--

COMMENT ON COLUMN public.par_rights.par_canmodprof IS 'Modification profil';


--
-- Name: COLUMN par_rights.par_canread; Type: COMMENT; Schema: public; Owner: cdms
--

COMMENT ON COLUMN public.par_rights.par_canread IS 'Lecture';


--
-- Name: COLUMN par_rights.can_communicate; Type: COMMENT; Schema: public; Owner: cdms
--

COMMENT ON COLUMN public.par_rights.can_communicate IS 'Communication';


--
-- Name: COLUMN par_rights.can_restitute; Type: COMMENT; Schema: public; Owner: cdms
--

COMMENT ON COLUMN public.par_rights.can_restitute IS 'Restitution';


--
-- Name: COLUMN par_rights.par_cancommunicate; Type: COMMENT; Schema: public; Owner: cdms
--

COMMENT ON COLUMN public.par_rights.par_cancommunicate IS 'Communication';


--
-- Name: COLUMN par_rights.par_canrestitute; Type: COMMENT; Schema: public; Owner: cdms
--

COMMENT ON COLUMN public.par_rights.par_canrestitute IS 'Restitution';


--
-- Name: param; Type: TABLE; Schema: public; Owner: cdms
--

CREATE TABLE public.param (
    paramid integer NOT NULL,
    paramsmtpserver text,
    paramsmtpuser text,
    paramsmtppassword text,
    paramsmtpport text,
    paramadminuser text,
    paramrepmail text,
    paramreppj text,
    paramaccueil text,
    paramconfid text,
    paramverrou integer,
    paramlogdetail boolean,
    elasticnode text DEFAULT 'dedicx.ckr-solutions.com'::text,
    elasticpath text DEFAULT 'jmax'::text,
    topmenu text,
    servletneoged text,
    portneoged text,
    baseneoged text,
    nodeneoged text,
    portavp text,
    databasename text,
    indexavp text,
    maxusers integer DEFAULT 15,
    logread boolean DEFAULT false,
    schemaneoged text,
    elasticcluster text,
    cryptage boolean DEFAULT false,
    mirror boolean DEFAULT false,
    mirroringurl text,
    elasticlogarchivage boolean DEFAULT false,
    elasticlogevent boolean DEFAULT false,
    pdfacheck boolean DEFAULT false,
    pdfalevel integer DEFAULT 0,
    stamptype text DEFAULT 'SHA'::text,
    externaltimestamp boolean DEFAULT false,
    archivageserver text,
    urlneoged text,
    passwdlevel integer DEFAULT 0,
    neogedserver text,
    updateged boolean DEFAULT false,
    initnumber text,
    openofficepath text,
    maxconvertsize integer DEFAULT 200,
    archivage_doublon boolean,
    cryptageid uuid,
    storageid integer,
    storagemirrorid integer
);


ALTER TABLE public.param OWNER TO cdms;

--
-- Name: COLUMN param.paramid; Type: COMMENT; Schema: public; Owner: cdms
--

COMMENT ON COLUMN public.param.paramid IS 'Id paramètres généraux';


--
-- Name: COLUMN param.paramsmtpserver; Type: COMMENT; Schema: public; Owner: cdms
--

COMMENT ON COLUMN public.param.paramsmtpserver IS 'Serveur SMTP';


--
-- Name: COLUMN param.paramsmtpuser; Type: COMMENT; Schema: public; Owner: cdms
--

COMMENT ON COLUMN public.param.paramsmtpuser IS 'Utilisateur SMTP';


--
-- Name: COLUMN param.paramsmtppassword; Type: COMMENT; Schema: public; Owner: cdms
--

COMMENT ON COLUMN public.param.paramsmtppassword IS 'Password SMTP';


--
-- Name: COLUMN param.paramsmtpport; Type: COMMENT; Schema: public; Owner: cdms
--

COMMENT ON COLUMN public.param.paramsmtpport IS 'Port SMTP';


--
-- Name: COLUMN param.paramadminuser; Type: COMMENT; Schema: public; Owner: cdms
--

COMMENT ON COLUMN public.param.paramadminuser IS 'Emetteur des alertes';


--
-- Name: COLUMN param.paramrepmail; Type: COMMENT; Schema: public; Owner: cdms
--

COMMENT ON COLUMN public.param.paramrepmail IS 'Répertoire des mails';


--
-- Name: COLUMN param.paramreppj; Type: COMMENT; Schema: public; Owner: cdms
--

COMMENT ON COLUMN public.param.paramreppj IS 'Répertoire des pièces jointes';


--
-- Name: COLUMN param.paramaccueil; Type: COMMENT; Schema: public; Owner: cdms
--

COMMENT ON COLUMN public.param.paramaccueil IS 'Ecran d''accueil par defaut';


--
-- Name: COLUMN param.paramconfid; Type: COMMENT; Schema: public; Owner: cdms
--

COMMENT ON COLUMN public.param.paramconfid IS 'Chaînes de confidentialité';


--
-- Name: COLUMN param.paramverrou; Type: COMMENT; Schema: public; Owner: cdms
--

COMMENT ON COLUMN public.param.paramverrou IS 'Niveau de verrouillage';


--
-- Name: COLUMN param.paramlogdetail; Type: COMMENT; Schema: public; Owner: cdms
--

COMMENT ON COLUMN public.param.paramlogdetail IS 'Log du détail';


--
-- Name: COLUMN param.elasticnode; Type: COMMENT; Schema: public; Owner: cdms
--

COMMENT ON COLUMN public.param.elasticnode IS 'Serveur Elastic';


--
-- Name: COLUMN param.elasticpath; Type: COMMENT; Schema: public; Owner: cdms
--

COMMENT ON COLUMN public.param.elasticpath IS 'Serveur Elastic path';


--
-- Name: COLUMN param.topmenu; Type: COMMENT; Schema: public; Owner: cdms
--

COMMENT ON COLUMN public.param.topmenu IS 'Top menu';


--
-- Name: COLUMN param.servletneoged; Type: COMMENT; Schema: public; Owner: cdms
--

COMMENT ON COLUMN public.param.servletneoged IS 'Servlet neoged';


--
-- Name: COLUMN param.portneoged; Type: COMMENT; Schema: public; Owner: cdms
--

COMMENT ON COLUMN public.param.portneoged IS 'Port NEOGED';


--
-- Name: COLUMN param.baseneoged; Type: COMMENT; Schema: public; Owner: cdms
--

COMMENT ON COLUMN public.param.baseneoged IS 'Base NEOGED';


--
-- Name: COLUMN param.nodeneoged; Type: COMMENT; Schema: public; Owner: cdms
--

COMMENT ON COLUMN public.param.nodeneoged IS 'Noeud NEOGED';


--
-- Name: COLUMN param.portavp; Type: COMMENT; Schema: public; Owner: cdms
--

COMMENT ON COLUMN public.param.portavp IS 'Port elastic log archivage';


--
-- Name: COLUMN param.databasename; Type: COMMENT; Schema: public; Owner: cdms
--

COMMENT ON COLUMN public.param.databasename IS 'Nom base';


--
-- Name: COLUMN param.indexavp; Type: COMMENT; Schema: public; Owner: cdms
--

COMMENT ON COLUMN public.param.indexavp IS 'Schéma log archivage';


--
-- Name: COLUMN param.logread; Type: COMMENT; Schema: public; Owner: cdms
--

COMMENT ON COLUMN public.param.logread IS 'Log des lectures';


--
-- Name: COLUMN param.elasticcluster; Type: COMMENT; Schema: public; Owner: cdms
--

COMMENT ON COLUMN public.param.elasticcluster IS 'nom du cluster elastic';


--
-- Name: COLUMN param.cryptageid; Type: COMMENT; Schema: public; Owner: cdms
--

COMMENT ON COLUMN public.param.cryptageid IS 'id de l algorythm de chiffrage à utiliser';


--
-- Name: COLUMN param.storageid; Type: COMMENT; Schema: public; Owner: cdms
--

COMMENT ON COLUMN public.param.storageid IS 'id à utiliser pour appeler le module de stockage';


--
-- Name: COLUMN param.storagemirrorid; Type: COMMENT; Schema: public; Owner: cdms
--

COMMENT ON COLUMN public.param.storagemirrorid IS 'id de l entree dans la table paramstockage';


--
-- Name: param_automate; Type: TABLE; Schema: public; Owner: cdms
--

CREATE TABLE public.param_automate (
    paramid integer NOT NULL,
    hostname text,
    port text,
    servlet text
);


ALTER TABLE public.param_automate OWNER TO cdms;

--
-- Name: param_paramid_seq; Type: SEQUENCE; Schema: public; Owner: cdms
--

CREATE SEQUENCE public.param_paramid_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.param_paramid_seq OWNER TO cdms;

--
-- Name: param_paramid_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: cdms
--

ALTER SEQUENCE public.param_paramid_seq OWNED BY public.param.paramid;


--
-- Name: paramstorage; Type: TABLE; Schema: public; Owner: cdms
--

CREATE TABLE public.paramstorage (
    paramid integer NOT NULL,
    type_storage text NOT NULL,
    remoteorlocal text NOT NULL,
    hostname text,
    port text,
    servlet text,
    directory text,
    storageid text
);


ALTER TABLE public.paramstorage OWNER TO cdms;

--
-- Name: COLUMN paramstorage.paramid; Type: COMMENT; Schema: public; Owner: cdms
--

COMMENT ON COLUMN public.paramstorage.paramid IS 'id du parametre de stockage';


--
-- Name: COLUMN paramstorage.type_storage; Type: COMMENT; Schema: public; Owner: cdms
--

COMMENT ON COLUMN public.paramstorage.type_storage IS 'type de stockage';


--
-- Name: COLUMN paramstorage.remoteorlocal; Type: COMMENT; Schema: public; Owner: cdms
--

COMMENT ON COLUMN public.paramstorage.remoteorlocal IS 'remote or local storage';


--
-- Name: COLUMN paramstorage.hostname; Type: COMMENT; Schema: public; Owner: cdms
--

COMMENT ON COLUMN public.paramstorage.hostname IS 'nom du host pour le stockage distant';


--
-- Name: COLUMN paramstorage.port; Type: COMMENT; Schema: public; Owner: cdms
--

COMMENT ON COLUMN public.paramstorage.port IS 'numero du port pour le stockage distant';


--
-- Name: COLUMN paramstorage.servlet; Type: COMMENT; Schema: public; Owner: cdms
--

COMMENT ON COLUMN public.paramstorage.servlet IS 'servlet pour le stockage distant';


--
-- Name: COLUMN paramstorage.directory; Type: COMMENT; Schema: public; Owner: cdms
--

COMMENT ON COLUMN public.paramstorage.directory IS 'nom du directory pour le stockage local';


--
-- Name: paramuser; Type: TABLE; Schema: public; Owner: cdms
--

CREATE TABLE public.paramuser (
    lstmuser character varying(30) NOT NULL,
    lstmmenu character varying(800),
    lstmwidgets character varying(800),
    lstmcolor character varying(20),
    lstmimage character varying(30),
    lstmparam character varying(250),
    lstmcolorfond text,
    lstmcolorform text,
    iduser integer NOT NULL
);


ALTER TABLE public.paramuser OWNER TO cdms;

--
-- Name: prof; Type: TABLE; Schema: public; Owner: cdms
--

CREATE TABLE public.prof (
    profid character varying(30) NOT NULL,
    proflib character varying(30),
    profdroits numeric(2,0),
    welcomescreen text DEFAULT 'ELASTICPROC'::text
);


ALTER TABLE public.prof OWNER TO cdms;

--
-- Name: profils; Type: TABLE; Schema: public; Owner: cdms
--

CREATE TABLE public.profils (
    par_id integer NOT NULL,
    ar_profile text,
    par_conservation integer,
    destructioncriteriaid integer,
    sortfinalid integer
);


ALTER TABLE public.profils OWNER TO cdms;

--
-- Name: COLUMN profils.par_id; Type: COMMENT; Schema: public; Owner: cdms
--

COMMENT ON COLUMN public.profils.par_id IS 'Id profil d''archivage';


--
-- Name: COLUMN profils.ar_profile; Type: COMMENT; Schema: public; Owner: cdms
--

COMMENT ON COLUMN public.profils.ar_profile IS 'Profil d''archivage';


--
-- Name: COLUMN profils.par_conservation; Type: COMMENT; Schema: public; Owner: cdms
--

COMMENT ON COLUMN public.profils.par_conservation IS 'Durée de conservation';


--
-- Name: profils_par_id_seq; Type: SEQUENCE; Schema: public; Owner: cdms
--

CREATE SEQUENCE public.profils_par_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.profils_par_id_seq OWNER TO cdms;

--
-- Name: profils_par_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: cdms
--

ALTER SEQUENCE public.profils_par_id_seq OWNED BY public.profils.par_id;


--
-- Name: restitutionlist; Type: TABLE; Schema: public; Owner: cdms
--

CREATE TABLE public.restitutionlist (
    restitutionid uuid NOT NULL,
    docid uuid NOT NULL,
    title text,
    restitue boolean DEFAULT false
);


ALTER TABLE public.restitutionlist OWNER TO cdms;

--
-- Name: COLUMN restitutionlist.restitutionid; Type: COMMENT; Schema: public; Owner: cdms
--

COMMENT ON COLUMN public.restitutionlist.restitutionid IS 'Numéro de restitution';


--
-- Name: COLUMN restitutionlist.docid; Type: COMMENT; Schema: public; Owner: cdms
--

COMMENT ON COLUMN public.restitutionlist.docid IS 'Id document archivé';


--
-- Name: COLUMN restitutionlist.title; Type: COMMENT; Schema: public; Owner: cdms
--

COMMENT ON COLUMN public.restitutionlist.title IS 'Titre du document';


--
-- Name: restitutions; Type: TABLE; Schema: public; Owner: cdms
--

CREATE TABLE public.restitutions (
    restitutionid uuid NOT NULL,
    restitutionmotif text,
    restitutionstatus text,
    userid text,
    domnnom text,
    horodatage timestamp(6) with time zone,
    destinataire text,
    restitution_end timestamp(6) with time zone
);


ALTER TABLE public.restitutions OWNER TO cdms;

--
-- Name: COLUMN restitutions.restitutionid; Type: COMMENT; Schema: public; Owner: cdms
--

COMMENT ON COLUMN public.restitutions.restitutionid IS 'Numéro de restitution';


--
-- Name: COLUMN restitutions.restitutionmotif; Type: COMMENT; Schema: public; Owner: cdms
--

COMMENT ON COLUMN public.restitutions.restitutionmotif IS 'Motif de restitution';


--
-- Name: COLUMN restitutions.restitutionstatus; Type: COMMENT; Schema: public; Owner: cdms
--

COMMENT ON COLUMN public.restitutions.restitutionstatus IS 'Statut de restitution';


--
-- Name: COLUMN restitutions.userid; Type: COMMENT; Schema: public; Owner: cdms
--

COMMENT ON COLUMN public.restitutions.userid IS 'ID utilisateur';


--
-- Name: COLUMN restitutions.domnnom; Type: COMMENT; Schema: public; Owner: cdms
--

COMMENT ON COLUMN public.restitutions.domnnom IS 'Silo';


--
-- Name: secret_key; Type: TABLE; Schema: public; Owner: cdms
--

CREATE TABLE public.secret_key (
    keyid uuid NOT NULL,
    algorythm text,
    encodedkey bytea
);


ALTER TABLE public.secret_key OWNER TO cdms;

--
-- Name: COLUMN secret_key.keyid; Type: COMMENT; Schema: public; Owner: cdms
--

COMMENT ON COLUMN public.secret_key.keyid IS 'Id de la clé de chiffrement';


--
-- Name: COLUMN secret_key.algorythm; Type: COMMENT; Schema: public; Owner: cdms
--

COMMENT ON COLUMN public.secret_key.algorythm IS 'Algorythme de chiffrement pour lequel est utilisé la clé';


--
-- Name: COLUMN secret_key.encodedkey; Type: COMMENT; Schema: public; Owner: cdms
--

COMMENT ON COLUMN public.secret_key.encodedkey IS 'contenu de la clé';


--
-- Name: services; Type: TABLE; Schema: public; Owner: cdms
--

CREATE TABLE public.services (
    service text NOT NULL,
    libservice text
);


ALTER TABLE public.services OWNER TO cdms;

--
-- Name: COLUMN services.service; Type: COMMENT; Schema: public; Owner: cdms
--

COMMENT ON COLUMN public.services.service IS 'Service verseur';


--
-- Name: COLUMN services.libservice; Type: COMMENT; Schema: public; Owner: cdms
--

COMMENT ON COLUMN public.services.libservice IS 'Libellé';


--
-- Name: sortfinal_sortfinalid_seq; Type: SEQUENCE; Schema: public; Owner: cdms
--

CREATE SEQUENCE public.sortfinal_sortfinalid_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.sortfinal_sortfinalid_seq OWNER TO cdms;

--
-- Name: sortfinal; Type: TABLE; Schema: public; Owner: cdms
--

CREATE TABLE public.sortfinal (
    sortfinalid integer DEFAULT nextval('public.sortfinal_sortfinalid_seq'::regclass) NOT NULL,
    sortfinal text
);


ALTER TABLE public.sortfinal OWNER TO cdms;

--
-- Name: COLUMN sortfinal.sortfinalid; Type: COMMENT; Schema: public; Owner: cdms
--

COMMENT ON COLUMN public.sortfinal.sortfinalid IS 'Id du sort final';


--
-- Name: types; Type: TABLE; Schema: public; Owner: cdms
--

CREATE TABLE public.types (
    doctype_archivage text NOT NULL,
    doctypelib text
);


ALTER TABLE public.types OWNER TO cdms;

--
-- Name: COLUMN types.doctype_archivage; Type: COMMENT; Schema: public; Owner: cdms
--

COMMENT ON COLUMN public.types.doctype_archivage IS 'Type de document';


--
-- Name: COLUMN types.doctypelib; Type: COMMENT; Schema: public; Owner: cdms
--

COMMENT ON COLUMN public.types.doctypelib IS 'Libellé';


--
-- Name: types_manu; Type: TABLE; Schema: public; Owner: cdms
--

CREATE TABLE public.types_manu (
    typemanuid integer NOT NULL,
    source text,
    doctype text,
    keywordslist text,
    typagename text,
    mailowner text,
    domaineowner text,
    shareddomains text,
    workflowid integer
);


ALTER TABLE public.types_manu OWNER TO cdms;

--
-- Name: COLUMN types_manu.typemanuid; Type: COMMENT; Schema: public; Owner: cdms
--

COMMENT ON COLUMN public.types_manu.typemanuid IS 'Id type';


--
-- Name: COLUMN types_manu.source; Type: COMMENT; Schema: public; Owner: cdms
--

COMMENT ON COLUMN public.types_manu.source IS 'application source';


--
-- Name: COLUMN types_manu.doctype; Type: COMMENT; Schema: public; Owner: cdms
--

COMMENT ON COLUMN public.types_manu.doctype IS 'Type de document';


--
-- Name: COLUMN types_manu.keywordslist; Type: COMMENT; Schema: public; Owner: cdms
--

COMMENT ON COLUMN public.types_manu.keywordslist IS 'Mots cl?s';


--
-- Name: COLUMN types_manu.typagename; Type: COMMENT; Schema: public; Owner: cdms
--

COMMENT ON COLUMN public.types_manu.typagename IS 'Cat?gorie';


--
-- Name: COLUMN types_manu.mailowner; Type: COMMENT; Schema: public; Owner: cdms
--

COMMENT ON COLUMN public.types_manu.mailowner IS 'Propri?taire';


--
-- Name: COLUMN types_manu.domaineowner; Type: COMMENT; Schema: public; Owner: cdms
--

COMMENT ON COLUMN public.types_manu.domaineowner IS 'Service versant';


--
-- Name: COLUMN types_manu.shareddomains; Type: COMMENT; Schema: public; Owner: cdms
--

COMMENT ON COLUMN public.types_manu.shareddomains IS 'Domaine autoris?s';


--
-- Name: COLUMN types_manu.workflowid; Type: COMMENT; Schema: public; Owner: cdms
--

COMMENT ON COLUMN public.types_manu.workflowid IS 'Id workflow';


--
-- Name: types_manu_typemanuid_seq; Type: SEQUENCE; Schema: public; Owner: cdms
--

CREATE SEQUENCE public.types_manu_typemanuid_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.types_manu_typemanuid_seq OWNER TO cdms;

--
-- Name: types_manu_typemanuid_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: cdms
--

ALTER SEQUENCE public.types_manu_typemanuid_seq OWNED BY public.types_manu.typemanuid;


--
-- Name: util; Type: TABLE; Schema: public; Owner: cdms
--

CREATE TABLE public.util (
    userid character varying(30) NOT NULL,
    mailid character varying(50),
    userpassword text,
    profid text NOT NULL,
    userlib text,
    image bytea,
    langue text,
    iduser integer NOT NULL,
    domnnom text,
    service text
);


ALTER TABLE public.util OWNER TO cdms;

--
-- Name: util_iduser_seq; Type: SEQUENCE; Schema: public; Owner: cdms
--

CREATE SEQUENCE public.util_iduser_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.util_iduser_seq OWNER TO cdms;

--
-- Name: util_iduser_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: cdms
--

ALTER SEQUENCE public.util_iduser_seq OWNED BY public.util.iduser;


--
-- Name: zauthmails; Type: VIEW; Schema: public; Owner: cdms
--

CREATE VIEW public.zauthmails AS
 SELECT x.userid,
    x.mailid,
    x.iduser,
    x.estobservable
   FROM ( SELECT a.userid,
            a.mailid,
            a.iduser,
            y.estobservable
           FROM (public.util a
             JOIN public.mail y ON (((a.mailid)::text = y.mailid)))
        UNION
         SELECT b.userid,
            a.usermail AS mailid,
            b.iduser,
            y.estobservable
           FROM ((public.authmails a
             JOIN public.util b ON ((a.iduser = b.iduser)))
             JOIN public.mail y ON (((b.mailid)::text = y.mailid)))) x;


ALTER TABLE public.zauthmails OWNER TO cdms;

--
-- Name: zdocument; Type: VIEW; Schema: public; Owner: cdms
--

CREATE VIEW public.zdocument AS
 SELECT count(*) AS nbdoc,
    x.datercstats,
    x.rcsilo,
    x.doctype,
    x.categorie
   FROM ( SELECT count(*) AS nbdoc,
            c.doctype,
            c.categorie,
            c.datercstats,
            c.rcsilo,
            c.keywords
           FROM ( SELECT document.doctype,
                    document.categorie,
                    date_trunc('day'::text, document.archive_date) AS datercstats,
                    document.domnnom AS rcsilo,
                    document.keywords
                   FROM public.document
                  WHERE (document.statut = 1)) c
          GROUP BY c.doctype, c.categorie, c.datercstats, c.rcsilo, c.keywords) x
  GROUP BY x.doctype, x.categorie, x.rcsilo, x.datercstats;


ALTER TABLE public.zdocument OWNER TO cdms;


--
-- Name: conversions conversion_id; Type: DEFAULT; Schema: public; Owner: cdms
--

ALTER TABLE ONLY public.conversions ALTER COLUMN conversion_id SET DEFAULT nextval('public.conversions_conversion_id_seq'::regclass);



--
-- Name: destructioncriterias destructioncriteriaid; Type: DEFAULT; Schema: public; Owner: cdms
--

ALTER TABLE ONLY public.destructioncriterias ALTER COLUMN destructioncriteriaid SET DEFAULT nextval('public.destructioncriterias_destructioncriteriaid_seq'::regclass);


--
-- Name: doctypes doctypeid; Type: DEFAULT; Schema: public; Owner: cdms
--

ALTER TABLE ONLY public.doctypes ALTER COLUMN doctypeid SET DEFAULT nextval('public.doctypes_doctypeid_seq'::regclass);



--
-- Name: log_mo logid; Type: DEFAULT; Schema: public; Owner: cdms
--

ALTER TABLE ONLY public.log_mo ALTER COLUMN logid SET DEFAULT nextval('public.log_mo_logid_seq'::regclass);


--
-- Name: mail idmail; Type: DEFAULT; Schema: public; Owner: cdms
--

ALTER TABLE ONLY public.mail ALTER COLUMN idmail SET DEFAULT nextval('public.mail_idmail_seq'::regclass);


--
-- Name: mime_type mime_type_id; Type: DEFAULT; Schema: public; Owner: cdms
--

ALTER TABLE ONLY public.mime_type ALTER COLUMN mime_type_id SET DEFAULT nextval('public.mime_type_mime_type_id_seq'::regclass);


--
-- Name: param paramid; Type: DEFAULT; Schema: public; Owner: cdms
--

ALTER TABLE ONLY public.param ALTER COLUMN paramid SET DEFAULT nextval('public.param_paramid_seq'::regclass);


--
-- Name: profils par_id; Type: DEFAULT; Schema: public; Owner: cdms
--

ALTER TABLE ONLY public.profils ALTER COLUMN par_id SET DEFAULT nextval('public.profils_par_id_seq'::regclass);


--
-- Name: types_manu typemanuid; Type: DEFAULT; Schema: public; Owner: cdms
--

ALTER TABLE ONLY public.types_manu ALTER COLUMN typemanuid SET DEFAULT nextval('public.types_manu_typemanuid_seq'::regclass);


--
-- Name: util iduser; Type: DEFAULT; Schema: public; Owner: cdms
--

ALTER TABLE ONLY public.util ALTER COLUMN iduser SET DEFAULT nextval('public.util_iduser_seq'::regclass);


--
-- Data for Name: applications; Type: TABLE DATA; Schema: public; Owner: cdms
--

COPY public.applications (applicationcode, applicationname, applicationvalidation) FROM stdin;
NEOGED	NEOGED	t
NEOGED AVP	NEOGED AVP	t
ADELIS	ADELIS	t
nfz42013	nfz42013	t
\.


--
-- Data for Name: authmails; Type: TABLE DATA; Schema: public; Owner: cdms
--

COPY public.authmails (iduser, usermail) FROM stdin;
\.


--
-- Data for Name: bgservices; Type: TABLE DATA; Schema: public; Owner: cdms
--

COPY public.bgservices (bgs_cod, bgs_descr, bgs_on, bgs_param, bgs_start, bgs_process, bgs_encours) FROM stdin;
CREATELOGARCHIVE	Scellement des journaux de cycle de vie des archives	t	\N	\N	\N	f
CREATELOGEVENT	Scellement des journaux d'évenements	t	\N	\N	\N	f
DESTROY	Destruction à échéance	t	\N	\N	\N	f
CHECKFILES	Contrôle de lintégralité des archives	t	\N	\N	\N	f
IMPORTFILES	Importe des documents avec aumtomateRC module	t	\N	\N	\N	f
\.


--
-- Data for Name: chiffrement; Type: TABLE DATA; Schema: public; Owner: cdms
--

COPY public.chiffrement (cryptid, algorythm, idcrypkey) FROM stdin;
\.


--
-- Data for Name: communicationlist; Type: TABLE DATA; Schema: public; Owner: cdms
--

COPY public.communicationlist (communicationid, docid, communique, title) FROM stdin;
\.


--
-- Data for Name: communications; Type: TABLE DATA; Schema: public; Owner: cdms
--

COPY public.communications (communicationid, communicationmotif, communicationstatus, userid, domnnom, horodatage, destinataire, communication_end) FROM stdin;
\.


--
-- Data for Name: conversions; Type: TABLE DATA; Schema: public; Owner: cdms
--

COPY public.conversions (conversion_id, conversion_name, conversion_program, conversion_source, conversion_target) FROM stdin;
\.


--
-- Data for Name: depots; Type: TABLE DATA; Schema: public; Owner: cdms
--

COPY public.depots (iddepot, demandeur, status, message, horodatage) FROM stdin;
\.


--
-- Data for Name: destinataires; Type: TABLE DATA; Schema: public; Owner: cdms
--

COPY public.destinataires (destinataire, infosdestinataires) FROM stdin;
\.


--
-- Data for Name: destructioncriterias; Type: TABLE DATA; Schema: public; Owner: cdms
--

COPY public.destructioncriterias (destructioncriteriaid, destructioncriteria, mindestructiondelay) FROM stdin;
1	Standard	60
2	Express	0
\.


--
-- Data for Name: doctypes; Type: TABLE DATA; Schema: public; Owner: cdms
--

COPY public.doctypes (doctypeid, doctype_archivage, categorie, keywordslist, par_id) FROM stdin;
1	Photo	Voyages	Lieu,Objet	\N
2	Photo	Trombinoscope	Nom,Organisation	\N
4	Document	Compte-rendus	Type,Sujet,Auteur	1
5	Photo	Publicité	Société,Objet	\N
6	Photo	Paysages	Lieu,Type de paysage	\N
3	Factures	Facture	Tiers,Numéro de fournisseur,Siret,Numéro facture, Date de facture,Date de réception,Exercice,Budget,Service,Objet,Montant TTC, Montant HT, Montant TVA,Type de document,Engagement, Commande, Numéro de marché,Exercice du marché,Date du service fait,Numéro de lot,Imputation	\N
\.


--
-- Data for Name: document; Type: TABLE DATA; Schema: public; Owner: cdms
--

COPY public.document (docid, title, date, archiver_id, content_type, content_length, keywords, doctype, archive_date, application, idsource, categorie, archive_end, author, mailowner, domaineowner, archiver_mail, par_id, elasticid, content, domnnom, conteneur, lot, iddepot, serviceverseur, description, cryptage, cryptage_algo, organisationverseuse, organisationversante, logicaldelete, logicaldeletedate, md5, cryptage_algoid, cryptage_iv, statut, pronom_type, pronom_id, num_archive) FROM stdin;
\.


--
-- Data for Name: domn; Type: TABLE DATA; Schema: public; Owner: cdms
--

COPY public.domn (domnnom, domnlib, orgaparent, estobservable, externe) FROM stdin;
*	Racine	\N	t	f
\.


--
-- Data for Name: drafts; Type: TABLE DATA; Schema: public; Owner: cdms
--

COPY public.drafts (docid, doctype, categorie, keywords, content, content_length, content_type, domaineowner, organisationversante, docsdate, description, title, domnnom, mailowner, transmis, statut, motif, userid, draftdate, archiveid, pronom_type, pronom_id) FROM stdin;
\.


--
-- Data for Name: droo; Type: TABLE DATA; Schema: public; Owner: cdms
--

COPY public.droo (profid, objecod, drooread, droowrite, droorewrite, droodelete, drooprint, drooexport, droodownload, secusup1, secusup2, secusup3) FROM stdin;
SUPERUSER	profils	O	O	O	O	f	f	f	\N	\N	\N
SUPERUSER	par_rights	O	O	O	O	f	f	f	\N	\N	\N
ADMINARCHIVAGE	paramuser	O	O	O	N	f	f	f	\N	\N	\N
ADMINARCHIVAGE	UTIL	O	O	O	O	f	f	f	\N	\N	\N
ADMINARCHIVAGE	mail	O	O	O	O	f	f	f	\N	\N	\N
ADMINARCHIVAGE	PARAM	O	O	O	N	f	f	f	\N	\N	\N
SUPERUSER	depots	O	N	N	N	f	f	f	\N	\N	\N
SUPERUSER	mime_doctypes	O	O	O	O	f	f	f	\N	\N	\N
SUPERUSER	doctypes	O	O	O	O	f	f	f	\N	\N	\N
SUPERUSER	log_archive	O	O	O	O	f	f	f	\N	\N	\N
SUPERUSER	log_event	O	O	O	O	f	f	f	\N	\N	\N
SUPERUSER	restitutions	O	O	N	N	f	f	f	\N	\N	\N
SUPERUSER	document	O	O	N	N	f	f	f	\N	\N	\N
SUPERUSER	TYPES_MANU	O	O	N	N	f	f	f	\N	\N	\N
SUPERUSER	mime_type	O	O	O	O	f	f	f	\N	\N	\N
ARCHIVISTE	document	O	N	N	O	t	f	f	\N	\N	\N
ARCHIVISTE	paramuser	O	N	O	N	f	t	f	\N	\N	\N
ARCHIVISTE	neoged	O	N	N	N	f	f	f	\N	\N	\N
ADMINARCHIVAGE	DOMN	O	O	O	O	f	f	f	\N	\N	\N
ARCHIVISTE	draftstoarchive	O	O	O	O	f	f	f	\N	\N	\N
VERSANT	document	N	N	N	N	f	f	f	\N	\N	\N
ARCHIVISTE	documentdelete	N	N	N	N	f	f	f	\N	\N	\N
ARCHIVISTE	communicationlist	O	O	O	N	f	f	f	\N	\N	\N
VERSANT	communicationsvalid	O	O	O	O	f	f	f	\N	\N	\N
VERSANT	communications	O	O	N	N	f	f	f	\N	\N	\N
COMPTABLE	communicationsvalid	O	N	N	N	f	f	f	\N	\N	\N
COMPTABLE	communications	O	O	N	N	f	f	f	\N	\N	\N
COMPTABLE	creationdraft	O	O	O	O	f	f	f	\N	\N	\N
COMPTABLE	document	O	N	N	N	f	f	f	\N	\N	\N
COMPTABLE	paramuser	O	O	O	N	f	f	f	\N	\N	\N
ARCHIVISTE	restitutions	O	O	O	O	f	f	f	\N	\N	\N
ARCHIVISTE	communicationsvalid	O	O	O	O	f	f	f	\N	\N	\N
ARCHIVISTE	communications	O	O	O	O	f	f	f	\N	\N	\N
COMPTABLE	login	N	N	N	N	f	f	f	\N	\N	\N
ARCHIVISTE	DEMABOX	O	O	O	N	f	f	f	\N	\N	\N
ARCHIVISTE	depots	O	N	N	N	f	f	f	\N	\N	\N
ARCHIVISTE	documents	O	O	O	O	t	f	f	\N	\N	\N
COMPTABLE	DEMABOX	O	O	O	N	f	f	f	\N	\N	\N
VERSANT	login	N	N	N	N	f	f	f	\N	\N	\N
\.


--
-- Data for Name: empreintes; Type: TABLE DATA; Schema: public; Owner: cdms
--

COPY public.empreintes (docid, empreinte, empreinte_algo, empreinte_unique, empreinte_telino) FROM stdin;
\.


--
-- Data for Name: exp_comments; Type: TABLE DATA; Schema: public; Owner: cdms
--

COPY public.exp_comments (comid, comdate, comment, userid, task_id) FROM stdin;
\.


--
-- Data for Name: exp_task; Type: TABLE DATA; Schema: public; Owner: cdms
--

COPY public.exp_task (taskid, datedeb, datefin, docid, horodatage, logid, nbtries, state, tasktypeid, userid) FROM stdin;
\.


--
-- Data for Name: exp_task_type; Type: TABLE DATA; Schema: public; Owner: cdms
--

COPY public.exp_task_type (typeid, taskname, expirationtime, maxnbtries) FROM stdin;
1	NeedHumanIntervention	10	3
2	RelaunchFileEntiretyCheck	10	3
3	CheckRestoreMasterHash	10	3
4	CheckRestoreMirrorHash	10	3
5	RestoreMasterFile	10	3
6	RestoreMirrorFile	10	3
7	CheckRestoreMasterMetaData	10	3
8	CheckRestoreMirrorMetaData	10	3
\.


--
-- Data for Name: keyword; Type: TABLE DATA; Schema: public; Owner: cdms
--

COPY public.keyword (keyword, doctype, categorie, keywordvalues) FROM stdin;
\.


--
-- Data for Name: log_archive; Type: TABLE DATA; Schema: public; Owner: cdms
--

COPY public.log_archive (logid, horodatage, operation, userid, docid, mailid, docsname, logtype, timestamptoken, hash, attestation) FROM stdin;
\.



--
-- Data for Name: log_event; Type: TABLE DATA; Schema: public; Owner: cdms
--

COPY public.log_event (logid, origin, processus, action, horodatage, detail, customer_name, operateur, versionprocessus, logtype, timestamptoken, archiveid, journalid, trace, methode, hash, journalxmlid, statexp) FROM stdin;
\.


--
-- Data for Name: log_mo; Type: TABLE DATA; Schema: public; Owner: cdms
--

COPY public.log_mo (logid, userid, mailid, profdroits, logip, loghost, logreason, logbrisdeglace, logtime) FROM stdin;
\.


--
-- Data for Name: login; Type: TABLE DATA; Schema: public; Owner: cdms
--

COPY public.login (userid, usermail, userpassword, nom, prenom) FROM stdin;
ADMIN	admin@telino.com	$2a$06$C7PldZyvtwy4UUv0rAiJ.ett7rTNKJ8Fhz0xrtAzfnHXGVa6ICt8K	\N	\N
PASSWORD1	\N	$2a$06$Mb1cT1qEYW16FJFVFxPQPOzSOMKzzMkGaS.Gt4M.U6eJ/QU0hhS9K	\N	\N
PASSWORD2	\N	$2a$06$ZozsLK65NFPC8vIEXdvLZeS9zDWwoDRcmC7jpLZVQoIFCjCGEEXgW	\N	\N
system	system@telino.com	\N	\N	\N
\.


--
-- Data for Name: mail; Type: TABLE DATA; Schema: public; Owner: cdms
--

COPY public.mail (mailid, maillib, mailorg, mailinterne, idmail, estobservable) FROM stdin;
admin@telino.com	Administrateur		f	1	t
password1@telino.com	password1	\N	\N	3	t
password2@telino.com	password2	\N	\N	4	t
\.


--
-- Data for Name: mime_doctypes; Type: TABLE DATA; Schema: public; Owner: cdms
--

COPY public.mime_doctypes (mime_type_id, doctypeid) FROM stdin;
2	1
3	1
5	1
5	6
7	6
3	6
2	6
7	1
10	4
12	4
1	3
13	3
14	3
15	6
16	6
13	4
14	4
1	4
19	4
20	4
\.


--
-- Data for Name: mime_type; Type: TABLE DATA; Schema: public; Owner: cdms
--

COPY public.mime_type (mime_type_id, content_type, mime_description) FROM stdin;
1	application/pdf	Document PDF
2	image/gif	Image GIF
3	image/png	Images PNG
4	application/octet-stream	Octets
5	image/jpeg	Images JPEG
6	text/plain	texte
7	image/jpg	image jpg
10	application/msword	Document Word
12	application/vnd.openxmlformats-officedocument.wordprocessingml.document	Document OpenWord
13	application/xml	Fichier XML
14	text/xml	Fichier XML
15	image/tiff	Image TIFF
16	image/bmp	Bitmap
17	application/xml-dtd	Fichier DTD
18	pdfa	Fichier PDFA
19	application/vnd.oasis.opendocument.spreadsheet	Fichier ODS
20	application/vnd.oasis.opendocument.text	Fichier ODT
\.


--
-- Data for Name: modu; Type: TABLE DATA; Schema: public; Owner: cdms
--

COPY public.modu (moducod, modulib, modubasins, moducolor) FROM stdin;
BASE	Archivage	O	\N
\.


--
-- Data for Name: par_rights; Type: TABLE DATA; Schema: public; Owner: cdms
--

COPY public.par_rights (par_id, userid, par_candeposit, par_candelay, par_candestroy, par_canmodprof, par_canread, can_communicate, can_restitute, par_cancommunicate, par_canrestitute) FROM stdin;
\.


--
-- Data for Name: param; Type: TABLE DATA; Schema: public; Owner: cdms
--

COPY public.param (paramid, paramsmtpserver, paramsmtpuser, paramsmtppassword, paramsmtpport, paramadminuser, paramrepmail, paramreppj, paramaccueil, paramconfid, paramverrou, paramlogdetail, elasticnode, elasticpath, topmenu, servletneoged, portneoged, baseneoged, nodeneoged, portavp, databasename, indexavp, maxusers, logread, schemaneoged, elasticcluster, cryptage, mirror, mirroringurl, elasticlogarchivage, elasticlogevent, pdfacheck, pdfalevel, stamptype, externaltimestamp, archivageserver, urlneoged, passwdlevel, neogedserver, updateged, initnumber, openofficepath, maxconvertsize, archivage_doublon, cryptageid, storageid, storagemirrorid) FROM stdin;
1	localhost	admin@neoged.com	none	587	admin@neoged.fr	noPdf	noPdf	HTML		0	f	gastronomix	evol	Liste des dépôts(ARCHIVELIST)-documents_search.png,Archiver(ARCHIVEFROMAVP)-archive.png,Demandes d'archivages(drafts)-archive_demande.png,Rechercher un dépôt(ELASTICDOCHORSMAILSALL)-loupe.png,Importer depuis exterieur(ARCHIVEFROMNEOGED)-importfromneoged.png,Profils d'archivage(profils)-parametres.png,Factures(ELASTICDOCHORSMAILSALLDOM)-neoged.png,Utilisateurs(login)-users.png,Types de documents(doctypes)-typesdocuments.png	cdmsserveur	8087	TELINO	neoged.telino.fr	9300	avp_001	archivage	15	t	\N	elasticsearch	t	t	AVP_001_M	f	f	f	0	SHA	f	Rémy Cointreau	https://neoged.telino.fr/neoged?environnement=TELINO	0	Rémy Cointreau	t	1522939094343	/usr/lib64/libreoffice	200	t	\N	1	2
\.


--
-- Data for Name: param_automate; Type: TABLE DATA; Schema: public; Owner: cdms
--

COPY public.param_automate (paramid, hostname, port, servlet) FROM stdin;
1	localhost	8087	automateRC/AutomateServiceServlet
\.


--
-- Data for Name: paramstorage; Type: TABLE DATA; Schema: public; Owner: cdms
--

COPY public.paramstorage (paramid, type_storage, remoteorlocal, hostname, port, servlet, directory, storageid) FROM stdin;
1	FileStorage	remote	\N	\N	modulestockage/StorageService	\N	\N
2	FileStorage	remote	\N	\N	modulestockage/StorageService	\N	\N
\.


--
-- Data for Name: paramuser; Type: TABLE DATA; Schema: public; Owner: cdms
--

COPY public.paramuser (lstmuser, lstmmenu, lstmwidgets, lstmcolor, lstmimage, lstmparam, lstmcolorfond, lstmcolorform, iduser) FROM stdin;
ADMIN	PARAM,DOMN,environnements,CONFIG,CTLS,iniparam,PROF,ORGATREE,ELASTICLOGEVENT,LOGARCHIVAGE,bgservices,STARTSERVICE,STOPSERVICE,applications,TYPES_MANU		#46a6a6			white	white	1
\.


--
-- Data for Name: prof; Type: TABLE DATA; Schema: public; Owner: cdms
--

COPY public.prof (profid, proflib, profdroits, welcomescreen) FROM stdin;
SUPERUSER	Responsable Archivage	0	Profils
ADMINARCHIVAGE	Administrateur AVP	2	Archives
ADMIN	Administrateur	2	Archives
PASSWORD2	password	2	Welcome
PASSWORD1	password	1	Welcome
ARCHIVISTE	Archiviste	1	Archives
COMPTABLE	comptable	1	Archives
VERSANT	Service versant	1	Archives
\.


--
-- Data for Name: profils; Type: TABLE DATA; Schema: public; Owner: cdms
--

COPY public.profils (par_id, ar_profile, par_conservation, destructioncriteriaid, sortfinalid) FROM stdin;
1	Documents	120	1	2
\.


--
-- Data for Name: restitutionlist; Type: TABLE DATA; Schema: public; Owner: cdms
--

COPY public.restitutionlist (restitutionid, docid, title, restitue) FROM stdin;
\.


--
-- Data for Name: restitutions; Type: TABLE DATA; Schema: public; Owner: cdms
--

COPY public.restitutions (restitutionid, restitutionmotif, restitutionstatus, userid, domnnom, horodatage, destinataire, restitution_end) FROM stdin;
\.


--
-- Data for Name: secret_key; Type: TABLE DATA; Schema: public; Owner: cdms
--

COPY public.secret_key (keyid, algorythm, encodedkey) FROM stdin;
\.


--
-- Data for Name: services; Type: TABLE DATA; Schema: public; Owner: cdms
--

COPY public.services (service, libservice) FROM stdin;
\.


--
-- Data for Name: sortfinal; Type: TABLE DATA; Schema: public; Owner: cdms
--

COPY public.sortfinal (sortfinalid, sortfinal) FROM stdin;
1	Restitution
2	Destruction
3	Tri ou échantillonnage
\.


--
-- Data for Name: types; Type: TABLE DATA; Schema: public; Owner: cdms
--

COPY public.types (doctype_archivage, doctypelib) FROM stdin;
Document	Document
Factures	Factures
Photo	Photo
\.


--
-- Data for Name: types_manu; Type: TABLE DATA; Schema: public; Owner: cdms
--

COPY public.types_manu (typemanuid, source, doctype, keywordslist, typagename, mailowner, domaineowner, shareddomains, workflowid) FROM stdin;
\.


--
-- Data for Name: util; Type: TABLE DATA; Schema: public; Owner: cdms
--

COPY public.util (userid, mailid, userpassword, profid, userlib, image, langue, iduser, domnnom, service) FROM stdin;
ADMIN	admin@telino.com	\N	ADMIN	Administrateur système	\\xffd8ffe000104a46494600010200000100010000ffdb004300080606070605080707070909080a0c140d0c0b0b0c1912130f141d1a1f1e1d1a1c1c20242e2720222c231c1c2837292c30313434341f27393d38323c2e333432ffdb0043010909090c0b0c180d0d1832211c213232323232323232323232323232323232323232323232323232323232323232323232323232323232323232323232323232ffc0001108008000ab03012200021101031101ffc4001f0000010501010101010100000000000000000102030405060708090a0bffc400b5100002010303020403050504040000017d01020300041105122131410613516107227114328191a1082342b1c11552d1f02433627282090a161718191a25262728292a3435363738393a434445464748494a535455565758595a636465666768696a737475767778797a838485868788898a92939495969798999aa2a3a4a5a6a7a8a9aab2b3b4b5b6b7b8b9bac2c3c4c5c6c7c8c9cad2d3d4d5d6d7d8d9dae1e2e3e4e5e6e7e8e9eaf1f2f3f4f5f6f7f8f9faffc4001f0100030101010101010101010000000000000102030405060708090a0bffc400b51100020102040403040705040400010277000102031104052131061241510761711322328108144291a1b1c109233352f0156272d10a162434e125f11718191a262728292a35363738393a434445464748494a535455565758595a636465666768696a737475767778797a82838485868788898a92939495969798999aa2a3a4a5a6a7a8a9aab2b3b4b5b6b7b8b9bac2c3c4c5c6c7c8c9cad2d3d4d5d6d7d8d9dae2e3e4e5e6e7e8e9eaf2f3f4f5f6f7f8f9faffda000c03010002110311003f00e7d57b629b28222623afd6a7038a64ec1554609c9ae03a56e509226fb306ce493daaa05264456258ee048157de3c80091cff0008e95db785be1d7991bea7e2357b1b48d4490ed9016900e73819e302b58a1cd98765a6dcdeba436f033b30f940e01fc7a5763a3f802408d3eb47ecb0a658c608f31971c918cfb67fa57532eb9a7db476f6564a6d637c36cfe29908c00bb4166278e73ed9ee396b8f14595ac514906babb55c37971287c027206480e579e7248e3a8e954a096e67a9725f0b785e60af06a72402450c9194de71cf38fc0f27d2aedbf81f436b569a2d583f98abe53f05796e38cf39c818fe75c8dedf6997525d49e63a4f3b6e7774624b739ce7be0f3c85c718c7154bcdbfb7d492e62b91e431da04b287d8372b6402795040c640c648ee00ae58f62753aad5fc1096d0cd3da5cf988a4945c64ede4fe3e9c753dab8d6899588605483d0d5bf0b78a356d3b5078756bbcaed8e2813cd048396e02e793c37bf1d1b240ed35ff0fd9ea71b5f68f32cac46e68864920e0823d38c71fe359ce1d51499c469f62d7572a8ab9e7935e8767a5adad9a9231f856478634fd8fbdd7073ce474ae9354ba10da93ed596e744172ab9c96b374859a067da3a0c5600b691ecd93ef3a36e181f7853f509fed33939cf3e9562ce7410f96709229cab1a89de3aa319bb991723738fa7a543e5e3b71e82ad4ab899f3eb4c29f2e7a8aa8e889b154afa8e7b7151b2607bfb56cdae95717f224712e59ba0ff00f5f4ad183c11aa5ccb222c0c16319f331953f423ae7afe5549df624c2d3a16f319c216dbd7da96e2ee471e586201eb835d4d8784f588525c5a952074930a5be99fcfff00af58d71e1cd551c87b19038ddf2853ce003c1c60f04f4cf43e952a0dcb99a0be96473cd1f3cf24d4062e6aeb0ff22a22873d2b415868538c638f5a8a703e518c919eb538eb57b4cb48aeaed44a4280783b41cfb73506f1dcea7e1de8564f31d4afe38a4f2be68c365883eb81d7ebebef55bc77e33717cb04774f1c688db8c6048ca7f849030320e7043679e457491c96c34b10473ed458c0e5b241e9dfa71f89e0906b8bbfb1b652cd15bf9b2b10449381260f4e00183df04e73c77c56eb6b132dee711ad788aeae248a6ba698c8db56388950c3a6dc1001c6076c019e2b2175cbc925c4739800e365b818279ea0000f6edce3f1adad6618ad8497977299a47e007e0b74e719e7ff00ae38ae723b77b990304db8390a00381eb815641e8fa2cd2dec6425da79a53e77918e49cf43ebf4e9d056a4ff00f1282263622e8952219164050be0b1cc63af01b201c6700d61787fc83a718da191ae40c2150e131feef3cf5e783d05626bfabc1653bcb15c25c3ee4923893681b7a90548c8e5065baf6c006aac45f5362f75a8f4fd3d23d2c469a8b335c4c60850969480046013cffb5853c929c722ba2f07ebd36ab771ef9bcb936c6927924100804719240518ebd09e7d6bc866bf5bdbf78a1855b3831050460372cc71d58fca09e9d7039af5af0759496103a45e6ca6e36b308a58c2c6a73c0e0923a92474001ebc526b42a2eecf5b8a5d363b732dc49685c0cbbc63682776dcfe7dea2bd8f45b84749206940566241600639233f88fceb925beb6bb8a693cf92d6d802f731c870ea4a82a366329b83b0c67278c1c81598da95fc5e53c525c6a0d3960f8b7d9e4101be41bb8207523d720f3d63e45fcce82e7c31a0bc2e6de49ad199895924cb20e42f7c752477ebc0e78acf5f03cd260c3a9599393b436e04e3f0f6ed5911df35cdfe67876a4215de685bcf8909395232dd98053fdef2db0735b167e210e1239137c3291024b03929239073b4aaf233f2818c8da7047352e098c86e3c09ac472eef2a3987526371fd706abda7856f5262f796b2c5029f99997000cff009f5ae820f123dbd92a22c9be4c96887df41d324939249c0dc7804302411ce6eb5e2397ecf3059b119884928ddb5b6b21c9cbb6002405c723e6e39e8bd9ae82d4cc7f1643a5cefa65869f3fdb1240ad2468263b421c9c0619ceec00082719c7515bd07882f758b393ed05e3b78654f3254c7c8c1549561c02032b1201c80c01da7705f1f4d76e6cfc43792d92244f6d0f97246d26fca3b29452080362058c15c03b891ee3a5b9b3d47514877dec5240aca2da08e3c45b4720000072727a1e464718ad52b2b220edafaef44d423925b8bc69618b3be7173bdd5086c67a6d5c8cf70412a7b5548af9f5a844b109e578181b72ec19db0c36e30410dc127054752768da6bce35bb7d474fb14293c1f6ac88fc858ca2852ca428da4024b28041031d39c71574df106a51595c1827951a1684b47e62883cadc4004e776ecb0c29cf0c78c2f2c0f55d4fc39a66a91dcdd69f98e4dc44489f724704ef52081b5874ebcf3815c4c9a6ddc52b235aca194e0e50ff008569e81e269aca5b99269bfb4e193319084a12541f98951b400060900f201eab8aec61d55ef22171269bad967e4edbb1101edb495c63a74e719c9ea62504c168795a1038ee6b634768f2fbcaa64632549cf73dfa7f5ac55c0fa5588e42cbc6768e428eff005ac3a9b53d9b3b98357325a1b6f262893b6304f20753d89007e95cddfdfc56fe63247e6c849cc9c2ec273f7739c9eb83c8ebf4aad62f25fdcf932aaadac632503601627f88f73f9f4f5e2ae5d593f02ca2519cfef39381d0e3bfa7f9c67689333cf3c40659a7f36e3eeb630ae7007d07f8d4ba269ab753a849923c9ecacf9c7e9fad4ba9592bddf9680dd5d1380ce7217fcfbd6fe95e1df2ed035d4cd25c63958c9c0f4e00e38ad12326ec45ada45a769c504a27b820ed2001b723b6385e33cf5e95e6da96ab782fe74f98cb2011beee58e0e31ebd7b7b0f4af52d46d4db5bc9202e8e838624f1deb84f0f68136ade32881dbe5231999d8a8518e85b3c0e481cfad5224e8bc0be1d8e15b3b9fb2b4d70f20f31fe6c202172381e8f93df8ea335ec3a6e87751ed8a1fb28802b24913092366206ecf19f9492dce40c93c126acf8534af2e491fcf902451b40ac8df2c8c725f0c7ef9dc09c8c0c638e38dcd4675815590c8ec84c6e1e56031f37cd8e1491d7a8271d72054b772d2b1cd2c4d6f752c692a4709648a261b814724858f6b839385523a06c0dd9e08b234ad2e3b5b75b9b3459ada6dd1f989bbefbecdcdbb24950c8c58e39e72bd174249ccb1b2dae4b8914a48081fbbdead8c023a720023a9e79383349248d61328924f29e44532ca8aab1801412379e41f6eb9e0e4e690cc0beb162f1df436f730b450e2141703319dccbc855209c6700676edea985ac1beb2ea8b13986e23f2d228e22c8a194966f987983e78f1b783f2f190307ac96e5918c769249b9995d4464cbb9f7907904ed5dd9ce319cfa66b2e68a59f558e2ba59dac923dca497760a0ba1077a9ece8719c64100934147213c68b6af242f6e5c43bd82cb1b36d249dc411c8c3300bd7939da715c8eabaeaa493dbc2c3c9450efb51763bb0e08527006dd8bd064f3d2bb5f104e2eafe3bb9228db74c23b7b74872e9b9400cdce18e4b1018e4ece08e83cd35cd264b147b8890daed1f3032e37c5b8ed042b1e8428c96ce0af1c6692dc9667f86753b9d275c6b9bb892e21c18e495797500962f1b0232e0238049c76240aefed7c54f6fa5cb72f7ae5930c657018213821593ae480c41c0e0679af2bfed2912ed258005190c0c591b38e9f98c9e79e46715d0c50cd7f0edd8c92931962ebbb77001eb9ce7807a74e739ad199a6695ef8934fbc825792ee4b895d08c2b3a073819c8c7d481d3b561c17b234a3cb1124614a63600a5793c8ce0e33c67d07a55b9fc1ed6e81ad250ca33b8b9c1ce7b0c7a7bd32df4c33ff00a99d3cec1c06241c60f3fd7a549a46c753a2dbc6358d3e05b795cda4ec257217f78711a151db6fcaab86e00624e464d7a445ab4f044912e9733aa0c02250703b2f2d9e3a60f231d0741e51e1bd36786f45c48ef0c85e32015e154641c8208c63a8c76fc2bbd4d3ecb508d2eaf6d449732282ec67da49c770509fcc9cf5a09662ea5a62d842856ea39379c0dbd6a9c512c8f142d288c4876b31acad04cd751fda6699a48e5394cff0d45e279664b091d19977308f7a9e809e4d735bdeb1d56b44f44d3358d0f48b6fb2c11a5cc409cb31dede9c01fcab46fd2caeaccb5986098f9e2538e31d3fa570fa46bfe17f0ee9823b7945d3a85dcd27cf96239fe95dff008320b0d5a0924b262c64cf9f196ced27a11ed5b596c60ddf5381d4231a64462b78b6cf20dccc002547a9f4fa7bd5cd16e36d9955dcea0ffac76e4fa927fa574d7de1b26e27b66529233fcecc71b87639a8c689158c3c0051476e0628531381c96ad3248acaf975ce70bc6efad743e03d0c7f665d5d341e5fda71107dc542a6ee49e40232bea324019e79b30e9169a8ee0a499083db815bd74478774f69a116aa2c2352cd3211b18e7a60f24e7db1838e4d0a571f235a9b8d7f60a4d9adc30fb3068dd581713701594a75738c1e87a1ec6b1e3fb5a3aa476d8d9bda274010ee75e471c038049e4b600c1ef5870de59ebab05d0b7b4fed0b7f2c4ab6afe6aed39f9b9e4670411d79ce4f5ae82de287ec7235e18edc2202448c507decf5cfcbc9e4f279aa02add4d3c578b15a2c02dd010c20ca953bb0c7ef124f03a8e5828ce79a853569a311a061f2ccc884ef03193b40f6048e80e327031d1b79e3af0cdcdcbdac7a9217f302b4be510ac7be09386e01cff2ec593c31c00ceb224dbe331f98eb9628790474cf18c60e46f3c77a04247710d8451248ecc10ab9f379e48e32415c1c6f5dfc00d8ea01ab1f6e965b390452169234f3d9ad1c00ec1588624e370263653e87fbc4f185a9cd1da235cc13470caa994cc7bb072a3e551f2807e5193cf047ae7317c413cb71770a3ef6742596356c9dc464960c0e4000723391d48e401735375a1bb372d73e6ca8326e24c864493736ddd201182ac4e063a153c1381c3f896492ccdc25dda2309164843ccfc91b00caf38032a3a0230719cf5eeb4e6ff4b4b4cb7d9b68944b23f2c3af1dd73f3107a6703800eee675a48e5d3ac6531c82052547eebe5539cb6ec1e00dc31839cfe2096d44ddd1e4768d326a0b1b803e65f9d41181d38c738e79c7515eafe178eda0b3b77de5d981693cc6da0f27e50dce3200ee7a02335cfa68b0c7745ce59436d720139272319c0e9d3d7e800aea6d21b3b38536c78912400c81770fbb9e47a63f5ad0ccbfad7872cf51866b98d9a3257708ddf0ad8e983dba633cfe38ae1ee2d6e2cb7ec7f2dd718def8519e01dddb9e9c63b66bd16fe4be8f4c9b74d01b7650d14d0b6e543c0c91900f46e99233f748e0f0fa3df473dcbd95f5b9565398c3c9b7cb65dbb941c71c8e40feee783c91a052d0d2d16e6f2d6d1a736e5a38f719adb0198aa8037c79c80a06463d875ea3b0fec6bdbcc4f62c925ab01e5b095172071d0f4e95cdf87f4cb88584e8a6385be57562555338c8c91c1c8f43f747a66bbfb2d28c9671b5b5d4b6d0e30b0c739509838c63239f5f7ee7a9976652ba3ceed7493a74491bb00557007a5727f106e3ecd650d943d5df2c7db15dff896ea38a550bd48c9af29f15ddb5e499639607839ae4a576d3677d6b2ba472f6d74d6ef90b91debd93e1aeba6cde19209cc618fcc33c91e95e287835bda06a8f03a5bae41ce41aeb9aba3822eccfb1e7b4b7f116980b90920c8591792a7fc3dab84f1069da9699e5c37037c1fc2e9f75bff00af58de17f19c9610289199c8003316edf4af44d2bc5165ab5a3add88c82c460e0822b16aeb5dcd62dadb630bc3966620b215264272a31f9556f1ce9cbac6917ba3daceb189a442640accdbd49249ec01c0f4edea6bacbcb48ad207bcb06c851c20e707b63f1c57996a179771de4b6a25f92360924b9da1016c2e016f9d8e32170327ae4628a71b2b32ea4d37a16fc01a25b787b4db8b68ee45c4ecc1e56ce4e4e141d9d80623f0c1aaface355b868aedbcab181d912dd24e25c1c166c75c81c63b51e1db5d43458353bebc942c76e81cb02a5838236e4e704162bdb818ea46471faaf8822dea03a861c6d1d31cd6a918b91d2deda695f6116cb05ba04e171101cf6c91cff2ac3d175596ca57d2ef9432400490315182a3863c0c92463ae7a1ed59b06a71dd4caa2560991919c55c7996499044aa6645326e3c9dbe991cf3e9dea9ad09b96aeef24bf59238e0f3647c80db723033d4e3a81c7af1db3cd65d720d3ee96059f4f82e446177dcc6ceacc78000180aa39c9edb7181d6abe9bab41631c77320dbcb2a1c6081919c6383c93d3e87d6b3353f0fc3addf47730dec7862141591724638503d7009fc6914b63b6b4d59ee3fb3750bd86211bee74f2d9550b64a9c139da005cf5c74c75cd4f35a2c7726ce428601334a9b100c06030303ee9c820818e631c536df4e161a3269cd1da10a80db17058eece32b81f7981271ea07b9a7599496de2da8aae49287616e57a60124f2b939239c9c8e2825b391d5ed72cb12f0d1820aa2e08cf3827ea4ff003ad4d16d2e61d2a57460dbd3397524065076f4e4f3d3e9f9dbbeb78cdcab30542c7e4654c6723a13d3bfb1c569e82d1a4eda5b00e7cbf3395c12727bfd3f9d2bea095f41d7fb97c364db46d3144326d9240be6329520b107b15ebd3bf604f27a2e9f7376ab3bb08a78caae5c616504e371ce3270c0f4c1073f5edcdb89da4b21295578c3a019e71fc3f96463dea375b458d3ecd112814a124e4eec703f1c63e9f5e54aa6972a30d6c599ddac34df2109919c11e64a497607e72d807e5e09fceb26e6e87daa510450bc21cf96cf1292573c7247a53afae7ce9814076140369278e39e4f3d49f5c74e7bd166dc72cdce31584e7766d18d9187e2ad44b4bb7392060e3d2bcef50919c839ce7b574daa33cacf213c31ae712d5ee6fe1b6032d2c817f0aaa7648d2a3bbb9b76fe0f8eefc1eb76148bd6cc8a7d47a563e8da2ceb3b4d326d0bc007bd7adc512c10a42806d550a05655ee92b0c9f6a8471d593d288d57d4ca74d5ae8ceb48a5f2f051d3d73deb7f4eb9b8b264119f9077639c1a65a40937cd9c91c726baad1b4c826dab247d39354d99c53b9aba46aad2d884690f9929db82bc67dab93d7ad6697588922df97b8694480e766e4c676f6e7a1e0e4718ce475175a65cd932436ec3ca760cb81c2d67eb5035bdd29750d0c808e990dea3dfae31fad284ba1551697287c4cf145a689e02b0d1ac248f75ea6f751f78aae064e40dc09c0ddfec7535f3e497b2bc8cdbdb93919af58f1ac305e4e161b06730444bc99dcc222d9ce7278562738c7dfcf635e5fac69f0d85e2ac532c88ea1b8fe1f6add3326b4083559a2c61b07d7dfd6bbef066eb8fdf4c5d8b93f7467681cff9fa579dc362d36d72c238c9c06635eb5e05fb1e95a07daaf6655576755320c028781fe79eb4db42b3286b9e146d395351443320dd9520b6ded9238fe7c13db354edb548ad2217102cf68095873804230c8c061c80077c9e873e87afd4f58b8950aa5b0962dc36b4876af00e47a71f337639fa57276d118ae0a431c9247291e65bb9dc067070a38208e07e27a54dd01d45a5e3cf6cc4163b559e40d16dc22e46ff0041f74e33b88dc4678ab504eecd345186660bbfcb29c824639e7af271f52715520be223768d9538f2812a795183b4fe1ebc9ebef5343b732191c9c6fea4ee6048c0c73d87f9eb45c0b051b1bd832c71e64c230e40c1e71dba7ebebc7136facdcc5e248efe26deaceb195607727cc0704f1fe715afaf6ad2d8d9158646373748523ddd3041c939e839e3dcf4ae661965fb30f322caa29f9ddb712a464f4e873ce7b71cd2433d50c8f2ef98be1e2726328b9dca7dbf1ace9a56dc555b2849e7b1e3193fe35cd786f5c16f745e57dd00244cbce06ef41eb8effe15d44f6fe4b2e0ee4650c0ff004ac2a2b1bc2572be72d93cfe3473ebf98a76de7bd183d89c5646879f5d486585554125ba01dea7f0e6913af8a2ddaea078c244651bd719ec3fad6af85a38cde4f2b0563028f98ae76b6783fa7eb5d6378821d565911a31be25c23f71cf3f51c56b6b2072bbb098cbfad0fb23864964e11473f4a8c4cb9ac2f156a4eb682cadf9964196c7615090365ab1bbb79541872431e31dabaab199e12183b107af3d6bc7348d75f49bff0026627cb90e3e86bd334fbe49a207791c74ad24b4318e8cf53d39a3ba802b6d27a9a8352d19750b3b8b7886580df1fd476ae7746d4d9015cb6f2428cd7716b711aab0e32a02e4739f5acb691abd627965b4b1da6af6ef2aaa10fe4bb31e554e4018ec3247e15c7f89fc2364d7ed243105c9dc5401c1f4af50f14e850ea4f35cc3fbb970770eaae3be4579f5c4f7b0030de6645ff9e8fd7e80ff009eb5d0a57460d72b394b5f0dc1e6a195372a905579c7e550f8ad62b836f676ff0034c98040fe11838181f811ff00d7ab9aa6a7791031daa80dd438e4fe159566896acd3bb2b33310779cb139fd79a6989ea7436d712c3a3468e63668d37090007803a1c7391efefdaa9c570f2c13309364a58676e463383c1f5e3bf5e6ab5cdd42c4f50a48e7a91fe734c0a9f675219942963804e40e3fc68b88dfb49e5dfbe6c142dc8193ce7b7f9ffebd97becb4934b32c11060ccc1b0b83c73ef827fcf358736b515ad9b4f712ab4806428237124fa7a7d2b99bcd5ae3510198158f76edabc283823f1a6aec45fd435992f7598a4f28aec654841fe24ee7a71ffeba8114993e799b04e72c32cbea40ebf5ea3a552b74dc81db7328390ec0b2f427bfa9e3b726b4609824acd20de1c73903d81dbf9f5e7bfbd588b568ef024c39570dba55661b5bff00d7efd45775a25f1b9b392dde42c62019031ced18e467e98ae15d8308ced0c012a54af19c8e739f5079f4ad5f0c5d13ab2624558402a41271c9036e7271dbd2a26ae8b83b33b40a4fff005ea5f2f3ce3f4a628c11d454c1b0300815ca74dcf3ef125ec9a143fd9766d84c9df2f7989073f801d2aef8445edeb48442cc1d372b63afcc73935eb5ad7c3cb2f113b1090090746e83f97bd5bf0f7809f458de24ba8a58db868d4703f3addeb1b192d257670ad041691335d4e55c0c8518233e95c8dd69f737177249e66f77e41ff67b62badf1f68faf41298ed745b8963ce7cc8549079e3a66b12de0bcb5d39525b2b81295db9d873cd672d1686b0b4a5aec79ff882c9ada52af8e07045745a0dd5ddb69b6e6e54aef19463dc5516d3350d67578a036d2987ccd8cc509c63935dcdfe9715ce9d1c289b5a15c27e155cda24c99c55f42de93a8179a10adc8392474af41d26f8481d037ce476af1db2796deec46c0a107041aedf4fd4a48ade4954aee03001349a213e86a78a7c6569a28fb3244d73704658230017a704f6eb5e5babf8e1ae65d874d41ce062504827a03c53f5fd4c5d5cca88b2aca14b0465209e392483cf5f619e3d6b87bddd6bb998cd2798a4348576bab10485c1ebce0938cfa62b582d0ce76b897fac5ccf70cc91c6318390a7f0ef544df5c9002205fd71fe715a36d0a5d49e4fd982cec81d14b152c3d467a7e03bfe34a34f5b8b7998b15583ef4bb582b75eb81c0f4e0f6e95a5919dca2b7b7cf190bb530304ede71c5244f752ba7993393b481bcf001f4f7c935a8f690a47b67982e570c245c15f4fbb838fd3f4a8c40d02234518ddb76ee46018bf19183cb751c63b9028b05ca7043e6169243b5d579dc9c723d7a0e3fce6af3da0842e494452779d84ecce08c1eb9edfe38ab2e0ca8b11f351d026e8a4db1f99cff08cf7183c9c7d33553ed020dd1a8790618a194ae073cf4e324f6fc39eb40096e32f13196675660b9c95d8dd8e78ff001ab0f016254111ed5c641505bea72723afe9c75c53f30a04760f91b483236771cf2704e307d307fc2521d582101234dca9c019e833d4f3f87a1a06595d8aacbb8aee07f8b9383fe208fcbeb56ec2454ba491571e5942e8c70cb8f63f53595b19c34a02ae3ac6831919cf1ebd3afd3deaf69b24c6e636761e66e21189ea3d38e071498d1e9705c09cbe17001e0e2ad82b8ebfad665b380990796c1e956bce23a138f615c8bcce98ec7fffd9	fr_FR	1	*	\N
PASSWORD1	password1@telino.com	\N	PASSWORD1	\N	\N	fr_FR	3	*	\N
PASSWORD2	password2@telino.com	\N	PASSWORD2	\N	\N	fr_FR	4	*	\N
\.


--
-- Name: conversions_conversion_id_seq; Type: SEQUENCE SET; Schema: public; Owner: cdms
--

SELECT pg_catalog.setval('public.conversions_conversion_id_seq', 1, false);


--
-- Name: destructioncriterias_destructioncriteriaid_seq; Type: SEQUENCE SET; Schema: public; Owner: cdms
--

SELECT pg_catalog.setval('public.destructioncriterias_destructioncriteriaid_seq', 3, true);


--
-- Name: doctypes_doctypeid_seq; Type: SEQUENCE SET; Schema: public; Owner: cdms
--

SELECT pg_catalog.setval('public.doctypes_doctypeid_seq', 7, true);


--
-- Name: hibernate_sequence; Type: SEQUENCE SET; Schema: public; Owner: cdms
--

SELECT pg_catalog.setval('public.hibernate_sequence', 1, true);


--
-- Name: log_mo_logid_seq; Type: SEQUENCE SET; Schema: public; Owner: cdms
--

SELECT pg_catalog.setval('public.log_mo_logid_seq', 1, true);


--
-- Name: mail_idmail_seq; Type: SEQUENCE SET; Schema: public; Owner: cdms
--

SELECT pg_catalog.setval('public.mail_idmail_seq', 2, true);


--
-- Name: mime_type_mime_type_id_seq; Type: SEQUENCE SET; Schema: public; Owner: cdms
--

SELECT pg_catalog.setval('public.mime_type_mime_type_id_seq', 21, true);


--
-- Name: param_paramid_seq; Type: SEQUENCE SET; Schema: public; Owner: cdms
--

SELECT pg_catalog.setval('public.param_paramid_seq', 2, false);


--
-- Name: profils_par_id_seq; Type: SEQUENCE SET; Schema: public; Owner: cdms
--

SELECT pg_catalog.setval('public.profils_par_id_seq', 2, true);


--
-- Name: sortfinal_sortfinalid_seq; Type: SEQUENCE SET; Schema: public; Owner: cdms
--

SELECT pg_catalog.setval('public.sortfinal_sortfinalid_seq', 4, true);


--
-- Name: types_manu_typemanuid_seq; Type: SEQUENCE SET; Schema: public; Owner: cdms
--

SELECT pg_catalog.setval('public.types_manu_typemanuid_seq', 1, false);


--
-- Name: util_iduser_seq; Type: SEQUENCE SET; Schema: public; Owner: cdms
--

SELECT pg_catalog.setval('public.util_iduser_seq', 4, true);


--
-- Name: modu cle_modu; Type: CONSTRAINT; Schema: public; Owner: cdms
--

ALTER TABLE ONLY public.modu
    ADD CONSTRAINT cle_modu PRIMARY KEY (moducod);


--
-- Name: domn domncle; Type: CONSTRAINT; Schema: public; Owner: cdms
--

ALTER TABLE ONLY public.domn
    ADD CONSTRAINT domncle PRIMARY KEY (domnnom);


--
-- Name: droo droocle; Type: CONSTRAINT; Schema: public; Owner: cdms
--

ALTER TABLE ONLY public.droo
    ADD CONSTRAINT droocle PRIMARY KEY (profid, objecod);


--
-- Name: exp_comments exp_comments_pkey; Type: CONSTRAINT; Schema: public; Owner: cdms
--

ALTER TABLE ONLY public.exp_comments
    ADD CONSTRAINT exp_comments_pkey PRIMARY KEY (comid);


--
-- Name: exp_task exp_task_pkey; Type: CONSTRAINT; Schema: public; Owner: cdms
--

ALTER TABLE ONLY public.exp_task
    ADD CONSTRAINT exp_task_pkey PRIMARY KEY (taskid);


--
-- Name: exp_task_type exptasktypepkey; Type: CONSTRAINT; Schema: public; Owner: cdms
--

ALTER TABLE ONLY public.exp_task_type
    ADD CONSTRAINT exptasktypepkey PRIMARY KEY (typeid);


--
-- Name: empreintes fkey1_empreintes; Type: CONSTRAINT; Schema: public; Owner: cdms
--

ALTER TABLE ONLY public.empreintes
    ADD CONSTRAINT fkey1_empreintes UNIQUE (empreinte_unique);


--
-- Name: mime_doctypes mime_doctypescle; Type: CONSTRAINT; Schema: public; Owner: cdms
--

ALTER TABLE ONLY public.mime_doctypes
    ADD CONSTRAINT mime_doctypescle UNIQUE (mime_type_id, doctypeid);


--
-- Name: applications pkey_applications; Type: CONSTRAINT; Schema: public; Owner: cdms
--

ALTER TABLE ONLY public.applications
    ADD CONSTRAINT pkey_applications PRIMARY KEY (applicationcode);


--
-- Name: authmails pkey_authmails; Type: CONSTRAINT; Schema: public; Owner: cdms
--

ALTER TABLE ONLY public.authmails
    ADD CONSTRAINT pkey_authmails PRIMARY KEY (iduser, usermail);


--
-- Name: bgservices pkey_bgservices; Type: CONSTRAINT; Schema: public; Owner: cdms
--

ALTER TABLE ONLY public.bgservices
    ADD CONSTRAINT pkey_bgservices PRIMARY KEY (bgs_cod);


--
-- Name: chiffrement pkey_chiffrement; Type: CONSTRAINT; Schema: public; Owner: cdms
--

ALTER TABLE ONLY public.chiffrement
    ADD CONSTRAINT pkey_chiffrement PRIMARY KEY (cryptid);


--
-- Name: communicationlist pkey_communicationlist; Type: CONSTRAINT; Schema: public; Owner: cdms
--

ALTER TABLE ONLY public.communicationlist
    ADD CONSTRAINT pkey_communicationlist PRIMARY KEY (communicationid, docid);


--
-- Name: communications pkey_communications; Type: CONSTRAINT; Schema: public; Owner: cdms
--

ALTER TABLE ONLY public.communications
    ADD CONSTRAINT pkey_communications PRIMARY KEY (communicationid);


--
-- Name: conversions pkey_conversions; Type: CONSTRAINT; Schema: public; Owner: cdms
--

ALTER TABLE ONLY public.conversions
    ADD CONSTRAINT pkey_conversions PRIMARY KEY (conversion_id);


--
-- Name: depots pkey_depots; Type: CONSTRAINT; Schema: public; Owner: cdms
--

ALTER TABLE ONLY public.depots
    ADD CONSTRAINT pkey_depots PRIMARY KEY (iddepot);


--
-- Name: destinataires pkey_destinataires; Type: CONSTRAINT; Schema: public; Owner: cdms
--

ALTER TABLE ONLY public.destinataires
    ADD CONSTRAINT pkey_destinataires PRIMARY KEY (destinataire);


--
-- Name: destructioncriterias pkey_destructioncriterias; Type: CONSTRAINT; Schema: public; Owner: cdms
--

ALTER TABLE ONLY public.destructioncriterias
    ADD CONSTRAINT pkey_destructioncriterias PRIMARY KEY (destructioncriteriaid);


--
-- Name: doctypes pkey_doctypes; Type: CONSTRAINT; Schema: public; Owner: cdms
--

ALTER TABLE ONLY public.doctypes
    ADD CONSTRAINT pkey_doctypes PRIMARY KEY (doctypeid);


--
-- Name: document pkey_document; Type: CONSTRAINT; Schema: public; Owner: cdms
--

ALTER TABLE ONLY public.document
    ADD CONSTRAINT pkey_document PRIMARY KEY (docid);


--
-- Name: drafts pkey_drafts; Type: CONSTRAINT; Schema: public; Owner: cdms
--

ALTER TABLE ONLY public.drafts
    ADD CONSTRAINT pkey_drafts PRIMARY KEY (docid);


--
-- Name: empreintes pkey_empreintes; Type: CONSTRAINT; Schema: public; Owner: cdms
--

ALTER TABLE ONLY public.empreintes
    ADD CONSTRAINT pkey_empreintes PRIMARY KEY (docid);


--
-- Name: keyword pkey_keyword; Type: CONSTRAINT; Schema: public; Owner: cdms
--

ALTER TABLE ONLY public.keyword
    ADD CONSTRAINT pkey_keyword PRIMARY KEY (keyword, doctype, categorie);


--
-- Name: log_archive pkey_log_archive; Type: CONSTRAINT; Schema: public; Owner: cdms
--

ALTER TABLE ONLY public.log_archive
    ADD CONSTRAINT pkey_log_archive PRIMARY KEY (logid);


--
-- Name: log_event pkey_log_event; Type: CONSTRAINT; Schema: public; Owner: cdms
--

ALTER TABLE ONLY public.log_event
    ADD CONSTRAINT pkey_log_event PRIMARY KEY (logid);


--
-- Name: log_mo pkey_log_mo; Type: CONSTRAINT; Schema: public; Owner: cdms
--

ALTER TABLE ONLY public.log_mo
    ADD CONSTRAINT pkey_log_mo PRIMARY KEY (logid);


--
-- Name: login pkey_login; Type: CONSTRAINT; Schema: public; Owner: cdms
--

ALTER TABLE ONLY public.login
    ADD CONSTRAINT pkey_login PRIMARY KEY (userid);


--
-- Name: mail pkey_mail; Type: CONSTRAINT; Schema: public; Owner: cdms
--

ALTER TABLE ONLY public.mail
    ADD CONSTRAINT pkey_mail PRIMARY KEY (idmail);


--
-- Name: mime_doctypes pkey_mime_doctypes; Type: CONSTRAINT; Schema: public; Owner: cdms
--

ALTER TABLE ONLY public.mime_doctypes
    ADD CONSTRAINT pkey_mime_doctypes PRIMARY KEY (mime_type_id, doctypeid);


--
-- Name: mime_type pkey_mime_type; Type: CONSTRAINT; Schema: public; Owner: cdms
--

ALTER TABLE ONLY public.mime_type
    ADD CONSTRAINT pkey_mime_type PRIMARY KEY (mime_type_id);


--
-- Name: par_rights pkey_par_rights; Type: CONSTRAINT; Schema: public; Owner: cdms
--

ALTER TABLE ONLY public.par_rights
    ADD CONSTRAINT pkey_par_rights PRIMARY KEY (par_id, userid);


--
-- Name: param_automate pkey_param_automate; Type: CONSTRAINT; Schema: public; Owner: cdms
--

ALTER TABLE ONLY public.param_automate
    ADD CONSTRAINT pkey_param_automate PRIMARY KEY (paramid);


--
-- Name: paramstorage pkey_paramstorage; Type: CONSTRAINT; Schema: public; Owner: cdms
--

ALTER TABLE ONLY public.paramstorage
    ADD CONSTRAINT pkey_paramstorage PRIMARY KEY (paramid);


--
-- Name: paramuser pkey_paramuser; Type: CONSTRAINT; Schema: public; Owner: cdms
--

ALTER TABLE ONLY public.paramuser
    ADD CONSTRAINT pkey_paramuser PRIMARY KEY (iduser);


--
-- Name: profils pkey_profils; Type: CONSTRAINT; Schema: public; Owner: cdms
--

ALTER TABLE ONLY public.profils
    ADD CONSTRAINT pkey_profils PRIMARY KEY (par_id);


--
-- Name: restitutionlist pkey_restitutionlist; Type: CONSTRAINT; Schema: public; Owner: cdms
--

ALTER TABLE ONLY public.restitutionlist
    ADD CONSTRAINT pkey_restitutionlist PRIMARY KEY (restitutionid, docid);


--
-- Name: restitutions pkey_restitutions; Type: CONSTRAINT; Schema: public; Owner: cdms
--

ALTER TABLE ONLY public.restitutions
    ADD CONSTRAINT pkey_restitutions PRIMARY KEY (restitutionid);


--
-- Name: secret_key pkey_secretkey; Type: CONSTRAINT; Schema: public; Owner: cdms
--

ALTER TABLE ONLY public.secret_key
    ADD CONSTRAINT pkey_secretkey PRIMARY KEY (keyid);


--
-- Name: services pkey_services; Type: CONSTRAINT; Schema: public; Owner: cdms
--

ALTER TABLE ONLY public.services
    ADD CONSTRAINT pkey_services PRIMARY KEY (service);


--
-- Name: sortfinal pkey_sortfinal; Type: CONSTRAINT; Schema: public; Owner: cdms
--

ALTER TABLE ONLY public.sortfinal
    ADD CONSTRAINT pkey_sortfinal PRIMARY KEY (sortfinalid);


--
-- Name: types pkey_types; Type: CONSTRAINT; Schema: public; Owner: cdms
--

ALTER TABLE ONLY public.types
    ADD CONSTRAINT pkey_types PRIMARY KEY (doctype_archivage);


--
-- Name: types_manu pkey_types_manu; Type: CONSTRAINT; Schema: public; Owner: cdms
--

ALTER TABLE ONLY public.types_manu
    ADD CONSTRAINT pkey_types_manu PRIMARY KEY (typemanuid);


--
-- Name: util pkey_util; Type: CONSTRAINT; Schema: public; Owner: cdms
--

ALTER TABLE ONLY public.util
    ADD CONSTRAINT pkey_util PRIMARY KEY (iduser);


--
-- Name: prof profcle; Type: CONSTRAINT; Schema: public; Owner: cdms
--

ALTER TABLE ONLY public.prof
    ADD CONSTRAINT profcle PRIMARY KEY (profid);


--
-- Name: doctypes unique_doctype; Type: CONSTRAINT; Schema: public; Owner: cdms
--

ALTER TABLE ONLY public.doctypes
    ADD CONSTRAINT unique_doctype UNIQUE (doctype_archivage, categorie);


--
-- Name: fki_fk0_document; Type: INDEX; Schema: public; Owner: cdms
--

CREATE INDEX fki_fk0_document ON public.document USING btree (archiver_id);


--
-- Name: fki_fkey0_domn; Type: INDEX; Schema: public; Owner: cdms
--

CREATE INDEX fki_fkey0_domn ON public.domn USING btree (orgaparent);


--
-- Name: fki_fkey2_par_rights; Type: INDEX; Schema: public; Owner: cdms
--

CREATE INDEX fki_fkey2_par_rights ON public.par_rights USING btree (userid);


--
-- Name: ukey_mail_lower_mailid; Type: INDEX; Schema: public; Owner: cdms
--

CREATE UNIQUE INDEX ukey_mail_lower_mailid ON public.mail USING btree (lower(mailid));


--
-- Name: ukey_mail_mailid; Type: INDEX; Schema: public; Owner: cdms
--

CREATE UNIQUE INDEX ukey_mail_mailid ON public.mail USING btree (mailid);


--
-- Name: unique_mail; Type: INDEX; Schema: public; Owner: cdms
--

CREATE UNIQUE INDEX unique_mail ON public.mail USING btree (lower(mailid));


--
-- Name: droo droo_fkey1; Type: FK CONSTRAINT; Schema: public; Owner: cdms
--

ALTER TABLE ONLY public.droo
    ADD CONSTRAINT droo_fkey1 FOREIGN KEY (profid) REFERENCES public.prof(profid);


--
-- Name: chiffrement fk0_chiffrement; Type: FK CONSTRAINT; Schema: public; Owner: cdms
--

ALTER TABLE ONLY public.chiffrement
    ADD CONSTRAINT fk0_chiffrement FOREIGN KEY (idcrypkey) REFERENCES public.secret_key(keyid);


--
-- Name: document fk0_document; Type: FK CONSTRAINT; Schema: public; Owner: cdms
--

ALTER TABLE ONLY public.document
    ADD CONSTRAINT fk0_document FOREIGN KEY (archiver_id) REFERENCES public.login(userid);


--
-- Name: exp_comments fkalwli6nf6kay1q9sw6iswli5v; Type: FK CONSTRAINT; Schema: public; Owner: cdms
--

ALTER TABLE ONLY public.exp_comments
    ADD CONSTRAINT fkalwli6nf6kay1q9sw6iswli5v FOREIGN KEY (task_id) REFERENCES public.exp_task(taskid);


--
-- Name: communicationlist fkey0_communicationlist; Type: FK CONSTRAINT; Schema: public; Owner: cdms
--

ALTER TABLE ONLY public.communicationlist
    ADD CONSTRAINT fkey0_communicationlist FOREIGN KEY (communicationid) REFERENCES public.communications(communicationid) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: doctypes fkey0_doctypes; Type: FK CONSTRAINT; Schema: public; Owner: cdms
--

ALTER TABLE ONLY public.doctypes
    ADD CONSTRAINT fkey0_doctypes FOREIGN KEY (doctype_archivage) REFERENCES public.types(doctype_archivage);

--
-- Name: doctypes fkey1_doctypes; Type: FK CONSTRAINT; Schema: public; Owner: cdms
--

ALTER TABLE ONLY public.doctypes
    ADD CONSTRAINT fkey1_doctypes FOREIGN KEY (par_id) REFERENCES public.profils(par_id);

--
-- Name: domn fkey0_domn; Type: FK CONSTRAINT; Schema: public; Owner: cdms
--

ALTER TABLE ONLY public.domn
    ADD CONSTRAINT fkey0_domn FOREIGN KEY (orgaparent) REFERENCES public.domn(domnnom);


--
-- Name: empreintes fkey0_empreintes; Type: FK CONSTRAINT; Schema: public; Owner: cdms
--

ALTER TABLE ONLY public.empreintes
    ADD CONSTRAINT fkey0_empreintes FOREIGN KEY (docid) REFERENCES public.document(docid) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: keyword fkey0_keyword; Type: FK CONSTRAINT; Schema: public; Owner: cdms
--

ALTER TABLE ONLY public.keyword
    ADD CONSTRAINT fkey0_keyword FOREIGN KEY (doctype, categorie) REFERENCES public.doctypes(doctype_archivage, categorie) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: mime_doctypes fkey0_mime_doctypes; Type: FK CONSTRAINT; Schema: public; Owner: cdms
--

ALTER TABLE ONLY public.mime_doctypes
    ADD CONSTRAINT fkey0_mime_doctypes FOREIGN KEY (mime_type_id) REFERENCES public.mime_type(mime_type_id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: paramuser fkey0_paramuser; Type: FK CONSTRAINT; Schema: public; Owner: cdms
--

ALTER TABLE ONLY public.paramuser
    ADD CONSTRAINT fkey0_paramuser FOREIGN KEY (iduser) REFERENCES public.util(iduser) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: profils fkey0_profils; Type: FK CONSTRAINT; Schema: public; Owner: cdms
--

ALTER TABLE ONLY public.profils
    ADD CONSTRAINT fkey0_profils FOREIGN KEY (destructioncriteriaid) REFERENCES public.destructioncriterias(destructioncriteriaid);


--
-- Name: restitutionlist fkey0_restitutionlist; Type: FK CONSTRAINT; Schema: public; Owner: cdms
--

ALTER TABLE ONLY public.restitutionlist
    ADD CONSTRAINT fkey0_restitutionlist FOREIGN KEY (restitutionid) REFERENCES public.restitutions(restitutionid) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: util fkey0_util; Type: FK CONSTRAINT; Schema: public; Owner: cdms
--

ALTER TABLE ONLY public.util
    ADD CONSTRAINT fkey0_util FOREIGN KEY (domnnom) REFERENCES public.domn(domnnom) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: document fkey1_document; Type: FK CONSTRAINT; Schema: public; Owner: cdms
--

ALTER TABLE ONLY public.document
    ADD CONSTRAINT fkey1_document FOREIGN KEY (par_id) REFERENCES public.profils(par_id);


--
-- Name: mime_doctypes fkey1_mime_doctypes; Type: FK CONSTRAINT; Schema: public; Owner: cdms
--

ALTER TABLE ONLY public.mime_doctypes
    ADD CONSTRAINT fkey1_mime_doctypes FOREIGN KEY (doctypeid) REFERENCES public.doctypes(doctypeid) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: par_rights fkey1_par_rights; Type: FK CONSTRAINT; Schema: public; Owner: cdms
--

ALTER TABLE ONLY public.par_rights
    ADD CONSTRAINT fkey1_par_rights FOREIGN KEY (par_id) REFERENCES public.profils(par_id);


--
-- Name: param fkey1_param; Type: FK CONSTRAINT; Schema: public; Owner: cdms
--

ALTER TABLE ONLY public.param
    ADD CONSTRAINT fkey1_param FOREIGN KEY (storageid) REFERENCES public.paramstorage(paramid);


--
-- Name: profils fkey1_profils; Type: FK CONSTRAINT; Schema: public; Owner: cdms
--

ALTER TABLE ONLY public.profils
    ADD CONSTRAINT fkey1_profils FOREIGN KEY (sortfinalid) REFERENCES public.sortfinal(sortfinalid);


--
-- Name: document fkey2_document; Type: FK CONSTRAINT; Schema: public; Owner: cdms
--

ALTER TABLE ONLY public.document
    ADD CONSTRAINT fkey2_document FOREIGN KEY (domnnom) REFERENCES public.domn(domnnom);


--
-- Name: par_rights fkey2_par_rights; Type: FK CONSTRAINT; Schema: public; Owner: cdms
--

ALTER TABLE ONLY public.par_rights
    ADD CONSTRAINT fkey2_par_rights FOREIGN KEY (userid) REFERENCES public.login(userid);


--
-- Name: param fkey2_param; Type: FK CONSTRAINT; Schema: public; Owner: cdms
--

ALTER TABLE ONLY public.param
    ADD CONSTRAINT fkey2_param FOREIGN KEY (storagemirrorid) REFERENCES public.paramstorage(paramid);


--
-- Name: util fkey2_util; Type: FK CONSTRAINT; Schema: public; Owner: cdms
--

ALTER TABLE ONLY public.util
    ADD CONSTRAINT fkey2_util FOREIGN KEY (mailid) REFERENCES public.mail(mailid) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: document fkey3_document; Type: FK CONSTRAINT; Schema: public; Owner: cdms
--

ALTER TABLE ONLY public.document
    ADD CONSTRAINT fkey3_document FOREIGN KEY (cryptage_algoid) REFERENCES public.chiffrement(cryptid);


--
-- Name: util fkey3_util; Type: FK CONSTRAINT; Schema: public; Owner: cdms
--

ALTER TABLE ONLY public.util
    ADD CONSTRAINT fkey3_util FOREIGN KEY (userid) REFERENCES public.login(userid) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: util fkey4_util; Type: FK CONSTRAINT; Schema: public; Owner: cdms
--

ALTER TABLE ONLY public.util
    ADD CONSTRAINT fkey4_util FOREIGN KEY (service) REFERENCES public.services(service) ON UPDATE CASCADE;


--
-- Name: param param; Type: FK CONSTRAINT; Schema: public; Owner: cdms
--

ALTER TABLE ONLY public.param
    ADD CONSTRAINT param FOREIGN KEY (storageid) REFERENCES public.paramstorage(paramid);


--
-- Name: util util_fkey1; Type: FK CONSTRAINT; Schema: public; Owner: cdms
--

ALTER TABLE ONLY public.util
    ADD CONSTRAINT util_fkey1 FOREIGN KEY (profid) REFERENCES public.prof(profid);


--
-- PostgreSQL database dump complete
--

