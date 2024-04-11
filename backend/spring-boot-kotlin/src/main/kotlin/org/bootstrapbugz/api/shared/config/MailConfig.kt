package org.bootstrapbugz.api.shared.config

import java.util.*
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.JavaMailSenderImpl

@Configuration
class MailConfig(
  @Value("\${spring.mail.host}") private val host: String,
  @Value("\${spring.mail.port}") private val port: String,
  @Value("\${spring.mail.username}") private val username: String,
  @Value("\${spring.mail.password}") private val password: String
) {
  @Bean
  fun getJavaMailSender(): JavaMailSender {
    val mailSender = createMailSender()
    setProperties(mailSender)
    return mailSender
  }

  private fun createMailSender(): JavaMailSenderImpl {
    return JavaMailSenderImpl().also { sender ->
      sender.host = this.host
      sender.port = this.port.toInt()
      sender.username = this.username
      sender.password = this.password
    }
  }

  private fun setProperties(mailSender: JavaMailSenderImpl) {
    mailSender.javaMailProperties.apply {
      put("mail.transport.protocol", "smtp")
      put("mail.smtp.auth", "true")
      put("mail.smtp.starttls.enable", "true")
      put("mail.debug", "true")
    }
  }
}
