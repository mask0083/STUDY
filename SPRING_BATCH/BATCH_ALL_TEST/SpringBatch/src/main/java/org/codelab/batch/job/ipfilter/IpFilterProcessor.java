package org.codelab.batch.job.ipfilter;

import org.codelab.batch.dto.IpAddr;
import org.springframework.batch.item.ItemProcessor;

public class IpFilterProcessor implements ItemProcessor<IpAddr, IpAddr> {
	@Override
	public IpAddr process(IpAddr item) throws Exception {
		if (item.getIp().startsWith("210."))
			item.setFiltered(true);

		return item;
	}
}