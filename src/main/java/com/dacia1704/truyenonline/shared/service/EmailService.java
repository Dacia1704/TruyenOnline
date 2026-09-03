package com.dacia1704.truyenonline.shared.service;

public interface EmailService {

    /**
     * Gửi email dạng HTML.
     *
     * @param to Email người nhận
     * @param subject Tiêu đề email
     * @param html Nội dung HTML
     */
    void sendHtmlEmail(String to, String subject, String html);
}
