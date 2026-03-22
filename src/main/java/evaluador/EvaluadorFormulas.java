/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package evaluador;

import listas.ListaDouble;
import operaciones.*;

/**
 * @author ALFREDO
 */
public class EvaluadorFormulas {
    private OperacionMatematica operacion;

    /**
     * Cuando creás un evaluador sin decirle nada, por defecto
     * asume que vas a sumar. Siempre hay que tener algo definido
     * para no arrancar en null.
     */
    public EvaluadorFormulas() {
        this.operacion = new Suma();
    }

    /**
     * Con esto cambiás la operación que se va a usar en el próximo
     * cálculo. Lo llama el parser cuando ya sabe si toca sumar,
     * restar, multiplicar o dividir.
     */
    public void setOperacion(OperacionMatematica op) {
        this.operacion = op;
    }

    /**
     * Ejecuta el cálculo con los valores que le pasás y la operación
     * que esté configurada en ese momento. Si alguien se olvidó de
     * setear la operación, lanza   un error antes de intentar.
     */
    public double calcular(ListaDouble valores) {
        if (operacion == null)
            throw new IllegalStateException("No se ha establecido una operación");
        return operacion.ejecutar(valores);
    }

    /**
     * Le pasás el texto de la fórmula y los valores
     * juntos, y él solo detecta qué operación usar leyendo palabras clave
     * como "suma", "resta", "mult" o "div". Útil cuando no querés
     * llamar setOperacion por separado.
     */
    public double evaluarFormula(String formula, ListaDouble valores) {
        if (formula.toLowerCase().contains("suma"))         setOperacion(new Suma());
        else if (formula.toLowerCase().contains("resta"))   setOperacion(new Resta());
        else if (formula.toLowerCase().contains("mult"))    setOperacion(new Multiplicacion());
        else if (formula.toLowerCase().contains("div"))     setOperacion(new Division());
        return calcular(valores);
    }
}