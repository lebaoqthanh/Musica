package assign11;



import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JPanel;

/**
 * A grid of rectangles that can be clicked and dragged to draw cells.
 * Creating or removing a cell results in some task being performed.
 * That task must be specified by the subclass.
 *
 * @author Eric Heisler and Thanh Le
 * @version 2024-11-9
 */
public abstract class GridCanvas extends JPanel implements MouseListener, MouseMotionListener {

	// Grid information
	private int width, height;
	private int columns, rows;
	private int columnMajorTickSpacing, rowMajorTickSpacing;
	private int rowRestriction, colRestriction;

	// The collection of cells (active, colored rectangles)
	// Note that Cell is a nested class at the end of this file
	private BetterDynamicArray<Cell> cells;

	// Feel free to change coloring however you want.
	// To get started, just set one color in the constructor.
	private Color cellColor;

	// These are useful for keeping track of a cell currently being drawn.
	private boolean drawing;
	private int currentRow, currentColumn, currentWidth, currentHeight;

	/**
	 * Construct a grid with a given configuration.
	 *
	 * @param width - of grid in pixels
	 * @param height - of grid in pixels
	 * @param rows - number of rows
	 * @param columns - number of columns
	 * @param rowMajorTicks - where darker lines will be drawn
	 * @param columnMajorTicks - where darker lines will be drawn
	 */
	public GridCanvas(int width, int height, int rows, int columns,
					  int rowMajorTickSpacing, int columnMajorTickSpacing) {
		this.width = width;
		this.height = height;
		this.rows = rows;
		this.columns = columns;
		this.rowMajorTickSpacing = rowMajorTickSpacing;
		this.columnMajorTickSpacing = columnMajorTickSpacing;

		cellColor = new Color(200, 30, 30); // change as desired
		drawing = false;
		currentRow = -1;
		currentColumn = -1;
		currentWidth = -1;
		currentHeight = -1;
		rowRestriction = -1;
		colRestriction = -1;
		cells = new BetterDynamicArray<Cell>();

		// TODO set preferred size, background color, and mouse listeners
		setPreferredSize(new Dimension(width, height));
		setBackground(Color.WHITE);
		this.addMouseListener(this);
		this.addMouseMotionListener(this);

	}

	/**
	 * Set the number of columns in the grid.
	 * Then repaint the component.
	 * If the number is < 1, set to 1.
	 *
	 * @param columns - new number of columns
	 */
	public void setColumns(int newColumns) {
		// TODO
		if (newColumns < 1) {
			this.columns = 1; // Ensure at least one column
		} else {
			this.columns = newColumns;
		}
		repaint(); // Redraw the component to reflect changes

	}

	/**
	 * Set the number of rows in the grid.
	 * Then repaint the component.
	 * If the number is < 1, set to 1.
	 *
	 * @param rows - new number of rows
	 */
	public void setRows(int newRows) {
		// TODO
		if(newRows < 1) {
			this.rows = 1;
		}else {
			this.rows = newRows;
		}
		repaint();
	}

	/**
	 * Set restrictions for the width or height of cells.
	 * Setting to a non-positive number means that dimension is not restricted.
	 *
	 * @param rowRestriction - height of cells or unrestricted if not positive
	 * @param colRestriction - width of cells or unrestricted if not positive
	 */
	public void setRestrictions(int rowRestriction, int colRestriction) {
		// TODO set the appropriate instance variables
		this.rowRestriction = rowRestriction;
		this.colRestriction = colRestriction;
	}

	/**
	 * Adds a cell to the collection with the given position and size.
	 * Then repaints the component.
	 * This is where colors can be set.
	 *
	 * @param row - vertical position of cell
	 * @param column - horizontal position of cell
	 * @param width - of cell
	 * @param height - of cell
	 */
	public void addCell(int row, int column, int height, int width) {
		// TODO construct Cell, add to collection, then repaint
		Cell newCell = new Cell(row, column, height, width, cellColor);

		// Add the new cell to the cells list
		cells.add(newCell);

		// Repaint the component to reflect the changes
		repaint();

	}

	/**
	 * Clears the collection of selected cells.
	 * Then repaints the component.
	 */
	public void clear() {
		// TODO
		cells.clear();
		repaint();
	}

