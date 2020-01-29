package com.github.lamba92.firebasemultiplatform.storage

import com.github.lamba92.firebasemultiplatform.core.resume
import com.google.android.gms.tasks.OnCanceledListener
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.storage.FileDownloadTask
import com.google.firebase.storage.OnPausedListener
import com.google.firebase.storage.OnProgressListener
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.suspendCancellableCoroutine

actual class DownloadTask(val delegate: PlatformSpecificDownloadTask) : StorageTask<DownloadTask.Snapshot> {

    override val isCanceled: Boolean
        get() = delegate.isCanceled
    override val isComplete: Boolean
        get() = delegate.isComplete
    override val isInProgress: Boolean
        get() = delegate.isInProgress
    override val isPaused: Boolean
        get() = delegate.isInProgress
    override val isSuccessful: Boolean
        get() = delegate.isSuccessful

    @ExperimentalCoroutinesApi
    override val stateChangesFlow
        get() = callbackFlow {
            val pauseCallback = OnPausedListener<FileDownloadTask.TaskSnapshot> {
                offer(StorageTask.Snapshot.State.PAUSED)
            }
            val cancelCallback = OnCanceledListener {
                offer(StorageTask.Snapshot.State.CANCELED)
            }
            val successCallback = OnSuccessListener<FileDownloadTask.TaskSnapshot> {
                offer(StorageTask.Snapshot.State.FINISHED_SUCCESSFULLY)
            }
            val progressCallback = OnProgressListener<FileDownloadTask.TaskSnapshot> {
                offer(StorageTask.Snapshot.State.RUNNING)
            }
            val errorCallback = OnFailureListener {
                offer(StorageTask.Snapshot.State.ERRORED)
            }
            with(delegate) {
                addOnPausedListener(pauseCallback)
                addOnCanceledListener(cancelCallback)
                addOnSuccessListener(successCallback)
                addOnProgressListener(progressCallback)
                addOnFailureListener(errorCallback)
                awaitClose {
                    removeOnPausedListener(pauseCallback)
                    removeOnCanceledListener(cancelCallback)
                    removeOnSuccessListener(successCallback)
                    removeOnProgressListener(progressCallback)
                }
            }
        }

    @ExperimentalCoroutinesApi
    override val progressFlow
        get() = callbackFlow {
            val progressCallback = OnProgressListener<FileDownloadTask.TaskSnapshot> { offer(it.toMpp()) }
            delegate.addOnProgressListener(progressCallback)
            awaitClose { delegate.removeOnProgressListener(progressCallback) }
        }

    override val snapshot: Snapshot
        get() = delegate.snapshot.toMpp()

    override fun cancel() {
        delegate.cancel()
    }

    override fun pause() {
        delegate.pause()
    }

    override fun resume() {
        delegate.resume()
    }

    actual class Snapshot(private val delegate: FileDownloadTask.TaskSnapshot) : StorageTask.Snapshot {

        override val storage: StorageReference
            get() = delegate.storage.toMpp()
        override val bytesTransferred: Long
            get() = delegate.bytesTransferred
        override val totalByteCount: Long
            get() = delegate.totalByteCount

    }

    override suspend fun await() = suspendCancellableCoroutine<Unit> { cont ->
        val l = OnCompleteListener<FileDownloadTask.TaskSnapshot> {
            cont.resume()
        }
        delegate.addOnCompleteListener(l)
        cont.invokeOnCancellation { delegate.removeOnCompleteListener(l) }
    }

}