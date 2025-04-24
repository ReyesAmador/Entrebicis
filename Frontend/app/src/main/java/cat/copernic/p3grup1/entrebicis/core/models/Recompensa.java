package cat.copernic.p3grup1.entrebicis.core.models;

import java.time.LocalDate;

import cat.copernic.p3grup1.entrebicis.core.enums.EstatRecompensa;

public class Recompensa {

    private Long id;
    private String descripcio;
    private String observacions;
    private double valor;
    private EstatRecompensa estat;
    private String puntRecollida;
    private String direccio;
    private LocalDate dataCreacio;
    private LocalDate dataReserva;
    private LocalDate dataRecollida;
    private LocalDate dataAssignacio;
    private Usuari usuari;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescripcio() {
        return descripcio;
    }

    public void setDescripcio(String descripcio) {
        this.descripcio = descripcio;
    }

    public String getObservacions() {
        return observacions;
    }

    public void setObservacions(String observacions) {
        this.observacions = observacions;
    }

    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }

    public EstatRecompensa getEstat() {
        return estat;
    }

    public void setEstat(EstatRecompensa estat) {
        this.estat = estat;
    }

    public String getPuntRecollida() {
        return puntRecollida;
    }

    public void setPuntRecollida(String puntRecollida) {
        this.puntRecollida = puntRecollida;
    }

    public String getDireccio() {
        return direccio;
    }

    public void setDireccio(String direccio) {
        this.direccio = direccio;
    }

    public LocalDate getDataAssignacio() {
        return dataAssignacio;
    }

    public void setDataAssignacio(LocalDate dataAssignacio) {
        this.dataAssignacio = dataAssignacio;
    }

    public Usuari getUsuari() {
        return usuari;
    }

    public void setUsuari(Usuari usuari) {
        this.usuari = usuari;
    }

    public LocalDate getDataCreacio() {
        return dataCreacio;
    }

    public void setDataCreacio(LocalDate dataCreacio) {
        this.dataCreacio = dataCreacio;
    }

    public LocalDate getDataReserva() {
        return dataReserva;
    }

    public void setDataReserva(LocalDate dataReserva) {
        this.dataReserva = dataReserva;
    }

    public LocalDate getDataRecollida() {
        return dataRecollida;
    }

    public void setDataRecollida(LocalDate dataRecollida) {
        this.dataRecollida = dataRecollida;
    }
}
