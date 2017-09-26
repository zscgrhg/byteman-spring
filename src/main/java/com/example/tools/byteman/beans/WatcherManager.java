package com.example.tools.byteman.beans;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class WatcherManager {
    final List<BytemanScriptWatcher> watchers=new CopyOnWriteArrayList<BytemanScriptWatcher>();

    public void addWatcher(BytemanScriptWatcher watcher){
        watchers.add(watcher);
    }

    public void clearBTM() {
        try {
            org.jboss.byteman.agent.submit.Submit.main(new String[]{"-u"});
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    public void resetAll(){
        clearBTM();
        for (BytemanScriptWatcher watcher : watchers) {
            watcher.installAll();
        }
    }
}
