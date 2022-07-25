package org.codelab.batch.job.partitioner;

import java.util.HashMap;
import java.util.Map;

import org.mybatis.spring.SqlSessionTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.item.ExecutionContext;

public class RangePartitioner implements Partitioner {

	private static final Logger log = LoggerFactory.getLogger(RangePartitioner.class);

	private String queryId;
	private SqlSessionTemplate sqlSessionTemplate;
	private Map<String, Object> param;
	
	public RangePartitioner() {}
	
	public RangePartitioner(SqlSessionTemplate sqlSessionTemplate, String queryId) {
		this.sqlSessionTemplate = sqlSessionTemplate;
		this.queryId = queryId;
	}
	   
	@Override
	public Map<String, ExecutionContext> partition(int gridSize) {
		Map<String, ExecutionContext> result = new HashMap<>();
		int total = sqlSessionTemplate.<Integer> selectOne(queryId, param);
		int pageSize = (total / gridSize);
		int skipRows = 0;
		int loopSize = total % gridSize == 0 ? gridSize : gridSize +1;
		for(int i=1; i<=loopSize; i++) {
			ExecutionContext value = new ExecutionContext();
			if(pageSize == 0) {
				value.putInt("skipRows", skipRows);
				value.putInt("pageSize", gridSize);
				value.putString("name", "Thread" + i);
				result.put("partition"+i, value);
				break;
			}
			value.putInt("skipRows", skipRows);
			value.putInt("pageSize", pageSize);
			value.putString("name", "Thread"+i);
			result.put("partition"+i, value);
			skipRows += pageSize;
		}
		return result;
	}
}