	/**
	 * This method is called by the system when a component needs to be painted.
	 * Which can be at one of three times: --when the component first appears --when
	 * the size of the component changes (including resizing by the user) --when
	 * repaint() is called
	 *
	 * Partially overrides the paintComponent method of JPanel.
	 *
	 * @param g -- graphics context to draw onto
	 */
	public void paintComponent(Graphics g) {

		super.paintComponent(g); // Call parent JPanel's paintComponent method to ensure proper component rendering

		// Draw the grid: loop through rows and columns to draw the grid lines
		width = getWidth();  // Get the width of the component
		height = getHeight(); // Get the height of the component
		g.setColor(Color.GRAY); // Set color for grid lines

		// Draw horizontal grid lines (one for each row)
		for (int i = 0; i <= rows; i++) {
			int y = rowToPixel(i); // Convert row index to pixel position
			g.drawLine(0, y, width, y); // Draw a horizontal line
		}

		// Draw vertical grid lines (one for each column)
		for (int i = 0; i <= columns; i++) {
			int x = colToPixel(i); // Convert column index to pixel position
			g.drawLine(x, 0, x, height); // Draw a vertical line
		}

		// Draw major row and column ticks (thicker lines to mark significant positions)
		g.setColor(Color.DARK_GRAY); // Set color for major ticks

		// Draw major horizontal ticks (for major rows based on spacing)
		for (int i = 0; i <= rows; i++) {
			if (i % rowMajorTickSpacing == 0) { // Only draw tick at major intervals
				int y = rowToPixel(i); // Convert row index to pixel position
				g.fillRect(0, y - 2, width, 4); // Draw a horizontal thick tick at this row
			}
		}

		// Draw major vertical ticks (for major columns based on spacing)
		for (int i = 0; i <= columns; i++) {
			if (i % columnMajorTickSpacing == 0) { // Only draw tick at major intervals
				int x = colToPixel(i); // Convert column index to pixel position
				g.fillRect(x - 2, 0, 4, height); // Draw a vertical thick tick at this column
			}
		}

		// Draw all cells in the collection (render all the filled cells)
		for (int i = 0; i < cells.size(); i++) {
			// For each cell, convert row and column to pixel positions, and calculate its width and height
			int x = colToPixel(cells.get(i).col);
			int y = rowToPixel(cells.get(i).row);
			int width = colToPixel(cells.get(i).col + cells.get(i).colSpan) - x; // Calculate the width of the cell
			int height = rowToPixel(cells.get(i).row + cells.get(i).rowSpan) - y; // Calculate the height of the cell

			g.setColor(cells.get(i).color); // Set the color of the cell
			g.fillRect(x, y + height, width, -height); // Draw the cell as a filled rectangle
		}

		// If the mouse is currently drawing a cell, show a preview of the current cell being drawn
		if (drawing) {
			g.setColor(cellColor); // Set the color for the preview cell
			// Calculate the dimensions of the preview cell based on current position and size
			int x = colToPixel(currentColumn);
			int y = rowToPixel(currentRow);
			int width = colToPixel(currentColumn + currentWidth) - x; // Width of the preview cell
			int height = rowToPixel(currentRow - currentHeight) - y; // Height of the preview cell

			g.fillRect(x, y - height, width, height); // Draw the preview of the cell
		}
	}


	//////////////////////////////////////////////////////////////////////
	// Abstract methods to be implemented in SongEditor and TrackEditor.
	//////////////////////////////////////////////////////////////////////

	/**
	 * This is called when a mouse button is pressed on a given cell.
	 * This is NOT for the MouseListener interface. It will be implemented in
	 * your subclasses.
	 *
	 * @param row - vertical position index of cell
	 * @param col - horizontal position index of cell
	 * @param rowSpan - number of rows currently selected (height)
	 * @param colSpan - number of columns currently selected (width)
	 */
	public abstract void onCellPressed(int row, int col, int rowSpan, int colSpan);

	/**
	 * This is called when a mouse is dragged onto a given cell while the buton is pressed.
	 * This is NOT for the MouseMotionListener interface. It will be implemented in
	 * your subclasses.
	 *
	 * @param row - vertical position index of cell
	 * @param col - horizontal position index of cell
	 * @param rowSpan - number of rows currently selected (height)
	 * @param colSpan - number of columns currently selected (width)
	 */
	public abstract void onCellDragged(int row, int col, int rowSpan, int colSpan);

	/**
	 * This is called when a mouse is released on a given cell.
	 * This is NOT for the MouseListener interface. It will be implemented in
	 * your subclasses.
	 *
	 * @param row - vertical position index of cell
	 * @param col - horizontal position index of cell
	 * @param rowSpan - number of rows currently selected (height)
	 * @param colSpan - number of columns currently selected (width)
	 */
	public abstract void onCellReleased(int row, int col, int rowSpan, int colSpan);

	/**
	 * This is called when a cell is removed from the collection.
	 *
	 * @param row - index of cell removed
	 * @param col - index of cell removed
	 */
	public abstract void onCellRemoved(int row, int col);

	//////////////////////////////////////////////////////////////////////
	// The following are methods from the mouse listening interfaces.
	// They are used for drawing cells. Note that they each call one
	// of the abstract methods.
	//////////////////////////////////////////////////////////////////////

