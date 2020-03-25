package app.model;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Point;
import java.util.HashSet;
import java.util.Objects;

import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.border.EtchedBorder;

import app.controller.MazeDelegator;
import app.controller.components.AbstractCell.CellState;
import app.model.components.CellPanel;
import app.view.components.RangedSlider.BoundedRange;

/**
 * Graphical-User-Inteface (GUI) Model-View-Controller (MVC) architecture
 * pivotal <code>app.model.MazePanel</code> component, extending
 * <code>javax.swing.JPanel</code> and storing
 * <code>app.model.components.CellPanel</code>.
 *
 * @author JoshGoA
 * @version 0.1
 * @see javax.swing.JPanel JPanel
 * @see app.model.components.CellPanel CellPanel
 */
public class MazePanel extends JPanel {

    private static final long serialVersionUID = 1L;

    /**
     * Two-sided <code>app.controller.MazeDelegator</code>
     * <code>app.view.MazeFrame</code> interaction pipeline.
     *
     * @see app.controller.MazeDelegator MazeDelegator
     */
    private transient MazeDelegator delegator;

    /**
     * <code>app.model.MazePanel</code> dimension resizing
     * <code>app.view.components.RangedSlider.BoundedRange</code>.
     *
     * @see app.view.components.RangedSlider.BoundedRange BoundedRange
     */
    private final BoundedRange dimension = new BoundedRange(10, 50, 20);

    /**
     * Bi-dimensional <code>app.model.components.CellPanel</code> array.
     */
    private CellPanel[][] grid;

    /**
     * Grid starting <code>app.model.components.CellPanel</code> pointer.
     */
    private CellPanel start = null;

    /**
     * Grid ending <code>app.model.components.CellPanel</code> pointer.
     */
    private CellPanel end = null;

    /**
     * Current maze trasversal <code>app.model.PathFinder</code> algorithm.
     *
     * @see app.model.PathFinder PathFinder
     */
    private transient PathFinder pathfinder = new PathFinder.Dijkstra();

    /**
     * Current maze generation <code>app.model.Generator</code> algorithm.
     *
     * @see app.model.Generator Generator
     */
    private transient Generator generator = new Generator.BackTracker();

    {
        this.setBorder(new EtchedBorder(EtchedBorder.LOWERED));
        this.setBackground(Color.WHITE);
    }

    /**
     * Create a new two-sided <code>app.controller.MazeDelegator</code> interaction
     * <code>app.model.MazePanel</code> pipeline component.
     *
     * @param delegator MazeDelegator
     */
    public MazePanel(final MazeDelegator delegator) {
        this.setDelegator(delegator);
        this.setGrid(this.dimension.getValue(), this.dimension.getValue());
    }

    /**
     * Create a new isolated pipeline component.
     */
    public MazePanel() {
        this(null);
    }

    // TODO: Fix serialization
    public final void override(final MazePanel model) {
        // Remove previous components
        this.removeAll();
        // Update layout and grid
        this.setLayout(new GridLayout(model.getGrid().length, model.getGrid().length));
        this.grid = new CellPanel[model.getGrid().length][model.getGrid().length];
        // Initialize CellPanel with only seed
        for (int row = 0; row < model.getGrid().length; row++)
            for (int col = 0; col < model.getGrid().length; col++) {
                this.grid[row][col] = model.getGrid()[row][col];
                this.add(this.grid[row][col]);
            }
        // Update draw changes
        this.revalidate();
        this.repaint();
    }

    /**
     * Reset grid with identical row and column values.
     *
     * @throws ClassCastException if (!(getLayout() instanceof GridLayout))
     */
    public final void reset() throws ClassCastException {
        if (!(this.getLayout() instanceof GridLayout))
            throw new ClassCastException("GridLayout might not have been initialized");
        this.pathfinder.setIsRunning(false);
        this.setGrid(((GridLayout) this.getLayout()).getRows(), ((GridLayout) this.getLayout()).getColumns());
    }

    /**
     * Fire <code>app.model.components.CellPanel.clear()</code> event.
     */
    public final void clear() {
        if (this.pathfinder.getIsRunning() || this.start == null)
            return;
        this.start.clear();
    }

    /**
     * Return current <code>app.controller.MazeDelegator</code> instance.
     *
     * @return MazeDelegator
     */
    public final MazeDelegator getDelegator() {
        return this.delegator;
    }

    /**
     * Set current <code>app.controller.MazeDelegator</code> instance.
     *
     * @param delegator MazeDelegator
     */
    public final void setDelegator(final MazeDelegator delegator) {
        this.delegator = delegator;
    }

    /**
     * Request <code>app.view.MazeFrame.releaseCellPopup(CellPanel cell)</code>
     * event.
     *
     * @param cell CellPanel
     * @return JPopupMenu
     */
    public final JPopupMenu releaseCellPopup(final CellPanel cell) {
        return this.delegator.releaseCellPopup(cell);
    }

    /**
     * Return current dimension
     * <code>app.view.components.RangedSlider.BoundedRange</code>.
     *
     * @return BoundedRange
     */
    public final BoundedRange getDimension() {
        return this.dimension;
    }

    public final Dimension getEucliadeanDimension() {
        return new Dimension(this.dimension.getValue(), this.dimension.getValue());
    }

