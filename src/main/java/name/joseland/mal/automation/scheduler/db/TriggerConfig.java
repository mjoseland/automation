package name.joseland.mal.automation.scheduler.db;

import javax.persistence.*;

/**
 * Stores configuration that can be used to create a {@link org.springframework.scheduling.Trigger}.
 */
@Entity
@Table(name = "sc_trigger_config")
public class TriggerConfig {

    // when a type is added, fix the test params in TriggerConfigTest
    public enum Type {
        CRON
    }


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "itype")
    @Enumerated(EnumType.STRING)
    private Type type;

    @Column(name = "cron_expression")
    private String cronExpression;


    /* ********************************************************************************************************** */
    /* ********************************************* PUBLIC METHODS ********************************************* */
    /* ********************************************************************************************************** */

    @Override
    public String toString() {
        return "TriggerConfig id=" + getId() + ", type=" + getType().name() + ", cronExpression=" + getCronExpression();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;

        if (!(o instanceof TriggerConfig))
            return false;

        return getId() != null && getId().equals(((TriggerConfig) o).getId());
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

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public String getCronExpression() {
        return cronExpression;
    }

    public void setCronExpression(String cronExpression) {
        this.cronExpression = cronExpression;
    }

}
