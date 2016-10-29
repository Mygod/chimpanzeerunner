package be.mygod.chimpanzeerunner.action;

public abstract class AbstractAction {
    public abstract boolean perform();

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public boolean equals(Object o) {
        return this == o || o != null && getClass() == o.getClass();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }
}
