package evaluador;

import listas.ListaDouble;
import operaciones.*;

public class EvaluadorFormulas {
    private OperacionMatematica operacion;

    public EvaluadorFormulas() {
        this.operacion = new Suma();
    }

    public void setOperacion(OperacionMatematica op) {
        this.operacion = op;
    }

    public double calcular(ListaDouble valores) {
        if (operacion == null)
            throw new IllegalStateException("No se ha establecido una operación");
        return operacion.ejecutar(valores);
    }

    public double evaluarFormula(String formula, ListaDouble valores) {
        if (formula.toLowerCase().contains("suma"))
            setOperacion(new Suma());
        else if (formula.toLowerCase().contains("resta"))
            setOperacion(new Resta());
        else if (formula.toLowerCase().contains("mult"))
            setOperacion(new Multiplicacion());
        else if (formula.toLowerCase().contains("div"))
            setOperacion(new Division());
        return calcular(valores);
    }
}
