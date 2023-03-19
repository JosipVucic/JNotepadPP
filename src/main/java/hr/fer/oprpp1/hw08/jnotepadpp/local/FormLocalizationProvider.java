package hr.fer.oprpp1.hw08.jnotepadpp.local;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
/**
 * The class responsible for defining a localisation provider bridge for a given JFrame object.
 * @author Josip Vucić
 *
 */
public class FormLocalizationProvider extends LocalizationProviderBridge {
	/**
	 * Creates a new FormLocalisationProvider that acts as a bridge to the given localisation provider and connects as soon a sthe given frame is opened. Disconnects when the frame is closed.
	 * @param provider The given localisation provider.
	 * @param frame The frame that determines the start and end of the connection.
	 */
	public FormLocalizationProvider(ILocalizationProvider provider, JFrame frame) {
		super(provider);
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowOpened(WindowEvent e) {
				connect();
			}
			
			@Override
			public void windowClosed(WindowEvent e) {
				disconnect();
			}
		});
	}

}
