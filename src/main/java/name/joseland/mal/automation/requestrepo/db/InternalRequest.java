package name.joseland.mal.automation.requestrepo.db;

import com.fasterxml.jackson.databind.JsonNode;
import org.hibernate.annotations.Type;
import org.springframework.http.HttpMethod;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "rr_internal_request")
public class InternalRequest {

    @Id
    private int id;

    // aka. HTTP verb
    @Column(name = "http_method")
    private HttpMethod httpMethod;

    // eg. "scheduler"
    @Column(name = "service_id")
    private String serviceId;

    // should start with "/", eg. "/cron-expressions/2"
    @Column(name = "resource_id")
    private String resource;

    @Type(type = "jsonb")
    @Column(name = "body", columnDefinition = "jsonb")
    private JsonNode body;




    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public void setHttpMethod(HttpMethod httpMethod) {
        this.httpMethod = httpMethod;
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public String getResource() {
        return resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }

    public JsonNode getBody() {
        return body;
    }

    public void setBody(JsonNode body) {
        this.body = body;
    }

}
