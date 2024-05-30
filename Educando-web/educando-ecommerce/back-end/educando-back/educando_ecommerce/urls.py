from django.urls import path, include
from rest_framework import routers
from educando_ecommerce import views
from .views import test_connection
from rest_framework_simplejwt.views import TokenObtainPairView, TokenRefreshView


# Definición del enrutador
router = routers.DefaultRouter()
router.register(r'categoria', views.CategoriaViewSet, basename='categoria')
router.register(r'curso', views.CursoViewSet, basename='curso')
router.register(r'carrito', views.CarritoViewSet, basename='carrito')
router.register(r'foro', views.ForoViewSet, basename='foro')
router.register(r'contacto', views.ContactoViewSet, basename='contacto')
router.register(r'recordatorio', views.RecordatoriosViewSet, basename='recordatorio')

# URLs de la aplicación
urlpatterns = [
    # Incluir las URLs del enrutador
    path('', include(router.urls)),

    path('test_connection/', test_connection, name='test_connection'),
    
    # URL para la vista de inicio de sesión
    path('login/', views.UsuarioView.as_view({'post': 'inicio_sesion'}), name='login'),
    
    # URL para la vista de creación de usuario
    path('registro/', views.UsuarioView.as_view({'post': 'create_user'}), name='crear_usuario'),
    
    # URL para la vista de lista de usuarios
    path('usuarios/', views.UsuarioView.as_view({'get': 'list_users'}), name='lista_usuarios'),

     # URL para la vista de usuario-modificar
    path('usuarios/<int:pk>/', views.UsuarioDetailView.as_view(), name='detalle_usuario'), #PUT
    
    # URL para la vista de mis cursos
    path('mis_cursos/', views.MisCursosView.as_view(), name='mis_cursos'),
    
    # URL para la vista de adquirir curso
    path('adquirir_curso/', views.AdquirirCursoView.as_view(), name='adquirir_curso'),

    # URL para la vista de cursos por categoría
    path('cursos_por_categoria/<int:pk>/', views.PorCategoriaViewSet.as_view({'get': 'retrieve'}), name='cursos_por_categoria'),  

    # URL para la vista de foro
    path('foro/', views.ForoViewSet.as_view({'get': 'list', 'post': 'create'}), name='foro'),

    # URL para la vista de foro borrado
    path('foro/<int:pk>/', views.ForoViewSet.as_view({'delete': 'destroy'}), name='foro-delete'),
    
    # URL para la vista de foro-respuestas
    path('foro-respuestas/', views.ForoRespuestaViewSet.as_view({'get': 'list', 'post': 'create'}), name='foro-respuestas'),

    # URL para la vista de detalle de una respuesta específica del foro
    path('foro-respuestas/<int:pk>/', views.ForoRespuestaViewSet.as_view({'get': 'retrieve'}), name='foro-respuesta-detail'),

    # URL para la vista para eliminar una respuesta específica del foro
    path('foro-respuestas/<int:pk>/eliminar/', views.ForoRespuestaViewSet.as_view({'delete': 'destroy'}), name='eliminar-respuesta-foro'),

    path('obtener-usuario/', views.ObtenerUsuarioView.as_view(), name='obtener_usuario'),

    # URL para obtener usuario por Id
    path('usuario/<int:id_usuario>/', views.UsuarioView.as_view({'get': 'obtener_usuario_por_id'}), name='obtener_usuario_por_id'),

    # URL para la vista de inicio de sesión
    path('auth/validar_password/', views.UsuarioView.as_view({'post': 'validar_password'}), name='validar_password'),

    # URL para obtener las categorías
    path('categorias/', views.CategoriaViewSet.as_view({'get': 'list'}), name='categorias'),

    # URL para obtener todos los cursos
    path('cursos/', views.CursoViewSet.as_view({'get': 'list'}), name='cursos'),

    path('contacto/', views.ContactoView.as_view(), name='contacto'),

    path('contactos/', views.ContactoListView.as_view(), name='contactos'),

    path('recordatorios/<int:id_usuario>/', views.RecordatoriosUsuarioView.as_view(), name='recordatorios-usuario'),

    path('recordatorios/', views.CrearRecordatorioView.as_view(), name='crear-recordatorio'),

    path('recordatorios/eliminar/<int:id_recordatorio>/', views.EliminarRecordatorioView.as_view(), name='eliminar-recordatorio'),


    path('api/token/', TokenObtainPairView.as_view(), name='token_obtain_pair'),
    path('api/token/refresh/', TokenRefreshView.as_view(), name='token_refresh'),

]