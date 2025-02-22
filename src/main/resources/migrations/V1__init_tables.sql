CREATE TABLE tag
(
    id         UUID PRIMARY KEY,
    name       TEXT UNIQUE NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

CREATE TABLE anecdote
(
    id         UUID PRIMARY KEY,
    text       TEXT         NOT NULL,
    created_by varchar(255) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

CREATE TABLE tag_anecdote
(
    tag_id      UUID NOT NULL REFERENCES tag (id) ON DELETE CASCADE,
    anecdote_id UUID NOT NULL REFERENCES anecdote (id) ON DELETE CASCADE,
    created_at  TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    PRIMARY KEY (anecdote_id, tag_id)
);

CREATE TABLE rate
(
    id          UUID PRIMARY KEY,
    session_id  VARCHAR(256) NOT NULL,
    rate        INT          NOT NULL,
    anecdote_id UUID         NOT NULL REFERENCES anecdote (id) ON DELETE CASCADE,
    created_at  TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    UNIQUE (session_id, anecdote_id)
);
