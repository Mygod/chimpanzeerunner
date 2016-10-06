package be.mygod.chimpanzeerunner.action;

public abstract class AbstractAction {
    public abstract void perform();

    @Override
    public int hashCode() {
        return toString().hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AbstractAction)) return false;
        AbstractAction that = (AbstractAction) o;
        return toString().equals(that.toString());
    }

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }
}
