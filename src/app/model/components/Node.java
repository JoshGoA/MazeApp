package app.model.components;

import java.awt.Color;
import java.io.Serializable;
import java.util.Objects;

import app.controller.components.AbstractCell;
import app.controller.State;

/**
 * <code>app.controller.components.AbstractCell</code> helper for
 * <code>app.controller.components.AbstractAlgorithm</code> tasks, which
 * stores multiple parent and outer pointers of different iteration generations.
 *
 * @param <T> AbstractCell<T>
 * @see app.controller.components.AbstractCell AbstractCell
 * @see app.controller.components.AbstractAlgorithm AbstractAlgorithm
 */
public final class Node<T extends AbstractCell<T>> implements Serializable {

    /**
     * Enum representing state and implementing
     * <code>app.controller.State</code>.
     *
     * @see app.controller.State State
     */
    public static enum NodeState implements State<Color> {

        VISITED(Color.CYAN), GERMINATED(Color.BLUE), PATH(Color.YELLOW);

        /**
         * State <code>java.awt.Color</code> pointer.
         */
        private Color color;

        /**
         * Create a new state.
         *
         * @param color Color
         */
        private NodeState(final Color color) {
            this.color = color;
        }

        @Override
        public final Color getReference() {
            return this.color;
        }

        @Override
        public void setReference(final Color color) {
            this.color = Objects.requireNonNull(color, "'color' must not be null");
        }

    }

    private static final long serialVersionUID = 1L;

    /**
     * Previous generation <code>app.model.components.Node</code> parent pointer.
     *
     * @see app.model.components.Node#parent Recursion is fun!
     */
    private final Node<T> parent;

    /**
     * Enclosing <code>app.controller.components.AbstractCell</code> instance
     * outer pointer.
     */
    private final T outer;

    /**
     * Current <code>app.model.components.Node.NodeState</code> instance.
     */
    private NodeState state = NodeState.GERMINATED;

    /**
     * Enclose <code>app.model.components.Node</code> parent and outer.
     *
     * @param parent Node<T extends AbstractCell<T>>
     * @param outer  T extends AbstractCell<T>
     */
    public Node(final Node<T> parent, final T outer) {
        this.parent = parent;
        this.outer = outer;
    }

    /**
     * No-parent ancestor <code>app.model.components.Node</code>.
     *
     * @param outer T extends AbstractCell<T>
     */
    public Node(final T outer) {
        this(null, outer);
    }

    /**
     * Return previous generation <code>app.model.components.Node</code> parent
     * pointer.
     *
     * @return Node<T extends AbstractCell<T>>
     */
    public final Node<T> getParent() {
        return this.parent;
    }

    /**
     * Return enclosing <code>app.controller.components.AbstractCell</code> instance
     * outer pointer.
     *
     * @return T extends AbstractCell<T>
     */
    public final T getOuter() {
        return this.outer;
    }

    /**
     * Return current <code>app.model.components.Node.NodeState</code> instance.
     *
     * @return NodeState
     */
    public final NodeState getState() {
        return this.state;
    }

    /**
     * Set current <code>app.model.components.Node.NodeState</code> instance.
     *
     * @param state NodeState
     */
    public final void setState(final NodeState state) {
        this.state = Objects.requireNonNull(state, "'state' must not be null");
        this.outer.notifyChange();
    }

    @Override
    public final int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((this.outer == null) ? 0 : this.outer.hashCode());
        result = prime * result + ((this.parent == null) ? 0 : this.parent.hashCode());
        result = prime * result + ((this.state == null) ? 0 : this.state.hashCode());
        return result;
    }

    @Override
    public final boolean equals(final Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (this.getClass() != obj.getClass())
            return false;
        final Node<?> other = (Node<?>) obj;
        if (this.outer == null) {
            if (other.outer != null)
                return false;
        } else if (!this.outer.equals(other.outer))
            return false;
        if (this.parent == null) {
            if (other.parent != null)
                return false;
        } else if (!this.parent.equals(other.parent))
            return false;
        if (this.state != other.state)
            return false;
        return true;
    }

    @Override
    public final String toString() {
        return String.format("Node [state: %s]", this.state);
    }

}