    /**
     * Set current dimension
     * <code>app.view.components.RangedSlider.BoundedRange</code> value and fire
     * <code>app.model.MazePanel.setGrid(int rows, int cols)</code> event.
     *
     * @param val int
     */
    public final void setDimension(final int val) {
        this.dimension.setValue(val);
        this.setGrid(this.dimension.getValue(), this.dimension.getValue());
        this.reset();
    }

    /**
     * Return current grid <code>app.model.components.CellPanel</code> structure.
     *
     * @return CellPanel[][]
     */
    public final CellPanel[][] getGrid() {
        return this.grid;
    }

    /**
     * Set current grid <code>app.model.components.CellPanel</code> row and column
     * structure.
     *
     * @param rows int
     * @param cols int
     * @throws NegativeArraySizeException if (rows < 0 || cols < 0)
     */
    public final void setGrid(final int rows, final int cols) throws NegativeArraySizeException {
        if (rows < 0 || cols < 0)
            throw new NegativeArraySizeException("Invalid size...");
        // Remove previous components
        this.removeAll();
        // Update layout and grid
        this.setLayout(new GridLayout(rows, cols));
        this.grid = new CellPanel[rows][cols];
        // Initialize CellPanel with only seed
        for (int row = 0; row < rows; row++)
            for (int col = 0; col < cols; col++) {
                this.grid[row][col] = new CellPanel(this, new Point(row, col));
                this.add(this.grid[row][col]);
            }
        // Set CellPanel neighbors
        for (int row = 0; row < rows; row++)
            for (int col = 0; col < cols; col++) {
                // TODO: Add diagonals by calling delegator
                final int CellPanelRow = row;
                final int CellPanelCol = col;
                this.grid[row][col].setNeighbors(new HashSet<CellPanel>() {
                    private static final long serialVersionUID = 1L;
                    {
                        if (CellPanelRow - 1 >= 0)
                            this.add(grid[CellPanelRow - 1][CellPanelCol]);
                        if (CellPanelCol + 1 < cols)
                            this.add(grid[CellPanelRow][CellPanelCol + 1]);
                        if (CellPanelRow + 1 < rows)
                            this.add(grid[CellPanelRow + 1][CellPanelCol]);
                        if (CellPanelCol - 1 >= 0)
                            this.add(grid[CellPanelRow][CellPanelCol - 1]);
                    }
                });
            }
        // Update draw changes
        this.revalidate();
        this.repaint();
    }

    /**
     * Return current grid starting <code>app.model.components.CellPanel</code>
     * pointer.
     *
     * @return CellPanel
     */
    public final CellPanel getStart() {
        return this.start;
    }

    /**
     * Set current grid starting <code>app.model.components.CellPanel</code>
     * pointer.
     *
     * @param start CellPanel
     */
    public final void setStart(final CellPanel start) {
        if (start == null)
            this.start = null;
        else
            // Override start
            if (this.start != null && !start.equals(this.start)) {
                start.setState(CellState.START);
                this.start.setState(CellState.EMPTY);
                this.start = start;
            } else
                // Delete start
                if (start.getState() == CellState.START) {
                    start.setState(CellState.EMPTY);
                    this.start = null;
                    // Set new start
                } else {
                    start.setState(CellState.START);
                    this.start = start;
                }
    }

    /**
     * Return current grid ending <code>app.model.components.CellPanel</code>
     * pointer.
     *
     * @return CellPanel
     */
    public final CellPanel getEnd() {
        return this.end;
    }

    /**
     * Set current grid ending <code>app.model.components.CellPanel</code> pointer.
     *
     * @param end CellPanel
     */
    public final void setEnd(final CellPanel end) {
        if (end == null)
            this.end = null;
        else
            // Override start
            if (this.end != null && !end.equals(this.end)) {
                end.setState(CellState.END);
                this.end.setState(CellState.EMPTY);
                this.end = end;
            } else
                // Delete start
                if (end.getState() == CellState.END) {
                    end.setState(CellState.EMPTY);
                    this.end = null;
                    // Set new start
                } else {
                    end.setState(CellState.END);
                    this.end = end;
                }
    }

    /**
     * Return current <code>app.model.PathFinder</code> instance.
     *
     * @return PathFinder
     */
    public final PathFinder getPathFinder() {
        return this.pathfinder;
    }

    /**
     * Set current <code>app.model.PathFinder</code> instance.
     */
    public final void setPathFinder(final PathFinder pathfinder) {
        this.pathfinder = Objects.requireNonNull(pathfinder, "'pathfinder' must not be null");
    }

    /**
     * Fire <code>app.model.PathFinder.awake(CellPanel[][] grid)</code> event.
     */
    public final void awakePathFinder() {
        this.clear();
        this.pathfinder.awake(this.getGrid(),
                (this.start != null) ? this.start.getSeed() : null,
                (this.end != null) ? this.start.getSeed() : null);
    }

    /**
     * Return current <code>app.model.Generator</code> instance.
     *
     * @return Generator
     */
    public final Generator getGenerator() {
        return this.generator;
    }

    /**
     * Set current <code>app.model.Generator</code> instance.
     */
    public final void setGenerator(final Generator generator) {
        this.generator = Objects.requireNonNull(generator, "'generator' must not be null");
    }

    /**
     * Fire <code>app.model.Generator.awake(CellPanel[][] grid)</code> event.
     */
    public final void awakeGenerator() {
        this.clear();
        this.generator.awake(this.getGrid(),
                (this.start != null) ? this.start.getSeed() : null,
                (this.end != null) ? this.start.getSeed() : null);
    }

}