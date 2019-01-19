package name.joseland.mal.automation.scheduler.db;

import javax.persistence.*;

/**
 * Mapped DB entity for the table scheduler.sc_scheduled_request_config. Stores the config required to schedule sending
 * a HTTP request with Spring's scheduling API (see: {@link org.springframework.scheduling.Trigger}.
 */
@Entity
@Table(name = "sc_scheduled_request_config")
public class ScheduledRequestConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trigger_config_id")
    private TriggerConfig triggerConfig;

    // appended to ${automation.request-repository.path} to form uri of a HTTP request
    // eg. "/internal-requests/52/assembled"
    @Column(name = "request_repository_mapping")
    private String requestRepositoryMapping;


    /* ********************************************************************************************************** */
    /* ********************************************* PUBLIC METHODS ********************************************* */
    /* ********************************************************************************************************** */


    @Override
    public String toString() {
        return "ScheduledRequestConfig id=" + getId() + ", triggerConfig=" + getTriggerConfig().toString() +
                ", requestRepositoryMapping=" + getRequestRepositoryMapping();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;

        if (!(o instanceof ScheduledRequestConfig))
            return false;

        return getId() != null && getId().equals(((ScheduledRequestConfig) o).getId());
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

    public TriggerConfig getTriggerConfig() {
        return triggerConfig;
    }

    public void setTriggerConfig(TriggerConfig triggerConfig) {
        this.triggerConfig = triggerConfig;
    }

    public String getRequestRepositoryMapping() {
        return requestRepositoryMapping;
    }

    public void setRequestRepositoryMapping(String requestRepositoryMapping) {
        this.requestRepositoryMapping = requestRepositoryMapping;
    }

}
