package com.practice.astra.ui.auth.data

import android.util.Log
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import com.practice.astra.ui.auth.data.model.LoggedInUser
import kotlinx.coroutines.tasks.await
import java.io.IOException
import java.util.concurrent.TimeUnit

class LoginDataSource {

    private val auth = FirebaseAuth.getInstance()

    suspend fun login(username: String, password: String): Result<LoggedInUser> {
        return try {
            val authResult = auth.signInWithEmailAndPassword(username, password).await()
            val firebaseUser = authResult.user

            if (firebaseUser != null) {
                // ログイン成功
                val user = LoggedInUser(
                    userId = firebaseUser.uid,
                    displayName = firebaseUser.email ?: "ユーザー"
                )
                Result.Success(user)
            } else {
                Result.Error(IOException("ログインに失敗しました"))
            }

        }  catch (e: Exception) {
        Log.e("AUTH_DEBUG", "エラーの種類: ${e::class.java.simpleName}")
        Log.e("AUTH_DEBUG", "エラーメッセージ: ${e.message}")
        Result.Error(IOException("ログインエラー: ${e.localizedMessage}", e))
        }
    }

    fun logout() {
        auth.signOut()
    }
}