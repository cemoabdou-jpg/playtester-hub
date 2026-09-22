package com.example.data.remote

import android.util.Log
import com.example.data.model.CommunityApp
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class FirestoreService {

    private val firestore: FirebaseFirestore? by lazy {
        try {
            FirebaseFirestore.getInstance()
        } catch (e: Exception) {
            Log.e("FirestoreService", "Firebase Firestore unavailable or not initialized: ${e.message}")
            null
        }
    }

    suspend fun publishAppToFirestore(app: CommunityApp): Boolean {
        val db = firestore ?: return false
        return try {
            val docData = hashMapOf(
                "name" to app.name,
                "developerName" to app.developerName,
                "packageName" to app.packageName,
                "description" to app.description,
                "category" to app.category,
                "googleGroupUrl" to app.googleGroupUrl,
                "webOptInUrl" to app.webOptInUrl,
                "androidOptInUrl" to app.androidOptInUrl,
                "testersCurrent" to app.testersCurrent,
                "testersTarget" to app.testersTarget,
                "rewardPoints" to app.rewardPoints,
                "isFeatured" to app.isFeatured,
                "priorityScore" to app.priorityScore,
                "createdAt" to System.currentTimeMillis()
            )
            val docRef = if (app.packageName.isNotBlank()) {
                db.collection("community_apps").document(app.packageName)
            } else {
                db.collection("community_apps").document()
            }
            docRef.set(docData).await()
            true
        } catch (e: Exception) {
            Log.e("FirestoreService", "Error publishing app to Firestore: ${e.message}")
            false
        }
    }

    fun observeRemoteCommunityApps(): Flow<List<CommunityApp>> = callbackFlow {
        val db = firestore
        if (db == null) {
            trySend(emptyList())
            close()
            return@callbackFlow
        }

        val listenerRegistration = db.collection("community_apps")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("FirestoreService", "Listen error: ${error.message}")
                    trySend(emptyList())
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val apps = snapshot.documents.mapNotNull { doc ->
                        try {
                            CommunityApp(
                                name = doc.getString("name") ?: "",
                                developerName = doc.getString("developerName") ?: "مطور معتمد",
                                packageName = doc.getString("packageName") ?: doc.id,
                                description = doc.getString("description") ?: "",
                                category = doc.getString("category") ?: "أدوات",
                                googleGroupUrl = doc.getString("googleGroupUrl") ?: "",
                                webOptInUrl = doc.getString("webOptInUrl") ?: "",
                                androidOptInUrl = doc.getString("androidOptInUrl") ?: "",
                                testersCurrent = (doc.getLong("testersCurrent") ?: 0L).toInt(),
                                testersTarget = (doc.getLong("testersTarget") ?: 20L).toInt(),
                                rewardPoints = (doc.getLong("rewardPoints") ?: 40L).toInt(),
                                isFeatured = doc.getBoolean("isFeatured") ?: false,
                                priorityScore = (doc.getLong("priorityScore") ?: 0L).toInt()
                            )
                        } catch (e: Exception) {
                            null
                        }
                    }
                    trySend(apps)
                }
            }

        awaitClose { listenerRegistration.remove() }
    }

    suspend fun updateAppTestersInFirestore(packageName: String, newTestersCount: Int) {
        val db = firestore ?: return
        try {
            db.collection("community_apps").document(packageName)
                .update("testersCurrent", newTestersCount)
                .await()
        } catch (e: Exception) {
            Log.e("FirestoreService", "Error updating testers count: ${e.message}")
        }
    }
}
