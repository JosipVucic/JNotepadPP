package hr.fer.oprpp1.hw08.jnotepadpp.local;

import java.util.Locale;
import java.util.ResourceBundle;
/**
 * The class responsible for defining a localisation provider singleton.
 * @author Josip Vucić
 *
 */
public class LocalizationProvider extends AbstractLocalizationProvider {
	/**
	 * The instance of this localisation provider.
	 */
	private static final LocalizationProvider instance = new LocalizationProvider();
	/**
	 * The current language.
	 */
	private String language;
	/**
	 * The resource bundle to access strings.
	 */
	private ResourceBundle bundle;
	/**
	 * Creates a new localisation provider instance.
	 */
	private LocalizationProvider() {
		language = "en";
		bundle = ResourceBundle.getBundle("hr.fer.oprpp1.hw08.jnotepadpp.localization.lan", Locale.forLanguageTag(language));
	}
	/**
	 * 
	 * @return The single instance of the localisation provider.
	 */
	public static LocalizationProvider getInstance() {
		return instance;
	}
	/**
	 * Returns the requested string according to the key.
	 */
	@Override
	public String getString(String key) {
		return bundle.getString(key);
	}
	/**
	 * Sets the current language.
	 * @param language
	 */
	public void setLanguage(String language) {
		this.language = language;
		bundle = ResourceBundle.getBundle("hr.fer.oprpp1.hw08.jnotepadpp.localization.lan", Locale.forLanguageTag(language));
		this.fire();
	}
	/**
	 * Returns the current language.
	 */
	public String getCurrentLanguage() {
		return language;
	}

}
