package org.bootstrapbugz.api.shared.email.service

interface EmailService {
    fun sendHtmlEmail(to: String, subject: String, body: String)
}
