# Adomusic Remote

<div align="center">
  <img src="https://github.com/adgutech/adomusic-remote/blob/master/fastlane/metadata/android/en-US/images/featureGraphic.png" alt="AdoMusic Banner" width="100%"/>

  ### Conectar a Spotify para acceder la biblioteca y reproducir música mediante remota para Android.

  [![Licencia](https://img.shields.io/github/license/adgutech/adomusic-remote?style=flat-square&logo=gnu&color=2B3137&labelColor=161B22)](https://github.com/adgutech/adomusic-remote/blob/master/README.md)
  [![Android](https://img.shields.io/badge/Plataforma-Android%208.0+-3DDC84.svg?style=flat-square&logo=android&logoColor=white&labelColor=161B22)](https://www.android.com)
  [![Estrallas](https://img.shields.io/github/stars/adgutech/adomusic-remote?style=flat-square&logo=github&color=yellow&labelColor=161B22)](https://github.com/adgutech/adomusic-remote/stargazers)
</div>

---

## Tabla de contenido

- [Introducción](#-introductión)
- [Vista previa](#-vista-previa)
- [Características principales](#-características-principales)
- [¿Cómo participar la prueba de Spotify API?](#-cómo-participar-la-prueba-de-spotify-api)
- [Apoyar el proyecto](#-apoyar-el-proyecto)
- [Licencia](#-licencia)

---

## 📃 Introducción

AdoMusic Remote is a remote app that connects to Spotify and lets you play music.

Puedes controlar la reproducción de tus canciones favoritas, tan solo vincular para acceder el contenido de la biblioteca y controlar a través de esta aplicación. Encontrar un álbum, artistas y lista de reproducciones de la navegación nunca fue fácil, explorar en el menú principal y el buscador que puedes encontrar millones de canciones. Además, puede tener sus artistas y canciones favoritas más reproducidas y canciones guardadas que te gusta.

Por el momento Spotify API está en **Modo desarrollo**, la nueva política de Spotify a partir de 15 de mayo de 2025, solo acepta solicitudes de organizaciones (no de particulares) y necesito **25 usuarios** mínimo para poder pasar a **Modo de Cuota Extendida** y favor de compartir este proyecto o [Formulario de Google](https://docs.google.com/forms/d/13sAzgb2yt4gdgqZrvB9T612I74mCwb1ipacf37T0Ps8). Si usas la aplicación AdoMusic Remote no mostrará la biblioteca y solo controlar la reproducción de música y tienes que invitar a probar Spotify API. Vea en [¿Cómo participar la prueba de Spotify API?](#-cómo-participar-la-prueba-de-spotify-api).

> **Nota**: AdoMusic Remote es un proyecto independiente y no está afiliado, patrocinado ni respaldado por Spotify.
> **No admite archivos de MP3.**

## 📱 Vista previa
### Temas de la aplicación
| <img src="fastlane/metadata/android/en-US/images/screenshots/screenshot (1).jpg" width="200"/> | <img src="fastlane/metadata/android/en-US/images/screenshots/screenshot (2).jpg" width="200"/> | <img src="fastlane/metadata/android/en-US/images/screenshots/screenshot (3).jpg" width="200"/> |
|:---:|:---:|:---:|
| Claramente blanco | Poco oscuro | Solo negro |

### Pantalla de reproducción
| <img src="fastlane/metadata/android/en-US/images/screenshots/screenshot (4).jpg" width="200"/>| <img src="fastlane/metadata/android/en-US/images/screenshots/screenshot (5).jpg" width="200"/>| <img src="fastlane/metadata/android/en-US/images/screenshots/screenshot (6).jpg" width="200"/>|
|:---:|:---:|:---:|
| Claramente blanco en reproducción | Poco oscuro en reproducción | Solo negro en reproducción |

### Navegación
| <img src="fastlane/metadata/android/en-US/images/screenshots/screenshot (1).jpg" width="200"/> | <img src="fastlane/metadata/android/en-US/images/screenshots/screenshot (7).jpg" width="200"/> | <img src="fastlane/metadata/android/en-US/images/screenshots/screenshot (8).jpg" width="200"/> | <img src="fastlane/metadata/android/en-US/images/screenshots/screenshot (9).jpg" width="200"/> | <img src="fastlane/metadata/android/en-US/images/screenshots/screenshot (10).jpg" width="200"/> |
|:-----: |:-----: |:-----: |:-----: |:-----: |
| Principal | Álbumes | Artistas | Lista de reproducción | Buscar |

### 8 Temas de reproducción
| <img src="fastlane/metadata/android/en-US/images/screenshots/screenshot (16).jpg" width="200"/>	|<img src="fastlane/metadata/android/en-US/images/screenshots/screenshot (17).jpg" width="200"/>|   <img src="fastlane/metadata/android/en-US/images/screenshots/screenshot (18).jpg" width="200"/>  	|    <img src="fastlane/metadata/android/en-US/images/screenshots/screenshot (19).jpg" width="200"/> 	|
|:-----:	|:-----:	|:-----:	|:-----:	|
| Normal 	| Blur 	| Color 	| Material 	|

| <img src="fastlane/metadata/android/en-US/images/screenshots/screenshot (20).jpg" width="200"/>	|<img src="fastlane/metadata/android/en-US/images/screenshots/screenshot (21).jpg" width="200"/>|   <img src="fastlane/metadata/android/en-US/images/screenshots/screenshot (22).jpg" width="200"/>  	|    <img src="fastlane/metadata/android/en-US/images/screenshots/screenshot (23).jpg" width="200"/> 	|
|:-----:	|:-----:	|:-----:	|:-----:	|
| MD3 	| Peek 	| Plain 	| Simple 	|

### Más vista previa
| <img src="fastlane/metadata/android/en-US/images/screenshots/screenshot (11).jpg" width="200"/> | <img src="fastlane/metadata/android/en-US/images/screenshots/screenshot (12).jpg" width="200"/> | <img src="fastlane/metadata/android/en-US/images/screenshots/screenshot (13).jpg" width="200"/> | <img src="fastlane/metadata/android/en-US/images/screenshots/screenshot (14).jpg" width="200"/> | <img src="fastlane/metadata/android/en-US/images/screenshots/screenshot (15).jpg" width="200"/> |
|:-----: |:-----: |:-----: |:-----: |:-----: |
| Ecualizador | Tus me gusta | Top Canciones | Detalles del Artista | Más álbumes sobre del artista |

___

## 📦 Características principales

- ⚪ Temas base 3 (claramente blanco, un poco oscuro y solo negro).
- 📱 8 Temas de reproducción.
- 🔊 Controles de volumen.
- 🎵 Crear y editar listas de reproducción.
- ▶️ Cola de reproducción (solo Spotify Premium).
- 👤 Perfil de usuario.
- 🧭 Explore y reproduzca su música por canciones, álbumes, artistas, listas de reproducción.
- 🎚️ Ecualizador.
- 🎨 Acento de colores.
- ☑️ Seguir a los artistas y/o lista de reproducción.
- 🔝 Top artistas y canciones que has escuchado.
- 🔍 Navegación de búsqueda.

## 📝 ¿Cómo participar la prueba de Spotify API?

Para invitar una prueba de Spotify API, debes enviar a través de formulario de Google.
Los siguientes los pasos son:

- **Paso 1** Descargue la aplicación AdoMusic Remote (actualmente está en Beta) en los vínculos [Web](https://play.google.com/apps/testing/com.adgutech.adomusic.remote), [Android](https://play.google.com/store/apps/details?id=com.adgutech.adomusic.remote) o get it from the [GitHub Releases](https://github.com/adgutech/adomusic-remote/releases).
- **Paso 2** Enviar un [Formulario de Google](https://docs.google.com/forms/d/13sAzgb2yt4gdgqZrvB9T612I74mCwb1ipacf37T0Ps8), ingresa tu nombre y correo electrónico y finalizar, da clic enviar y esperar unos minutos o días a parecerá la biblioteca.
- **Paso 3** Listo! ya puedes hacer las pruebas.

---

## 💵 Apoyar el proyecto

Si encuentras valor en **AdoMusic Remote** y quieres contribuir a su desarrollo continuo, considera hacer una donación. Tu apoyo financiero nos permite:

- Implementar nuevas características y mejoras
- Corregir errores y optimizar el rendimiento
- Mantener la infraestructura del proyecto
- Dedicar más tiempo al desarrollo y mantenimiento

<div align="center">
  
[![PayPal](https://img.shields.io/badge/PayPal-00457C?style=for-the-badge&logo=paypal&logoColor=white)](mailto:adgutech@gmail.com)

</div>

---

## 🗂️ Licencia

**Copyright © 2022-2025 Adolfo Gutiérrez**

Este programa es software libre: puedes redistribuirlo y/o modificarlo bajo los términos de la Licencia Pública General GNU publicada por la Free Software Foundation, ya sea la versión 3 de la Licencia, o (a tu elección) cualquier versión posterior.

Este programa se distribuye con la esperanza de que sea útil, pero **SIN NINGUNA GARANTÍA**; ni siquiera la garantía implícita de COMERCIABILIDAD o IDONEIDAD PARA UN PROPÓSITO PARTICULAR. Consulta la [Licencia Pública General GNU](https://github.com/adgutech/adomusic-remote/blob/main/LICENSE) para más detalles.

<div align="center">
  
[![GPL v3](https://img.shields.io/badge/Licencia-GPLv3-blue.svg?style=for-the-badge&logo=gnu&logoColor=white)](https://www.gnu.org/licenses/gpl-3.0)

</div>

> **Importante**: Cualquier uso comercial no autorizado de este software o sus derivados constituye una violación de los términos de licencia.
