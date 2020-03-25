package utils;

import java.awt.Component;
import java.awt.Container;

/**
 * <code>java.lang.FunctionalInterface</code> wrapper for
 * <code>java.awt.Component</code> references.
 */
@FunctionalInterface
public interface JWrapper {

    /**
     * Self-reference pointer.
     *
     * @param component Component
     * @return Component
     */
    public abstract Component wrap(final Component component);

    /**
     * Add <code>java.awt.Component</code> to <code>java.awt.Container</code>.
     *
     * @param <T>      Container
     * @param <U>      Component
     * @param parent   T
     * @param children U...
     */
    @SafeVarargs
    public static <T extends Container, U extends Component> void add(final T parent, final U... children) {
        for (final U child : children) {
            parent.add(child);
        }
    }

    /**
     * Dispatch generic exception by printing in error output steam.
     *
     * @param e Exception
     */
    public static void dispatchException(Throwable e) {
        System.err.println(e.toString());
    }

}