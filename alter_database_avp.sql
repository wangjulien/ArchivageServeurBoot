ALTER TABLE public.doctypes
    ADD COLUMN par_id integer;
ALTER TABLE public.doctypes
    ADD CONSTRAINT fk_profile FOREIGN KEY (par_id)
    REFERENCES public.profils (par_id) MATCH SIMPLE
    ON UPDATE NO ACTION
    ON DELETE NO ACTION;
	
	
UPDATE doctypes SET par_id = 1 WHERE doctypeid = 4;


ALTER TABLE public.document
    ADD COLUMN "timestamp" timestamp with time zone;
	
ALTER TABLE public.log_archive
    ADD COLUMN "timestamp" timestamp with time zone;
	
ALTER TABLE public.log_event
    ADD COLUMN "timestamp" timestamp with time zone;
	

ALTER TABLE public.mime_doctypes DROP COLUMN mime_doctype_id;

ALTER TABLE public.log_event
    ALTER COLUMN statexp TYPE character varying (1) COLLATE pg_catalog."default";