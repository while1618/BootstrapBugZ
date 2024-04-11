package org.bootstrapbugz.api.shared.message.service.impl

import org.bootstrapbugz.api.shared.message.service.MessageService
import org.springframework.context.MessageSource
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.stereotype.Service

@Service
class MessageServiceImpl(private val messageSource: MessageSource) : MessageService {
    override fun getMessage(code: String): String {
        return messageSource.getMessage(code, null, LocaleContextHolder.getLocale())
    }
}
