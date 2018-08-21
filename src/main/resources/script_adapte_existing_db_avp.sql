CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

/* 									  */
/* Creation of UUID column for tables */
/* 									  */
DROP VIEW getkeywords;

DROP FUNCTION getar_profiles(text);
DROP FUNCTION getformatted_write_profiles(text); 
DROP FUNCTION getformatted_write_profiles(text, integer);

/*
-- Document
*/
ALTER TABLE document ADD COLUMN timestamp timestamp with time zone;
UPDATE document set timestamp = archive_date;

ALTER TABLE document ADD COLUMN uuid_id UUID DEFAULT uuid_generate_v4();

-- docid in empreintes
ALTER TABLE empreintes ADD COLUMN uuid_docid UUID DEFAULT null;
UPDATE empreintes d set uuid_docid = p.uuid_id FROM document p WHERE d.docid = p.docid;
ALTER TABLE empreintes DROP COLUMN docid;
ALTER TABLE empreintes RENAME COLUMN uuid_docid TO docid;
ALTER TABLE empreintes ADD PRIMARY KEY (docid);

-- archiveid in drafts
ALTER TABLE drafts ADD COLUMN uuid_archiveid UUID DEFAULT null;
UPDATE drafts d set uuid_archiveid = p.uuid_id FROM document p WHERE d.archiveid = p.docid;
ALTER TABLE drafts DROP COLUMN archiveid;
ALTER TABLE drafts RENAME COLUMN uuid_archiveid TO archiveid;

-- docid in log_archive
ALTER TABLE log_archive ADD COLUMN uuid_docid UUID DEFAULT null;
UPDATE log_archive d set uuid_docid = p.uuid_id FROM document p WHERE d.docid = p.docid;
ALTER TABLE log_archive DROP COLUMN docid;
ALTER TABLE log_archive RENAME COLUMN uuid_docid TO docid;

-- attestation in log_archive
ALTER TABLE log_archive ADD COLUMN uuid_attestation UUID DEFAULT null;
UPDATE log_archive d set uuid_attestation = p.uuid_id FROM document p WHERE d.attestation = p.docid;
ALTER TABLE log_archive DROP COLUMN attestation;
ALTER TABLE log_archive RENAME COLUMN uuid_attestation TO attestation;

-- archiveid in log_event
ALTER TABLE log_event ADD COLUMN uuid_archiveid UUID DEFAULT null;
UPDATE log_event d set uuid_archiveid = p.uuid_id FROM document p WHERE d.archiveid = p.docid;
ALTER TABLE log_event DROP COLUMN archiveid;
ALTER TABLE log_event RENAME COLUMN uuid_archiveid TO archiveid;

-- archiveid in log_event
ALTER TABLE log_event ADD COLUMN uuid_journalxmlid UUID DEFAULT null;
UPDATE log_event d set uuid_journalxmlid = p.uuid_id FROM document p WHERE d.journalxmlid = p.docid;
ALTER TABLE log_event DROP COLUMN journalxmlid;
ALTER TABLE log_event RENAME COLUMN uuid_journalxmlid TO journalxmlid;

-- docid in exp_task
ALTER TABLE exp_task ADD COLUMN uuid_docid UUID DEFAULT null;
UPDATE exp_task d set uuid_docid = p.uuid_id FROM document p WHERE d.docid = p.docid;
ALTER TABLE exp_task DROP COLUMN docid;
ALTER TABLE exp_task RENAME COLUMN uuid_docid TO docid;

-- docid in communicationlist
ALTER TABLE communicationlist ADD COLUMN uuid_docid UUID DEFAULT null;
UPDATE communicationlist d set uuid_docid = p.uuid_id FROM document p WHERE d.docid = p.docid;
ALTER TABLE communicationlist DROP COLUMN docid;
ALTER TABLE communicationlist RENAME COLUMN uuid_docid TO docid;

-- docid in restitutionlist
ALTER TABLE restitutionlist ADD COLUMN uuid_docid UUID DEFAULT null;
UPDATE restitutionlist d set uuid_docid = p.uuid_id FROM document p WHERE d.docid = p.docid;
ALTER TABLE restitutionlist DROP COLUMN docid;
ALTER TABLE restitutionlist RENAME COLUMN uuid_docid TO docid;

-- Change PK
ALTER TABLE document DROP COLUMN docid;
ALTER TABLE document RENAME COLUMN uuid_id TO docid;
ALTER TABLE document ADD PRIMARY KEY (docid);

ALTER TABLE empreintes ADD CONSTRAINT fk_docid FOREIGN KEY (docid) REFERENCES document(docid) ON UPDATE CASCADE ON DELETE CASCADE;


/*
-- Drafts
*/
ALTER TABLE drafts ADD COLUMN uuid_id UUID DEFAULT uuid_generate_v4();

