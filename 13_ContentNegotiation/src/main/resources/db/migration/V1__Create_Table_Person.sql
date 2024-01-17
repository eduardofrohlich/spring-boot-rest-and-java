CREATE TABLE public.person (
    id bigint NOT NULL,
    first_name character varying(80) NOT NULL,
    last_name character varying(80) NOT NULL,
    address character varying(80) NOT NULL,
    gender character varying(6) NOT NULL
);

ALTER TABLE ONLY public.person
    ADD CONSTRAINT person_pkey PRIMARY KEY (id);


