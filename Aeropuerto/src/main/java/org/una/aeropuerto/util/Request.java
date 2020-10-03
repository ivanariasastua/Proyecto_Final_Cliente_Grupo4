package org.una.aeropuerto.util;

import java.util.Map;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

/**
 *
 * @author Dios
 */
public class Request {

    private Client client;
    private Invocation.Builder builder;
    private WebTarget webTarget;
    private Response response;
    private final String apiUrl = "http://localhost:8989/";

    public Request(String direccion) {
        this.client = ClientBuilder.newClient();
        this.webTarget = client.target(apiUrl + direccion);
        this.builder = webTarget.request(MediaType.APPLICATION_JSON);
        MultivaluedMap<String, Object> headers = new MultivaluedHashMap<>();
        headers.add("Content-Type", "application/json; charset=UTF-8");
        headers.add("Accept", "application/json");
        headers.add("Authorization", UserAuthenticated.getInstance().getToken());
        builder.headers(headers);
    }

    public Request(String direccion, String parametros, Map<String, Object> valores) {
        this.client = ClientBuilder.newClient();
        this.webTarget = client.target(apiUrl + direccion).path(parametros).resolveTemplates(valores);
        this.builder = webTarget.request(MediaType.APPLICATION_JSON);
        MultivaluedMap<String, Object> headers = new MultivaluedHashMap<>();
        headers.add("Content-Type", "application/json; charset=UTF-8");
        headers.add("Accept", "application/json");
        headers.add("Authorization", UserAuthenticated.getInstance().getToken());
        builder.headers(headers);
    }

    public void get() {
        response = builder.get();
    }

    public void post(Object clazz) {
        Entity<?> entity = Entity.entity(clazz, "application/json; charset=UTF-8");
        response = builder.post(entity);
    }

    public void put(Object clazz) {
        Entity<?> entity = Entity.entity(clazz, "application/json; charset=UTF-8");
        response = builder.put(entity);
    }

    public void delete() {
        response = builder.delete();
    }

    public int getStatus() {
        return response.getStatus();
    }

    public Boolean isError() {
        return getStatus() != HttpServletResponse.SC_OK && getStatus() != HttpServletResponse.SC_CREATED;
    }

    public String getError() {
        if (response.getStatus() != HttpServletResponse.SC_OK) {
            String mensaje;
            if (response.getMediaType().equals(MediaType.APPLICATION_JSON_TYPE)) {
                mensaje = response.readEntity(String.class);
            } else {
                mensaje = response.getStatusInfo().getReasonPhrase();
            }
            return mensaje;
        }
        return null;
    }
    
    public String getMensajeRespuesta(){
        if(response.getStatus() == HttpServletResponse.SC_NO_CONTENT)
            return "No existen registros";
        else if(response.getStatus() == HttpServletResponse.SC_INTERNAL_SERVER_ERROR)
            return "No se pudo comunicar con el servidor";
        else if(response.getStatus() == HttpServletResponse.SC_BAD_REQUEST)
            return "Las credenciales ingresadas no coinciden";
        else if(response.getStatus() == HttpServletResponse.SC_UNAUTHORIZED)
            return "Hubo problemas procesando su solicitud, el servidor rechazo la solicitud";
        else if(response.getStatus() == HttpServletResponse.SC_CREATED)
            return "El registro ha sido guardado con exito";
        else if(response.getStatus() == HttpServletResponse.SC_NOT_FOUND)
            return "No se encontraron coincidencias con el registro";
        else if(response.getStatus() == HttpServletResponse.SC_CONFLICT)
            return "Conflicto de datos, posiblemente exista un registro muy similar";
        else
            return "Error inesperado";
    }

    public Object readEntity(Class clazz) {
        return response.readEntity(clazz);
    }

    public Object readEntity(GenericType<?> genericType) {
        return response.readEntity(genericType);
    }
}
