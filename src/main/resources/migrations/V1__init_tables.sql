CREATE TABLE anecdote
(
    id         UUID PRIMARY KEY,
    text       TEXT NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
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