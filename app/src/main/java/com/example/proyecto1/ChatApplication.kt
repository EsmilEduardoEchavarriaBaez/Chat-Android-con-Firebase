package com.example.proyecto1

import android.app.Application
import com.example.proyecto1.util.NotificationUtils
import com.google.firebase.appcheck.FirebaseAppCheck
import com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory
import com.google.firebase.database.FirebaseDatabase

class ChatApplication : Application() {

    // Se ejecuta una sola vez al abrir la app, antes que cualquier pantalla
    override fun onCreate() {
        super.onCreate()

        // App Check se instala aquí para que quede listo una sola vez para toda la app
        val firebaseAppCheck = FirebaseAppCheck.getInstance()
        firebaseAppCheck.installAppCheckProviderFactory(
            DebugAppCheckProviderFactory.getInstance()
        )

        // Guarda una copia de Realtime Database en el teléfono para no descargar todo otra vez y funcionar sin internet
        FirebaseDatabase.getInstance().setPersistenceEnabled(true)

        // El canal de notificaciones se crea una sola vez aquí, en vez de cada vez que se muestra una
        NotificationUtils.createChannel(this)
    }
}
