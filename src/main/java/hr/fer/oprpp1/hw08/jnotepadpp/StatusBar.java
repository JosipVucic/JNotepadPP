package hr.fer.oprpp1.hw08.jnotepadpp;

import java.awt.GridLayout;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import hr.fer.oprpp1.hw08.jnotepadpp.local.ILocalizationListener;
import hr.fer.oprpp1.hw08.jnotepadpp.local.ILocalizationProvider;
/**
 * The class responsible for defining a localisable status bar for our app.
 * @author Josip Vucić
 *
 */
public class StatusBar extends JPanel {

	private static final long serialVersionUID = 1L;
	/**
	 * The length of the document.
	 */
	private JLabel length;
	/**
	 * The line, column and selection size number.
	 */
	private JLabel lineColSel;
	/**
	 * The clock to display current date and time.
	 */
	private Clock clock;
	
	/**
	 * The length of the document.
	 */
	private int len = 0;
	/**
	 * The line number.
	 */
	private int line = 1;
	/**
	 * The column number.
	 */
	private int column = 1;
	/**
	 * The size of the selection.
	 */
	private int selection = 0;
	/**
	 * The format for displaying document length.
	 */
	private String lengthFormat;
	/**
	 * The format for displaying line, column and selection size.
	 */
	private String LCSFormat;
	/**
	 * The localisation provider.
	 */
	private ILocalizationProvider provider;
	/**
	 * The listener for our localisation provider.
	 */
	private ILocalizationListener listener = () -> {
		this.lengthFormat = provider.getString("LengthFormat");
		this.LCSFormat = provider.getString("LCSFormat");
		
		this.setStatus(len, line, column, selection);
	};
	/**
	 * Creates a new status bar with the given localisation provider.
	 * @param provider
	 */
	public StatusBar(ILocalizationProvider provider) {
		this.provider = provider;
		provider.addLocalizationListener(listener);
		
		length = new JLabel();
		lineColSel = new JLabel();
		clock = new Clock();
		length.setBorder(BorderFactory.createEtchedBorder());
		lineColSel.setBorder(BorderFactory.createEtchedBorder());
		
		listener.localizationChanged();

		this.setLayout(new GridLayout(1, 3));
		this.setBorder(BorderFactory.createEtchedBorder());
		this.add(length);
		this.add(lineColSel);
		this.add(clock);

	}
	/**
	 * Sets the status of the status bar.
	 * @param len The document length.
	 * @param line The current line.
	 * @param column The current column.
	 * @param selection The selection size.
	 */
	public void setStatus(int len, int line, int column, int selection) {
		length.setText(String.format(lengthFormat, len));
		lineColSel.setText(String.format(LCSFormat, line, column, selection));
	}
	/**
	 * Stops the clock thread.
	 */
	public void stopClock() {
		clock.stopRequested=true;
	}
	/**
	 * The class responsible for defining a clock for our status bar needs.
	 * @author Josip Vucić
	 *
	 */
	private class Clock extends JLabel {

		private static final long serialVersionUID = 1L;
		/**
		 * The string that contains the current date and time.
		 */
		volatile String dateTime;
		/**
		 * Whether the clock thread should stop working.
		 */
		volatile boolean stopRequested;
		/**
		 * The formatter for the display of date time.
		 */
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(provider.getString("DateTimeFormat"));
		/**
		 * Creates a new clock object and updates it in a new thread.
		 */
		public Clock() {
			super("-", SwingConstants.RIGHT);
			this.setBorder(BorderFactory.createEtchedBorder());
			updateTime();
			
			Thread t = new Thread(()->{
				while(true) {
					try {
						Thread.sleep(500);
					} catch(Exception ex) {}
					if(stopRequested) break;
					SwingUtilities.invokeLater(()->{
						updateTime();
					});
				}
				System.out.println("Zaustavljen.");
			});
			t.setDaemon(true);
			t.start();
		}
		/**
		 * Updates the clock.
		 */
		private void updateTime() {
			dateTime = formatter.format(LocalDateTime.now());
			this.setText(dateTime);
		}
	}

}
