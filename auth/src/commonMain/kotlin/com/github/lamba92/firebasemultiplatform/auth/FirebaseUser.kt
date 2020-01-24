package com.github.lamba92.firebasemultiplatform.auth

expect class FirebaseUser {

    val displayName: String?
    val email: String?
    val metadata: FirebaseUserMetadata?
    val phoneNumber: String?
    val photoUrl: String?
    val providerData: List<FirebaseUserInfo>
    val providerId: String
    val uid: String
    val isAnonymous: Boolean
    val isEmailVerified: Boolean

    suspend fun delete()
    suspend fun getIdToken(forceRefresh: Boolean): GetTokenResults
    suspend fun linkWithCredentials(credential: AuthCredential): AuthResult
    suspend fun reauthenticate(credential: AuthCredential)
    suspend fun reauthenticateAndRetrieveData(credential: AuthCredential): AuthResult
    suspend fun reload()
    suspend fun sendEmailVerification()
    suspend fun unlink(providerId: String)
    suspend fun updateEmail(email: String)
    suspend fun updatePassword(password: String)
    suspend fun updatePhoneNumber(phoneAuthCredential: PhoneAuthCredential)
    suspend fun updateProfile(userProfileChangeRequest: UserProfileChangeRequest)

}