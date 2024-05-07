export interface ForoRespuesta {
    id_foro_respuesta: number;
    id_foro: number;
    mensaje: string | null;
    nombre: string;
    fecha: Date | null;
    id_usuario: number;
  }