-- Change PK
ALTER TABLE drafts DROP COLUMN docid;
ALTER TABLE drafts RENAME COLUMN uuid_id TO docid;
ALTER TABLE drafts ADD PRIMARY KEY (docid);


/*
-- Depots
*/
ALTER TABLE depots ADD COLUMN uuid_id UUID DEFAULT uuid_generate_v4();

-- iddepot in Document
ALTER TABLE document ADD COLUMN uuid_iddepot UUID DEFAULT null;
UPDATE document d set uuid_iddepot = p.uuid_id FROM depots p WHERE d.iddepot = p.iddepot;
ALTER TABLE document DROP COLUMN iddepot;
ALTER TABLE document RENAME COLUMN uuid_iddepot TO iddepot;

-- Change PK
ALTER TABLE depots DROP COLUMN iddepot;
ALTER TABLE depots RENAME COLUMN uuid_id TO iddepot;
ALTER TABLE depots ADD PRIMARY KEY (iddepot);


/*
-- LogArchive
*/
ALTER TABLE log_archive ADD COLUMN timestamp timestamp with time zone;
UPDATE log_archive set timestamp = horodatage;

ALTER TABLE log_archive ADD COLUMN uuid_id UUID DEFAULT uuid_generate_v4();

-- journalid in log_event
ALTER TABLE log_event ADD COLUMN uuid_journalid UUID DEFAULT null;
UPDATE log_event d set uuid_journalid = p.uuid_id FROM log_archive p WHERE d.journalid = p.logid;
ALTER TABLE log_event DROP COLUMN journalid;
ALTER TABLE log_event RENAME COLUMN uuid_journalid TO journalid;

-- Change PK
ALTER TABLE log_archive DROP COLUMN logid;
ALTER TABLE log_archive RENAME COLUMN uuid_id TO logid;
ALTER TABLE log_archive ADD PRIMARY KEY (logid);


/*
-- LogEvent
*/
ALTER TABLE log_event ADD COLUMN timestamp timestamp with time zone;
UPDATE log_event set timestamp = horodatage;

ALTER TABLE log_event ADD COLUMN uuid_id UUID DEFAULT uuid_generate_v4();

-- logid in exp_task
ALTER TABLE exp_task ADD COLUMN uuid_logid UUID DEFAULT null;
UPDATE exp_task d set uuid_logid = p.uuid_id FROM log_event p WHERE d.logid = p.logid;
ALTER TABLE exp_task DROP COLUMN logid;
ALTER TABLE exp_task RENAME COLUMN uuid_logid TO logid;

-- Change PK
ALTER TABLE log_event DROP COLUMN logid;
ALTER TABLE log_event RENAME COLUMN uuid_id TO logid;
ALTER TABLE log_event ADD PRIMARY KEY (logid);

ALTER TABLE log_event ALTER COLUMN statexp TYPE character varying (1) COLLATE pg_catalog."default";
ALTER TABLE exp_task ADD CONSTRAINT fk_logid FOREIGN KEY (logid) REFERENCES log_event (logid) MATCH SIMPLE ON UPDATE CASCADE ON DELETE NO ACTION;


/*
-- Communications
*/
ALTER TABLE communications ADD COLUMN uuid_id UUID DEFAULT uuid_generate_v4();

-- communicationid in communicationlist
ALTER TABLE communicationlist ADD COLUMN uuid_communicationid UUID DEFAULT null;
UPDATE communicationlist d set uuid_communicationid = p.uuid_id FROM communications p WHERE d.communicationid = p.communicationid;
ALTER TABLE communicationlist DROP COLUMN communicationid;
ALTER TABLE communicationlist RENAME COLUMN uuid_communicationid TO communicationid;
ALTER TABLE communicationlist ADD PRIMARY KEY (communicationid, docid);

-- Change PK
ALTER TABLE communications DROP COLUMN communicationid;
ALTER TABLE communications RENAME COLUMN uuid_id TO communicationid;
ALTER TABLE communications ADD PRIMARY KEY (communicationid);

ALTER TABLE communicationlist ADD CONSTRAINT fk_communicationid FOREIGN KEY (communicationid) REFERENCES communications(communicationid) ON UPDATE CASCADE ON DELETE CASCADE;


/*
-- Restitutions
*/
ALTER TABLE restitutions ADD COLUMN uuid_id UUID DEFAULT uuid_generate_v4();

-- restitutionid in restitutionlist
ALTER TABLE restitutionlist ADD COLUMN uuid_restitutionid UUID DEFAULT null;
UPDATE restitutionlist d set uuid_restitutionid = p.uuid_id FROM restitutions p WHERE d.restitutionid = p.restitutionid;
ALTER TABLE restitutionlist DROP COLUMN restitutionid;
ALTER TABLE restitutionlist RENAME COLUMN uuid_restitutionid TO restitutionid;
ALTER TABLE restitutionlist ADD PRIMARY KEY (restitutionid, docid);

