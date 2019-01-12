package name.joseland.mal.automation.scheduler.db;

import javax.persistence.*;

/**
 * Stores configuration that can be used to create a {@link org.springframework.scheduling.Trigger}.
 */
@Entity
@Table(name = "sc_trigger_config")
@DiscriminatorColumn(name = "discriminator", discriminatorType = DiscriminatorType.STRING)
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public abstract class TriggerConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;


    /**************************************************************************************************************/
    /*********************************************** PUBLIC METHODS ***********************************************/
    /**************************************************************************************************************/


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


    /**************************************************************************************************************/
    /*********************************************** GETTERS/SETTERS **********************************************/
    /**************************************************************************************************************/


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

}
