package com.chaboshi.scf.server.filter;

import com.chaboshi.scf.server.contract.context.SCFContext;
import com.chaboshi.scf.server.contract.filter.IFilter;
import com.chaboshi.scf.server.contract.log.ILog;
import com.chaboshi.scf.server.contract.log.LogFactory;
import com.chaboshi.scf.server.util.IPTable;

public class IPFilter implements IFilter {

  private static ILog logger = LogFactory.getLogger(IPFilter.class);

  @Override
  public void filter(SCFContext context) throws Exception {
    if (IPTable.isAllow(context.getChannel().getRemoteIP())) {
      logger.info("new channel conected:" + context.getChannel().getRemoteIP());
    } else {
      logger.error("forbid ip not allow connect:" + context.getChannel().getRemoteIP());
      context.getChannel().close();
    }
  }

  @Override
  public int getPriority() {
    return 100;
  }

}