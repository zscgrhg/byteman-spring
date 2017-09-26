package com.example.tools.byteman.beans;

import org.apache.commons.io.monitor.FileAlterationListenerAdaptor;

import java.io.File;

public class BytemanScriptWatcher extends FileAlterationListenerAdaptor {
    private final File folder;
    private final WatcherManager manager;

    public BytemanScriptWatcher(File folder,WatcherManager manager) {
        this.folder = folder;
        this.manager=manager;
    }

    @Override
    public void onFileCreate(File file) {
        manager.resetAll();
    }

    // Is triggered when a file is deleted from the monitored folder
    @Override
    public void onFileDelete(File file) {
        manager.resetAll();
    }

    @Override
    public void onFileChange(File file) {
        manager.resetAll();
    }



    public void installAll() {

        try {
            for (File file : folder.listFiles()) {
                installBTM(file);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }



    public void installBTM(File file) {
        try {
            String btm = file.getCanonicalPath();
            if (btm.endsWith(".btm")) {
                org.jboss.byteman.agent.submit.Submit.main(new String[]{"-l", btm});
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
