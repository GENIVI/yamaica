/* Copyright (C) 2013 BMW Group
 * Author: Manfred Bathelt (manfred.bathelt@bmw.de)
 * Author: Juergen Gehring (juergen.gehring@bmw.de)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package de.bmw.yamaica.base.ui.utils;

import java.awt.image.BufferedImage;
import java.awt.image.DirectColorModel;
import java.awt.image.IndexColorModel;
import java.awt.image.WritableRaster;
import java.io.File;
import java.util.HashMap;

import javax.swing.ImageIcon;
import javax.swing.filechooser.FileSystemView;

import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Display;

import sun.awt.image.ToolkitImage;
import de.bmw.yamaica.base.ui.internal.Activator;

public class FileSystemLabelProvider extends ColumnLabelProvider
{
    protected HashMap<String, Image> fileImageCache = new HashMap<String, Image>();
    protected Image                  folderImageCache;

    @Override
    public Image getImage(Object element)
    {
        File file = (File) element;
        Display display = Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell().getDisplay();

        if (file.isFile())
        {
            String filename = ((File) element).getName();
            String fileExtension = filename.substring(filename.lastIndexOf(".") + 1);

            Image image = null;

            if (fileImageCache.containsKey(fileExtension))
            {
                image = fileImageCache.get(fileExtension);
            }
            else
            {
                Program program = Program.findProgram(fileExtension);

                if (null != program)
                {
                    ImageData imageData = program.getImageData();

                    if (null != imageData)
                    {
                        image = new Image(display, imageData);
                    }
                }

                if (null == image)
                {
                    // Fall back if above implementation fails.
                    image = Activator.imageDescriptorFromPlugin(YamaicaUIConstants.ECLIPSE_UI_IDE_PLUGIN_ID,
                            YamaicaUIConstants.FILE_TYPE_ICON_GIF_PATH).createImage();
                }

                fileImageCache.put(fileExtension, image);
            }

            return image;
        }
        else
        {
            if (null == folderImageCache)
            {
                ImageIcon icon = (ImageIcon) FileSystemView.getFileSystemView().getSystemIcon(file);
                java.awt.Image image = icon.getImage();

                if (image instanceof BufferedImage)
                {
                    // Default implementation.
                    folderImageCache = new Image(display, convertToSWT((BufferedImage) image));
                }
                else if (image instanceof ToolkitImage)
                {
                    // Workaround for badly supported systems (mainly UNIX systems).
                    folderImageCache = new Image(display, convertToSWT(((ToolkitImage) image).getBufferedImage()));
                }
                else
                {
                    // Fall back if above implementations fail.
                    folderImageCache = Activator.imageDescriptorFromPlugin(YamaicaUIConstants.ECLIPSE_UI_IDE_PLUGIN_ID,
                            YamaicaUIConstants.FOLDER_GIF_PATH).createImage();
                }
            }

            return folderImageCache;
        }
    }

    @Override
    public String getText(Object element)
    {
        return ((File) element).getName();
    }

    @Override
    public void dispose()
    {
        folderImageCache = null;
        fileImageCache = null;

        super.dispose();
    }

    static ImageData convertToSWT(BufferedImage bufferedImage)
    {
        if (bufferedImage.getColorModel() instanceof DirectColorModel)
        {
            DirectColorModel colorModel = (DirectColorModel) bufferedImage.getColorModel();
            PaletteData palette = new PaletteData(colorModel.getRedMask(), colorModel.getGreenMask(), colorModel.getBlueMask());
            ImageData data = new ImageData(bufferedImage.getWidth(), bufferedImage.getHeight(), colorModel.getPixelSize(), palette);

            for (int y = 0; y < data.height; y++)
            {
                for (int x = 0; x < data.width; x++)
                {
                    int rgb = bufferedImage.getRGB(x, y);
                    int pixel = palette.getPixel(new RGB((rgb >> 16) & 0xFF, (rgb >> 8) & 0xFF, rgb & 0xFF));
                    data.setPixel(x, y, pixel);

                    if (colorModel.hasAlpha())
                    {
                        data.setAlpha(x, y, (rgb >> 24) & 0xFF);
                    }
                }
            }

            return data;
        }
        else if (bufferedImage.getColorModel() instanceof IndexColorModel)
        {
            IndexColorModel colorModel = (IndexColorModel) bufferedImage.getColorModel();
            int size = colorModel.getMapSize();
            byte[] reds = new byte[size];
            byte[] greens = new byte[size];
            byte[] blues = new byte[size];
            colorModel.getReds(reds);
            colorModel.getGreens(greens);
            colorModel.getBlues(blues);
            RGB[] rgbs = new RGB[size];

            for (int i = 0; i < rgbs.length; i++)
            {
                rgbs[i] = new RGB(reds[i] & 0xFF, greens[i] & 0xFF, blues[i] & 0xFF);
            }

            PaletteData palette = new PaletteData(rgbs);
            ImageData data = new ImageData(bufferedImage.getWidth(), bufferedImage.getHeight(), colorModel.getPixelSize(), palette);
            data.transparentPixel = colorModel.getTransparentPixel();
            WritableRaster raster = bufferedImage.getRaster();
            int[] pixelArray = new int[1];

            for (int y = 0; y < data.height; y++)
            {
                for (int x = 0; x < data.width; x++)
                {
                    raster.getPixel(x, y, pixelArray);
                    data.setPixel(x, y, pixelArray[0]);
                }
            }

            return data;
        }

        return null;
    }
}
