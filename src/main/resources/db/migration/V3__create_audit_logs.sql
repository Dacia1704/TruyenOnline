-- ==========================================================
-- Create audit_logs table
-- ==========================================================

CREATE TABLE audit_logs (
    id CHAR(36) NOT NULL,

    actor_id CHAR(36) NOT NULL,
    actor_role VARCHAR(30) NOT NULL,

    action VARCHAR(50) NOT NULL,

    object_type VARCHAR(30) NOT NULL,
    object_id CHAR(36) NULL,

    description TEXT NULL,

    old_value JSON NULL,
    new_value JSON NULL,

    ip_address VARCHAR(45) NULL,
    user_agent VARCHAR(255) NULL,

    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

    PRIMARY KEY (id),

    CONSTRAINT fk_audit_logs_actor
        FOREIGN KEY (actor_id)
        REFERENCES users(id)
);

-- ==========================================================
-- Indexes
-- ==========================================================

CREATE INDEX idx_audit_logs_actor
    ON audit_logs(actor_id);

CREATE INDEX idx_audit_logs_object
    ON audit_logs(object_type, object_id);

CREATE INDEX idx_audit_logs_action
    ON audit_logs(action);

CREATE INDEX idx_audit_logs_created_at
    ON audit_logs(created_at);