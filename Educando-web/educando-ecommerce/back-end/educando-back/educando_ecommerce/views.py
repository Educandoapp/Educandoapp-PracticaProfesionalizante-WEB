from .serializer import  ForoRespuestaSerializer, UsuarioSerializer, CategoriaSerializer, CursoSerializer, MisCursoSerializer, CarritoSerializer, ForoSerializer, ContactoSerializer, RecordatorioSerializer, CursoFavoritoSerializer
from .models import  ForoRespuesta, Usuario, Categoria,Curso, MisCurso, Carrito, Foro, Contacto, Recordatorio, CursoFavorito
from rest_framework import viewsets, permissions
from rest_framework.permissions import AllowAny, IsAuthenticated
from rest_framework.views import APIView
from rest_framework.response import Response
from rest_framework.exceptions import AuthenticationFailed, NotFound
from django.utils import timezone
from django.conf import settings
from django.contrib.auth import authenticate
from django.shortcuts import get_object_or_404
from django.views.decorators.csrf import csrf_exempt
from rest_framework import status
import datetime, jwt
from django.contrib.auth.models import Group
from django.contrib.auth.hashers import check_password
from rest_framework.decorators import action
from rest_framework_simplejwt.authentication import JWTAuthentication


from django.http import Http404, JsonResponse

def test_connection(request):
    return JsonResponse({'message': 'Connection successful'})

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
    
    def obtener_usuario_por_id(self, request, id_usuario):
        try:
            # Buscar el usuario por su ID
            usuario = Usuario.objects.get(id_usuario=id_usuario)
            
            # Serializar los datos del usuario
            serializer = UsuarioSerializer(usuario)
            
            # Devolver los datos del usuario
            return Response(serializer.data, status=status.HTTP_200_OK)
        except Usuario.DoesNotExist:
            # Si el usuario no existe, devolver un mensaje de error
            return Response({'mensaje': 'El usuario no existe'}, status=status.HTTP_404_NOT_FOUND)

    @action(detail=False, methods=['post'])
    def validar_password(self, request):
        password_plana = request.data.get('password_plana')
        password_encriptada = request.data.get('password_encriptada')

        if password_plana and password_encriptada:
            # Verificar si la contraseña plana coincide con la encriptada
            coincide = check_password(password_plana, password_encriptada)

            if coincide:
                return Response({'mensaje': 'Las contraseñas coinciden'}, status=200)
            else:
                return Response({'mensaje': 'Las contraseñas no coinciden'}, status=200)
        else:
            return Response({'mensaje': 'Faltan datos en la solicitud'}, status=400)

class UsuarioDetailView(APIView):
    def put(self, request, pk):
        # Obtener el usuario existente
        usuario = get_object_or_404(Usuario, pk=pk)

        # Validar la contraseña antigua
        old_password = request.data.get('old_password')
        if old_password and not check_password(old_password, usuario.password):
            return Response({'mensaje': 'La contraseña antigua es incorrecta'}, status=status.HTTP_400_BAD_REQUEST)

        # Actualizar el email si se proporciona en la solicitud
        if 'email' in request.data:
            usuario.email = request.data['email']

        # Actualizar la urlImagen si se proporciona en la solicitud
        if 'urlImagen' in request.data:
            usuario.urlImagen = request.data['urlImagen']

        # Actualizar la contraseña si se proporciona en la solicitud
        if 'password' in request.data:
            usuario.set_password(request.data['password'])

        # Guardar los cambios en el usuario
        usuario.save()

        # Devolver una respuesta exitosa
        serializer = UsuarioSerializer(usuario)
        return Response(serializer.data, status=status.HTTP_200_OK)

#===========================================================================================================================================================================    

class ObtenerUsuarioView(APIView):
    def verificar_token(self, token):
        # Registra el token recibido para depuración
        # print("Token recibido:", token)

        try:
            payload = jwt.decode(token, settings.SECRET_KEY, algorithms=['HS256'])
            # print("Payload decodificado:", payload)
            usuario_id = payload.get('id_usuario')
            return usuario_id
        except jwt.ExpiredSignatureError:
            # print("Token expirado")
            raise AuthenticationFailed('Token expirado')
        except jwt.InvalidTokenError:
            # print("Token inválido")
            raise AuthenticationFailed('Token inválido')

    def post(self, request):
        # Imprimir el cuerpo de la solicitud
        # print(request.data)  
        # Obtén el token del usuario desde el cuerpo de la solicitud
        token = request.data.get('token')   

        if not token:
            return Response({'mensaje': 'Token no proporcionado'}, status=400)   
        
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
        data = serializer.data
        print("Data enviada al front (lista de categorías):", data)
        return Response(data)

    def retrieve(self, request, pk=None):
        categoria = Categoria.objects.get(pk=pk)
        cursos = Curso.objects.filter(id_categoria=categoria)
        serializer = CursoSerializer(cursos, many=True)
        data = serializer.data
        print("Data enviada al front (cursos de una categoría):", data)
        return Response(data)

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
    
