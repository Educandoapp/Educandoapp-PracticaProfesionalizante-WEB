export interface Foro {
    id_foro: number;  // Modificar el tipo para permitir null
    id_usuarios: number ; // Modificar el tipo para permitir null
    id_rol: number | null; // Modificar el tipo para permitir null
    titulo: string;
    nombre: string;
    mensaje: string;
    fecha: Date | null;
    respondiendo: boolean;
    id_foro_respuesta_id: number | null; // Modificar el tipo para permitir null
  }