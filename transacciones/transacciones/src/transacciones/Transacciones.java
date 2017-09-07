package transacciones;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.typeadapters.RuntimeTypeAdapterFactory;

public class Transacciones {
    
    public static void main(String[] args) {
        RuntimeTypeAdapterFactory<Jsonable> rta = RuntimeTypeAdapterFactory.of(Jsonable.class, "_class")
                    .registerSubtype(Transaccion.class, "Transaccion");
        Gson gson = new GsonBuilder().registerTypeAdapterFactory(rta).setDateFormat("yyyy/MM/dd").create();
        DB db = new DB(); 
        db.guardar();
    }
}
