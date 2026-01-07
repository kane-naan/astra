package com.practice.astra.ui.auth.data

import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import com.practice.astra.ui.auth.data.model.LoggedInUser
import java.io.IOException
import java.util.concurrent.TimeUnit

class LoginDataSource {

    private val firebaseAuth = FirebaseAuth.getInstance()

    fun login(username: String, password: String): Result<LoggedInUser> {
        return try {
            val task = firebaseAuth.signInWithEmailAndPassword(username, password)
            val result = Tasks.await(task, 10, TimeUnit.SECONDS)

            val firebaseUser = result.user
            if (firebaseUser != null) {
                val user = LoggedInUser(
                    userId = firebaseUser.uid,
                    displayName = firebaseUser.displayName ?: "ユーザー"
                )
                Result.Success(user)
            } else {
                Result.Error(IOException("ユーザー情報の取得に失敗しました"))
            }
        } catch (e: Exception) {
            Result.Error(IOException("ログインエラー: ${e.localizedMessage}", e))
        }
    }

    fun logout() {
        firebaseAuth.signOut()
    }
}