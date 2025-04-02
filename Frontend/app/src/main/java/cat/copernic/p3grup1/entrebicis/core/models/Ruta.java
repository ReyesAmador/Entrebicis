package cat.copernic.p3grup1.entrebicis.core.models;

import java.time.LocalDateTime;
import java.util.List;

public class Ruta {

    private Long id;
    private Usuari usuari;
    private LocalDateTime inici;
    private LocalDateTime fi;
    private boolean estat;
    private boolean validada;
    private double km_total;
    private double velocitat_mitjana;
    private List<PuntGps> punts;

    public Ruta() {
    }

    public Ruta(Long id, Usuari usuari, LocalDateTime inici, LocalDateTime fi, boolean estat, boolean validada,
                double km_total, double velocitat_mitjana, List<PuntGps> punts) {
        this.id = id;
        this.usuari = usuari;
        this.inici = inici;
        this.fi = fi;
        this.estat = estat;
        this.validada = validada;
        this.km_total = km_total;
        this.velocitat_mitjana = velocitat_mitjana;
        this.punts = punts;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Usuari getUsuari() {
        return usuari;
    }

    public void setUsuari(Usuari usuari) {
        this.usuari = usuari;
    }

    public LocalDateTime getInici() {
        return inici;
    }

    public void setInici(LocalDateTime inici) {
        this.inici = inici;
    }

    public LocalDateTime getFi() {
        return fi;
    }

    public void setFi(LocalDateTime fi) {
        this.fi = fi;
    }

    public boolean isEstat() {
        return estat;
    }

    public void setEstat(boolean estat) {
        this.estat = estat;
    }

    public boolean isValidada() {
        return validada;
    }

    public void setValidada(boolean validada) {
        this.validada = validada;
    }

    public double getKm_total() {
        return km_total;
    }

    public void setKm_total(double km_total) {
        this.km_total = km_total;
    }

    public double getVelocitat_mitjana() {
        return velocitat_mitjana;
    }

    public void setVelocitat_mitjana(double velocitat_mitjana) {
        this.velocitat_mitjana = velocitat_mitjana;
    }

    public List<PuntGps> getPunts() {
        return punts;
    }

    public void setPunts(List<PuntGps> punts) {
        this.punts = punts;
    }
}
