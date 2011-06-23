package com.proserus.stocks.view.summaries;

import java.math.BigDecimal;
import java.util.Collection;

import javax.swing.table.AbstractTableModel;

import com.proserus.stocks.model.analysis.SymbolAnalysis;

public class PerformanceSymbolModel extends AbstractTableModel {
	private static final long serialVersionUID = 20080113L;

	public static final String[] COLUMN_NAMES = { "Symbol", "Cost Basis", "Mkt Value", "Mkt Growth (%)", "Capital Gain",
 "Dividend", "Dividend (%)", "Overall Return (%)", "Annual Rate Monthly Compound (%)" };

	private Object[] data = null;

	/**
	 * Default constructor to create the Table Model for the contractor table.
	 */
	public PerformanceSymbolModel() {
	}

	/**
	 * Replace the list of contractors to be displayed in the table.
	 * 
	 * @param data
	 *            Array of contractors (as Object).
	 * @see Contractor
	 */
	public void setData(final Collection<? extends SymbolAnalysis> col) {
		this.data = col.toArray(new SymbolAnalysis[0]);;
		fireTableDataChanged();
	}
	

	public SymbolAnalysis getSummary(final int rowIndex) {
		return (SymbolAnalysis) data[rowIndex];
	}

	/**
	 * Retrieves the number of column in the table.
	 */
	public int getColumnCount() {
		return COLUMN_NAMES.length;
	}

	/**
	 * Retrieves the number of row in the table.
	 */
	public int getRowCount() {
		if (data != null) {
			return data.length;
		} else {
			return 0;
		}
	}

	/**
	 * Retrieves the name of a specific column in the table.
	 * 
	 * @param columnIndex
	 *            The column number requested.
	 * @return The name to be displayed for that column.
	 */
	@Override
	public String getColumnName(final int columnIndex) {
		return COLUMN_NAMES[columnIndex];
	}

	/**
	 * Retrieves the value of a specific cell in the table.
	 * 
	 * @param rowIndex
	 *            The row number of the field requested.
	 * @param columnIndex
	 *            The column number of the field requested.
	 * @return The corresponding field of the contracotr at the specified row and column.
	 * @see Contractor
	 */
	public Object getValueAt(final int rowIndex, final int columnIndex) {
		return (getColValue((SymbolAnalysis) data[rowIndex], columnIndex));
	}

	/**
	 * Invoked when the table is displayed, to identify the value type
	 */
	@Override
	public Class<?> getColumnClass(int col) {
		switch (col) {
		case 0:
		case 1:
			return String.class;
		default:
			return BigDecimal.class;
		}
	}
	

	private Object getColValue(final SymbolAnalysis summary, final int column) {
		int i = 0;
		
		if (column == i++) {
			return summary.getSymbol().getTicker();
		} else if (column == i++) {
			return summary.getCurrentCost();
		} else if (column == i++) {
			return summary.getMarketValue();
		} else if (column == i++) {
			return summary.getMarketGrowth();
		} else if (column == i++) {
			return summary.getCapitalGain();
		} else if (column == i++) {
			return summary.getDividend();
		} else if (column == i++) {
			return summary.getDividendYield();
		} else if (column == i++) {
			return summary.getOverallReturn();
		} else if (column == i++) {
			return summary.getAnnualizedReturn();
		}
		throw new AssertionError();
	}
}