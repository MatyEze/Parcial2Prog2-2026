package cine.ui;

import cine.modelo.Butaca;
import cine.modelo.Sala;
import cine.modelo.TipoEntrada;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

// Datos que el usuario va cargando entre la selección de butacas y la confirmación.
// Se guardan el id de la función y los códigos de las butacas, no los objetos,
// porque después de un error al guardar el estado se recarga y esos objetos dejan de ser los vigentes.
public class ReservaEnCurso {

    private int idFuncion;
    // El orden de inserción es el orden en que se seleccionaron las butacas.
    private Map<String, TipoEntrada> tiposPorButaca;
    private String nombre;
    private String identificacion;

    public ReservaEnCurso(int idFuncion) {
        this.idFuncion = idFuncion;
        this.tiposPorButaca = new LinkedHashMap<>();
        this.nombre = "";
        this.identificacion = "";
    }

    public int getIdFuncion() {
        return idFuncion;
    }

    public void seleccionar(String codigo) {
        if (!tiposPorButaca.containsKey(codigo)) {
            tiposPorButaca.put(codigo, TipoEntrada.GENERAL);
        }
    }

    public void quitar(String codigo) {
        tiposPorButaca.remove(codigo);
    }

    public boolean estaSeleccionada(String codigo) {
        return tiposPorButaca.containsKey(codigo);
    }

    public List<String> getCodigos() {
        return new ArrayList<>(tiposPorButaca.keySet());
    }

    public int getCantidad() {
        return tiposPorButaca.size();
    }

    public TipoEntrada getTipo(String codigo) {
        return tiposPorButaca.get(codigo);
    }

    public void cambiarTipo(String codigo, TipoEntrada tipo) {
        if (tiposPorButaca.containsKey(codigo)) {
            tiposPorButaca.put(codigo, tipo);
        }
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getIdentificacion() {
        return identificacion;
    }

    public void setIdentificacion(String identificacion) {
        this.identificacion = identificacion;
    }

    // Arma el mapa que recibe el servicio con las butacas de la sala recién consultada,
    // respetando el orden de selección.
    public Map<Butaca, TipoEntrada> crearTiposPorButaca(Sala sala) {
        Map<Butaca, TipoEntrada> tipos = new LinkedHashMap<>();
        for (Map.Entry<String, TipoEntrada> par : tiposPorButaca.entrySet()) {
            tipos.put(buscarButaca(sala, par.getKey()), par.getValue());
        }
        return tipos;
    }

    // Si el código no está en la sala se devuelve null y el servicio rechaza la butaca.
    private Butaca buscarButaca(Sala sala, String codigo) {
        for (Butaca butaca : sala.getButacas()) {
            if (butaca.getCodigo().equals(codigo)) {
                return butaca;
            }
        }
        return null;
    }
}
