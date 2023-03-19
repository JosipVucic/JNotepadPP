package hr.fer.oprpp1.hw08.jnotepadpp.local;
/**
 * The class responsible for defining a bridge to a localisation provider. Acts as a decorator.
 * @author Josip Vucić
 *
 */
public class LocalizationProviderBridge extends AbstractLocalizationProvider {
	/**
	 * Whether the bridge is connected.
	 */
	private boolean connected;
	/**
	 * The provider to connect to.
	 */
	private ILocalizationProvider provider;
	/**
	 * The current language.
	 */
	private String currentLanguage = " ";
	/**
	 * The localisation listener for the connected provider.
	 */
	private ILocalizationListener listener = () -> {
		LocalizationProviderBridge.this.fire();
		currentLanguage = provider.getCurrentLanguage();
	};
	/**
	 * Creates a new LocalisationProviderBridge with the given provider
	 * @param provider
	 */
	public LocalizationProviderBridge(ILocalizationProvider provider) {
		this.provider = provider;
	}
	/**
	 * Connects to the provider if not already connected. Alerts listeners if the language has changed after connecting.
	 */
	public void connect() {
		if (connected)
			return;

		connected = true;
		provider.addLocalizationListener(listener);

		if (!currentLanguage.equals(provider.getCurrentLanguage())) {
			listener.localizationChanged();
		}
	}
	/**
	 * Disconnects from the provider if not already disconnected.
	 */
	public void disconnect() {
		if (!connected)
			return;

		connected = false;
		provider.removeLocalizationListener(listener);
	}
	/**
	 * Returns the requested string according to the key.
	 */
	@Override
	public String getString(String key) {
		return provider.getString(key);
	}
	/**
	 * Returns the current language.
	 */
	public String getCurrentLanguage() {
		return provider.getCurrentLanguage();
	}

}
