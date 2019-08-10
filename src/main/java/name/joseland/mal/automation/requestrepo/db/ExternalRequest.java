package name.joseland.mal.automation.requestrepo.db;

import org.springframework.http.HttpMethod;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.MapKeyColumn;
import javax.persistence.Table;
import java.util.HashMap;
import java.util.Map;

/**
 * An HTTP request that can be sent to any URL.
 */
@Entity
@Table(name = "rr_external_request")
public class ExternalRequest {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(name = "description", nullable = false)
	private String description;

	// aka. HTTP verb
	@Column(name = "http_method", nullable = false)
	@Enumerated(EnumType.STRING)
	private HttpMethod httpMethod;

	@Column(name = "url", nullable = false)
	private String url;

	// eg. key="Accept", value="application/hal+json;charset=UTF-8"
	@ElementCollection
	@CollectionTable(name = "rr_external_request_header_field",
					 joinColumns = @JoinColumn(name = "rr_external_request_id"))
	@MapKeyColumn(name = "field")
	private Map<String, String> headerFields = new HashMap<>();

	@Column(name = "body")
	private String body;


	/* ********************************************************************************************************** */
	/* ********************************************* PUBLIC METHODS ********************************************* */
	/* ********************************************************************************************************** */


	@Override
	public String toString() {
		// body too long to include
		return "InternalRequest id=" + getId() + ", description=" + getDescription() +
				", httpMethod=" + getHttpMethod().name() + ", url=" + getUrl();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;

		if (!(o instanceof ExternalRequest))
			return false;

		return getId() != null && getId().equals(((ExternalRequest) o).getId());
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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public HttpMethod getHttpMethod() {
		return httpMethod;
	}

	public void setHttpMethod(HttpMethod httpMethod) {
		this.httpMethod = httpMethod;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Map<String, String> getHeaderFields() {
		return headerFields;
	}

	public void setHeaderFields(Map<String, String> headerFields) {
		this.headerFields = headerFields;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}
}
