package com.telino.avp.dao;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.telino.avp.dao.masterdao.MasterExpTaskRepository;
import com.telino.avp.dao.mirrordao.MirrorExpTaskRepository;
import com.telino.avp.entity.auxil.ExpComment;
import com.telino.avp.entity.auxil.ExpTask;
import com.telino.avp.protocol.DbEntityProtocol.ExpTaskState;

@Repository
@Transactional
public class ExpTaskDao {

	@Autowired
	private MasterExpTaskRepository masterExpTaskRepository;

	@Autowired
	private MirrorExpTaskRepository mirrorExpTaskRepository;

	public void saveExpTasks(List<ExpTask> tasks) {
		tasks.forEach(e -> setUUID(e));
		
		masterExpTaskRepository.saveAll(tasks);
		mirrorExpTaskRepository.saveAll(tasks);
	}

	public void saveExpTask(ExpTask task) {
		setUUID(task);
				
		masterExpTaskRepository.save(task);
		mirrorExpTaskRepository.save(task);
	}
	
	
	private void setUUID(final ExpTask task) {
		if (Objects.isNull(task.getTaskId()))
			task.setTaskId(UUID.randomUUID());
		
		for (ExpComment cm : task.getComments()) {
			if (Objects.isNull(cm.getComId()))
				cm.setComId(UUID.randomUUID());
		}
	}
	
	@Transactional(rollbackFor = Exception.class)
	public void updateExpTaskStateInBothDb(UUID taskId, ExpTaskState state) {
		Optional<ExpTask> optTask = masterExpTaskRepository.findById(taskId);
		if (optTask.isPresent()) {
			
			ExpTask expTask = optTask.get();
			expTask.setState(state);
			expTask.setDateFin(ZonedDateTime.now());
			saveExpTask(expTask);
		}
	}
}
