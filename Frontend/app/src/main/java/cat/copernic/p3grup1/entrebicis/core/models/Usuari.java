package cat.copernic.p3grup1.entrebicis.core.models;

import androidx.annotation.Size;

import com.google.gson.annotations.SerializedName;

import org.intellij.lang.annotations.Pattern;

import java.util.List;

import cat.copernic.p3grup1.entrebicis.core.enums.Rol;

public class Usuari {

    private String email;
    //aquesta es la password
    private String paraula;
    private String nom;
    private Rol rol;
    @SerializedName("imatgeBase64")
    private String  imatge;
    private double saldo;
    private String observacions;
    private String mobil;
    private String poblacio;

    public Usuari() {
    }

    public Usuari(String email, String paraula, String nom, Rol rol, String  imatge, double saldo, String observacions, String mobil, String poblacio) {
        this.email = email;
        this.paraula = paraula;
        this.nom = nom;
        this.rol = rol;
        this.imatge = imatge;
        this.saldo = saldo;
        this.observacions = observacions;
        this.mobil = mobil;
        this.poblacio = poblacio;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getParaula() {
        return paraula;
    }

    public void setParaula(String paraula) {
        this.paraula = paraula;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public Rol getRol() {
        return rol;
    }

    public void setRol(Rol rol) {
        this.rol = rol;
    }

    public String  getImatge() {
        return imatge;
    }

    public void setImatge(String  imatge) {
        this.imatge = imatge;
    }

    public double getSaldo() {
        return saldo;
    }

    public void setSaldo(double saldo) {
        this.saldo = saldo;
    }

    public String getObservacions() {
        return observacions;
    }

    public void setObservacions(String observacions) {
        this.observacions = observacions;
    }

    public String getMobil() {
        return mobil;
    }

    public void setMobil(String mobil) {
        this.mobil = mobil;
    }

    public String getPoblacio() {
        return poblacio;
    }

    public void setPoblacio(String poblacio) {
        this.poblacio = poblacio;
    }

}
