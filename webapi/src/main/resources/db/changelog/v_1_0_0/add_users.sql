CREATE TABLE public.users
(
    username varchar(25) NOT NULL,
    password varchar(100) NOT NULL,
    is_expired boolean NULL DEFAULT false,
    is_locked boolean NULL DEFAULT false,
    role varchar(50) NULL DEFAULT 'user',
    version integer NOT NULL DEFAULT 0,

    CONSTRAINT "PK_users" PRIMARY KEY (username)
);