package scouter.plugin.server.dump.thread;

import java.util.HashMap;
import java.util.Map;

import scouter.lang.TimeTypeEnum;
import scouter.lang.counters.CounterConstants;
import scouter.lang.pack.MapPack;
import scouter.lang.pack.ObjectPack;
import scouter.lang.pack.PerfCounterPack;
import scouter.lang.plugin.PluginConstants;
import scouter.lang.plugin.annotation.ServerPlugin;
import scouter.net.RequestCmd;
import scouter.server.Configure;
import scouter.server.CounterManager;
import scouter.server.Logger;
import scouter.server.core.AgentManager;
import scouter.server.netio.AgentCall;
import scouter.util.HashUtil;

public class ThreadDumpPlugin {
	
	private static Map<Integer, Long> cpuHighTimeMap = new HashMap<Integer, Long>();
	private static Map<Integer, Long> dumpTimeMap = new HashMap<Integer, Long>();
	private static Map<Integer, Integer> dumpCountMap = new HashMap<Integer, Integer>();

    final Configure conf = Configure.getInstance();

    @ServerPlugin(PluginConstants.PLUGIN_SERVER_COUNTER)
    public void counter(PerfCounterPack pack) {
        String objName = pack.objName;
        int objHash = HashUtil.hash(objName);
        String objType = null;
        String objFamily = null;

        try {
	        if (AgentManager.getAgent(objHash) != null) {
	        	objType = AgentManager.getAgent(objHash).objType;
	        }
	        
	        if (objType != null) {
	        	objFamily = CounterManager.getInstance().getCounterEngine().getObjectType(objType).getFamily().getName();
	        }
	        
	        // objFamily가 javaee인 경우
	        if (CounterConstants.FAMILY_JAVAEE.equals(objFamily)) {
	        	if (pack.timetype == TimeTypeEnum.REALTIME && pack.data.getFloat(CounterConstants.PROC_CPU) > 0f) {
	            	float maxCpu = new Float(conf.getValue("ext_plugin_dump_thread_threshold_cpu", "80.0"));
	
	            	// CPU 임계치를 초과한 경우
	            	if (pack.data.getFloat(CounterConstants.PROC_CPU) >= maxCpu) {
	            		long currentTime = System.currentTimeMillis();
	            		long firstTime = cpuHighTimeMap.get(objHash) == null ? currentTime : cpuHighTimeMap.get(objHash);
	            		long lastTime = dumpTimeMap.get(objHash) == null ? 0L : dumpTimeMap.get(objHash);
	            		int count = dumpCountMap.get(objHash) == null ? 0 : dumpCountMap.get(objHash);
	                	
	            		// CPU 임계치 초과 상태가 300초동안 지속될 경우 
	            		if (currentTime - firstTime > (1000 * conf.getInt("ext_plugin_dump_thread_cpu_high_duration", 300))) {
		            		// 5초 단위로 3회 스레드 덤프를 수행한다.
		            		if (currentTime - lastTime > (1000 * conf.getInt("ext_plugin_dump_thread_dump_interval", 5)) && count < conf.getInt("ext_plugin_dump_thread_dump_count", 3)) {
		                		println("[" + objName + "] Exceed the limit of cpu's threshold(" + maxCpu + "). ThreadDump will be generated.");
		                    	ObjectPack objectPack = AgentManager.getAgent(objHash);
		                    	MapPack mapPack = new MapPack();
		                    	mapPack.put("objHash", objHash);
		                        AgentCall.call(objectPack, RequestCmd.TRIGGER_THREAD_DUMP, mapPack);
		                        
		                        dumpTimeMap.put(objHash, currentTime);
		                        dumpCountMap.put(objHash, ++count);
		            		}
	            		}
	            		
	            		cpuHighTimeMap.put(objHash, firstTime);
	            	} else {
	            		cpuHighTimeMap.put(objHash, null);
	            		dumpTimeMap.put(objHash, 0L);
	            		dumpCountMap.put(objHash, 0);
	            	}
	        	}
	    	}
        } catch (Exception e) {
        	Logger.printStackTrace(e);
        }
    }

    private void println(Object o) {
        if (conf.getBoolean("ext_plugin_dump_thread_debug", false)) {
            Logger.println(o);
        }
    }
}
