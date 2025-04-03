/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cat.copernic.entrebicis.logic;

import cat.copernic.entrebicis.dto.PuntGpsDTO;
import cat.copernic.entrebicis.entities.PuntGps;
import cat.copernic.entrebicis.entities.Ruta;
import cat.copernic.entrebicis.entities.Usuari;
import cat.copernic.entrebicis.repository.PuntGpsRepo;
import cat.copernic.entrebicis.repository.RutaRepo;
import cat.copernic.entrebicis.repository.UsuariRepo;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author reyes
 */
@Service
public class RutaLogic {
    
    @Autowired
    RutaRepo rutaRepo;
    
    @Autowired
    PuntGpsRepo puntRepo;
    
    @Autowired
    UsuariRepo usuariRepo;
    
    public List<Ruta> getAllRutes(){
        return rutaRepo.findAllByOrderByIdDesc();
    }
    
    public void afegirPuntGps(String email, PuntGpsDTO dto){
        
        Usuari usuari = usuariRepo.findById(email)
                .orElseThrow(() -> new RuntimeException("Usuari no trobat"));
        
        Ruta ruta = rutaRepo.findByUsuariAndEstatTrue(usuari)
                .orElseGet(() -> {
                    Ruta nova = new Ruta();
                    nova.setUsuari(usuari);
                    nova.setEstat(true);
                    nova.setInici(LocalDateTime.now());
                    return rutaRepo.save(nova);
                });
        
        PuntGps punt = new PuntGps();
        punt.setTemps(Instant.ofEpochMilli(dto.getTemps()).atZone(ZoneId.systemDefault()).toLocalDateTime());
        punt.setLatitud(dto.getLatitud());
        punt.setLongitud(dto.getLongitud());
        punt.setRuta(ruta);
        
        puntRepo.save(punt);
    }
    
    public void finalitzarRuta(String email){
        Usuari usuari = usuariRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuari no trobat"));
        
        Ruta ruta = rutaRepo.findByUsuariAndEstatTrue(usuari)
                .orElseThrow(() -> new RuntimeException("No hi ha cap ruta activa"));
        
        ruta.setEstat(false);
        ruta.setFi(LocalDateTime.now());
        
        double distancia = calcularDistanciaTotal(ruta);
        String temps = calcularTempsTotal(ruta);
        long segons = calcularTempsEnSegons(ruta);
        double velocitat = calcularVelocitatMitjana(distancia,segons);
        
        ruta.setKm_total(redondejar2Decimals(distancia));
        ruta.setTemps_total(temps);
        ruta.setVelocitat_mitjana(redondejar2Decimals(velocitat));
        
        rutaRepo.save(ruta);
    }
    
    private double calcularDistanciaTotal(Ruta ruta){
        
        List<PuntGps> punts = ruta.getPunts();
        
        double distancia = 0.0;
        for(int i = 1; i < punts.size(); i++){
            distancia += calcularDistanciaEntrePunts(punts.get(i - 1), punts.get(i));
        }
        
        return distancia; //en quilòmetres
    }
    
    private double calcularDistanciaEntrePunts(PuntGps p1, PuntGps p2){
        double R = 6371e3; // radi de la terra en metres
        /*Convierte las latitudes de ambos puntos de grados a radianes, 
        porque las funciones trigonométricas en Java usan radianes.*/
        double lat1 = Math.toRadians(p1.getLatitud());
        double lat2 = Math.toRadians(p2.getLatitud());
        /*Calcula la diferencia angular entre las latitudes y longitudes de 
        los dos puntos, también en radianes.*/
        double dLat = Math.toRadians(p2.getLatitud() - p1.getLatitud());
        double dLon = Math.toRadians(p2.getLongitud() - p1.getLongitud());

        /*Esta es la fórmula de Haversine.
        Calcula la "distancia angular" entre los dos puntos teniendo en cuenta la curvatura de la Tierra.
        Se basa en una fórmula trigonométrica para medir distancias en esferas.*/
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                   Math.cos(lat1) * Math.cos(lat2) *
                   Math.sin(dLon/2) * Math.sin(dLon/2);
        /*Convierte ese valor a en un ángulo central (en radianes).
        Esta parte completa la fórmula de Haversine.*/
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        /*Multiplica el ángulo central por el radio de la Tierra para obtener la distancia en metros.
        Divide por 1000.0 para devolver la distancia en kilómetros.*/
        return (R * c) / 1000.0; // retorna en quilòmetres
    }
    
    private long calcularTempsEnSegons(Ruta ruta){
        List<PuntGps> punts = ruta.getPunts();
        if(punts.isEmpty()) return 0;
        
        LocalDateTime inici = punts.get(0).getTemps();
        LocalDateTime fi = punts.get(punts.size() - 1).getTemps();
        
        long segons = Duration.between(inici, fi).getSeconds();
        
        return segons;
    }
    
    private String calcularTempsTotal (Ruta ruta){
        List<PuntGps> punts = ruta.getPunts();
        if(punts.isEmpty()) return "00:00:00";
        
        LocalDateTime inici = punts.get(0).getTemps();
        LocalDateTime fi = punts.get(punts.size() - 1).getTemps();
        
        Duration duracio = Duration.between(inici, fi);
        long hores = duracio.toHours();
        long minuts = duracio.toMinutes() %60;
        long segons = duracio.toSeconds() %60;
        
        return String.format("%02d:%02d:%02d", hores,minuts,segons);
    }
    
    private double calcularVelocitatMitjana(double distanciaKm, double tempsSegons){
        
        if(tempsSegons == 0) return 0.0;
        
        return (distanciaKm / tempsSegons) * 3600; //km/h
    }
    
    private double redondejar2Decimals(double valor) {
    return BigDecimal.valueOf(valor)
            .setScale(2, RoundingMode.HALF_UP)
            .doubleValue();
}
}
