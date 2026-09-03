package com.dacia1704.truyenonline.module.administration.entity;

public enum AuditAction {

    // Authentication
    REGISTER,
    LOGIN,
    LOGOUT,
    LOGOUT_ALL,

    // CRUD
    CREATE,
    UPDATE,
    DELETE,

    // Publish
    PUBLISH,
    UNPUBLISH,

    // Moderation
    APPROVE,
    REJECT,
    BAN,
    UNBAN,

    // User
    CHANGE_ROLE,
    CHANGE_PASSWORD,
    CHANGE_EMAIL,
    RESET_PASSWORD,
    SUCCESS,
    FAIL
}
