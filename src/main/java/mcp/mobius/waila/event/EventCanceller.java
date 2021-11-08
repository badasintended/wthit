package mcp.mobius.waila.event;

import mcp.mobius.waila.api.IEventListener;

public enum EventCanceller implements IEventListener.Canceller {

    INSTANCE;

    private boolean canceled = false;

    @Override
    public void cancel() {
        canceled = true;
    }

    public void setCanceled(boolean canceled) {
        this.canceled = canceled;
    }

    public boolean isCanceled() {
        return canceled;
    }

}
