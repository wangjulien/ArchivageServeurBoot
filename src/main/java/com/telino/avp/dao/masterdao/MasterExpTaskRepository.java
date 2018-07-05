package com.telino.avp.dao.masterdao;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.telino.avp.entity.auxil.ExpTask;
import com.telino.avp.protocol.DbEntityProtocol.ExpTaskState;

public interface MasterExpTaskRepository extends JpaRepository<ExpTask, UUID> {

	@Query(value = "select ts.* from exp_task ts join exp_task_type tp "
			+ "on ts.tasktypeid = tp.typeid and ts.nbtries >= tp.maxnbtries "
			+ "and tp.expirationtime < EXTRACT(EPOCH FROM current_timestamp - ts.datedeb)/60 "
			+ "where ts.state = :state", nativeQuery = true)
	public List<ExpTask> findExpiredExpTask(@Param("state") String state);
	

	@Query(value = "select ts.* from exp_task ts join exp_task_type tp "
			+ "on ts.tasktypeid = tp.typeid and ts.nbtries < tp.maxnbtries "
			+ "and tp.expirationtime < EXTRACT(EPOCH FROM current_timestamp - ts.datedeb)/60 "
			+ "where ts.state = :state", nativeQuery = true)
	public List<ExpTask> findToRelauchByStatusAndExpired(@Param("state") String state);

	public List<ExpTask> findByStateAndTaskTypeIdIsNot(ExpTaskState state, Long typeId);
}
