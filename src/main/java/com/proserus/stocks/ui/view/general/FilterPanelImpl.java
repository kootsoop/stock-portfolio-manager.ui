/**
 * 
 */
package com.proserus.stocks.ui.view.general;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Collection;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JTextField;

import com.proserus.stocks.bo.symbols.CurrencyEnum;
import com.proserus.stocks.bo.symbols.SectorEnum;
import com.proserus.stocks.bo.symbols.Symbol;
import com.proserus.stocks.bo.transactions.TransactionType;
import com.proserus.stocks.bp.events.Event;
import com.proserus.stocks.bp.events.EventBus;
import com.proserus.stocks.bp.events.EventListener;
import com.proserus.stocks.bp.events.ModelChangeEvents;
import com.proserus.stocks.bp.model.Filter;
import com.proserus.stocks.bp.utils.DateUtils;
import com.proserus.stocks.ui.controller.ViewControllers;
import com.proserus.stocks.ui.view.common.SortedComboBoxModel;
import com.proserus.stocks.ui.view.symbols.EmptySymbol;

/**
 * @author Alex
 * 
 */
public class FilterPanelImpl extends AbstractFilterPanel implements ActionListener, EventListener, KeyListener {
	private static final long serialVersionUID = 201404041920L;
	private SortedComboBoxModel modelSymbols = new SortedComboBoxModel();
	private SortedComboBoxModel modelYears = new SortedComboBoxModel(new FilterYearComparator());
	private static FilterPanelImpl singleton = new FilterPanelImpl();
	private Filter filter = ViewControllers.getFilter();

	static public FilterPanelImpl getInstance() {
		return singleton;
	}

	private FilterPanelImpl() {
		setBorder(BorderFactory.createLineBorder(Color.black));
		getLabelList().setAddEnabled(false);
		getLabelList().setListColor(getLabelList().getBackground());
		getLabelList().setHorizontal(true);
		getLabelList().setModeFilter(true);
		EventBus.getInstance().add(getLabelList(), ModelChangeEvents.LABELS_UPDATED);

		EventBus.getInstance().add(this, ModelChangeEvents.TRANSACTION_UPDATED);

		getLabelList().setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

		getYearField().addActionListener(this);
		getYearField().setModel(modelYears);

		// populateSymbols();

		getSymbolField().setModel(modelSymbols);
		EventBus.getInstance().add(this, ModelChangeEvents.SYMBOLS_LIST_UPDATED);
		getSymbolField().addActionListener(this);
		getSymbolField().setMaximumRowCount(12);

		getTransactionTypeField().addItem(" ");
		for (TransactionType transactionType : TransactionType.values()) {
			if (transactionType.isVisible()) {
				getTransactionTypeField().addItem(transactionType);
			}
		}
		getTransactionTypeField().addActionListener(this);

		getCurrencyField().addItem(" ");
		for (CurrencyEnum currency : CurrencyEnum.values()) {
			if (currency.isVisible()) {
				getCurrencyField().addItem(currency);
			}
		}
		getCurrencyField().setMaximumRowCount(12);
		getCurrencyField().addActionListener(this);

		getSectorField().addItem(" ");
		for (SectorEnum sector : SectorEnum.retrieveSortedSectors()) {
			getSectorField().addItem(sector);
		}
		getSectorField().setMaximumRowCount(12);
		getSectorField().addActionListener(this);
	}

	private void populateYears(int min) {
		getYearField().removeActionListener(this);
		Object o = modelYears.getSelectedItem();

		modelYears.removeAllElements();
		for (int i = DateUtils.getCurrentYear(); i >= min; i--) {
			modelYears.addElement(i);
		}
		modelYears.addElement(" ");
		modelYears.setSelectedItem(o);
		getYearField().addActionListener(this);
	}

	// private void populateSymbols() {
	// Iterator<Symbol> iter =
	// ViewControllers.getController().getSymbols().iterator();
	// modelSymbols.removeAllElements();
	// while (iter.hasNext()) {
	// modelSymbols.addElement(iter.next());
	// }
	// Symbol s = new EmptySymbol();
	// modelSymbols.addElement(s);
	// }

	@Override
	public void actionPerformed(ActionEvent arg0) {
		if (arg0.getSource().equals(getYearField())) {
			Object selectedYear = ((JComboBox) arg0.getSource()).getSelectedItem();
			if (selectedYear instanceof Integer) {
				filter.setYear((Integer) selectedYear);
			} else {
				filter.setYear(null);
			}
			ViewControllers.getController().refreshFilteredData();
		} else if (arg0.getSource().equals(getSymbolField())) {
			Object o = ((JComboBox) arg0.getSource()).getSelectedItem();
			if (o instanceof Symbol) {
				if (((Symbol) o).getId() != null) {
					filter.setSymbol((Symbol) o);
				}

				else {
					filter.setSymbol(null);
				}
			}
			ViewControllers.getController().refreshFilteredData();
		} else if (arg0.getSource().equals(getTransactionTypeField())) {
			Object o = ((JComboBox) arg0.getSource()).getSelectedItem();
			TransactionType type = null;
			if (o instanceof TransactionType) {
				type = (TransactionType) o;
			}

			filter.setTransactionType(type);
			ViewControllers.getController().refreshFilteredData();
		} else if (arg0.getSource().equals(getCurrencyField())) {
			Object o = ((JComboBox) arg0.getSource()).getSelectedItem();
			CurrencyEnum type = null;
			if (o instanceof CurrencyEnum) {
				type = (CurrencyEnum) o;
			}

			filter.setCurrency(type);
			ViewControllers.getController().refreshFilteredData();
		} else if (arg0.getSource().equals(getSectorField())) {
			Object o = ((JComboBox) arg0.getSource()).getSelectedItem();
			SectorEnum type = null;
			if (o instanceof SectorEnum) {
				type = (SectorEnum) o;
			}

			filter.setSector(type);
			ViewControllers.getController().refreshFilteredData();
		}
	}

	@Override
	public void update(Event event, Object model) {
		if (ModelChangeEvents.TRANSACTION_UPDATED.equals(event)) {
			populateYears(ViewControllers.getController().getFirstYear());
		} else if (ModelChangeEvents.SYMBOLS_LIST_UPDATED.equals(event)) {
			Object o = modelSymbols.getSelectedItem();
			getSymbolField().removeActionListener(this);
			modelSymbols.removeAllElements();
			Collection<Symbol> symbols = ModelChangeEvents.SYMBOLS_LIST_UPDATED.resolveModel(model);
			for (Symbol symbol : symbols) {
				modelSymbols.addElement(symbol);
			}
			Symbol s = new EmptySymbol();
			modelSymbols.addElement(s);
			if (o != null && symbols.contains(o)) {
				modelSymbols.setSelectedItem(o);
			} else {
				filter.setSymbol(null);
				ViewControllers.getController().refreshFilteredData();
			}
			getSymbolField().addActionListener(this);
		}
	}

	@Override
	public void keyPressed(KeyEvent arg0) {
	}

	@Override
	public void keyReleased(KeyEvent textField) {
		if (!((JTextField) textField.getSource()).getText().isEmpty()) {
			ViewControllers.getController().setCustomFilter((((JTextField) textField.getSource()).getText()));
		} else {
			ViewControllers.getController().setCustomFilter("");
		}

	}

	@Override
	public void keyTyped(KeyEvent arg0) {
	}
}
