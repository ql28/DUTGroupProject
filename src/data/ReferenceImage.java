package data;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageFilter;
import java.awt.image.ImageProducer;
import java.awt.image.RGBImageFilter;
import java.io.File;
import java.io.IOException;
import java.util.Hashtable;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

public class ReferenceImage {

	protected static Hashtable<String, ImageIcon> tabImage;

	public static void chargerImages() {
		tabImage = new Hashtable<String, ImageIcon>();
		File[] fileImages = new File("res/img_objets").listFiles();
		for(File tmpFile : fileImages) {
			String tmpFileName = tmpFile.getName();
			int positionDernierPoint = tmpFileName.lastIndexOf(".");
			if(positionDernierPoint>0) {
				try {
					tabImage.put(tmpFileName.substring(0, positionDernierPoint), new ImageIcon(ImageIO.read(tmpFile)));
				}
				catch (IOException e) { e.printStackTrace(); }
			}
		}
	}

	public static ImageIcon getImageIcon(String nomImage) {
		if(tabImage==null) {
			chargerImages();
		}
		ImageIcon imageRetour = tabImage.get(nomImage);
		imageRetour = imageRetour==null ? tabImage.get("image_par_defaut") : imageRetour;
		return imageRetour;
	}
	// redimensionne l'image avec les bonnes proportions
	public static BufferedImage getResizedImage(BufferedImage image, int width, int height) {

		Image imageRedim = null;

		int newW = width;
		int newH = height;

		if(image!=null) {
			if(image.getHeight()<image.getWidth()) {
				newH = (int)((double)image.getHeight()*((double)width/(double)image.getWidth()));
				if(newH<1) { newH++; }
				imageRedim = image.getScaledInstance(
						width, 
						newH,
						Image.SCALE_AREA_AVERAGING
						);
			}
			else {
				newW = (int)((double)image.getWidth()*((double)height/(double)image.getHeight()));
				if(newW<1) { newW++; }
				imageRedim = image.getScaledInstance(
						newW, 
						height,
						Image.SCALE_AREA_AVERAGING
						);
			}

			BufferedImage copyImage = new BufferedImage(newW, newH, BufferedImage.TYPE_INT_ARGB);
			Graphics2D g = copyImage.createGraphics();
			g.drawImage(imageRedim, (width/2)-(newW/2), (height/2)-(newH/2), null);

			return copyImage;
		}
		return null;
	}

	public static ImageIcon getMiniMiniature(String nomImage, int width, int height) {

		if(tabImage==null) {
			chargerImages();
		}
		Image imageMiniature = null;
		int newW = width;
		int newH = height;
		if(getImageIcon(nomImage)!=null) {
			if(getImageIcon(nomImage).getIconHeight()<getImageIcon(nomImage).getIconWidth()) {
				newH = (int)((double)getImageIcon(nomImage).getIconHeight()*((double)width/(double)getImageIcon(nomImage).getIconWidth()));
				if(newH<1) { newH++; }
				imageMiniature = getImageIcon(nomImage).getImage().getScaledInstance(
						width, 
						newH, 
						Image.SCALE_AREA_AVERAGING
						);
			}
			else {
				newW = (int)((double)getImageIcon(nomImage).getIconWidth()*((double)height/(double)getImageIcon(nomImage).getIconHeight()));
				if(newW<1) { newW++; }
				imageMiniature = getImageIcon(nomImage).getImage().getScaledInstance(
						newW, 
						height,
						Image.SCALE_AREA_AVERAGING
						);
			}

			BufferedImage nouvelleImage = new BufferedImage(width+1, height+1, BufferedImage.TYPE_INT_RGB);
			for(int i = 0 ; i<height+1 ; i++) {
				for(int j = 0 ; j<width+1 ; j++) {
					nouvelleImage.setRGB(j, i, new Color(0, 255, 0).getRGB());
				}
			}
			Graphics2D g = (Graphics2D)nouvelleImage.getGraphics();
			g.drawImage(imageMiniature, (width/2)-(newW/2), (height/2)-(newH/2), null);
			Image imageCopy = makeColorTransparent(nouvelleImage, new Color(0, 255, 0));

			return new ImageIcon(imageCopy);
		}
		return null;

	}



	public static ImageIcon getImageIcon(String nomImage, int degre) {
		if(tabImage==null) {
			chargerImages();
		}

		ImageIcon image = getImageIcon(nomImage);

		int w = image.getIconWidth();
		int h = image.getIconHeight();
		double sin = Math.abs(Math.sin(Math.toRadians(degre)));
		double cos = Math.abs(Math.cos(Math.toRadians(degre)));
		int newW = (int)Math.floor(w*cos+h*sin);
		int newH = (int)Math.floor(h*cos+w*sin);

		BufferedImage nouvelleImage = new BufferedImage(newW, newH, BufferedImage.TYPE_INT_RGB);
		for(int i = 0 ; i<newH ; i++) {
			for(int j = 0 ; j<newW ; j++) {
				nouvelleImage.setRGB(j, i, new Color(0, 255, 0).getRGB());
			}
		}
		Graphics2D g = (Graphics2D)nouvelleImage.getGraphics();
		g.translate((newW-w)/2, (newH-h)/2);
		g.rotate(Math.toRadians(degre), w/2, h/2);
		g.drawImage(image.getImage(), 0, 0, null);
		Image imageCopy = makeColorTransparent(nouvelleImage, new Color(0, 255, 0));

		return new ImageIcon(imageCopy);
	}

	public static Image replaceColor(Image image, final Color color, final Color color2) {
		ImageFilter filter = new RGBImageFilter() {
			public int markerRGB = color.getRGB() | 0xFF000000;
			public final int filterRGB(int x, int y, int rgb) {
				if((rgb | 0xFF000000 )==markerRGB) {
					return color2.getRGB();
				}
				else {
					return rgb;
				}
			}
		};
		ImageProducer ip = new FilteredImageSource(image.getSource(), filter);
		return Toolkit.getDefaultToolkit().createImage(ip);
	}

	public static Image makeColorTransparent(Image image, final Color color) {
		ImageFilter filter = new RGBImageFilter() {
			public int markerRGB = color.getRGB() | 0xFF000000;
			public final int filterRGB(int x, int y, int rgb) {
				if((rgb | 0xFF000000)==markerRGB) {
					return 0x00FFFFFF & rgb;
				}
				else {
					return rgb;
				}
			}
		};
		ImageProducer ip = new FilteredImageSource(image.getSource(), filter);
		return Toolkit.getDefaultToolkit().createImage(ip);
	}

}