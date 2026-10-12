package com.example.proyecto1

import android.app.Application
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

        // Guarda en el teléfono una copia de los datos de Realtime Database; así al volver
        // a abrir un chat no se descarga todo otra vez y la app sigue funcionando sin internet
        FirebaseDatabase.getInstance().setPersistenceEnabled(true)
    }
}