class CursoFavoritoList(APIView):
    permission_classes = []

    def get(self, request):
        cursos_favoritos = CursoFavorito.objects.all()
        serializer = CursoFavoritoSerializer(cursos_favoritos, many=True)
        return Response(serializer.data)

    def post(self, request):
        print("Datos recibidos en la solicitud POST de CursoFavoritoList:")
        print(request.data)
        serializer = CursoFavoritoSerializer(data=request.data)
        if serializer.is_valid():
            serializer.save()
            return Response(serializer.data, status=status.HTTP_201_CREATED)
        return Response(serializer.errors, status=status.HTTP_400_BAD_REQUEST)

class CursoFavoritoDetail(APIView):
    permission_classes = []

    def get_object(self, pk):
        try:
            return CursoFavorito.objects.get(pk=pk)
        except CursoFavorito.DoesNotExist:
            raise Http404

    def get(self, request, pk):
        curso_favorito = self.get_object(pk)
        serializer = CursoFavoritoSerializer(curso_favorito)
        return Response(serializer.data)

    def post(self, request, pk):
        try:
            # Filtrar los objetos CursoFavorito por el id_curso
            curso_favoritos = CursoFavorito.objects.filter(id_curso=pk)
            
            # Eliminar todos los objetos encontrados
            for curso_favorito in curso_favoritos:
                curso_favorito.delete()
                
            return Response(status=status.HTTP_204_NO_CONTENT)
        except CursoFavorito.DoesNotExist:
            return Response({"error": "Curso favorito no encontrado."}, status=status.HTTP_404_NOT_FOUND)
    
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

            # Obtén la lista de cursos desde los datos de la solicitud
            cursos_data = request.data.get('cursos', [])

            # Lista para almacenar las instancias de MisCurso creadas
            mis_cursos = []

            for curso_data in cursos_data:
                # Obtén el ID del curso de los datos recibidos
                id_curso = curso_data.get('id_curso')

                try:
                    # Verifica si el curso existe
                    curso = Curso.objects.get(id_curso=id_curso)
                    
                    # Crea una instancia de MisCurso para vincular el usuario y el curso
                    mis_curso = MisCurso.objects.create(id_usuario=usuario, id_curso=curso)
                    
                    # Agrega la instancia de MisCurso a la lista
                    mis_cursos.append(mis_curso)
                except Curso.DoesNotExist:
                    # Si el curso no existe, agrega un mensaje de error a la respuesta
                    return Response({'mensaje': f'El curso con ID {id_curso} no existe'}, status=400)
            
            # Serializa la lista de instancias de MisCurso
            serializer = MisCursoSerializer(mis_cursos, many=True)
            
            return Response(serializer.data, status=201)
        
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

class ContactoView(APIView):
    permission_classes = [AllowAny]

    def post(self, request):
        print("Datos recibidos en el backend:", request.data)
        serializer = ContactoSerializer(data=request.data)
        if serializer.is_valid():
            serializer.save()
            print("Mensaje de contacto guardado exitosamente.")
            return Response({'mensaje': '¡Gracias por ponerte en contacto con nosotros!'}, status=status.HTTP_201_CREATED)
        print("Errores de validación en el serializer:", serializer.errors)
        return Response(serializer.errors, status=status.HTTP_400_BAD_REQUEST)
    
class ContactoListView(APIView):
    permission_classes = [AllowAny]

    def get(self, request):
        email = request.query_params.get('email')
        print("Email recibido en la solicitud:", email)
        if email:
            contactos = Contacto.objects.filter(email=email)
            serializer = ContactoSerializer(contactos, many=True)
            print("Datos enviados en la respuesta:", serializer.data)
            return Response(serializer.data, status=status.HTTP_200_OK)
        return Response({'error': 'Email parameter is required'}, status=status.HTTP_400_BAD_REQUEST)
    

class RecordatoriosViewSet(viewsets.ModelViewSet):   
    queryset = Recordatorio.objects.all()
    permission_classes = [permissions.AllowAny]
    serializer_class = RecordatorioSerializer
class RecordatoriosUsuarioView(APIView):
    def get(self, request, id_usuario):
        # Buscar el usuario o devolver 404 si no existe
        usuario = get_object_or_404(Usuario, id_usuario=id_usuario)
        
        # Obtener los recordatorios del usuario
        recordatorios = Recordatorio.objects.filter(usuario=usuario)
        
        # Serializar los recordatorios y devolverlos como respuesta
        serializer = RecordatorioSerializer(recordatorios, many=True)
        return Response(serializer.data)
    
class CrearRecordatorioView(APIView):
    def post(self, request):
        print(f"Datos recibidos: {request.data}")
        serializer = RecordatorioSerializer(data=request.data)
        if serializer.is_valid():
            serializer.save()
            print("Tarea guardada correctamente")
            return Response(serializer.data, status=status.HTTP_201_CREATED)
        print(f"Errores de validación: {serializer.errors}")
        return Response(serializer.errors, status=status.HTTP_400_BAD_REQUEST)
    
class EliminarRecordatorioView(APIView):
    permission_classes = [AllowAny]

    def post(self, request, id_recordatorio):
        print(f'Solicitud POST recibida para eliminar el recordatorio con ID: {id_recordatorio}')
        recordatorio = get_object_or_404(Recordatorio, id_recordatorio=id_recordatorio)
        recordatorio.delete()
        print(f'Recordatorio con ID {id_recordatorio} eliminado correctamente.')
        return Response(status=status.HTTP_204_NO_CONTENT)