	/**
	 * This is called when a mouse button is pressed.
	 * Initialize values for a preview cell and call
	 * onCellPressed before repainting.
	 */
	@Override
	public void mousePressed(MouseEvent e) {
		if (e.getButton() == 1) {
			drawing = true;
			currentRow = pixelToRow(e.getY());
			currentColumn = pixelToCol(e.getX());
			currentHeight = Math.max(1, rowRestriction);
			currentWidth = Math.max(1, colRestriction);
			onCellPressed(currentRow, currentColumn, currentHeight, currentWidth);
			repaint();
		}
	}

	/**
	 * This is called when a mouse is moved while a button is pressed.
	 * Update values for the preview cell and call
	 * onCellDragged before repainting.
	 */
	@Override
	public void mouseDragged(MouseEvent e) {
		if (drawing) {
			if(rowRestriction > 0) {
				currentRow = pixelToRow(e.getY());
				currentHeight = Math.max(1, rowRestriction);
			}
			else
				currentHeight = pixelToRow(e.getY()) - currentRow + 1;
			if(colRestriction > 0) {
				currentColumn = pixelToCol(e.getX());
				currentWidth = Math.max(1, colRestriction);
			}
			else
				currentWidth = pixelToCol(e.getX()) - currentColumn + 1;
			onCellDragged(currentRow, currentColumn, currentHeight, currentWidth);
		}
		repaint();
	}

	/**
	 * This is called when a mouse button is released.
	 * If there is a valid preview cell, add it to the collection and call
	 * onCellReleased before repainting.
	 */
	@Override
	public void mouseReleased(MouseEvent e) {
		if (drawing) {
			if (currentHeight > 0 && currentWidth > 0) {
				addCell(currentRow, currentColumn, currentHeight, currentWidth);
			}
			onCellReleased(currentRow, currentColumn, currentHeight, currentWidth);
			currentRow = -1;
			currentColumn = -1;
			currentWidth = -1;
			currentHeight = -1;
			drawing = false;
			repaint();
		}
	}

	/**
	 * This is called when a mouse button is clicked.
	 * This is specifically for removal of a cell when any mouse button
	 * other than the left button is clicked. All cells matching this
	 * position are removed and onCellRemoved is called before repainting.
	 */
	@Override
	public void mouseClicked(MouseEvent e) {
		if(e.getButton() > 1) {
			// remove all cells from the list with this row and column
			int row = pixelToRow(e.getY());
			int col = pixelToCol(e.getX());
			for(int i = 0; i < cells.size(); i++)
				if(cells.get(i).row == row && cells.get(i).col == col) {
					cells.remove(i);
					i--;
				}
			onCellRemoved(row, col);
			repaint();
		}
	}

	// You can use the following methods in your project if you want.
	// They are not required for this assignment.

	@Override
	public void mouseEntered(MouseEvent e) {} // currently unused

	@Override
	public void mouseExited(MouseEvent e) {} // currently unused

	@Override
	public void mouseMoved(MouseEvent e) {} // currently unused

	//////////////////////////////////////////////////////////////////////
	// Private helper methods and Cell class below.
	// You can modify if desired, but you don't need to.
	//////////////////////////////////////////////////////////////////////

	/**
	 * Converts a row index to pixel y value of the BOTTOM edge of the row.
	 *
	 * @param row - index
	 * @return pixel y value of the top edge
	 */
	private int rowToPixel(int row) {
		return height - row * height / rows;
	}

	/**
	 * Converts a column index to pixel x value of the left side of the column.
	 *
	 * @param col - column index
	 * @return pixel x value of the left side
	 */
	private int colToPixel(int col) {
		return col * width / (columns);
	}

	/**
	 * Converts a pixel y value to a row index.
	 *
	 * @param py - pixel y value
	 * @return index of row containing that pixel
	 */
	private int pixelToRow(int py) {
		return rows * (height - 1 - py) / height;
	}

	/**
	 * Converts a pixel x value to a column index.
	 *
	 * @param px - pixel x value
	 * @return index of column containing that pixel
	 */
	private int pixelToCol(int px) {
		return columns * px / width;
	}

	/**
	 * Represents a colored cell in the grid.
	 */
	private class Cell {
		public int row;
		public int col;
		public int rowSpan;
		public int colSpan;
		public Color color;

		/**
		 * Constructs a cell object
		 * @param row - vertical position
		 * @param col - horizontal position
		 * @param rowSpan - height
		 * @param colSpan - width
		 * @param color - of cell
		 */
		public Cell(int row, int col, int rowSpan, int colSpan, Color color) {
			this.row = row;
			this.col = col;
			this.color = color;
			this.rowSpan = rowSpan;
			this.colSpan = colSpan;
		}
	}

	// Required by a serializable class (ignore for now)
	private static final long serialVersionUID = 1L;
}