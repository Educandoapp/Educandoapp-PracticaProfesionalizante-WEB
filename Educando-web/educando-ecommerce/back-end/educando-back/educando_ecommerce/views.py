from .serializer import  ForoRespuestaSerializer, UsuarioSerializer, CategoriaSerializer, CursoSerializer, MisCursoSerializer, CarritoSerializer, ForoSerializer, ContactoSerializer
from .models import  ForoRespuesta, Usuario, Categoria,Curso, MisCurso, Carrito, Foro, Contacto

from rest_framework import viewsets, permissions
from rest_framework.permissions import AllowAny
from rest_framework.views import APIView
from rest_framework.response import Response
from rest_framework.exceptions import AuthenticationFailed

from django.utils import timezone
from django.conf import settings
from django.contrib.auth import authenticate
from django.shortcuts import get_object_or_404
from django.views.decorators.csrf import csrf_exempt
from rest_framework import status

import datetime, jwt
from django.contrib.auth.models import Group
class UsuarioView(viewsets.ViewSet):
    permission_classes = [AllowAny]

    def create_user(self, request):
        # Validar los datos del serializer
        serializer = UsuarioSerializer(data=request.data)
        if serializer.is_valid():
            email = serializer.validated_data.get('email')
            password = serializer.validated_data.get('password')
            nombre = serializer.validated_data.get('nombre')
            apellido = serializer.validated_data.get('apellido')
            id_rol_id = serializer.validated_data.get('id_rol_id')

            # Verificar si el correo electrónico ya está registrado
            usuario_existente = Usuario.objects.filter(email=email).exists()
            if usuario_existente:
                return Response({'mensaje': 'El correo electrónico ya está registrado'}, status=400)

            # Crear un nuevo usuario
            usuario = Usuario.objects.create_user(email=email, password=password, nombre=nombre, apellido=apellido, id_rol_id=id_rol_id)

            # Obtener el objeto de grupo correspondiente al id_rol
            grupo = Group.objects.get(pk=id_rol_id)

            # Asignar el grupo al usuario
            usuario.groups.add(grupo)

            # Generar el token JWT
            expiration_time = timezone.now() + datetime.timedelta(hours=12)
            expiration_timestamp = int(expiration_time.timestamp())

            payload = {
                'id_usuario': usuario.id_usuario,
                'email': email,
                'nombre': usuario.nombre,
                'exp': expiration_timestamp
            }
            token = jwt.encode(payload, settings.SECRET_KEY, algorithm='HS256')

            # Devolver una respuesta de éxito con el token generado
            return Response({'mensaje': 'Registro exitoso', 'token': token}, status=201)
        else:
            # Devolver una respuesta de error con los mensajes de validación
            return Response({'mensaje': 'Datos no válidos', 'errores': serializer.errors}, status=400)
        
    def inicio_sesion(self, request):
        # Verificar si la solicitud es un método POST
        if request.method == 'POST':
            try:
                data = request.data
                email = data.get('email', '')
                password = data.get('password', '')

                # Autenticar al usuario
                usuario = authenticate(request, email=email, password=password)

                if usuario is not None:
                    # Generar el token JWT
                    expiration_time = timezone.now() + datetime.timedelta(hours=12)
                    expiration_timestamp = int(expiration_time.timestamp())

                    payload = {
                        'id_usuario': usuario.id_usuario,
                        'email': email,
                        'nombre': usuario.nombre,
                        'apellido': usuario.apellido,
                        'id_rol_id': usuario.id_rol_id,
                        'urlImagen' : usuario.urlImagen,
                        'exp': expiration_timestamp
                    }
                    token = jwt.encode(payload, settings.SECRET_KEY, algorithm='HS256')

                    # Devolver una respuesta de éxito con el token generado
                    return Response({'mensaje': 'Inicio de sesión exitoso', 'token': token, 'usuario': payload}, status=200)
                else:
                    # Devolver una respuesta de error si las credenciales son inválidas
                    return Response({'mensaje': 'Credenciales inválidas'}, status=401)
            except:
                # Devolver una respuesta de error si ocurre algún error durante el proceso
                return Response({'mensaje': 'Datos no válidos'}, status=400)

        # Devolver una respuesta de error si el método no está permitido
        return Response({'mensaje': 'Método no permitido'}, status=405)

    def list_users(self, request):
        # Obtener la lista de usuarios y serializarlos
        usuarios = Usuario.objects.all()
        serializer = UsuarioSerializer(usuarios, many=True)

        # Devolver la lista de usuarios serializada
        return Response(serializer.data, status=200)
    
