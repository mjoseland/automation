package name.joseland.mal.automation.scheduler.db;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

@Repository
@Transactional
public interface ScheduledRequestConfigRepository extends JpaRepository<ScheduledRequestConfig, Long> {

}
