package com.example.data.remote

import android.util.Log
import com.example.data.model.CommunityApp
import com.example.data.model.DeveloperConversation
import com.example.data.model.ReciprocalChatMessage
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class FirestoreService {
    private val firestore: FirebaseFirestore? by lazy {
        try {
            FirebaseFirestore.getInstance()
        } catch (e: Throwable) {
            Log.e("FirestoreService", "FirebaseFirestore.getInstance() failed: ${e.message}")
            null
        }
    }
    private val communityCollection get() = try { firestore?.collection("community_apps") } catch (_: Throwable) { null }

    fun getCommunityAppsFlow(): Flow<List<CommunityApp>> = callbackFlow {
        val collection = communityCollection
        if (collection == null) {
            trySend(emptyList())
            awaitClose {}
            return@callbackFlow
        }
        var listener: ListenerRegistration? = null
        try {
            listener = collection.addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("FirestoreService", "Error listening to community_apps: ${error.message}")
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val apps = snapshot.documents.mapNotNull { doc ->
                        try {
                            val isHidden = doc.getBoolean("isHidden") ?: false
                            if (isHidden) return@mapNotNull null

                            val name = doc.getString("name") ?: ""
                            val pkg = doc.getString("packageName") ?: doc.id
                            val dev = doc.getString("developerName") ?: "مطور معتمد"
                            val devEmail = doc.getString("developerEmail")
                                ?: doc.getString("userEmail")
                                ?: doc.getString("email")
                                ?: "${dev.lowercase().replace(" ", "").replace("[^a-zA-Z0-9]".toRegex(), "")}@gmail.com"
                            val cat = doc.getString("category") ?: "تطبيق"
                            val desc = doc.getString("description") ?: "تطبيق قيد الاختبار المغلق لـ 14 يوماً"

                            val icon = extractIconFromDoc(doc)

                            val points = (doc.getLong("rewardPoints") ?: 50L).toInt()
                            val current = (
                                doc.getLong("currentTesters")
                                    ?: doc.getLong("testersCurrent")
                                    ?: doc.getLong("testersCount")
                                    ?: doc.getLong("testers_count")
                                    ?: doc.getLong("testers")
                                    ?: 0L
                            ).toInt()
                            val target = (
                                doc.getLong("targetTesters")
                                    ?: doc.getLong("testersTarget")
                                    ?: 20L
                            ).toInt()
                            val web = doc.getString("webOptInUrl") ?: ""
                            val androidUrl = doc.getString("androidOptInUrl") ?: ""
                            val grp = doc.getString("googleGroupUrl") ?: ""
                            val featured = doc.getBoolean("isFeatured") ?: false

                            CommunityApp(
                                id = doc.id,
                                name = name,
                                packageName = pkg,
                                iconUrl = icon,
                                developerName = dev,
                                developerEmail = devEmail,
                                category = cat,
                                pointsReward = points,
                                currentTesters = current,
                                targetTesters = target,
                                webOptInUrl = web,
                                androidOptInUrl = androidUrl,
                                googleGroupUrl = grp,
                                isFeatured = featured,
                                description = desc
                            )
                        } catch (e: Exception) {
                            Log.e("FirestoreService", "Failed to parse doc ${doc.id}", e)
                            null
                        }
                    }
                    trySend(apps)
                } else {
                    trySend(emptyList())
                }
            }
        } catch (e: Throwable) {
            Log.e("FirestoreService", "Failed to register snapshot listener: ${e.message}")
            trySend(emptyList())
        }

        awaitClose {
            try {
                listener?.remove()
            } catch (_: Throwable) {}
        }
    }

    private fun extractIconFromDoc(doc: DocumentSnapshot): String {
        // Priority direct keys
        val stringKeys = listOf(
            "iconUrl", "icon", "imageUrl", "image", "appIcon", "iconBase64",
            "photoUrl", "photo_url", "logo", "logoUrl", "logo_url",
            "icon_url", "image_url", "avatar", "picture", "appImage",
            "app_image", "thumbnail", "thumb", "img", "imgUrl", "fileUrl",
            "file_url", "iconUri", "icon_uri", "banner", "cover"
        )
        for (k in stringKeys) {
            val v = doc.get(k)
            if (v is String && v.isNotBlank()) return v.trim()
            if (v is Map<*, *>) {
                val nestedUrl = v["url"] ?: v["uri"] ?: v["src"] ?: v["downloadUrl"]
                if (nestedUrl is String && nestedUrl.isNotBlank()) return nestedUrl.trim()
            }
            if (v is List<*> && v.isNotEmpty()) {
                val first = v.firstOrNull()
                if (first is String && first.isNotBlank()) return first.trim()
                if (first is Map<*, *>) {
                    val nestedUrl = first["url"] ?: first["uri"] ?: first["src"]
                    if (nestedUrl is String && nestedUrl.isNotBlank()) return nestedUrl.trim()
                }
            }
        }

        // Fuzzy search in any document field if not found
        doc.data?.forEach { (key, value) ->
            val lowerKey = key.lowercase()
            if (lowerKey.contains("icon") || lowerKey.contains("image") || lowerKey.contains("img") || lowerKey.contains("logo") || lowerKey.contains("photo")) {
                if (value is String && value.isNotBlank()) return value.trim()
                if (value is List<*> && value.isNotEmpty()) {
                    val first = value.firstOrNull()
                    if (first is String && first.isNotBlank()) return first.trim()
                }
            }
        }
        return ""
    }

    fun getDefaultCommunityApps(): List<CommunityApp> {
        return emptyList()
    }

    suspend fun addAppToFirestore(
        name: String,
        packageName: String,
        webOptInUrl: String,
        androidOptInUrl: String,
        googleGroupUrl: String,
        category: String,
        description: String,
        iconUrl: String = "",
        developerName: String = "المطور المعتمد",
        developerEmail: String = "",
        userId: String = "user_default",
        initialTesters: Int = 1
    ) {
        val docData = hashMapOf(
            "name" to name,
            "packageName" to packageName,
            "developerName" to developerName,
            "developerEmail" to developerEmail,
            "userId" to userId,
            "category" to category,
            "description" to if (description.isNotBlank()) description else "تطبيق قيد الاختبار المغلق لـ 14 يوماً",
            "iconUrl" to iconUrl,
            "icon" to iconUrl,
            "imageUrl" to iconUrl,
            "image" to iconUrl,
            "appIcon" to iconUrl,
            "photoUrl" to iconUrl,
            "logoUrl" to iconUrl,
            "rewardPoints" to 50,
            "currentTesters" to initialTesters,
            "testersCurrent" to initialTesters,
            "testersCount" to initialTesters,
            "targetTesters" to 20,
            "testersTarget" to 20,
            "webOptInUrl" to webOptInUrl,
            "androidOptInUrl" to androidOptInUrl,
            "googleGroupUrl" to googleGroupUrl,
            "isFeatured" to true,
            "isHidden" to false,
            "priorityScore" to 10,
            "updatedAt" to System.currentTimeMillis()
        )
        try {
            communityCollection?.document(packageName)?.set(docData)?.await()
        } catch (_: Throwable) {}
    }

    suspend fun incrementAppTesters(packageName: String) {
        try {
            communityCollection?.document(packageName)?.update(
                mapOf(
                    "currentTesters" to com.google.firebase.firestore.FieldValue.increment(1),
                    "testersCurrent" to com.google.firebase.firestore.FieldValue.increment(1),
                    "testersCount" to com.google.firebase.firestore.FieldValue.increment(1),
                    "updatedAt" to System.currentTimeMillis()
                )
            )?.await()
        } catch (_: Throwable) {}
    }

    suspend fun updateAppTestersInFirestore(packageName: String, count: Int) {
        try {
            communityCollection?.document(packageName)?.update(
                mapOf(
                    "currentTesters" to count,
                    "testersCurrent" to count,
                    "testersCount" to count,
                    "updatedAt" to System.currentTimeMillis()
                )
            )?.await()
        } catch (_: Throwable) {}
    }

    suspend fun setAppFeaturedInFirestore(packageName: String, isFeatured: Boolean) {
        try {
            communityCollection?.document(packageName)?.update(
                mapOf(
                    "isFeatured" to isFeatured,
                    "priorityScore" to if (isFeatured) 100 else 10,
                    "updatedAt" to System.currentTimeMillis()
                )
            )?.await()
        } catch (_: Throwable) {}
    }

    suspend fun updateAppIconInFirestore(packageName: String, newIconUrl: String) {
        try {
            communityCollection?.document(packageName)?.update(
                mapOf(
                    "iconUrl" to newIconUrl,
                    "icon" to newIconUrl,
                    "imageUrl" to newIconUrl,
                    "image" to newIconUrl,
                    "appIcon" to newIconUrl,
                    "photoUrl" to newIconUrl,
                    "logoUrl" to newIconUrl,
                    "updatedAt" to System.currentTimeMillis()
                )
            )?.await()
        } catch (_: Throwable) {}
    }

    suspend fun updateAppDetailsInFirestore(
        packageName: String,
        name: String,
        category: String,
        iconUrl: String,
        webOptInUrl: String,
        googleGroupUrl: String
    ) {
        try {
            communityCollection?.document(packageName)?.update(
                mapOf(
                    "name" to name,
                    "category" to category,
                    "iconUrl" to iconUrl,
                    "icon" to iconUrl,
                    "imageUrl" to iconUrl,
                    "image" to iconUrl,
                    "appIcon" to iconUrl,
                    "photoUrl" to iconUrl,
                    "logoUrl" to iconUrl,
                    "webOptInUrl" to webOptInUrl,
                    "googleGroupUrl" to googleGroupUrl,
                    "updatedAt" to System.currentTimeMillis()
                )
            )?.await()
        } catch (_: Throwable) {}
    }

    suspend fun deleteAppFromFirestore(packageName: String) {
        try {
            communityCollection?.document(packageName)?.delete()?.await()
        } catch (_: Throwable) {}
    }

    suspend fun adminUpdateAppFull(
        packageName: String,
        name: String,
        devName: String,
        category: String,
        iconUrl: String,
        currentTesters: Int,
        targetTesters: Int,
        isFeatured: Boolean,
        webOptInUrl: String,
        googleGroupUrl: String
    ) {
        try {
            communityCollection?.document(packageName)?.update(
                mapOf(
                    "name" to name,
                    "developerName" to devName,
                    "category" to category,
                    "iconUrl" to iconUrl,
                    "icon" to iconUrl,
                    "imageUrl" to iconUrl,
                    "image" to iconUrl,
                    "appIcon" to iconUrl,
                    "photoUrl" to iconUrl,
                    "logoUrl" to iconUrl,
                    "currentTesters" to currentTesters,
                    "testersCurrent" to currentTesters,
                    "testersCount" to currentTesters,
                    "targetTesters" to targetTesters,
                    "testersTarget" to targetTesters,
                    "isFeatured" to isFeatured,
                    "priorityScore" to if (isFeatured) 100 else 10,
                    "webOptInUrl" to webOptInUrl,
                    "googleGroupUrl" to googleGroupUrl,
                    "updatedAt" to System.currentTimeMillis()
                )
            )?.await()
        } catch (_: Throwable) {}
    }

    companion object {
        fun getChatChannelId(email1: String, email2: String): String {
            val e1 = email1.trim().lowercase()
            val e2 = email2.trim().lowercase()
            val first = if (e1 < e2) e1 else e2
            val second = if (e1 < e2) e2 else e1
            val raw = "${first}__${second}"
            return raw.replace(".", "_dot_").replace("@", "_at_").replace("/", "_")
        }
    }

    /**
     * Real-time listener for chat messages in a developer-to-developer channel.
     */
    fun getChatMessagesFlow(channelId: String): Flow<List<ReciprocalChatMessage>> = callbackFlow {
        val db = firestore
        if (db == null || channelId.isBlank()) {
            trySend(emptyList())
            awaitClose {}
            return@callbackFlow
        }

        val messagesColl = db.collection("developer_chats")
            .document(channelId)
            .collection("messages")

        var listener: ListenerRegistration? = null
        try {
            listener = messagesColl
                .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.ASCENDING)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        Log.e("FirestoreService", "Error listening to chat messages: ${error.message}")
                        return@addSnapshotListener
                    }
                    if (snapshot != null) {
                        val list = snapshot.documents.mapNotNull { doc ->
                            try {
                                ReciprocalChatMessage(
                                    id = doc.id,
                                    senderName = doc.getString("senderName") ?: "مطور",
                                    senderEmail = doc.getString("senderEmail") ?: "",
                                    message = doc.getString("message") ?: "",
                                    timestamp = doc.getLong("timestamp") ?: System.currentTimeMillis(),
                                    isFromMe = false,
                                    attachedAppName = doc.getString("attachedAppName"),
                                    attachedAppPackage = doc.getString("attachedAppPackage")
                                )
                            } catch (e: Exception) {
                                null
                            }
                        }
                        trySend(list)
                    }
                }
        } catch (e: Exception) {
            Log.e("FirestoreService", "Failed to register chat listener: ${e.message}")
            trySend(emptyList())
        }

        awaitClose { listener?.remove() }
    }

    /**
     * Sends a real message to another developer in Cloud Firestore.
     */
    suspend fun sendChatMessage(
        channelId: String,
        message: ReciprocalChatMessage,
        recipientEmail: String,
        recipientName: String
    ) {
        val db = firestore ?: return
        val chatDoc = db.collection("developer_chats").document(channelId)

        val cleanSenderEmail = message.senderEmail.lowercase().trim()
        val cleanRecipientEmail = recipientEmail.lowercase().trim()

        val msgData = hashMapOf(
            "senderName" to message.senderName,
            "senderEmail" to cleanSenderEmail,
            "recipientName" to recipientName,
            "recipientEmail" to cleanRecipientEmail,
            "message" to message.message,
            "timestamp" to message.timestamp,
            "attachedAppName" to (message.attachedAppName ?: ""),
            "attachedAppPackage" to (message.attachedAppPackage ?: "")
        )

        val summaryData = hashMapOf(
            "channelId" to channelId,
            "participants" to listOf(cleanSenderEmail, cleanRecipientEmail),
            "participantNames" to mapOf(
                cleanSenderEmail to message.senderName,
                cleanRecipientEmail to recipientName
            ),
            "lastMessage" to message.message,
            "lastSenderEmail" to cleanSenderEmail,
            "lastSenderName" to message.senderName,
            "lastTimestamp" to message.timestamp,
            "attachedAppName" to (message.attachedAppName ?: "")
        )

        try {
            chatDoc.set(summaryData, com.google.firebase.firestore.SetOptions.merge()).await()
            chatDoc.collection("messages").document(message.id).set(msgData).await()
        } catch (e: Exception) {
            Log.e("FirestoreService", "Failed to send chat message: ${e.message}")
        }
    }

    /**
     * Real-time listener for all active conversations of the current developer.
     */
    fun getUserConversationsFlow(userEmail: String): Flow<List<DeveloperConversation>> = callbackFlow {
        val cleanEmail = userEmail.lowercase().trim()
        val db = firestore
        if (cleanEmail.isBlank() || db == null) {
            trySend(emptyList())
            awaitClose {}
            return@callbackFlow
        }

        var listener: ListenerRegistration? = null
        try {
            listener = db.collection("developer_chats")
                .whereArrayContains("participants", cleanEmail)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        Log.e("FirestoreService", "Error listening to user conversations: ${error.message}")
                        return@addSnapshotListener
                    }
                    if (snapshot != null) {
                        val conversations = snapshot.documents.mapNotNull { doc ->
                            try {
                                val participants = doc.get("participants") as? List<*> ?: emptyList<Any>()
                                val otherEmail = participants.mapNotNull { it?.toString() }
                                    .firstOrNull { !it.equals(cleanEmail, ignoreCase = true) } ?: cleanEmail
                                val names = doc.get("participantNames") as? Map<*, *>
                                val otherName = names?.get(otherEmail)?.toString() ?: "مطور"
                                val lastMsg = doc.getString("lastMessage") ?: ""
                                val lastTime = doc.getLong("lastTimestamp") ?: 0L
                                val lastSender = doc.getString("lastSenderEmail") ?: ""
                                val appName = doc.getString("attachedAppName")

                                DeveloperConversation(
                                    channelId = doc.id,
                                    otherParticipantName = otherName,
                                    otherParticipantEmail = otherEmail,
                                    lastMessage = lastMsg,
                                    lastTimestamp = lastTime,
                                    lastSenderEmail = lastSender,
                                    appName = appName
                                )
                            } catch (e: Exception) {
                                null
                            }
                        }.sortedByDescending { it.lastTimestamp }
                        trySend(conversations)
                    }
                }
        } catch (e: Exception) {
            Log.e("FirestoreService", "Failed to listen to user conversations: ${e.message}")
            trySend(emptyList())
        }

        awaitClose { listener?.remove() }
    }
}
