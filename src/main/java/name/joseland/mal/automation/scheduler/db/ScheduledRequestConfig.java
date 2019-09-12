package name.joseland.mal.automation.scheduler.db;

import name.joseland.mal.automation.core.rest.out.internal.StoredRequestType;

import javax.persistence.*;

/**
 * Mapped DB entity for the table scheduler.sc_scheduled_request_config. Stores the config required to schedule sending
 * an internal HTTP request with Spring's scheduling API (see: {@link org.springframework.scheduling.Trigger}.
 *
 */
@Entity
@Table(name = "sc_scheduled_request_config")
public class ScheduledRequestConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "trigger_config_id")
    private TriggerConfig triggerConfig;

    @Column(name = "request_type")
    @Enumerated(EnumType.STRING)
    private StoredRequestType requestType;

    @Column(name = "request_id")
    private int requestId;


    /* ********************************************************************************************************** */
    /* ********************************************* PUBLIC METHODS ********************************************* */
    /* ********************************************************************************************************** */


    @Override
    public String toString() {
        return "ScheduledRequestConfig id=" + getId() + ", triggerConfig=" + getTriggerConfig().toString() +
                ", requestType=" + getRequestType().name() + ", requestId=" + getRequestId();
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

    public StoredRequestType getRequestType() {
        return requestType;
    }

    public void setRequestType(StoredRequestType requestType) {
        this.requestType = requestType;
    }

    public int getRequestId() {
        return requestId;
    }

    public void setRequestId(int requestId) {
        this.requestId = requestId;
    }

}
