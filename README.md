# Flins IoT

Aplicación Android para la gestión de usuarios y la visualización de datos de sensores IoT.

## Descripción

Flins IoT es una app de gestión orientada a monitoreo y administración de usuarios con autenticación, recuperación de contraseña, bloqueo temporal de cuentas y CRUD de usuarios. También incluye una sección para ver datos de sensores y una pantalla con información del equipo de desarrollo.

## Funcionalidades

- Inicio de sesión con validación de correo y contraseña
- Registro de usuarios
- Recuperación de contraseña con código temporal
- Bloqueo temporal de cuentas durante el proceso de recuperación
- CRUD de usuarios (crear, listar, modificar y eliminar)
- Visualización de datos de sensores mediante Volley
- Pantalla de información del desarrollador
- Interfaz moderna con Material Design y Lottie

## Tecnologías utilizadas

- Kotlin
- Android Studio
- Jetpack / AndroidX
- Material Components
- Volley para consumo de APIs
- SQLite para almacenamiento local
- Lottie para animaciones

## Requisitos

- Android Studio Hedgehog o superior
- JDK 11 o superior
- SDK Android 24+
- Dispositivo físico o emulador

## Instalación

1. Clona este repositorio:
   ```bash
   git clone https://github.com/daninson7985/flinsIOT.git
   ```
2. Abre la carpeta en Android Studio.
3. Espera a que Gradle sincronice las dependencias.
4. Ejecuta la aplicación en un emulador o dispositivo físico.

## Estructura del proyecto

```text
Myappeva/
├── app/
│   ├── src/
│   ├── build.gradle.kts
│   └── ...
├── build.gradle.kts
├── settings.gradle.kts
├── gradlew
├── gradlew.bat
├── README.md
└── .gitignore
```

## Notas

El proyecto ya está sincronizado con el repositorio remoto de GitHub:

https://github.com/daninson7985/flinsIOT.git

## Autor

- Daninson

## Licencia

Este proyecto se distribuye con fines educativos y de desarrollo.
