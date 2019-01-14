package name.joseland.mal.automation.scheduler.db;

import javax.persistence.*;

/**
 * Mapped DB entity for table scheduler.sc_cron_expression. Stores a single cron expression to be used to create
 * Quartz cron triggers.
 *
 * see: http://www.quartz-scheduler.org/documentation/quartz-2.x/tutorials/crontrigger.html,
 *      {@link org.springframework.scheduling.support.CronTrigger}
 */
@Entity
@Table(name = "sc_cron_expression")
public class CronExpression {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "description")
    private String description;

    @Column(name = "expression")
    private String expression;


    /**************************************************************************************************************/
    /*********************************************** PUBLIC METHODS ***********************************************/
    /**************************************************************************************************************/


    @Override
    public String toString() {
        return "CronExpression id=" + getId() + ", description=" + getDescription() + ", expression=" + getExpression();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;

        if (!(o instanceof CronExpression))
            return false;

        return getId() != null && getId().equals(((CronExpression) o).getId());
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getExpression() {
        return expression;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }


}
