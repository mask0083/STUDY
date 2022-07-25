package org.codelab.batch.job.ipfilter;

import java.util.HashMap;
import java.util.Map;

import org.codelab.batch.dto.IpAddr;
import org.springframework.batch.item.ItemWriter;
import org.springframework.classify.Classifier;

public class IpClassfier implements Classifier<IpAddr, ItemWriter<IpAddr>> {
	private Map<Boolean, ItemWriter<IpAddr>> writerMap = new HashMap<>();

	@Override
	public ItemWriter<IpAddr> classify(IpAddr ipInfo) {
		return writerMap.get(ipInfo.isFiltered());
	}

	public void setWriterMap(Map<Boolean, ItemWriter<IpAddr>> writerMap) {
		this.writerMap = writerMap;
	}
}
