package name.joseland.mal.automation.scheduler.db;

import javax.persistence.*;


/**
 * Mapped DB entity for the table scheduler.sc_http_request_config. Stores the config required to create and send a
 * single HTTP request.
 */
@Entity
@Table(name = "sc_http_request_config")
public class HttpRequestConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "url")
    private String url;

    @Column(name = "verb")
    private String verb;

    @Column(name = "body")
    private String body;


    /* ********************************************************************************************************** */
    /* ********************************************* PUBLIC METHODS ********************************************* */
    /* ********************************************************************************************************** */


    @Override
    public String toString() {
        return "HttpRequestConfig id=" + getId() + ", url=" + getUrl() + ", verb=" + getVerb();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;

        if (!(o instanceof HttpRequestConfig))
            return false;

        return getId() != null && getId().equals(((HttpRequestConfig) o).getId());
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

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getVerb() {
        return verb;
    }

    public void setVerb(String verb) {
        this.verb = verb;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

}
