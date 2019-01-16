package name.joseland.mal.automation.scheduler.db;

import javax.persistence.*;

/**
 * A {@link TriggerConfig} subtype that stores a {@link CronExpression}.
 */
@Entity
@DiscriminatorValue("CRON")
public class CronTriggerConfig extends TriggerConfig {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sc_cron_expression_id")
    private CronExpression cronExpression;


    /* ********************************************************************************************************** */
    /* ********************************************* PUBLIC METHODS ********************************************* */
    /* ********************************************************************************************************** */


    @Override
    public String toString() {
        return "CronTriggerConfig id=" + getId() + ", cronExpression=" + getCronExpression().toString();
    }


    /* ********************************************************************************************************** */
    /* ********************************************* GETTERS/SETTERS ******************************************** */
    /* ********************************************************************************************************** */


    public CronExpression getCronExpression() {
        return cronExpression;
    }

    public void setCronExpression(CronExpression cronExpression) {
        this.cronExpression = cronExpression;
    }

}
