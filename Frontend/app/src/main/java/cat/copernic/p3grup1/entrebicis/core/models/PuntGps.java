package cat.copernic.p3grup1.entrebicis.core.models;

public class PuntGps {

    private Long id;

    private Double latitud;

    private Double longitud;

    private Long temps;

    private Long idRuta;

    public PuntGps() {
    }

    public PuntGps(Long id, Double latitud, Double longitud, Long temps, Long idRuta) {
        this.id = id;
        this.latitud = latitud;
        this.longitud = longitud;
        this.temps = temps;
        this.idRuta = idRuta;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getLatitud() {
        return latitud;
    }

    public void setLatitud(Double latitud) {
        this.latitud = latitud;
    }

    public Double getLongitud() {
        return longitud;
    }

    public void setLongitud(Double longitud) {
        this.longitud = longitud;
    }

    public Long getTemps() {
        return temps;
    }

    public void setTemps(Long temps) {
        this.temps = temps;
    }

    public Long getIdRuta() {
        return idRuta;
    }

    public void setIdRuta(Long idRuta) {
        this.idRuta = idRuta;
    }
}
