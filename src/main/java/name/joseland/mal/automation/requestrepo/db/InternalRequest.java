package name.joseland.mal.automation.requestrepo.db;

import com.fasterxml.jackson.databind.JsonNode;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.springframework.http.HttpMethod;

import javax.persistence.*;
import javax.validation.constraints.Pattern;

/**
 * A request that can be sent within the automation environment. Allows services to specify hosts with a string ID eg.
 * with Eureka.
 * TODO add description
 */
@Entity
@Table(name = "rr_internal_request")
@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
public class InternalRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // aka. HTTP verb
    @Column(name = "http_method", nullable = false)
    @Enumerated(EnumType.STRING)
    private HttpMethod httpMethod;

    // eg. "data-source-monitor"
    @Column(name = "service_id", nullable = false, length = 64)
    @Pattern(regexp = "(?:(?:[a-z\\d]+-?[a-z\\d]+)*|(?:[a-z\\d]))")
    private String serviceId;

    // should start with "/", eg. "/http-monitors/2/perform-check"
    @Column(name = "resource", nullable = false)
	@Pattern(regexp = "(?:/(?:(?:[a-z\\d]+-?[a-z\\d]+)+|(?:[a-z\\d])))+")
    private String resource;

    @Column(name = "body", columnDefinition = "jsonb")
    @Type(type = "jsonb")
    private JsonNode body;


    /* ********************************************************************************************************** */
    /* ********************************************* PUBLIC METHODS ********************************************* */
    /* ********************************************************************************************************** */


    @Override
    public String toString() {
        // body too long to include
        return "InternalRequest id=" + getId() + ", httpMethod=" + getHttpMethod().name() +
                ", serviceId=" + getServiceId() + ", resource=" + getResource();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;

        if (!(o instanceof InternalRequest))
            return false;

        return getId() != null && getId().equals(((InternalRequest) o).getId());
    }

    @Override
    public int hashCode() {
        if (getId() != null)
            return getId();

        return System.identityHashCode(this);
    }


    /* ********************************************************************************************************** */
    /* ********************************************* GETTERS/SETTERS ******************************************** */
    /* ********************************************************************************************************** */


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
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
