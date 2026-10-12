# Chat-Android-con-Firebase

Aplicación de chat para Android hecha en Kotlin con XML Views, arquitectura MVVM y Firebase (Authentication, Realtime Database, Storage y Cloud Messaging).

## Escalabilidad y optimización

- **Mensajes paginados:** el chat solo descarga los últimos 50 mensajes (`limitToLast`) y pide 50 más cuando el usuario sube hasta arriba, así un chat con miles de mensajes abre igual de rápido.
- **Consultas indexadas:** los mensajes se ordenan por la clave de `push()`, que Firebase ya tiene indexada, así el filtro se hace en el servidor y no en el teléfono.
- **Datos separados por chat:** cada conversación se guarda en `chats/{uidA_uidB}`, así cada usuario solo descarga los chats que abre.
- **Listas con DiffUtil:** `ListAdapter` solo redibuja los elementos que cambiaron en vez de toda la lista.
- **Imágenes comprimidas:** antes de subirlas se reducen a 1280 px y se guardan como JPEG, así pesan mucho menos en Storage y gastan menos datos al descargarlas.
- **Caché local:** Realtime Database guarda una copia en el teléfono, así al volver a un chat no se descarga todo otra vez y la app funciona sin conexión.
- **Listeners controlados:** cada listener de Firebase se quita en `onCleared()` o al cerrar sesión, para no dejar conexiones abiertas ni fugas de memoria.
- **Sin cargas repetidas:** los ViewModel conservan los datos al girar la pantalla, así no se vuelven a pedir a Firebase.