-- Change PK
ALTER TABLE restitutions DROP COLUMN restitutionid;
ALTER TABLE restitutions RENAME COLUMN uuid_id TO restitutionid;
ALTER TABLE restitutions ADD PRIMARY KEY (restitutionid);

ALTER TABLE restitutionlist ADD CONSTRAINT fk_restitutionid FOREIGN KEY (restitutionid) REFERENCES restitutions(restitutionid) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Chiffrement
--
ALTER TABLE chiffrement ADD COLUMN uuid_id UUID DEFAULT uuid_generate_v4();

-- cryptage_algoid in document
ALTER TABLE document ADD COLUMN uuid_cryptage_algoid UUID DEFAULT null;
UPDATE document d set uuid_cryptage_algoid = p.uuid_id FROM chiffrement p WHERE d.cryptage_algoid = p.cryptid;
ALTER TABLE document DROP COLUMN cryptage_algoid;
ALTER TABLE document RENAME COLUMN uuid_cryptage_algoid TO cryptage_algoid;

-- cryptageid in param
ALTER TABLE param ADD COLUMN uuid_cryptageid UUID DEFAULT null;
UPDATE param d set uuid_cryptageid = p.uuid_id FROM chiffrement p WHERE d.cryptageid = p.cryptid;
ALTER TABLE param DROP COLUMN cryptageid;
ALTER TABLE param RENAME COLUMN uuid_cryptageid TO cryptageid;

-- Change PK
ALTER TABLE chiffrement DROP COLUMN cryptid;
ALTER TABLE chiffrement RENAME COLUMN uuid_id TO cryptid;
ALTER TABLE chiffrement ADD PRIMARY KEY (cryptid);


--
-- Secret_key
-- 
ALTER TABLE secret_key ADD COLUMN uuid_id UUID DEFAULT uuid_generate_v4();

-- idcrypkey in chiffrement
ALTER TABLE chiffrement ADD COLUMN uuid_idcrypkey UUID DEFAULT null;
UPDATE chiffrement d set uuid_idcrypkey = p.uuid_id FROM secret_key p WHERE d.idcrypkey = p.keyid;
ALTER TABLE chiffrement DROP COLUMN idcrypkey;
ALTER TABLE chiffrement RENAME COLUMN uuid_idcrypkey TO idcrypkey;

-- Change PK
ALTER TABLE secret_key DROP COLUMN keyid;
ALTER TABLE secret_key RENAME COLUMN uuid_id TO keyid;
ALTER TABLE secret_key ADD PRIMARY KEY (keyid);

ALTER TABLE chiffrement ADD CONSTRAINT fk_keyid FOREIGN KEY (idcrypkey) REFERENCES secret_key(keyid) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- ExpTask
-- 
ALTER TABLE exp_task ADD COLUMN uuid_id UUID DEFAULT uuid_generate_v4();

-- task_id in exp_comments
ALTER TABLE exp_comments ADD COLUMN uuid_task_id UUID DEFAULT null;
UPDATE exp_comments d set uuid_task_id = p.uuid_id FROM exp_task p WHERE d.task_id = p.taskid;
ALTER TABLE exp_comments DROP COLUMN task_id;
ALTER TABLE exp_comments RENAME COLUMN uuid_task_id TO task_id;

-- Change PK
ALTER TABLE exp_task DROP COLUMN taskid;
ALTER TABLE exp_task RENAME COLUMN uuid_id TO taskid;
ALTER TABLE exp_task ADD PRIMARY KEY (taskid);

ALTER TABLE exp_comments ADD CONSTRAINT fk_taskid FOREIGN KEY (task_id) REFERENCES exp_task(taskid) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- ExpComments
-- 
ALTER TABLE exp_comments ADD COLUMN uuid_id UUID DEFAULT uuid_generate_v4();

-- Change PK
ALTER TABLE exp_comments DROP COLUMN comid;
ALTER TABLE exp_comments RENAME COLUMN uuid_id TO comid;
ALTER TABLE exp_comments ADD PRIMARY KEY (comid);


/* 									  */
/* 			Alter tables 			  */
/* 									  */

-- doctypes
ALTER TABLE doctypes ADD COLUMN par_id integer;
ALTER TABLE doctypes ADD CONSTRAINT fk_profile FOREIGN KEY (par_id) REFERENCES profils (par_id) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION;
	
UPDATE doctypes d SET par_id = p.par_id from par_doctypes p WHERE d.doctypeid = p.doctypeid;

-- mime_doctypes
-- ALTER TABLE mime_doctypes DROP COLUMN mime_doctype_id;

-- default USER
INSERT INTO login (userid) VALUES ('system');

ALTER TABLE exp_task ALTER COLUMN tasktypeid TYPE integer ;




/* 									  */
/*  Recreate the Views and Functions  */
/* 									  */

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