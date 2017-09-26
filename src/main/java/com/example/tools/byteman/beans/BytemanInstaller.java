package com.example.tools.byteman.beans;

import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;
import org.springframework.boot.CommandLineRunner;

import java.io.File;
import java.io.FileFilter;
import java.lang.management.ManagementFactory;
import java.util.List;

public class BytemanInstaller implements CommandLineRunner {

    private final BytemanProperties props;
    private final WatcherManager watcherManager=new WatcherManager();
    public BytemanInstaller(BytemanProperties props) {
        this.props = props;
    }

    public void run(String... strings) throws Exception {
       /* SecurityManager sm = new BytemanSecurityManager( System.getSecurityManager() );
        System.setSecurityManager(sm);*/
        installByteman();
    }

    public void installByteman(){
        String pid = ManagementFactory.getRuntimeMXBean().getName().split("@")[0];
        org.jboss.byteman.agent.install.Install.main(new String[]{"-b", "-Dorg.jboss.byteman.transform.all", pid});
        List<String> scriptFolders = props.getScriptFolders();
        if(scriptFolders!=null&&!scriptFolders.isEmpty()){
            for (String folderName : scriptFolders) {
                watchFolder(folderName);
            }
        }

    }

    public void watchFolder(String folderName){
        File folder = new File(folderName);
        if(folder.exists()&&folder.isDirectory()){
            startWatcher(folder);
        }
    }

    public void startWatcher(File folder ){

        try {
            FileAlterationObserver observer = new FileAlterationObserver(folder, new FileFilter() {
                public boolean accept(File pathname) {
                    return pathname.getName().toLowerCase().endsWith(".btm");
                }
            });
            FileAlterationMonitor monitor =
                    new FileAlterationMonitor(props.getPollingInterval());
            BytemanScriptWatcher watcher = new BytemanScriptWatcher(folder,watcherManager);
            watcherManager.addWatcher(watcher);
            watcher.installAll();
            observer.addListener(watcher);
            monitor.addObserver(observer);
            monitor.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
