USE truyenonline;

ALTER TABLE moderation_actions
    MODIFY COLUMN violation_type ENUM (
        'COPYRIGHT',
        'PORNOGRAPHY',
        'VIOLENCE',
        'SPAM',
        'HARASSMENT',
        'OTHER'
    ) NULL;

ALTER TABLE moderation_actions
    MODIFY COLUMN reason TEXT NULL;