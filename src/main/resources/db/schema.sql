CREATE TABLE IF NOT EXISTS users (
    id bigserial PRIMARY KEY,
    username varchar(50) NOT NULL UNIQUE,
    password varchar(60) NOT NULL);

CREATE TABLE IF NOT EXISTS conversations (
    id bigserial PRIMARY KEY,
    user_id bigint NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    title text DEFAULT 'New Chat' NOT NULL,
    created_at timestamp with time zone NOT NULL
);

CREATE INDEX ON conversations (user_id);

CREATE TABLE IF NOT EXISTS messages (
    id bigserial PRIMARY KEY,
    conversation_id bigint NOT NULL REFERENCES conversations (id) ON DELETE CASCADE,
    type varchar(20) NOT NULL CHECK (type in ('CODE_SUBMISSION', 'PROMPT', 'BOT')),
    payload jsonb NOT NULL,
    created_at timestamp with time zone NOT NULL
);

CREATE INDEX ON messages (conversation_id);
CREATE INDEX ON messages (type);