package hr.fer.oprpp1.hw08.jnotepadpp;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.swing.ImageIcon;
/**
 * The class responsible for fetching image icons.
 * @author Josip Vucić
 *
 */
public abstract class ImageIcons {
	/**
	 * The map that contains the icon names as keys and icons as values.
	 */
	public static final Map<String, ImageIcon> icons = new HashMap<>();
	/**
	 * Returns the icon for the specified key.
	 * @param iconName The specified key.
	 * @return The icon related to the key.
	 */
	public static ImageIcon getIcon(String iconName) {
		if (!icons.containsKey(iconName)) {
			return loadIcon(iconName);
		}

		return icons.get(iconName);
	}
	/**
	 * Loads the icon specified by the key.
	 * @param iconName The specified key.
	 * @return The icon related to the key.
	 */
	private static ImageIcon loadIcon(String iconName) {
		byte[] bytes = null;
		ImageIcon icon;
		try (InputStream is = ImageIcons.class.getResourceAsStream("icons/"+iconName+".png")) {

			if (is == null) {
				throw new IllegalArgumentException("No such icon.");
			}

			bytes = is.readAllBytes();
		} catch (IOException ex) {
			ex.printStackTrace();
			System.err.println("Error while loading icon.");
		}
		icon = new ImageIcon(bytes);
		icons.put(iconName, icon);
		
		return icon;
	}

}
