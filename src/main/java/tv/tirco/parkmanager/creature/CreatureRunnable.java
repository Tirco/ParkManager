package tv.tirco.parkmanager.creature;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

public abstract class CreatureRunnable implements Runnable
{
    private BukkitTask task;
    
    public BukkitTask runTask(final Plugin plugin) {
        this.checkNotYetScheduled();
        return this.setupTask(Bukkit.getScheduler().runTask(plugin, (Runnable)this));
    }
    
    public BukkitTask runTaskAsynchronously(final Plugin plugin) {
        this.checkNotYetScheduled();
        return this.setupTask(Bukkit.getScheduler().runTaskAsynchronously(plugin, (Runnable)this));
    }
    
    public BukkitTask runTaskLater(final Plugin plugin, final long n) {
        this.checkNotYetScheduled();
        return this.setupTask(Bukkit.getScheduler().runTaskLater(plugin, (Runnable)this, n));
    }
    
    public BukkitTask runTaskLaterAsynchronously(final Plugin plugin, final long n) {
        this.checkNotYetScheduled();
        return this.setupTask(Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, (Runnable)this, n));
    }
    
    public BukkitTask runTaskTimer(final Plugin plugin, final long n, final long n2) {
        this.checkNotYetScheduled();
        return this.setupTask(Bukkit.getScheduler().runTaskTimer(plugin, (Runnable)this, n, n2));
    }
    
    public BukkitTask runTaskTimerAsynchronously(final Plugin plugin, final long n, final long n2) {
        this.checkNotYetScheduled();
        return this.setupTask(Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, (Runnable)this, n, n2));
    }
    
    private BukkitTask setupTask(final BukkitTask task) {
        return this.task = task;
    }
    
    public void cancel() {
        if (this.isRunning()) {
            Bukkit.getScheduler().cancelTask(this.getTaskId());
            this.task = null;
        }
    }
    
    public boolean isRunning() {
        return this.task != null;
    }
    
    public int getTaskId() {
        this.checkScheduled();
        return this.task.getTaskId();
    }
    
    private void checkScheduled() {
        if (this.task == null) {
            throw new IllegalStateException("Not scheduled yet");
        }
    }
    
    private void checkNotYetScheduled() {
        if (this.task != null) {
            throw new IllegalStateException("Already scheduled as " + this.task.getTaskId());
        }
    }
}

