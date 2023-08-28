package com.poliprotect.amst_proyecto.data;

public class Horario {
    int bateria = 0;
    String comando = "Done";
    String conected = "connected";
    String conexion = "";
    String conexion_tiempo = "";
    String horario = "";
    String matricula = "";
    Boolean reportado = false;
    String reportes = "";

    public Horario(String horario, String matricula) {
        this.horario = horario;
        this.matricula = matricula;
    }
}