#===========================================================================================================================================================================    

class ObtenerUsuarioView(APIView):
    def verificar_token(self, token):
        try:
            payload = jwt.decode(token, settings.SECRET_KEY, algorithms=['HS256'])
            usuario_id = payload.get('id_usuario')
            return usuario_id
        except jwt.ExpiredSignatureError:
            raise AuthenticationFailed('Token expirado')
        except jwt.InvalidTokenError:
            raise AuthenticationFailed('Token inválido')

    def post(self, request):
        # Obtén el token del usuario desde el cuerpo de la solicitud
        token = request.data.get('token')        
        
        # Verifica el token
        usuario_id = self.verificar_token(token)
        if usuario_id is None:
            return Response({'mensaje': 'Token inválido'}, status=401)
        
        # El token es válido, obtén el usuario autenticado
        usuario = get_object_or_404(Usuario, id_usuario=usuario_id)
        
        # Serializa el usuario obtenido
        serializer = UsuarioSerializer(usuario)
        
        # Devuelve el usuario serializado
        return Response(serializer.data, status=200)
    
class CategoriaViewSet(viewsets.ModelViewSet):   
    queryset = Categoria.objects.all()
    permission_classes = [permissions.AllowAny]
    serializer_class = CategoriaSerializer  

class PorCategoriaViewSet(viewsets.ViewSet):   
    permission_classes = [permissions.AllowAny]
    
    def list(self, request):
        categorias = Categoria.objects.all()
        serializer = CategoriaSerializer(categorias, many=True)
        return Response(serializer.data)

    def retrieve(self, request, pk=None):
        categoria = Categoria.objects.get(pk=pk)
        cursos = Curso.objects.filter(id_categoria=categoria)
        serializer = CursoSerializer(cursos, many=True)
        return Response(serializer.data)

class CursoViewSet(viewsets.ModelViewSet):  
    queryset = Curso.objects.all()
    permission_classes = [permissions.AllowAny]
    serializer_class = CursoSerializer


#===========================================================================================================================================================================

class MisCursosView(APIView):
    serializer_class = MisCursoSerializer
    permission_classes = [AllowAny]

    def verificar_token(self, token):
        try:
            payload = jwt.decode(token, settings.SECRET_KEY, algorithms=['HS256'])
            id_usuario = payload.get('id_usuario')
            return id_usuario
        except jwt.ExpiredSignatureError:
            raise AuthenticationFailed('Token expirado')
        except jwt.InvalidTokenError:
            raise AuthenticationFailed('Token inválido')

    def post(self, request):
        # Obtén el token del cuerpo de la solicitud
        token = request.data.get('token', '')

        # Verifica el token
        usuario_id = self.verificar_token(token)
        if usuario_id is None:
            # El token no es válido, devuelve un mensaje de error y un código de estado 401 (No autorizado)
            return Response({'mensaje': 'Token inválido'}, status=401)

        # El token es válido, obtén el usuario autenticado
        usuario = get_object_or_404(Usuario, id_usuario=usuario_id)

        # Obtén los cursos del usuario y serialízalos
        cursos = MisCurso.objects.filter(id_usuario=usuario)
        serializer = MisCursoSerializer(cursos, many=True)

        # Devuelve los cursos serializados
        return Response(serializer.data)
    


class AdquirirCursoView(APIView):
    def verificar_token(self, token):
        try:
            payload = jwt.decode(token, settings.SECRET_KEY, algorithms=['HS256'])
            id_usuario = payload.get('id_usuario')
            return id_usuario
        except jwt.ExpiredSignatureError:
            raise AuthenticationFailed('Token expirado')
        except jwt.InvalidTokenError:
            raise AuthenticationFailed('Token inválido')

    def post(self, request):
        # Obtén el ID del curso a adquirir desde los datos de la solicitud
        id_curso = request.data.get('id_curso')
        
        # Obtén el token del usuario desde los datos de la solicitud
        token = request.data.get('token')

        try:
            # Verifica si el token es válido y obtén el ID del usuario autenticado
            usuario_id = self.verificar_token(token)
            if usuario_id is None:
                # El token no es válido, devuelve un mensaje de error y un código de estado 401 (No autorizado)
                return Response({'mensaje': 'Token inválido'}, status=401)
            
            # El token es válido, obtén el usuario autenticado
            usuario = get_object_or_404(Usuario, id_usuario=usuario_id)
            
            try:
                # Verifica si el curso existe
                curso = Curso.objects.get(id_curso=id_curso)
                
                # Crea una instancia de MisCurso para vincular el usuario y el curso
                mis_curso = MisCurso.objects.create(id_usuario=usuario, id_curso=curso)
                
                # Serializa la instancia de MisCurso
                serializer = MisCursoSerializer(mis_curso)
                
                return Response(serializer.data, status=201)
            except Curso.DoesNotExist:
                return Response({'mensaje': 'El curso no existe'}, status=400)
        
        except AuthenticationFailed as e:
            return Response({'mensaje': str(e)}, status=401)


