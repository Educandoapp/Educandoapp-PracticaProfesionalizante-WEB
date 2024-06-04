
<h1 align="center"><b>Bienvenidos a Educando 🎓  </b><img src="https://media.giphy.com/media/hvRJCLFzcasrR4ia7z/giphy.gif" width="35"></h1>

## 📜 Descripción

**Educando** es una aplicación educativa diseñada para enseñar programación de manera efectiva y atractiva. Nuestro objetivo es proporcionar una plataforma donde los usuarios puedan aprender y practicar sus habilidades de programación a través de una experiencia interactiva y bien estructurada.

---

## 📂 Repositorios del Proyecto
El proyecto nace de los siguientes repositorios principales:

### 🎨 Maquetado del Proyecto
Aquí encontrarás el diseño y la estructura inicial del proyecto.
[Repositorio de Maquetado](https://github.com/aguirre-valeria/full-stack-project)

### 🛒 Educando-Ecommerce
Este repositorio contiene la implementación del comercio electrónico para la plataforma Educando.
[Repositorio de Educando-Ecommerce](https://github.com/aguirre-valeria/educando-ecommerce)

### 📱 Educando App
Este repositorio contiene la aplicación móvil inicial de EducandoApp.
[Repositorio de Educando-App](https://github.com/romeomoreno/educandoapp)

---
## 👥 Integrantes del Proyecto
El desarrollo de **Educando** ha sido posible gracias al esfuerzo y la dedicación de los siguientes integrantes:

-	<a href="https://github.com/aguirre-valeria">AGUIRRE, Cintia Valeria Bettiana</a>
- <a href="https://github.com/Aparicio-Fernando">APARICIO, Fernando</a> 
-	<a href="https://github.com/JuanBalza">BALZA, Juan</a>
-	<a href="https://github.com/dario1595">DIAZ, Dario</a>
-	<a href="https://github.com/eduscba">LUNA, Juan Eduardo</a>
-	<a href="https://github.com/robertomiranda94"> MIRANDA, Walter Roberto </a>
-	<a href="https://github.com/romeomoreno">MORENO, Romeo</a>


---

## 🛠️ Instalación y Uso
Para clonar y ejecutar este proyecto en tu máquina local, sigue los siguientes pasos:

### 🔧 Requisitos Previos
Asegúrate de tener instalados los siguientes programas en tu máquina:
- [Git](https://git-scm.com/)
- [Python](https://www.python.org/) (versión 3.6 o superior)
- [Node.js](https://nodejs.org/) (versión 12 o superior)
- [Angular CLI](https://angular.io/cli)
- [MySQL](https://www.mysql.com/)
- [Android Studio](https://developer.android.com/studio)

### 🐍 Backend (Django)
1. Clona el repositorio:

    ```bash
    git clone https://github.com/aguirre-valeria/educando-ecommerce
    ```

2. Navega al directorio del backend:

    ```bash
    cd educando-ecommerce/educando-back
    ```

3. Crea y activa un entorno virtual:

    ```bash
    python -m venv env
    source env/bin/activate  # En Windows usa `env\Scripts\activate`
    ```

4. Instala las dependencias:

    ```bash
    pip install -r requirements.txt
    ```

5. Configura la base de datos MySQL:

    - Crea una base de datos en MySQL:

        ```sql
        CREATE DATABASE educando_db;
        ```

    - Actualiza las configuraciones de base de datos en `settings.py`:

        ```python
        DATABASES = {
            'default': {
                'ENGINE': 'django.db.backends.mysql',
                'NAME': 'educando_db',
                'USER': 'tu_usuario',
                'PASSWORD': 'tu_contraseña',
                'HOST': 'localhost',
                'PORT': '3306',
            }
        }
        ```

6. Realiza las migraciones y crea un superusuario:

    ```bash
    python manage.py migrate
    python manage.py createsuperuser
    ```

7. Levanta el servidor de desarrollo:

    ```bash
    python manage.py runserver
    ```

### 🌐 Frontend (Angular)
1. Navega al directorio del frontend:

    ```bash
    cd ../educando-front
    ```

2. Instala las dependencias:

    ```bash
    npm install
    ```

3. Levanta el servidor de desarrollo:

    ```bash
    ng serve
    ```

    - El frontend estará disponible en `http://localhost:4200` y se conectará al backend en Django.

### 📱 Aplicación Móvil (Java con Android Studio)
1. Abre Android Studio y selecciona "Open an existing Android Studio project".
2. Navega al directorio del proyecto móvil:

    ```plaintext
    educando-ecommerce/educando-mobile
    ```

3. Configura la conexión al backend en los archivos de configuración necesarios, asegurándote de que la URL del backend sea correcta.
4. Conecta un dispositivo Android o utiliza un emulador.
5. Ejecuta la aplicación desde Android Studio.

---

## 📸 Capturas de Pantalla

### 🌐 Plataforma Web
Aquí se muestran algunas capturas de pantalla de la plataforma web:

![WEB1-pantallaprincipal](https://github.com/printech-educando/EducandoProyectoIntegrador/assets/102261096/e9a6dbc5-c2ae-4167-be76-a9e6b69bb204)
*Página Principal*

![WEB3-cursos](https://github.com/printech-educando/EducandoProyectoIntegrador/assets/102261096/3a54497b-bf41-479c-ad51-5857da7f0c53)
*Buscador de cursos y navegación*

### 📱 Aplicación Móvil
Aquí se muestran algunas capturas de pantalla de la aplicación móvil:

![MOBILE1-inicio](https://github.com/printech-educando/EducandoProyectoIntegrador/assets/102261096/8d7d9a15-0560-46b3-a67d-a00ea0d8881e) ![MOBILE2-registro](https://github.com/printech-educando/EducandoProyectoIntegrador/assets/102261096/1d267c20-ff6f-46c8-a26d-7014421e6597) ![MOBILE3-login](https://github.com/printech-educando/EducandoProyectoIntegrador/assets/102261096/1598b9ab-0fac-488b-8e7b-93dbabb3cc49) ![MOBILE4-home](https://github.com/printech-educando/EducandoProyectoIntegrador/assets/102261096/1f4c3bf8-7ffc-48ac-93e5-9a444c435a19) ![MOBILE5-micuenta](https://github.com/printech-educando/EducandoProyectoIntegrador/assets/102261096/488a44f4-30e5-49bd-9ca4-304d1c70bc22) ![MOBILE9-recordatorio](https://github.com/printech-educando/EducandoProyectoIntegrador/assets/102261096/fb748f3c-335e-4255-846c-f12bd5534b15)


---

## 🤝 Contribuciones
Las contribuciones son bienvenidas. Si deseas contribuir, por favor sigue estos pasos:

1. Haz un fork del repositorio.
2. Crea una nueva rama para tu funcionalidad:

    ```bash
    git checkout -b nueva-funcionalidad
    ```

3. Realiza los cambios y haz commits descriptivos:

    ```bash
    git commit -m 'Agrega nueva funcionalidad'
    ```

4. Sube los cambios a tu repositorio:

    ```bash
    git push origin nueva-funcionalidad
    ```

5. Crea un Pull Request hacia el repositorio original.

---

## 📧 Contacto
Si tienes alguna pregunta o sugerencia, no dudes en ponerte en contacto con cualquiera de los integrantes del equipo.

---

¡Gracias por contribuir a **Educando**! 🎉
