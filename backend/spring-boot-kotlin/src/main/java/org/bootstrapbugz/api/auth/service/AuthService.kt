package org.bootstrapbugz.api.auth.service

import org.bootstrapbugz.api.auth.payload.dto.AuthTokensDTO
import org.bootstrapbugz.api.auth.payload.request.AuthTokensRequest
import org.bootstrapbugz.api.auth.payload.request.ForgotPasswordRequest
import org.bootstrapbugz.api.auth.payload.request.RegisterUserRequest
import org.bootstrapbugz.api.auth.payload.request.ResetPasswordRequest
import org.bootstrapbugz.api.auth.payload.request.VerificationEmailRequest
import org.bootstrapbugz.api.auth.payload.request.VerifyEmailRequest
import org.bootstrapbugz.api.user.payload.dto.UserDTO

interface AuthService {
    fun register(registerUserRequest: RegisterUserRequest): UserDTO
    fun authenticate(authTokensRequest: AuthTokensRequest, ipAddress: String): AuthTokensDTO
    fun deleteTokens(accessToken: String, ipAddress: String)
    fun deleteTokensOnAllDevices()
    fun refreshTokens(refreshToken: String, ipAddress: String): AuthTokensDTO
    fun forgotPassword(forgotPasswordRequest: ForgotPasswordRequest)
    fun resetPassword(resetPasswordRequest: ResetPasswordRequest)
    fun sendVerificationMail(verificationEmailRequest: VerificationEmailRequest)
    fun verifyEmail(verifyEmailRequest: VerifyEmailRequest)
}