#===========================================================================================================================================================================
class CarritoViewSet(viewsets.ModelViewSet):    
    queryset = Carrito.objects.all()
    permission_classes = [permissions.AllowAny]
    serializer_class = CarritoSerializer

class ForoViewSet(viewsets.ModelViewSet):
    queryset = Foro.objects.all()
    permission_classes = [permissions.AllowAny]
    serializer_class = ForoSerializer

    def create(self, request, *args, **kwargs):
        serializer = self.get_serializer(data=request.data)  # Crear un serializador con los datos de la solicitud
        serializer.is_valid(raise_exception=True)  # Verificar si los datos son válidos
        serializer.save()  # Guardar el nuevo objeto Foro sin asignar el usuario actual
        headers = self.get_success_headers(serializer.data)  # Obtener las cabeceras de éxito
        return Response(serializer.data, status=status.HTTP_201_CREATED, headers=headers)  # Devolver los datos serializados del objeto creado



    def list(self, request, *args, **kwargs):
        mensaje_id = request.query_params.get('mensaje_id')
        if mensaje_id:
            queryset = self.queryset.filter(id_foro_id=mensaje_id)
        else:
            queryset = self.get_queryset()
        serializer = self.get_serializer(queryset, many=True)
        return Response(serializer.data)

    def retrieve(self, request, pk=None, *args, **kwargs):
        queryset = self.get_queryset()
        mensaje = get_object_or_404(queryset, pk=pk)
        serializer = self.get_serializer(mensaje)
        return Response(serializer.data)

    def update(self, request, pk=None, *args, **kwargs):
        partial = kwargs.pop('partial', False)
        instance = self.get_object()
        serializer = self.get_serializer(instance, data=request.data, partial=partial)
        serializer.is_valid(raise_exception=True)
        self.perform_update(serializer)
        return Response(serializer.data)

    @csrf_exempt 
    def destroy(self, request, pk=None, *args, **kwargs):
        instance = self.get_object()
        self.perform_destroy(instance)
        return Response(status=status.HTTP_204_NO_CONTENT)
 
class ForoRespuestaViewSet(viewsets.ModelViewSet):
    queryset = ForoRespuesta.objects.all()
    permission_classes = [permissions.AllowAny]
    serializer_class = ForoRespuestaSerializer

    def create(self, request, *args, **kwargs):
        mensaje_id = request.data.get('id_foro')  # Corregir aquí para obtener el ID del mensaje
        mensaje = get_object_or_404(Foro, pk=mensaje_id)
        serializer = self.get_serializer(data=request.data)
        serializer.is_valid(raise_exception=True)
        serializer.save(id_foro=mensaje)  # Aquí también, ajustar para asignar el mensaje al campo id_foro
        return Response(serializer.data, status=status.HTTP_201_CREATED)

    def retrieve(self, request, pk=None, *args, **kwargs):
        queryset = self.get_queryset()
        respuesta = get_object_or_404(queryset, pk=pk)
        serializer = self.get_serializer(respuesta)
        return Response(serializer.data)

    def list(self, request, *args, **kwargs):
        queryset = self.get_queryset()
        serializer = self.get_serializer(queryset, many=True)
        return Response(serializer.data)

    def update(self, request, pk=None, *args, **kwargs):
        partial = kwargs.pop('partial', False)
        instance = self.get_object()
        serializer = self.get_serializer(instance, data=request.data, partial=partial)
        serializer.is_valid(raise_exception=True)
        self.perform_update(serializer)
        return Response(serializer.data)

    @csrf_exempt   
    def destroy(self, request, pk=None):
        respuesta = self.get_object()
        respuesta.delete()
        return Response(status=status.HTTP_204_NO_CONTENT)

class ContactoViewSet(viewsets.ModelViewSet):   
    queryset = Contacto.objects.all()
    permission_classes = [permissions.AllowAny]
    serializer_class = ContactoSerializer  
